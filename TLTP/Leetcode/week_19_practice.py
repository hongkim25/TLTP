### NEETCODE 3 MEDIUM QUESTIONS ON GRAPHS ###

# Day 127, 5 DEC 2025
# LeetCode 62: Unique Paths
# Dynamic Programming (2D DP): Time O(m * n) Space O(n)

class Solution:
    def uniquePaths(self, m: int, n: int) -> int:
        row = [1] * n

        for i in range(m - 1):
            newRow = [1] * n
            for j in range(n - 2, -1, -1):
                newRow[j] = newRow[j + 1] + row[j]
            row = newRow
        return row[0]