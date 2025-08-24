#Day 24 - 정수가 담긴 리스트 num_list가 주어질 때, num_list의 원소 중 짝수와 홀수의 개수를 담은 배열을 return 하도록 solution 함수를 완성해보세요.

def solution(num_list):
    even_count = 0    
    odd_count = 0
    for num in num_list:
        if num % 2 == 0:
            even_count += 1
        else:
            odd_count += 1
    return [even_count, odd_count]

print(solution([1,2,3,4,5,6,7,8,9]))