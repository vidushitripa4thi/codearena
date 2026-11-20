package com.codearena.service.execution;

import com.codearena.entity.Language;
import com.codearena.entity.SubmissionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class CodeExecutionServiceTest {

    private CodeExecutionService executionService;

    @BeforeEach
    void setUp() {
        executionService = new CodeExecutionService();
    }

    @Test
    void compilationErrorWhenSyntaxIsInvalid() {
        String invalidCode = "public class Solution { invalid syntax }";
        List<TestCaseItem> testCases = List.of(new TestCaseItem(1L, "1", "1"));

        ExecutionResult result = executionService.execute(invalidCode, Language.JAVA, testCases, 2, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompileError()).isNotEmpty();
    }

    @Test
    void executeSimpleSolutionAccepted() {
        String code = """
            import java.util.Scanner;
            public class Solution {
                public static void main(String[] args) {
                    Scanner sc = new Scanner(System.in);
                    int a = sc.nextInt();
                    System.out.println(a * 2);
                }
            }
            """;
        List<TestCaseItem> testCases = List.of(
                new TestCaseItem(1L, "5", "10"),
                new TestCaseItem(2L, "12", "24")
        );

        ExecutionResult result = executionService.execute(code, Language.JAVA, testCases, 3, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(result.getTotalTestCases()).isEqualTo(2);
        assertThat(result.getPassedTestCases()).isEqualTo(2);
        assertThat(result.getTestCaseResults().get(0).isPassed()).isTrue();
        assertThat(result.getTestCaseResults().get(0).getActualOutput()).isEqualTo("10");
    }

    @Test
    void executeWrongAnswerDetected() {
        String code = """
            public class Solution {
                public static void main(String[] args) {
                    System.out.println("42");
                }
            }
            """;
        List<TestCaseItem> testCases = List.of(new TestCaseItem(1L, "5", "100"));

        ExecutionResult result = executionService.execute(code, Language.JAVA, testCases, 2, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.WRONG_ANSWER);
        assertThat(result.getPassedTestCases()).isEqualTo(0);
        assertThat(result.getTestCaseResults().get(0).isPassed()).isFalse();
    }

    @Test
    void executeGenerateParenthesesProblem() {
        String code = """
            import java.util.*;
            public class Solution {
                public static void main(String[] args) {
                    Scanner sc = new Scanner(System.in);
                    if (!sc.hasNext()) return;
                    String line = sc.nextLine().trim();
                    int n = 0;
                    java.util.regex.Matcher m = java.util.regex.Pattern.compile("-?\\\\d+").matcher(line);
                    if (m.find()) n = Integer.parseInt(m.group());
                    List<String> res = new ArrayList<>();
                    backtrack(res, new StringBuilder(), 0, 0, n);
                    StringBuilder sb = new StringBuilder("[");
                    for (int i = 0; i < res.size(); i++) {
                        sb.append("\\\"").append(res.get(i)).append("\\\"");
                        if (i < res.size() - 1) sb.append(",");
                    }
                    sb.append("]");
                    System.out.println(sb.toString());
                }

                private static void backtrack(List<String> list, StringBuilder cur, int open, int close, int max) {
                    if (cur.length() == max * 2) {
                        list.add(cur.toString());
                        return;
                    }
                    if (open < max) {
                        cur.append('(');
                        backtrack(list, cur, open + 1, close, max);
                        cur.deleteCharAt(cur.length() - 1);
                    }
                    if (close < open) {
                        cur.append(')');
                        backtrack(list, cur, open, close + 1, max);
                        cur.deleteCharAt(cur.length() - 1);
                    }
                }
            }
            """;

        List<TestCaseItem> testCases = List.of(
                new TestCaseItem(1L, "n = 1", "[\"()\"]"),
                new TestCaseItem(2L, "n = 2", "[\"(())\",\"()()\"]")
        );

        ExecutionResult result = executionService.execute(code, Language.JAVA, testCases, 5, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.ACCEPTED);
        assertThat(result.getTotalTestCases()).isEqualTo(2);
        assertThat(result.getPassedTestCases()).isEqualTo(2);
    }

    @Test
    void securityViolationRejectsSystemExit() {
        String code = """
            public class Solution {
                public static void main(String[] args) {
                    System.exit(0);
                }
            }
            """;
        List<TestCaseItem> testCases = List.of(new TestCaseItem(1L, "1", "1"));

        ExecutionResult result = executionService.execute(code, Language.JAVA, testCases, 2, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompileError()).contains("Security Policy Violation");
    }

    @Test
    void securityViolationRejectsProcessSpawning() {
        String code = """
            public class Solution {
                public static void main(String[] args) {
                    ProcessBuilder pb = new ProcessBuilder("calc");
                }
            }
            """;
        List<TestCaseItem> testCases = List.of(new TestCaseItem(1L, "1", "1"));

        ExecutionResult result = executionService.execute(code, Language.JAVA, testCases, 2, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompileError()).contains("Security Policy Violation");
    }

    @Test
    void securityViolationRejectsSocketNetworking() {
        String code = """
            public class Solution {
                public static void main(String[] args) {
                    java.net.Socket s = null;
                }
            }
            """;
        List<TestCaseItem> testCases = List.of(new TestCaseItem(1L, "1", "1"));

        ExecutionResult result = executionService.execute(code, Language.JAVA, testCases, 2, 128);

        assertThat(result.getStatus()).isEqualTo(SubmissionStatus.COMPILATION_ERROR);
        assertThat(result.getCompileError()).contains("Security Policy Violation");
    }
}
