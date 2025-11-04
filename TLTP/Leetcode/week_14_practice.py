# Day 92, 31 Oct 2025
# LeetCode 104: Maximum Depth of Binary Tree

# Recursive DFS
# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right

class Solution:
    def maxDepth(self, root: Optional[TreeNode]) -> int:
        if not root:
            return 0

        return 1 + max(self.maxDepth(root.left), self.maxDepth(root.right))
    


# Day 93, 1 Nov 2025
# LeetCode 543: Diameter of Binary Tree
# Definition for a binary tree node.
# class TreeNode:
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right

class Solution:
    def diameterOfBinaryTree(self, root: Optional[TreeNode]) -> int:
        res = 0

        def dfs(root):
            nonlocal res

            if not root:
                return 0
            left = dfs(root.left)
            right = dfs(root.right)
            res = max(res, left + right)

            return 1 + max(left, right)

        dfs(root)
        return res
    

### NEETCODE 3 EASY QUESTIONS ON BIT MANIPULATION ###

# Day 94, 2 Nov 2025
# LeetCode 136: Single Number
# Bit Manipulation: Time O(n) Space O(1)

class Solution:
    def singleNumber(self, nums: List[int]) -> int:
        res = 0
        for num in nums:
            res = num ^ res
        return res
    

# Day 96, 2 Nov 2025
# LeetCode 191: Number of 1 Bits
# Bit Mask: Time O(1) Space O(1)

class Solution:
    def hammingWeight(self, n: int) -> int:
        res = 0
        for i in range(32):
            if (1 << i) & n:
                res += 1
        return res