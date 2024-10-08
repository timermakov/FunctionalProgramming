import math

def check_prime(num):
    i = 2
    max = math.sqrt(num)
    while i <= max:
        if num % i == 0:
            return 0
        i += 1
    return 1

def solution(n):
    counter = 0
    primes = 1
    while counter < n:
        primes += 1
        if check_prime(primes):
            counter += 1
    return primes

print(solution(10001))
