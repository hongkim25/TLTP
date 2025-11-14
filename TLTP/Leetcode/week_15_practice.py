### NEETCODE 3 EASY/MEDIUM QUESTIONS ON TWO POINTERS ###


# Day 100, 8 NOV 2025
# LeetCode 125: Valid Palindrome

# Two Pointers: Time O(n) Space O(1)

class Solution:
    def isPalindrome(self, s: str) -> bool:
        l, r = 0, len(s) - 1

        while l < r:
            while l < r and not self.alphaNum(s[l]):
                l += 1
            while r > l and not self.alphaNum(s[r]):
                r -= 1
            if s[l].lower() != s[r].lower():
                return False
            l, r = l + 1, r - 1
        return True

    def alphaNum(self, c):
        return (ord('A') <= ord(c) <= ord('Z') or
                ord('a') <= ord(c) <= ord('z') or
                ord('0') <= ord(c) <= ord('9'))

# ord() means “ordinal value,” and it converts a character into its Unicode code number — 
# basically, the number that represents that character inside the computer.


# Day 101, 9 NOV 2025
# LeetCode 167: Two Sum II Input Array Is Sorted
# Two Pointers: Time O(n) Space O(1)

class Solution:
    def twoSum(self, numbers: List[int], target: int) -> List[int]:
        l, r = 0, len(numbers) - 1

        while l < r:
            curSum = numbers[l] + numbers[r]

            if curSum > target:
                r -= 1
            elif curSum < target:
                l += 1
            else:
                return [l + 1, r + 1]
        return []


# Brute Force: Time O(n^2) Space O(1)
class Solution:
    def twoSum(self, numbers: List[int], target: int) -> List[int]:
        for i in range(len(numbers)):
            for j in range(i + 1, len(numbers)):
                if numbers[i] + numbers[j] == target:
                    return [i + 1, j + 1]
        return []
    

# Day 102, 10 NOV 2025
# LeetCode 15: 3Sum
# Two Pointers: Time o(n^2) Space O(1)

class Solution:
    def threeSum(self, nums: List[int]) -> List[List[int]]:
        res = []
        nums.sort()

        for i, a in enumerate(nums):
            if a > 0:
                break

            if i > 0 and a == nums[i - 1]:
                continue

            l, r = i + 1, len(nums) - 1
            while l < r:
                threeSum = a + nums[l] + nums[r]
                if threeSum > 0:
                    r -= 1
                elif threeSum < 0:
                    l += 1
                else:
                    res.append([a, nums[l], nums[r]])
                    l += 1
                    r -= 1
                    while l < r and nums[l] == nums[l - 1]:
                        l += 1
                    while l < r and nums[r] == nums[r + 1]:
                        r -= 1

        return res
    

# if a > 0:
#    break
# Why break here? Since the array is sorted, if a > 0, 
# all remaining numbers are also positive (they come after a).
# It's impossible to get three positive numbers to sum to zero!

# if i > 0 and a == nums[i - 1]:
#     continue
# Why continue here?
# This skips duplicate values for the first number to avoid duplicate triplets.



### NEETCODE 2 EASY/MEDIUM QUESTIONS ON SLIDING WINDOW ###


# Day 104, 12 NOV 2025
# LeetCode 121: Best Time to Buy And Sell Stock

class Solution:
    def maxProfit(self, prices: List[int]) -> int:
        l, r = 0, 1  # Left pointer (l) starts at index 0, Right pointer (r) starts at index 1.
        maxP = 0      # Start with a profit of 0.

        while r < len(prices):  # Loop until right pointer goes beyond the last index.
            if prices[l] < prices[r]:  # Check if buying at 'l' and selling at 'r' is profitable.
                profit = prices[r] - prices[l]  # Calculate profit.
                maxP = max(maxP, profit)  # Update maxP if we found a higher profit.
            else:
                l = r  # If prices[r] < prices[l], we update 'l' to 'r', because we need to buy at a lower price.
            r += 1  # Move the right pointer to the next day.
        
        return maxP  # Return the maximum profit found.
    

# Day 105, 13 NOV 2025
# LeetCode 3: Longest Substring Without Repeating Characters
# Sliding Window: Time O(n) Space O(m)

class Solution:
    def lengthOfLongestSubstring(self, s: str) -> int:
        charSet = set()
        l = 0
        res = 0

        for r in range(len(s)):
            while s[r] in charSet:
                charSet.remove(s[l])
                l += 1
            charSet.add(s[r])
            res = max(res, r - l + 1)
        return res
