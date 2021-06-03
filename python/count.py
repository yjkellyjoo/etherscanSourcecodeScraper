import json
import os
import csv


'''
    simple code to count the number of addresses downloaded.
'''
def count_addresses():
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


def collect_cves_without_sc():
    cves_f = open("./cves/cves.csv", "r")
    cves_csv = csv.reader(cves_f, delimiter=',')
    cves_list = []
    for row in cves_csv:
        cves_list.append(row[0].lower())

    sc_cves_f = open("./cves/sc_existing_cves.csv", "r")
    sc_cves_csv = csv.reader(sc_cves_f, delimiter=',')
    sc_cves_list = []
    for row in sc_cves_csv:
        sc_cves_list.append(row[0].lower())

    new_cves_f = open("./cves/1.json", "w+")
    for one in cves_list:
        if one not in sc_cves_list:
            data ="{\"address\":\"" + one +"\",\"eth_balance\":0}\n"
            new_cves_f.write(data)

if __name__ == '__main__':
    # count_addresses()
    collect_cves_without_sc()