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
    

# Day 108, 16 NOV 2025
# LeetCode 155: Design Min Stack
# Stack: Time O(1) Space O(n)

class MinStack:
    def __init__(self):
        self.stack = []
        self.minStack = []

    def push(self, val: int) -> None:
        self.stack.append(val)
        val = min(val, self.minStack[-1] if self.minStack else val)
        self.minStack.append(val)

    def pop(self) -> None:
        self.stack.pop()
        self.minStack.pop()

    def top(self) -> int:
        return self.stack[-1]

    def getMin(self) -> int:
        return self.minStack[-1]



### NEETCODE 3 EASY/MEDIUM QUESTIONS ON BINARY SEARCH ###

# Day 109, 17 NOV 2025
# LeetCode 704: Binary Search
# Binary Search: Time O(log n) Space O(1)

class Solution:
    def search(self, nums: List[int], target: int) -> int:
        l, r = 0, len(nums) - 1

        while l <= r:
            m = l + ((r - l) // 2)

            if nums[m] > target:
                r = m - 1
            elif nums[m] < target:
                l = m + 1
            else:
                return m
        return -1
    

# (l + r) // 2 can lead to overflow
