print(12/(1+2)+2**2)

# Day 70: Codewars
# Sum of positive sums: You get an array of numbers, return the sum of all of the positives ones.
# The first is mine, the second is the best practice.
def positive_sum(arr):
    sum = 0
    for a in arr:    
        if a < 0:
            a = 0
        else:
            a = a
        sum += a
    return sum

def positive_sum(arr):
    return sum(x for x in arr if x > 0)