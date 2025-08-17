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


a = [i for i in range(20) if i % 2 == 1]
print(a)

a = [1, 2, 3, 4, 5, 5, 5]
remove_set = {3, 5}
result = [i for i in a if i not in remove_set]
print(result)   

# Dictionary Key-Value
a = dict()
a['name'] = 'Hong'
a['age'] = 35
a['city'] = 'Daejeon'
key_list = a.keys()
value_list = a.values()
print(key_list)
print(value_list)

# Python은 b > 0, b < 20 쓰지 않고 0 < b < 20로 쓸 수 있다.
b = int(15)
if 0 < b < 20:
    print("b is between 0 and 20")

#1부터 9까지 모든 정수의 합 구하기
i = 1
result = 0

while i <= 10:
    result += i
    i += 1

print(result)

# n = int(input())
# data = list(map(int, input().split()))
# data.sort(reverse=True)
# print(data)

# a, b, c를 공백을 기준으로 구분하여 입력
# a, b, c = map(int, input().split())
# print(a, b, c)


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