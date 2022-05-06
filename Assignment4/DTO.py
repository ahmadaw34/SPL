# Data Transfer Objects:
class hats:
    def __init__(self,id,topping,supplier,quantity):
        self.id=id
        self.topping=topping
        self.supplier=supplier
        self.quantity=quantity

    def getID(self):
        return self.id

    def getTopping(self):
        return self.topping

    def getSupplier(self):
        return self.supplier

    def getQuantity(self):
        return self.quantity

class suppliers:
    def __init__(self,id,name):
        self.id=id
        self.name=name

    def getID(self):
        return self.id

    def getName(self):
        return self.name

class orders:
    def __init__(self,id,location,hat):
        self.id=id
        self.location=location
        self.hat=hat

    def getID(self):
        return self.id

    def getLocation(self):
        return self.location

    def getHat(self):
        return self.hat