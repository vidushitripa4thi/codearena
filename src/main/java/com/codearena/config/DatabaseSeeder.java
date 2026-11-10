package com.codearena.config;

import com.codearena.entity.Difficulty;
import com.codearena.entity.Example;
import com.codearena.entity.Problem;
import com.codearena.entity.Role;
import com.codearena.entity.TestCase;
import com.codearena.entity.User;
import com.codearena.repository.ProblemRepository;
import com.codearena.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Component
public class DatabaseSeeder implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSeeder.class);

    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.enabled:true}")
    private boolean seedEnabled;

    @Value("${app.admin.username:}")
    private String adminUsername;

    @Value("${app.admin.email:}")
    private String adminEmail;

    @Value("${app.admin.password:}")
    private String adminPassword;

    public DatabaseSeeder(
            ProblemRepository problemRepository,
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.problemRepository = problemRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!seedEnabled) {
            log.info("Database seeding is disabled (app.seed.enabled=false).");
            return;
        }

        seedProblems();
        seedAdmin();
    }

    @Transactional
    public void seedProblems() {
        log.info("Starting idempotent problem seeding...");
        int seededCount = 0;

        // 1. Two Sum Pairs (Arrays & Hash Maps)
        if (createProblemIfAbsent(
                "Two Sum Pairs",
                "Given an array of integers nums and an integer target, return the 0-based indices of the two numbers such that they add up to target.\n\nYou may assume that each input would have exactly one solution, and you may not use the same element twice.\n\nYou can return the answer in any order.",
                Difficulty.EASY,
                "• 2 <= nums.length <= 10^4\n• -10^9 <= nums[i] <= 10^9\n• -10^9 <= target <= 10^9\n• Exactly one valid answer exists.",
                "First line: comma-separated integers representing nums.\nSecond line: integer representing target.",
                "0-based indices in format [i, j].",
                1,
                256,
                List.of(
                        new String[]{"nums = [2,7,11,15], target = 9", "[0,1]"},
                        new String[]{"nums = [3,2,4], target = 6", "[1,2]"}
                ),
                List.of(
                        new String[]{"nums = [3,3], target = 6", "[0,1]"},
                        new String[]{"nums = [-1,-2,-3,-4,-5], target = -8", "[2,4]"},
                        new String[]{"nums = [0,4,3,0], target = 0", "[0,3]"}
                )
        )) seededCount++;

        // 2. Valid Palindrome (Strings & Two Pointers)
        if (createProblemIfAbsent(
                "Valid Palindrome",
                "A phrase is a palindrome if, after converting all uppercase letters into lowercase letters and removing all non-alphanumeric characters, it reads the same forward and backward. Alphanumeric characters include letters and numbers.\n\nGiven a string s, return true if it is a palindrome, or false otherwise.",
                Difficulty.EASY,
                "• 1 <= s.length <= 2 * 10^5\n• s consists only of printable ASCII characters.",
                "A single line containing the string s.",
                "true or false.",
                1,
                256,
                List.of(
                        new String[]{"s = \"A man, a plan, a canal: Panama\"", "true"},
                        new String[]{"s = \"race a car\"", "false"}
                ),
                List.of(
                        new String[]{"s = \" \"", "true"},
                        new String[]{"s = \"0P\"", "false"},
                        new String[]{"s = \"ab_a\"", "true"}
                )
        )) seededCount++;

        // 3. Maximum Subarray Sum (Arrays)
        if (createProblemIfAbsent(
                "Maximum Subarray Sum",
                "Given an integer array nums, find the contiguous subarray (containing at least one number) which has the largest sum and return its sum.\n\nA subarray is a contiguous non-empty sequence of elements within an array.",
                Difficulty.MEDIUM,
                "• 1 <= nums.length <= 10^5\n• -10^4 <= nums[i] <= 10^4",
                "Comma-separated integers representing nums.",
                "A single integer denoting the maximum subarray sum.",
                1,
                256,
                List.of(
                        new String[]{"nums = [-2,1,-3,4,-1,2,1,-5,4]", "6"},
                        new String[]{"nums = [1]", "1"}
                ),
                List.of(
                        new String[]{"nums = [5,4,-1,7,8]", "23"},
                        new String[]{"nums = [-1]", "-1"},
                        new String[]{"nums = [-3,-2,-1]", "-1"}
                )
        )) seededCount++;

        // 4. Longest Substring Without Repeating Characters (Sliding Window & Hash Maps)
        if (createProblemIfAbsent(
                "Longest Substring Without Repeating Characters",
                "Given a string s, find the length of the longest substring without duplicate characters.\n\nA substring is a contiguous sequence of characters within a string.",
                Difficulty.MEDIUM,
                "• 0 <= s.length <= 5 * 10^4\n• s consists of English letters, digits, symbols and spaces.",
                "A single string s.",
                "An integer representing the length of the longest unique substring.",
                1,
                256,
                List.of(
                        new String[]{"s = \"abcabcbb\"", "3"},
                        new String[]{"s = \"bbbbb\"", "1"}
                ),
                List.of(
                        new String[]{"s = \"pwwkew\"", "3"},
                        new String[]{"s = \"\"", "0"},
                        new String[]{"s = \"au\"", "2"},
                        new String[]{"s = \"abba\"", "2"}
                )
        )) seededCount++;

        // 5. Binary Search (Binary Search)
        if (createProblemIfAbsent(
                "Binary Search",
                "Given an array of integers nums which is sorted in ascending order, and an integer target, write a function to search target in nums. If target exists, then return its index. Otherwise, return -1.\n\nYou must write an algorithm with O(log n) runtime complexity.",
                Difficulty.EASY,
                "• 1 <= nums.length <= 10^4\n• -10^4 < nums[i], target < 10^4\n• All integers in nums are unique.\n• nums is sorted in ascending order.",
                "First line: sorted integers separated by commas.\nSecond line: target integer.",
                "Index of target, or -1 if not found.",
                1,
                256,
                List.of(
                        new String[]{"nums = [-1,0,3,5,9,12], target = 9", "4"},
                        new String[]{"nums = [-1,0,3,5,9,12], target = 2", "-1"}
                ),
                List.of(
                        new String[]{"nums = [5], target = 5", "0"},
                        new String[]{"nums = [2,5], target = 0", "-1"},
                        new String[]{"nums = [1,3,5,7,9], target = 7", "3"}
                )
        )) seededCount++;

        // 6. Valid Parentheses (Stack & Strings)
        if (createProblemIfAbsent(
                "Valid Parentheses",
                "Given a string s containing just the characters '(', ')', '{', '}', '[' and ']', determine if the input string is valid.\n\nAn input string is valid if:\n1. Open brackets must be closed by the same type of brackets.\n2. Open brackets must be closed in the correct order.\n3. Every close bracket has a corresponding open bracket of the same type.",
                Difficulty.EASY,
                "• 1 <= s.length <= 10^4\n• s consists of parentheses only '()[]{}'.",
                "A string containing brackets.",
                "true or false.",
                1,
                256,
                List.of(
                        new String[]{"s = \"()[]{}\"", "true"},
                        new String[]{"s = \"(]\"", "false"}
                ),
                List.of(
                        new String[]{"s = \"([])\"", "true"},
                        new String[]{"s = \"[\"", "false"},
                        new String[]{"s = \"]\"", "false"},
                        new String[]{"s = \"(((\"", "false"}
                )
        )) seededCount++;

        // 7. Container With Most Water (Two Pointers & Arrays)
        if (createProblemIfAbsent(
                "Container With Most Water",
                "You are given an integer array height of length n. There are n vertical lines drawn such that the two endpoints of the ith line are (i, 0) and (i, height[i]).\n\nFind two lines that together with the x-axis form a container, such that the container contains the most water.\n\nReturn the maximum amount of water a container can store.",
                Difficulty.MEDIUM,
                "• n == height.length\n• 2 <= n <= 10^5\n• 0 <= height[i] <= 10^4",
                "An array of non-negative integers representing line heights.",
                "An integer representing the maximum area of water.",
                1,
                256,
                List.of(
                        new String[]{"height = [1,8,6,2,5,4,8,3,7]", "49"},
                        new String[]{"height = [1,1]", "1"}
                ),
                List.of(
                        new String[]{"height = [4,3,2,1,4]", "16"},
                        new String[]{"height = [1,2,1]", "2"},
                        new String[]{"height = [2,3,4,5,18,17,6]", "17"}
                )
        )) seededCount++;

        // 8. Group Anagrams (Hash Maps & Strings)
        if (createProblemIfAbsent(
                "Group Anagrams",
                "Given an array of strings strs, group the anagrams together. You can return the answer in any order.\n\nAn Anagram is a word or phrase formed by rearranging the letters of a different word or phrase, typically using all the original letters exactly once.",
                Difficulty.MEDIUM,
                "• 1 <= strs.length <= 10^4\n• 0 <= strs[i].length <= 100\n• strs[i] consists of lowercase English letters.",
                "Array of lowercase strings.",
                "List of groups of anagrams.",
                2,
                512,
                List.of(
                        new String[]{"strs = [\"eat\",\"tea\",\"tan\",\"ate\",\"nat\",\"bat\"]", "[[\"bat\"],[\"nat\",\"tan\"],[\"ate\",\"eat\",\"tea\"]]"},
                        new String[]{"strs = [\"\"]", "[[\"\"]]"}
                ),
                List.of(
                        new String[]{"strs = [\"a\"]", "[[\"a\"]]"},
                        new String[]{"strs = [\"ab\",\"ba\"]", "[[\"ab\",\"ba\"]]"},
                        new String[]{"strs = [\"abc\",\"bca\",\"cba\",\"def\",\"fed\"]", "[[\"abc\",\"bca\",\"cba\"],[\"def\",\"fed\"]]"}
                )
        )) seededCount++;

        // 9. Fibonacci Number (Recursion)
        if (createProblemIfAbsent(
                "Fibonacci Number",
                "The Fibonacci numbers, commonly denoted F(n), form a sequence called the Fibonacci sequence, such that each number is the sum of the two preceding ones, starting from 0 and 1.\n\nThat is:\nF(0) = 0, F(1) = 1\nF(n) = F(n - 1) + F(n - 2), for n > 1.\n\nGiven n, calculate F(n).",
                Difficulty.EASY,
                "• 0 <= n <= 30",
                "A single non-negative integer n.",
                "An integer representing F(n).",
                1,
                256,
                List.of(
                        new String[]{"n = 2", "1"},
                        new String[]{"n = 4", "3"}
                ),
                List.of(
                        new String[]{"n = 0", "0"},
                        new String[]{"n = 1", "1"},
                        new String[]{"n = 10", "55"},
                        new String[]{"n = 30", "832040"}
                )
        )) seededCount++;

        // 10. Generate Parentheses (Recursion & Strings)
        if (createProblemIfAbsent(
                "Generate Parentheses",
                "Given n pairs of parentheses, write a function to generate all combinations of well-formed parentheses.\n\nAll combinations must be syntactically valid matching parentheses.",
                Difficulty.MEDIUM,
                "• 1 <= n <= 8",
                "An integer n representing the number of bracket pairs.",
                "A list of strings containing all valid bracket combinations.",
                1,
                256,
                List.of(
                        new String[]{"n = 3", "[\"((()))\",\"(()())\",\"(())()\",\"()(())\",\"()()()\"]"},
                        new String[]{"n = 1", "[\"()\"]"}
                ),
                List.of(
                        new String[]{"n = 2", "[\"(())\",\"()()\"]"},
                        new String[]{"n = 4", "[\"(((())))\",\"((()()))\",\"((())())\",\"((()))()\",\"(()(()))\",\"(()()())\",\"(()())()\",\"(())(())\",\"(())()()\",\"()((()))\",\"()(()())\",\"()(())()\",\"()()(())\",\"()()()()\"]"}
                )
        )) seededCount++;

        log.info("Problem seeding completed. Seeded {} new problem(s).", seededCount);
    }

    private boolean createProblemIfAbsent(
            String title,
            String description,
            Difficulty difficulty,
            String constraints,
            String inputFormat,
            String outputFormat,
            int timeLimit,
            int memoryLimit,
            List<String[]> examplesData,
            List<String[]> testCasesData
    ) {
        if (problemRepository.existsByTitle(title)) {
            log.debug("Problem '{}' already exists. Skipping.", title);
            return false;
        }

        Problem problem = new Problem();
        problem.setTitle(title);
        problem.setDescription(description);
        problem.setDifficulty(difficulty);
        problem.setConstraints(constraints);
        problem.setInputFormat(inputFormat);
        problem.setOutputFormat(outputFormat);
        problem.setTimeLimit(timeLimit);
        problem.setMemoryLimit(memoryLimit);
        problem.setCreatedAt(Instant.now());

        for (String[] ex : examplesData) {
            Example example = new Example();
            example.setInput(ex[0]);
            example.setOutput(ex[1]);
            problem.addExample(example);
        }

        for (String[] tc : testCasesData) {
            TestCase testCase = new TestCase();
            testCase.setInput(tc[0]);
            testCase.setExpectedOutput(tc[1]);
            problem.addTestCase(testCase);
        }

        problemRepository.save(problem);
        log.info("Seeded problem: '{}' with {} example(s) and {} test case(s).",
                title, examplesData.size(), testCasesData.size());
        return true;
    }

    @Transactional
    public void seedAdmin() {
        if (adminUsername == null || adminUsername.isBlank() ||
            adminPassword == null || adminPassword.isBlank()) {
            log.info("No initial ADMIN credentials configured (app.admin.username/password unset). Skipping admin creation.");
            return;
        }

        String username = adminUsername.trim();
        String email = (adminEmail != null && !adminEmail.isBlank())
                ? adminEmail.trim()
                : (username + "@codearena.local");

        if (userRepository.existsByUsername(username)) {
            log.info("User '{}' already exists. Skipping initial admin creation.", username);
            return;
        }

        if (userRepository.existsByEmail(email)) {
            log.info("User with email '{}' already exists. Skipping initial admin creation.", email);
            return;
        }

        User admin = new User();
        admin.setUsername(username);
        admin.setEmail(email);
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(Role.ADMIN);
        admin.setCreatedAt(Instant.now());

        userRepository.save(admin);
        log.info("Initial ADMIN account created successfully: '{}' ({}) with role ADMIN.", username, email);
    }
}

