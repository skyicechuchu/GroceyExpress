import pymysql
import pandas as pd

# Open database connection
db = pymysql.connect(host="database-1.cngbcqgzo5zf.us-east-2.rds.amazonaws.com", user="admin", port=3306, password="8439523026_Zqc")

# Create a cursor object
cursor = db.cursor()

# Drop the database if it exists
cursor.execute("DROP DATABASE IF EXISTS grocery_db")

# Create the database
cursor.execute("CREATE DATABASE IF NOT EXISTS grocery_db \
   DEFAULT CHARACTER SET utf8mb4 \
   DEFAULT COLLATE utf8mb4_unicode_ci")

# Select the database
cursor.execute("USE grocery_db")


# Creating User table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS User ( \
    account VARCHAR(255) NOT NULL, \
    password VARCHAR(255) NOT NULL, \
    role VARCHAR(255) NOT NULL, \
    firstName VARCHAR(255) NOT NULL, \
    lastName VARCHAR(255) NOT NULL, \
    phone VARCHAR(255) NOT NULL, \
    PRIMARY KEY (account) \
);")


# Creating Customer table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS Customer ( \
    account VARCHAR(255) NOT NULL, \
    rating INT NOT NULL, \
    credits INT NOT NULL, \
    available_credits INT NOT NULL, \
    location_x INT NOT NULL, \
    location_y INT NOT NULL, \
    angryBirdID INT NOT NULL DEFAULT -1, \
    FOREIGN KEY (account) REFERENCES User(account), \
    PRIMARY KEY (account) \
);")

# Creating Pilot table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS Pilot ( \
    account VARCHAR(255) NOT NULL, \
    taxID VARCHAR(255) NOT NULL, \
    licenseID VARCHAR(255) NOT NULL, \
    numberSuccessfulDelivery INT NOT NULL, \
    current_drone_id INT NOT NULL, \
    PRIMARY KEY (account), \
    FOREIGN KEY (account) REFERENCES User(account) \
);")

# storeId of storeManager references the store table
cursor.execute("CREATE TABLE IF NOT EXISTS StoreManager ( \
    account VARCHAR(50), \
    storename VARCHAR(255), \
    PRIMARY KEY (account), \
    FOREIGN KEY (account) REFERENCES User(account) \
);")


cursor.execute("CREATE TABLE IF NOT EXISTS Store ( \
    name VARCHAR(255) NOT NULL, \
    revenue INT NOT NULL, \
    purchase INT NOT NULL DEFAULT 0, \
    overload INT NOT NULL DEFAULT 0, \
    transfers INT NOT NULL DEFAULT 0, \
    repairs INT NOT NULL DEFAULT 0, \
    location_x INT NOT NULL, \
    location_y INT NOT NULL, \
    manager VARCHAR(50), \
    storeCosts INT NOT NULL DEFAULT 0, \
    angryBirdID INT NOT NULL DEFAULT -1, \
    PRIMARY KEY (name) \
);")

cursor.execute("CREATE TABLE IF NOT EXISTS Drone ( \
    id INT NOT NULL, \
    store_name VARCHAR(255) NOT NULL, \
    capacity INT NOT NULL, \
    remaining_cap INT NOT NULL, \
    delivery_before_refuel INT NOT NULL, \
    number_orders INT NOT NULL, \
    current_pilot_id VARCHAR(255), \
    PRIMARY KEY (store_name, id),\
    FOREIGN KEY (store_name) REFERENCES Store(name) \
);")

cursor.execute("CREATE TABLE IF NOT EXISTS StoreAttackByBird ( \
    store_name VARCHAR(255), \
    angryBird_at_store_count INT NOT NULL DEFAULT 0, \
    angryBird_at_customer_count INT NOT NULL DEFAULT 0, \
    distance_store_customer INT NOT NULL DEFAULT 0,\
    PRIMARY KEY (store_name), \
    FOREIGN KEY (store_name) REFERENCES Store(name) \
);")

cursor.execute("CREATE TABLE IF NOT EXISTS CustomerAttackByBird ( \
    account VARCHAR(255), \
    angryBird_count INT NOT NULL DEFAULT 0, \
    PRIMARY KEY (account), \
    FOREIGN KEY (account) REFERENCES Customer(account) \
);")

# Creating Item table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS Item ( \
    name VARCHAR(255) NOT NULL, \
    weight INT NOT NULL, \
    store_name VARCHAR(255) NOT NULL, \
    PRIMARY KEY (name), \
    FOREIGN KEY (store_name) REFERENCES Store(name) \
);")

# Creating Order table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS Orders ( \
    id INT NOT NULL AUTO_INCREMENT, \
    store_name VARCHAR(255) NOT NULL, \
    drone_id INT NOT NULL, \
    customer_id VARCHAR(255) , \
    cost INT NOT NULL DEFAULT 0, \
    PRIMARY KEY (id, store_name), \
    FOREIGN KEY (store_name) REFERENCES Store(name) \
);")


# Creating AngryBird table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS AngryBird ( \
    angryBirdID INT NOT NULL AUTO_INCREMENT, \
    attackRate FLOAT NOT NULL, \
    PRIMARY KEY (angryBirdID) \
);")

# Creating Line table if not exists
cursor.execute("CREATE TABLE IF NOT EXISTS Line ( \
    item_name VARCHAR(255) NOT NULL, \
    quantity INT NOT NULL, \
    weight INT NOT NULL, \
    price INT NOT NULL, \
    order_id INT NOT NULL,\
    store_name VARCHAR(255) NOT NULL,\
    PRIMARY KEY (store_name, order_id, item_name),\
    FOREIGN KEY (store_name, order_id) REFERENCES Orders(store_name, id),\
    FOREIGN KEY (item_name) REFERENCES Item(name)\
)")


# Read data from users.csv
users_df = pd.read_csv("testdata/users.csv")
# Insert data into User table
for index, row in users_df.iterrows():
    account = row['account']
    password = row['password']
    role = row['role']  
    firstname = row['firstName']
    lastname = row['lastName']
    phone = row['phone']
    sql = "INSERT INTO User (account, password, role, firstName, lastName, phone) VALUES (%s, %s, %s, %s, %s, %s)"
    values = (account, password, role, firstname, lastname, phone)
    cursor.execute(sql, values)

# Read data from customer.csv
customer_df = pd.read_csv("testdata/customer.csv")
# Insert data into User table
for index, row in customer_df.iterrows():
    r1 = row['account']
    r2 = row["rating"]
    r3 = row['credits']  
    r4 = row['available_credits']
    r5 = row['location_x']
    r6 = row['location_y']
    sql = "INSERT INTO Customer (account, rating, credits, available_credits, location_x, location_y) VALUES (%s, %s, %s, %s, %s, %s)"
    values = (r1, r2, r3, r4, r5, r6)
    cursor.execute(sql, values)
    print("Inserted row", index+1)
cursor.close()
# Commit changes and close the database connection
db.commit()
db.close()

print("Grocery Service Database is ready!")
