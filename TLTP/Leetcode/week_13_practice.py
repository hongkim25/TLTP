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