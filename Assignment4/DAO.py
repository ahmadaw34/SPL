#Data Access Objects:

class hats_table:
    def __init__(self,conn):
        self.conn=conn

    def insert(self,hats):
        self.conn.execute("INSERT INTO hats (id,topping,supplier,quantity) VALUES (?,?,?,?)",
                          [hats.getID(),hats.getTopping(),hats.getSupplier(),hats.getQuantity()])
        self.conn.commit()

    def getconn(self):
        return self.conn

    def decreaseQuantity(self,id):
        cursor=self.conn.cursor()
        cursor.execute("""UPDATE hats SET quantity=quantity-1 WHERE id=(?)""",[id])
        self.conn.commit()
        cursor2=self.conn.cursor()
        cursor2.execute("""SELECT quantity FROM hats WHERE id=(?)""",[id])
        checkQuantity=cursor2.fetchall()
        if checkQuantity[0][0]==0:
            cursor3=self.conn.cursor()
            cursor3.execute("""DELETE FROM hats WHERE id=(?)""",[id])
            self.conn.commit()

    def findSupplierHelper(self,topping):
        newtopping=''
        for str in topping:
            if str!='\n':
                newtopping+=str
        cursor=self.conn.cursor()
        cursor.execute("""SELECT hats.id,hats.supplier
        FROM hats 
        WHERE hats.topping=(?)
        ORDER BY hats.supplier""",[newtopping])
        out=cursor.fetchall()
        return out

#------------------------------------------------------------------------------------------------------

class suppliers_table:
    def __init__(self,conn):
        self.conn=conn

    def insert(self,suppliers):
        self.conn.execute("INSERT INTO suppliers (id,name) VALUES (?,?)",
                          [suppliers.getID(),suppliers.getName()])
        self.conn.commit()

    def findSupplier(self,topping,hatsClass):
        hats=hatsClass
        firstFilter=hats.findSupplierHelper(topping)
        cursor=self.conn.cursor()
        cursor.execute("""SELECT name
         FROM suppliers 
         WHERE id=(?)""",[firstFilter[0][1]])
        result=cursor.fetchall()
        output=[firstFilter[0][0],result[0][0]]
        return output

#------------------------------------------------------------------------------------------------------------

class orders_table:
    def __init__(self,conn):
        self.conn=conn

    def insert(self,orders):
        self.conn.execute("INSERT INTO orders (id,location,hat) VALUES(?,?,?)",
                          [orders.getID(),orders.getLocation(),orders.getHat()])
        self.conn.commit()