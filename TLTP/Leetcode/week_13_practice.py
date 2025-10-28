# Day 85, 24 Oct 2025
# LeetCode 206: Reverse Linked List
# Iteration: Time O(n) Space O(1)
class Solution:
    def reverseList(self, head: ListNode) -> ListNode:
        prev, curr = None, head

        while curr:
            temp = curr.next
            curr.next = prev
            prev = curr
            curr = temp
        return prev
    
# Rercursive Time O(n) Space O(n)
class Solution:
    def reverseList(self, head: ListNode) -> ListNode:

        if not head:
            return None
        
        newHead = head
        if head.next:
            newHead = self.reverseList(head.next)
            head.next.next = head
        head.next = None

        return newHead
    

# Day 86, 25 Oct 2025
# LeetCode 21: Merged Two Sorted Lists
# Iteration: Time O(n+m) Space O(1)
class Solution:
    def mergeTwoLists(self, list1: ListNode, list2: ListNode) -> ListNode:
        dummy = node = ListNode()

        while list1 and list2:
            if list1.val < list2.val:
                node.next = list1
                list1 = list1.next
            else:
                node.next = list2
                list2 = list2.next
            node = node.next

        node.next = list1 or list2

        return dummy.next
    


# Day 87, 26 Oct 2025
# LeetCode 141: Linked List Cycle
# Hash set: Time O(n) Space O(n)
class Solution:
    def hasCycle(self, head: Optional[ListNode]) -> bool:
        seen = set()
        cur = head
        while cur:
            if cur in seen:
                return True
            seen.add(cur)
            cur = cur.next
        return False

# Explanation from the ChatGPT:
# seen.add(cur) — the key line
# seen: This is your set(). It’s just a container that remembers objects you’ve already visited.
# .add: The dot (.) means “access a property or a method of an object.”
# So seen.add means: “Use the add method that belongs to the set named seen.”
# (cur): Parentheses () mean “call this method or function, and give it an argument.”
# So seen.add(cur) means: “Call the add method of the set seen, and pass cur as the argument.”


# Fast and slow pointers: Time O(n) Space O(1)
class Solution:
    def hasCycle(self, head: Optional[ListNode]) -> bool:
        slow, fast = head, head

        while fast and fast.next:
            slow = slow.next
            fast = fast.next.next
            if slow == fast:
                return True
        return False
    

# Day 88, 27 Oct 2025
# LeetCode 226: Invert Binary Tree
# Depth-First Search (DFS):
class Solution:
    def invertTree(self, root: Optional[TreeNode]) -> Optional[TreeNode]:
        if not root: 
            return None

        root.left, root.right = root.right, root.left

        self.invertTree(root.left)
        self.invertTree(root.right)

        return rootc
    


# Day 89, 28 Oct 2025
# LeetCode 100: Same Tree
# Depth-First Search (DFS):
class Solution:
    def isSameTree(self, p: Optional[TreeNode], q: Optional[TreeNode]) -> bool:
        if not p and not q:
            return True
        if p and q and p.val == q.val:
            return self.isSameTree(p.left, q.left) and self.isSameTree(p.right, q.right)
        else:
            return False