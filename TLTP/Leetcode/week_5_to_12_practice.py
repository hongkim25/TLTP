print(12/(1+2)+2**2)

# Day 70: Codewars
# Sum of positive sums: You get an array of numbers, return the sum of all of the positives ones.
# The first is mine, the second is the best practice.
def positive_sum(arr):
    sum = 0
    for a in arr:    
        if a < 0:
            a = 0
        else:
            a = a
        sum += a
    return sum

def positive_sum(arr):
    return sum(x for x in arr if x > 0)


### NEETCODE 3 EASY QUESTIONS ON ARRAYS AND HASHING ###

# Day 82, 21 Oct 2025
# LeetCode 217: Contains Duplicate
# HashSet solution
class Solution:
    def hasDuplicate(self, nums: List[int]) -> bool:
        hashset = set()
        for num in nums:
            if num in hashset:
                return True
            hashset.add(num)
        return False
    
# Line-by-line explanation with ChatGPT:
# a class is just a way to group functions (called methods) together
# self — This is a special parameter automatically passed when you call the method on an instance of the class.
#   Think of self as “this object” — it lets the method access variables or other methods of the same class.
#   You always include it as the first parameter in class methods, even if you don’t use it.
# nums: List[int] — This means that the parameter nums is expected to be a list of integers.
# The part after the colon (:) is a type hint, not strict enforcement.
# You need to from typing import List for List to work, though in LeetCode it’s usually already imported.
# -> bool — Another type hint meaning “this function will return a bool (boolean) value,” i.e., either True or False.
# In plain English: 
#  “Define a function named hasDuplicate inside this class, which takes a list of integers called nums and returns a boolean.”

# Why is there no else?
# Not needed because return True stops the function. If that line doesn’t run, the code automatically continues to hashset.add(num). 
# It’s a style choice for simplicity (Pythonic).
# Because when the if condition is True, the function immediately returns.
# That means the rest of the code in the loop (including hashset.add(num)) will never execute in that case.
# So there’s no need to wrap it inside an else.

# Why is return False hanging there?
#   If the loop finishes without returning True, that means no duplicates were found. So we return False.
#   The last return False only runs after the for loop finishes. It means: “We checked every number, and never found a duplicate.”
#   It’s not inside the loop — it’s the final fallback result.

# Other solutions
# Brute force
class Solution:
    def hasDuplicate(self, nums: List[int]) -> bool:
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                if nums[i] == nums[j]:
                    return True
        return False
    
# Sorting
class Solution:
    def hasDuplicate(self, nums: List[int]) -> bool:
        nums.sort()
        for i in range(1, len(nums)):
            if nums[i] == nums[i - 1]:
                return True
        return False
    
# HashSet length:
class Solution:
    def hasDuplicate(self, nums: List[int]) -> bool:
        return len(set(nums)) < len(nums)




# Day 83, 22 Oct 2025
# LeetCode 242: Valid Anagram
# Sorting solution
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False
        return sorted(s) == sorted(t)

# HashMap (dictionary in Python) solution
class Solution:
    def isAnagram(self, s: str, t: str) -> bool:
        if len(s) != len(t):
            return False

        countS, countT = {}, {}

        for i in range(len(s)):
            countS[s[i]] = 1 + countS.get(s[i], 0)
            countT[t[i]] = 1 + countT.get(t[i], 0)
        return countS == countT

# Line-by-line explanation with ChatGPT:
# countS and countT: Create two empty dictionaries (hashmaps). We'll use them to count how many times each character appears:
#   countS will map characters from s to their counts, countT will map characters from t to their counts.
# Loop index i from 0 to len(s)-1. 
#   Because lengths are equal, using one index is enough to read characters from both s and t at the same positions.

# countS[s[i]] = 1 + countS.get(s[i], 0) = “Increase the count for this letter by 1. If it’s not in the dictionary yet, start from 0.”
# same for countT and t[i], but more verbose way is:
# ch = s[i]
# if ch in countS:
#     countS[ch] += 1
# else:
#     countS[ch] = 1
# return countS == countT  //what countS and countT are doing is to count the alphabet and how many times that alphabet appears.
#   Compare the two dictionaries for equality. 
#   In Python, two dicts are equal if they have the same keys and each corresponding key has the same value.
#   If the character counts match exactly, s is an anagram of t → return True. Otherwise False.
# In the hashmap solution, sorting is not needed because you don't care about ordr, you only care about counts of each character.
#   When comparing dictionaries in Python, the order doesn't matter, only the key-value pairs matter.


# Day 84, 23 Oct 2025
# LeetCode 1: Two Sum
# HashMap (dictionary in Python) solution
class Solution:
    def twoSum(self, nums: List[int], target: int) -> List[int]:
        prevMap = {}  # val -> index

        for i, n in enumerate(nums):
            diff = target - n
            if diff in prevMap:
                return [prevMap[diff], i]
            prevMap[n] = i


# Brute force solution
class Solution:
    def twoSum(self, nums: List[int], target: int) -> List[int]:
        for i in range(len(nums)):
            for j in range(i + 1, len(nums)):
                if nums[i] + nums[j] == target:
                    return [i, j]
        return []