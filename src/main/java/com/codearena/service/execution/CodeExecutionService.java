package com.codearena.service.execution;

import com.codearena.entity.Language;
import com.codearena.entity.SubmissionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class CodeExecutionService {

    private static final Logger log = LoggerFactory.getLogger(CodeExecutionService.class);
    private static final Pattern METHOD_PATTERN = Pattern.compile("public\\s+([\\w<>\\[\\]]+)\\s+([a-zA-Z0-9_]+)\\s*\\(([^)]*)\\)");

    private static final List<Pattern> DISALLOWED_PATTERNS = List.of(
            Pattern.compile("\\bSystem\\s*\\.\\s*exit\\b"),
            Pattern.compile("\\bRuntime\\s*\\.\\s*getRuntime\\b"),
            Pattern.compile("\\bProcessBuilder\\b"),
            Pattern.compile("\\bjava\\s*\\.\\s*lang\\s*\\.\\s*reflect\\b"),
            Pattern.compile("\\b(java\\s*\\.\\s*net\\s*\\.\\s*(Socket|ServerSocket|HttpURLConnection|DatagramSocket))\\b")
    );

    private volatile Boolean dockerAvailable = null;

    public ExecutionResult execute(String sourceCode, Language language, List<TestCaseItem> testCases,
                                   int timeLimitSeconds, int memoryLimitMb) {
        if (language != Language.JAVA) {
            ExecutionResult res = new ExecutionResult();
            res.setStatus(SubmissionStatus.INTERNAL_ERROR);
            res.setCompileError("Language " + language + " is currently in development. Java is supported.");
            return res;
        }

        if (testCases == null || testCases.isEmpty()) {
            ExecutionResult res = new ExecutionResult();
            res.setStatus(SubmissionStatus.PENDING);
            return res;
        }

        String securityViolation = validateSecurityPolicy(sourceCode);
        if (securityViolation != null) {
            ExecutionResult res = new ExecutionResult();
            res.setStatus(SubmissionStatus.COMPILATION_ERROR);
            res.setCompileError(securityViolation);
            res.setTotalTestCases(testCases.size());
            return res;
        }

        int timeout = Math.max(timeLimitSeconds, 2);
        int memory = Math.max(memoryLimitMb, 128);

        Path tempDir = null;
        try {
            tempDir = Files.createTempDirectory("codearena_run_");
            String preparedCode = prepareJavaCode(sourceCode);
            Path sourceFile = tempDir.resolve("Solution.java");
            Files.writeString(sourceFile, preparedCode, StandardCharsets.UTF_8);

            boolean useDocker = isDockerAvailable();

            // 1. Compile step
            CompileResult compileResult = compileJava(tempDir, useDocker);
            if (!compileResult.success) {
                ExecutionResult res = new ExecutionResult(SubmissionStatus.COMPILATION_ERROR, compileResult.errorMessage);
                res.setTotalTestCases(testCases.size());
                return res;
            }

            // 2. Execute test cases sequentially
            ExecutionResult result = new ExecutionResult();
            result.setStatus(SubmissionStatus.ACCEPTED);
            long maxExecutionTime = 0;

            int caseNumber = 1;
            for (TestCaseItem testCase : testCases) {
                String input = sanitizeInput(testCase.getInput());
                RunResult runResult = runTestCase(tempDir, input, timeout, memory, useDocker);

                TestCaseResult tcResult = new TestCaseResult();
                tcResult.setCaseNumber(caseNumber++);
                tcResult.setInput(testCase.getInput());
                tcResult.setExpectedOutput(testCase.getExpectedOutput());
                tcResult.setActualOutput(runResult.stdout);
                tcResult.setExecutionTimeMs(runResult.executionTimeMs);
                tcResult.setErrorMessage(runResult.stderr);

                maxExecutionTime = Math.max(maxExecutionTime, runResult.executionTimeMs);

                if (runResult.timedOut) {
                    tcResult.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    tcResult.setPassed(false);
                    if (result.getStatus() == SubmissionStatus.ACCEPTED) {
                        result.setStatus(SubmissionStatus.TIME_LIMIT_EXCEEDED);
                    }
                } else if (!runResult.success) {
                    tcResult.setStatus(SubmissionStatus.RUNTIME_ERROR);
                    tcResult.setPassed(false);
                    if (result.getStatus() == SubmissionStatus.ACCEPTED) {
                        result.setStatus(SubmissionStatus.RUNTIME_ERROR);
                    }
                } else {
                    boolean match = OutputComparator.isMatch(runResult.stdout, testCase.getExpectedOutput());
                    tcResult.setPassed(match);
                    if (match) {
                        tcResult.setStatus(SubmissionStatus.ACCEPTED);
                    } else {
                        tcResult.setStatus(SubmissionStatus.WRONG_ANSWER);
                        if (result.getStatus() == SubmissionStatus.ACCEPTED) {
                            result.setStatus(SubmissionStatus.WRONG_ANSWER);
                        }
                    }
                }

                result.addTestCaseResult(tcResult);
            }

            result.setExecutionTimeMs(maxExecutionTime);
            result.setMemoryUsedKb(32768L); // Baseline ~32MB JVM footprint
            return result;

        } catch (Exception e) {
            log.error("Execution failed unexpectedly", e);
            ExecutionResult res = new ExecutionResult();
            res.setStatus(SubmissionStatus.INTERNAL_ERROR);
            res.setCompileError("Execution engine error: " + e.getMessage());
            return res;
        } finally {
            if (tempDir != null) {
                deleteDirectoryRecursively(tempDir);
            }
        }
    }

    private String prepareJavaCode(String code) {
        String trimmed = code.trim();

        // Ensure class is named Solution
        if (!trimmed.contains("class Solution")) {
            trimmed = trimmed.replaceFirst("class\\s+[A-Za-z0-9_]+", "class Solution");
        }

        // If main method already exists, return as-is
        if (trimmed.contains("public static void main")) {
            return trimmed;
        }

        // If no main method, check if LeetCode-style method exists and inject a test driver main
        Matcher matcher = METHOD_PATTERN.matcher(trimmed);
        String targetMethod = null;
        String returnType = null;
        String paramTypes = null;

        while (matcher.find()) {
            String mName = matcher.group(2);
            if (!mName.equals("main") && !mName.equals("equals") && !mName.equals("hashCode") && !mName.equals("toString")) {
                returnType = matcher.group(1);
                targetMethod = mName;
                paramTypes = matcher.group(3);
                break;
            }
        }

        if (targetMethod != null && trimmed.lastIndexOf('}') > 0) {
            int lastBrace = trimmed.lastIndexOf('}');
            String driver = buildDriverMain(targetMethod, returnType, paramTypes);
            return trimmed.substring(0, lastBrace) + "\n\n" + driver + "\n}";
        }

        return trimmed;
    }

    private String buildDriverMain(String methodName, String returnType, String params) {
        StringBuilder sb = new StringBuilder();
        sb.append("    public static void main(String[] args) {\n");
        sb.append("        java.util.Scanner sc = new java.util.Scanner(System.in);\n");
        sb.append("        if (!sc.hasNext()) return;\n");
        sb.append("        String input = sc.useDelimiter(\"\\\\A\").next().trim();\n");
        sb.append("        Solution sol = new Solution();\n");

        if (params.contains("int n") || (params.contains("int ") && !params.contains(","))) {
            sb.append("        int n = 0;\n");
            sb.append("        java.util.regex.Matcher m = java.util.regex.Pattern.compile(\"-?\\\\d+\").matcher(input);\n");
            sb.append("        if (m.find()) n = Integer.parseInt(m.group());\n");
            sb.append("        Object res = sol.").append(methodName).append("(n);\n");
        } else if (params.contains("String s") || (params.contains("String ") && !params.contains(","))) {
            sb.append("        String s = input;\n");
            sb.append("        if (s.contains(\"=\")) s = s.substring(s.indexOf('=') + 1).trim();\n");
            sb.append("        if (s.startsWith(\"\\\"\") && s.endsWith(\"\\\"\") && s.length() >= 2) s = s.substring(1, s.length() - 1);\n");
            sb.append("        Object res = sol.").append(methodName).append("(s);\n");
        } else {
            sb.append("        Object res = null;\n");
        }

        sb.append("        if (res instanceof java.util.Collection) {\n");
        sb.append("            java.util.Collection<?> col = (java.util.Collection<?>) res;\n");
        sb.append("            StringBuilder json = new StringBuilder(\"[\");\n");
        sb.append("            int i = 0;\n");
        sb.append("            for (Object obj : col) {\n");
        sb.append("                json.append(\"\\\"\").append(obj).append(\"\\\"\");\n");
        sb.append("                if (++i < col.size()) json.append(\",\");\n");
        sb.append("            }\n");
        sb.append("            json.append(\"]\");\n");
        sb.append("            System.out.println(json.toString());\n");
        sb.append("        } else {\n");
        sb.append("            System.out.println(res);\n");
        sb.append("        }\n");
        sb.append("    }\n");
        return sb.toString();
    }

    private String sanitizeInput(String rawInput) {
        if (rawInput == null) return "";
        String trimmed = rawInput.trim();

        // For inputs like "n = 2", extract clean value "2" and append raw on next line so both parsers succeed
        Matcher m = Pattern.compile("^[a-zA-Z_]+\\s*=\\s*(.*)$").matcher(trimmed);
        if (m.matches()) {
            String cleanVal = m.group(1).trim();
            // If quoted string e.g. s = "0P"
            if (cleanVal.startsWith("\"") && cleanVal.endsWith("\"") && cleanVal.length() >= 2) {
                cleanVal = cleanVal.substring(1, cleanVal.length() - 1);
            }
            return cleanVal + "\n" + trimmed;
        }

        return trimmed;
    }

    private CompileResult compileJava(Path dir, boolean useDocker) {
        try {
            if (useDocker) {
                String wslPath = toWslPath(dir);
                ProcessBuilder pb = new ProcessBuilder(
                        resolveDockerCommand(), "run", "--rm",
                        "--network", "none",
                        "--memory", "256m",
                        "--cpus", "1.0",
                        "-v", wslPath + ":/sandbox",
                        "-w", "/sandbox",
                        "eclipse-temurin:17-alpine",
                        "javac", "Solution.java"
                );
                return executeProcess(pb, 15);
            } else {
                ProcessBuilder pb = new ProcessBuilder("javac", "Solution.java");
                pb.directory(dir.toFile());
                pb.environment().clear(); // Strict security: zero host env vars
                return executeProcess(pb, 15);
            }
        } catch (Exception e) {
            return new CompileResult(false, "Compilation error: " + e.getMessage());
        }
    }

    private RunResult runTestCase(Path dir, String input, int timeoutSeconds, int memoryLimitMb, boolean useDocker) {
        Path inputFile = dir.resolve("input.txt");
        try {
            Files.writeString(inputFile, input, StandardCharsets.UTF_8);
            long start = System.currentTimeMillis();

            if (useDocker) {
                String wslPath = toWslPath(dir);
                ProcessBuilder pb = new ProcessBuilder(
                        resolveDockerCommand(), "run", "--rm",
                        "--network", "none",
                        "--memory", memoryLimitMb + "m",
                        "--cpus", "1.0",
                        "-v", wslPath + ":/sandbox",
                        "-w", "/sandbox",
                        "eclipse-temurin:17-alpine",
                        "sh", "-c", "java -Xmx" + memoryLimitMb + "m Solution < input.txt"
                );
                return executeRunProcess(pb, timeoutSeconds, start);
            } else {
                ProcessBuilder pb = new ProcessBuilder("java", "-Xmx" + memoryLimitMb + "m", "Solution");
                pb.directory(dir.toFile());
                pb.environment().clear();
                pb.redirectInput(inputFile.toFile());
                return executeRunProcess(pb, timeoutSeconds, start);
            }
        } catch (Exception e) {
            return new RunResult(false, false, "", "Runtime error: " + e.getMessage(), 0);
        }
    }

    private CompileResult executeProcess(ProcessBuilder pb, int timeoutSeconds) throws Exception {
        Process process = pb.start();
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);

        if (!finished) {
            process.destroyForcibly();
            return new CompileResult(false, "Compilation timed out after " + timeoutSeconds + " seconds.");
        }

        int exitCode = process.exitValue();
        String stderr = readStream(process.getErrorStream());
        if (exitCode != 0) {
            return new CompileResult(false, stderr.isEmpty() ? "Compilation failed with exit code " + exitCode : stderr);
        }
        return new CompileResult(true, "");
    }

    private RunResult executeRunProcess(ProcessBuilder pb, int timeoutSeconds, long startTime) throws Exception {
        Process process = pb.start();
        boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
        long elapsed = System.currentTimeMillis() - startTime;

        if (!finished) {
            process.destroyForcibly();
            return new RunResult(false, true, "", "Time Limit Exceeded (" + timeoutSeconds + "s limit)", elapsed);
        }

        int exitCode = process.exitValue();
        String stdout = readStream(process.getInputStream());
        String stderr = readStream(process.getErrorStream());

        return new RunResult(exitCode == 0, false, stdout, stderr, elapsed);
    }

    private String readStream(java.io.InputStream stream) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            return sb.toString().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private String validateSecurityPolicy(String sourceCode) {
        if (sourceCode == null) return null;
        for (Pattern pattern : DISALLOWED_PATTERNS) {
            if (pattern.matcher(sourceCode).find()) {
                return "Security Policy Violation: Restricted operation or system call detected in source code (" + pattern.pattern() + ").";
            }
        }
        return null;
    }

    private String toWslPath(Path path) {
        String p = path.toAbsolutePath().toString().replace('\\', '/');
        return p;
    }

    private String resolveDockerCommand() {
        String cliOverride = System.getenv("DOCKER_CLI_PATH");
        if (cliOverride != null && new File(cliOverride).exists()) {
            return cliOverride;
        }
        String localAppData = System.getenv("LOCALAPPDATA");
        if (localAppData != null) {
            File userBin = new File(localAppData, "Programs\\DockerDesktop\\resources\\bin\\docker.exe");
            if (userBin.exists()) {
                return userBin.getAbsolutePath();
            }
        }
        File progFilesBin = new File("C:\\Program Files\\Docker\\Docker\\resources\\bin\\docker.exe");
        if (progFilesBin.exists()) {
            return progFilesBin.getAbsolutePath();
        }
        return "docker";
    }

    private boolean isDockerAvailable() {
        if (dockerAvailable != null) {
            return dockerAvailable;
        }
        try {
            ProcessBuilder pb = new ProcessBuilder(resolveDockerCommand(), "image", "inspect", "eclipse-temurin:17-alpine");
            Process p = pb.start();
            boolean ok = p.waitFor(3, TimeUnit.SECONDS) && p.exitValue() == 0;
            dockerAvailable = ok;
            log.info("Docker sandbox image detection result (eclipse-temurin:17-alpine): {}", ok);
            return ok;
        } catch (Exception e) {
            dockerAvailable = false;
            log.warn("Docker not available for code execution, falling back to process sandbox: {}", e.getMessage());
            return false;
        }
    }

    private void deleteDirectoryRecursively(Path path) {
        try {
            Files.walk(path)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (Exception ignored) {
        }
    }

    private static class CompileResult {
        final boolean success;
        final String errorMessage;

        CompileResult(boolean success, String errorMessage) {
            this.success = success;
            this.errorMessage = errorMessage;
        }
    }

    private static class RunResult {
        final boolean success;
        final boolean timedOut;
        final String stdout;
        final String stderr;
        final long executionTimeMs;

        RunResult(boolean success, boolean timedOut, String stdout, String stderr, long executionTimeMs) {
            this.success = success;
            this.timedOut = timedOut;
            this.stdout = stdout;
            this.stderr = stderr;
            this.executionTimeMs = executionTimeMs;
        }
    }
}
