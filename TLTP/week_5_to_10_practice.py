print(12/(1+2)+2**2)

# Day 70
# Codewars sum of positive sums
def positive_sum(arr):
    sum = 0
    for a in arr:    
        if a < 0:
            a = 0
        else:
            a = a
        sum += a
    return sum