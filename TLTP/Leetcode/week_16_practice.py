### NEETCODE 2 EASY/MEDIUM QUESTIONS ON STACK ###


# Day 106, 14 NOV 2025
# LeetCode 20: Valid Parentheses
# Stack: Time O(n) Space O(n)

class Solution:
    def isValid(self, s: str) -> bool:
        stack = []
        closeToOpen = { ")" : "(", "]" : "[", "}" : "{" }

        for c in s:
            if c in closeToOpen:
                if stack and stack[-1] == closeToOpen[c]:
                    stack.pop()
                else:
                    return False
            else:
                stack.append(c)

        return True if not stack else False