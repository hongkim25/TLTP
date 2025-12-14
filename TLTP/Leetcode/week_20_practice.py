# Day 134, 12 DEC 2025
# LeetCode 57: Insert Interval
# Greedy: Time O(n) Space O(n)

class Solution:
    def insert(self, intervals: List[List[int]], newInterval: List[int]) -> List[List[int]]:
        res = []

        for i in range(len(intervals)):
            if newInterval[1] < intervals[i][0]:
                res.append(newInterval)
                return res + intervals[i:]
            elif newInterval[0] > intervals[i][1]:
                res.append(intervals[i])
            else:
                newInterval = [
                    min(newInterval[0], intervals[i][0]),
                    max(newInterval[1], intervals[i][1]),
                ]
        res.append(newInterval)
        return res

# Day 135, 13 DEC 2025
# LeetCode 202: Happy Number
# HashSet: Time O(log n) Space O(log n)    

class Solution:
    def isHappy(self, n: int) -> bool:
        visit = set()

        while n not in visit:
            visit.add(n)
            n = self.sumOfSquares(n)
            if n == 1:
                return True
        return False

    def sumOfSquares(self, n: int) -> int:
        output = 0

        while n:
            digit = n % 10
            digit = digit ** 2
            output += digit
            n = n // 10
        return output

# If the number is 23, digit = 23 % 10 = 3, digit ** 2 = 9, output = 0 + 9 = 9, n = 23 // 10 = 2
# Next iteration: digit = 2 % 10 = 2, digit ** 2 = 4, output = 9 + 4 = 13, n = 2 // 10 = 0
# Return 13

# while n means while n != 0

# Day 135, 13 DEC 2025
# LeetCode 66: Plus One
# Time O(n) Space O(1)

class Solution:
    def plusOne(self, digits: List[int]) -> List[int]:
        one = 1
        i = 0
        digits.reverse()

        while one:
            if i < len(digits):
                if digits[i] == 9:
                    digits[i] = 0
                else:
                    digits[i] += 1
                    one = 0
            else:
                digits.append(one)
                one = 0
            i += 1

        digits.reverse()
        return digits
