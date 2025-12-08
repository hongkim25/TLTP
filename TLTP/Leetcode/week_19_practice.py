### NEETCODE 3 MEDIUM QUESTIONS ON 2-D DYNAMIC PROGRAMMING ###

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
    

# Day 128, 6 DEC 2025
# LeetCode 309: Best Time to Buy and Sell Stock with Cooldown
# Dynamic Programming (Top-down): Time O(n) Space O(n)

class Solution:
    def maxProfit(self, prices: List[int]) -> int:
        dp = {}  # key=(i, buying) val=max_profit

        def dfs(i, buying):
            if i >= len(prices):
                return 0
            if (i, buying) in dp:
                return dp[(i, buying)]

            cooldown = dfs(i + 1, buying)
            if buying:
                buy = dfs(i + 1, not buying) - prices[i]
                dp[(i, buying)] = max(buy, cooldown)
            else:
                sell = dfs(i + 2, not buying) + prices[i]
                dp[(i, buying)] = max(sell, cooldown)
            return dp[(i, buying)]

        return dfs(0, True)
# caching


# Day 129, 7 DEC 2025
# LeetCode 1143: Longest Common Subsequence
# Dynamic Programming (2D DP Bottom Up): Time O(m * n) Space O(m * n)

class Solution:
    def longestCommonSubsequence(self, text1: str, text2: str) -> int:
        dp = [[0 for j in range(len(text2) + 1)]
                 for i in range(len(text1) + 1)]

        for i in range(len(text1) - 1, -1, -1):
            for j in range(len(text2) - 1, -1, -1):
                if text1[i] == text2[j]:
                    dp[i][j] = 1 + dp[i + 1][j + 1]
                else:
                    dp[i][j] = max(dp[i][j + 1], dp[i + 1][j])

        return dp[0][0]
    

# Day 130, 8 DEC 2025
# LeetCode 53: Maximum Subarray
# Dynamic Programming (Kadane's Algorithm): Time O(n) Space O(1)

class Solution:
    def maxSubArray(self, nums: List[int]) -> int:
        maxSub, curSum = nums[0], 0
        for num in nums:
            if curSum < 0:
                curSum = 0
            curSum += num
            maxSub = max(maxSub, curSum)
        return maxSub
