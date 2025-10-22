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

##### Lambda Functions

print((lambda a,b: a + b)(34, 42))  # Lambda function to add two numbers

array = [('hong', 35), ('kim', 20), ('lee', 30)]
print(sorted(array, key=lambda x: x[1]))  # Sort by second element (age)

list1 = [1, 2, 3, 4, 5]
list2 = [6, 7, 8, 9, 10]
result = map(lambda x, y: x + y, list1, list2)  # Element-wise addition
print(list(result))  # Convert map object to list


# n = int(input())
# data = list(map(int, input().split()))
# data.sort(reverse=True)
# print(data)

# a, b, c를 공백을 기준으로 구분하여 입력
# a, b, c = map(int, input().split())
# print(a, b, c)


#Day 18 - basics

    # 리스트                a = []          
    # 딕셔너리 (빈 집합 X)    d = {}          
    # 빈 집합                s = set()       
    # 한 개짜리 튜플          t = (1,)        

#Day 19 - basics
    # 1부터 20까지의 짝수 출력    
for i in range (2, 21, 2):
    print(i, end=' ')

    # 1부터 20까지의 짝수 출력 (alternative method)
for i in range (1, 21):
    if i % 2 == 0:
        print(i, end=' ')

    # 구구단
for i in range(2, 10):
    for j in range(1, 10):
        print(f"{i} * {j} = {i * j}")

    # 역순으로 프린트
s = "Hello, World!"
print(s[::-1])  # 슬라이싱 사용 string[start:stop:step]. Output: !dlroW ,olleH

# def reverse_string(string):
#     result = ''
#     for i in string[::-1]:
#         result += i
#     print(result)


# Day 20 - basics
# 정수 n이 주어졌을 때, 0부터 n까지의 짝수의 합을 구하는 함수
def solution(n):
    return sum(range(0, n+1, 2))

# Day 21 - basics
# 배열의 평균값
def solution(numbers):
    return sum(numbers) / len(numbers)

# Programmers Level 1: Find the middle character (가운데 글자 가져오기)
# Problem: A function that returns the middle character of a string.
# If the length is even, return the two middle characters.
# Logic:
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