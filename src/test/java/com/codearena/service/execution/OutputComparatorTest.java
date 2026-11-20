package com.codearena.service.execution;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OutputComparatorTest {

    @Test
    void exactStringAndWhitespaceNormalization() {
        assertThat(OutputComparator.isMatch("hello", "hello")).isTrue();
        assertThat(OutputComparator.isMatch(" hello \n", "hello")).isTrue();
        assertThat(OutputComparator.isMatch("hello\r\nworld", "hello\nworld")).isTrue();
        assertThat(OutputComparator.isMatch("hello", "world")).isFalse();
    }

    @Test
    void booleanComparison() {
        assertThat(OutputComparator.isMatch("true", "true")).isTrue();
        assertThat(OutputComparator.isMatch("True", "true")).isTrue();
        assertThat(OutputComparator.isMatch("FALSE", "false")).isTrue();
        assertThat(OutputComparator.isMatch("false", "true")).isFalse();
    }

    @Test
    void numericComparison() {
        assertThat(OutputComparator.isMatch("23", "23")).isTrue();
        assertThat(OutputComparator.isMatch("23.0", "23")).isTrue();
        assertThat(OutputComparator.isMatch("-1", "-1")).isTrue();
        assertThat(OutputComparator.isMatch("24", "23")).isFalse();
    }

    @Test
    void jsonArrayAndListComparison() {
        // Generate Parentheses expected format
        String expected = "[\"((()))\",\"(()())\",\"(())()\",\"()(())\",\"()()()\"]";

        // Actual formatted with quotes
        String actualWithQuotes = "[\"((()))\",\"(()())\",\"(())()\",\"()(())\",\"()()()\"]";
        assertThat(OutputComparator.isMatch(actualWithQuotes, expected)).isTrue();

        // Actual formatted like standard Java List.toString(): [((())), (()()), (())()]
        String actualJavaList = "[((())), (()()), (())()]";
        assertThat(OutputComparator.isMatch(actualJavaList, "[\"((()))\",\"(()())\",\"(())()\"]")).isTrue();

        // Permuted order (valid combinations can be returned in any order)
        String permuted = "[\"()()()\",\"((()))\",\"(()())\",\"(())()\",\"()(())\"]";
        assertThat(OutputComparator.isMatch(permuted, expected)).isTrue();
    }

    @Test
    void twoSumArrayComparison() {
        assertThat(OutputComparator.isMatch("[0, 1]", "[0,1]")).isTrue();
        assertThat(OutputComparator.isMatch("[0,1]", "[0,1]")).isTrue();
        assertThat(OutputComparator.isMatch("[1, 0]", "[0,1]")).isTrue();
        assertThat(OutputComparator.isMatch("[0, 2]", "[0,1]")).isFalse();
    }
}
