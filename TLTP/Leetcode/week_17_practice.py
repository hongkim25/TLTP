### NEETCODE 2 EASY QUESTIONS ON HEAP / PRIORITY QUEUE ###

# Day 113, 21 NOV 2025
# LeetCode 703: Kth Largest Element in a Stream
# Min-Heap: Time O(m * logk) Space O(k)

class KthLargest:

    def __init__(self, k: int, nums: List[int]):
        self.minHeap, self.k = nums, k
        heapq.heapify(self.minHeap)
        while len(self.minHeap) > k:
            heapq.heappop(self.minHeap)

    def add(self, val: int) -> int:
        heapq.heappush(self.minHeap, val)
        if len(self.minHeap) > self.k:
            heapq.heappop(self.minHeap)
        return self.minHeap[0]


# Day 115, 23 NOV 2025
# LeetCode 1046: Last Stone Weight
# Max-Heap: Time O(n log n) Space O(n)

class Solution:
    def lastStoneWeight(self, stones: List[int]) -> int:
        stones = [-s for s in stones]
        heapq.heapify(stones)

        while len(stones) > 1:
            first = heapq.heappop(stones)
            second = heapq.heappop(stones)
            if second > first:
                heapq.heappush(stones, first - second)

        stones.append(0)
        return abs(stones[0])
    

### NEETCODE 23 MEDIUM BACKTRACKING ###

# Day 116, 24 NOV 2025
# LeetCode 78: Subsets
# Backtracking: Time O(n * 2^n) Space O(n)

class Solution:
    def subsets(self, nums: List[int]) -> List[List[int]]:
        res = []
        subset = []

        def dfs(i):
            if i >= len(nums):
                res.append(subset.copy())
                return
            subset.append(nums[i])
            dfs(i + 1)
            subset.pop()
            dfs(i + 1)

        dfs(0)
        return res