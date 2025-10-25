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