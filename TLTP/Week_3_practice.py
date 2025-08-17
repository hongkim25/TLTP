#Day 15 - test
print("This is my first code commit from the VS code!")


#Day 16 - basics
inf = 1e6 #e5 = 10^6
print(inf)


#Day 17 - basics
a = 0.2 + 0.6
print(a)

if a == 0.8:
    print(True)
else:
    print(False)


a = [i for i in range(10)]
print(a)


# Programmers Level 1: Find the middle character (가운데 글자 가져오기)
# Problem: A function that returns the middle character of a string.
# If the length is even, return the two middle characters.
#
# My Logic:
# 1. Find the length of the string.
# 2. Calculate the middle index.
# 3. Use an if/else statement to check if the length is odd or even.
# 4. Use string slicing to return the correct character(s).

def solution(s):
    length = len(s)
    mid = length // 2
    if length % 2 == 1:
        # Odd length
        return s[mid]
    else:
        # Even length
        return s[mid-1:mid+1]