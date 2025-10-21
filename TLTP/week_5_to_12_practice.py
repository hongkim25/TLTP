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