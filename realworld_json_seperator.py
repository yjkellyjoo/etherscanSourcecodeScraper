import json
import csv
import ijson


def convert_json(file_name):
    with open(file_name, 'r', encoding='UTF-8') as f:
        data = json.load(f)

        for code in data['rows']:
            contract_name = code["ANY_VALUE(ContractName)"]
            codefile = open('./realworld_code/' + contract_name + '.sol', 'w', encoding='UTF-8')
            codefile.writelines(code["SourceCode"])
            print(contract_name + ' written')
            codefile.close()


def convert_ijson(file_name):
    with open(file_name, 'r', encoding='UTF-8') as f:
        parser = ijson.parse(f)

        for prefix, event, value in parser:
            if (prefix == "rows.item.ANY_VALUE(ContractName)"):
                contract_name = value
                codefile = open('./realworld_code/' + value + '.sol', 'w', encoding='UTF-8')
                for prefix, event, value in parser:
                    if (prefix == "rows.item.SourceCode"):
                        codefile.writelines(value)
                        print(contract_name + ' written')
                        codefile.close()
                        break


def convert_ijson_count(file_name):
    with open(file_name, 'r', encoding='UTF-8') as f:
        parser = ijson.parse(f)

        i = 1
        for prefix, event, value in parser:
            sol_name = str(i) + '.sol'

            if (prefix == "rows.item.ANY_VALUE(ContractName)"):
                contract_name = value
                codefile = open('./realworld_code/' + sol_name, 'w', encoding='UTF-8')
                i += 1
                for prefix, event, value in parser:
                    if (prefix == "rows.item.SourceCode"):
                        codefile.writelines(value)

                        # metadata
                        with open('metadata.csv', 'a', newline='') as csvf:
                            metawriter = csv.writer(csvf)
                            metawriter.writerow([sol_name, contract_name])

                        print(contract_name + ' written')
                        break
                codefile.close()


def format():
    with open('./realworld_code/4449.sol', 'r', encoding='UTF-8') as f:
        file = open('./realworld_code/4449_format.sol', 'w', encoding='UTF-8')
        file.writelines(f)
        file.close()


if __name__ == '__main__':
    convert_ijson_count('./realworld_sourcecodes_distinct_0601.json')
    # format()
