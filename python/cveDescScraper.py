import mysql.connector as sql
from mysql.connector import Error

def create_connection(host_name, user_name, user_password, db_name):
    connection = None
    try:
        connection = sql.connect(
            host=host_name,
            user=user_name,
            passwd=user_password,
            port="33033",
            database=db_name
        )
        print("Connection to MySQL DB successful")
    except Error as e:
        print(f"The error '{e}' occurred")

    return connection

def execute_read_query(connection, query):
    cursor = connection.cursor()
    result = None
    try:
        cursor.execute(query)
        result = cursor.fetchall()
        return result
    except Error as e:
        print(f"The error '{e}' occurred")


if __name__ == '__main__':
    # connection
    connection = create_connection("121.128.246.13", "nvd", "nvdData1!@#", "nvd")

    # query statement
    cve_numbers = open("./name.txt", "r")
    cve_list = []
    for line in cve_numbers:
        cve_list.append(line.rstrip("\n"))

    select_cve = "SELECT DESCRIPTION FROM TB_CVE_DATA \n\t"
    where = "WHERE "
    for cve in cve_list:
        where = where + "TB_CVE_DATA.ID like \"%" + cve + "\" or \n\t\t"
    where = where.rstrip(" or \n\t\t")

    select_cve = select_cve + where
    print(select_cve)

    # # request query
    # cves = execute_read_query(connection, select_cve)
    #
    # # process received data
    # for cve in cves:
    #     print(cve)