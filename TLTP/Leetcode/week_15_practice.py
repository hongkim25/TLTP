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

 