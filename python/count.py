# simple code to count the number of addresses downloaded.

import os


path = "./json/"
count_lines = 0
addr = "0x3ac6cb00f5a44712022a51fbace4c7497f56ee31"
flag = False
which_file = ""

for file_name in os.listdir(path):
    if os.path.isfile(path + file_name):
        file_path = os.path.join(path, file_name)

        with open(file_path, "r", encoding='UTF-8') as f:
            for i, l in enumerate(f):
                if addr in l:
                    flag = True
                    which_file = which_file.join(path + file_name)
                    # break
        print(i+1)
        count_lines += i+1

print(count_lines)
print(flag)
print(which_file)