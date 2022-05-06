import atexit
import sqlite3
from DAO import *


class Repository:
    def __init__(self):
        self.conn = sqlite3.connect('database.db')
        self.hats_table = hats_table(self.conn)
        self.suppliers_table = suppliers_table(self.conn)
        self.orders_table = orders_table(self.conn)
        self.create()

    def close(self):
        self.conn.commit()
        self.conn.close()

    def create(self):
        self.conn.executescript("""
                CREATE TABLE "hats" (
                    "id"	INTEGER,
                    "topping"	STRING NOT NULL,
                    "supplier"	INTEGER,
                    "quantity"	INTEGER NOT NULL,
                    PRIMARY KEY("id"),
                    FOREIGN KEY("supplier") REFERENCES "suppliers"("id")
                );

                CREATE TABLE "suppliers"(
                    "id"    INTEGER, 
                    "name"	STRING NOT NULL,
                    PRIMARY KEY("id")
                );

                CREATE TABLE "orders"(
                    "id"    INTEGER, 
                    "location"	STRING NOT NULL,
                    "hat"	INTEGER,
                    PRIMARY KEY("id"),
                    FOREIGN KEY("hat") REFERENCES "hats"("id")
                );
        """)

repo = Repository()
atexit.register(repo.close)