def reciprocal_cycles(n):
    max_number = 0
    result = 1
    for number in range(1, n):
        new_list = []
        x = 1
        rem = 0
        rec_num = 0
        while rem not in new_list:
            new_list.append(rem)
            rem = x % number
            x = rem * 10
            rec_num += 1
        if max_number < rec_num:
            max_number = rec_num
            result = number
    return result

print(reciprocal_cycles(1000))