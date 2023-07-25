import pymysql
import matplotlib.pyplot as plt
import numpy as np

db = pymysql.connect(
    host="database-1.cngbcqgzo5zf.us-east-2.rds.amazonaws.com",
    user="admin",
    port=3306,
    password="8439523026_Zqc",
    database="grocery_db"
)
cursor = db.cursor()

hash_dict = {}
x = []
y = []
z = []
cursor.execute("SELECT Customer.location_x, Customer.location_y, CustomerAttackByBird.angryBird_count FROM CustomerAttackByBird JOIN Customer ON CustomerAttackByBird.account = Customer.account")
customer_attacks = cursor.fetchall()
for each in customer_attacks:
    if each == None:
        continue
    if (each[0],each[1]) in hash_dict:
        hash_dict[(each[0], each[1])] += each[2]
    else:
        hash_dict[(each[0],each[1])] = each[2]



cursor.execute("SELECT Store.location_x, Store.location_y, StoreAttackByBird.angryBird_at_store_count FROM StoreAttackByBird JOIN Store ON StoreAttackByBird.store_name = Store.name")
store_attacks = cursor.fetchall()
for each in store_attacks:
    if each == None:
        continue
    if (each[0],each[1]) in hash_dict:
        hash_dict[(each[0], each[1])] += each[2]
    else:
        hash_dict[(each[0],each[1])] = each[2]

for key, value in hash_dict.items():
    loc1, loc2 = key
    x.append(loc1)
    y.append(loc2)
    z.append(value)

# Create a scatter plot
plt.scatter(x, y, c=z, cmap='viridis')
plt.colorbar()

# Add labels and title
plt.xlabel('X')
plt.ylabel('Y')
plt.title('Scatter Plot For Attack Count in X-Y Location')

# Show the plot
plt.show()