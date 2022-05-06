import os
import sys

if os.path.exists('database.db'):
    os.remove('database.db')

from repository import repo

import DTO

def analyseconfig(configlines):
    firstLine=configlines[0].split(',')
    numOfHats=int(firstLine[0])
    numOfSuppliers=int(firstLine[1])

    for i in range(1,numOfHats+1):
        currentLine=configlines[i].split(',')
        newHat=DTO.hats(currentLine[0],currentLine[1],currentLine[2],currentLine[3])
        repo.hats_table.insert(newHat)

    counter=numOfHats+1
    for i in range(numOfSuppliers):
        currentLine = configlines[counter].split(',')
        newSupplier=DTO.suppliers(currentLine[0],currentLine[1])
        repo.suppliers_table.insert(newSupplier)
        counter+=1

def analyseorders(orderlines):
    orderID=1
    output=""
    for line in orderlines:
        currentOrder=line.split(',')
        result=repo.suppliers_table.findSupplier(currentOrder[1],repo.hats_table)
        repo.hats_table.decreaseQuantity(result[0])
        if result!=None:
            newOrder=DTO.orders(orderID,currentOrder[0],result[0])
            repo.orders_table.insert(newOrder)
            #some strings have been tooked with \n so I deleted all these \n
            finalOrder=''
            for o in currentOrder[1]:
                if o!='\n':
                    finalOrder+=o
            finalresult=''
            for r in result[1]:
                if r!='\n':
                    finalresult+=r
            finalOrder2=''
            for o in currentOrder[0]:
                if o!='\n':
                    finalOrder2+=o
            output+=finalOrder+","+finalresult+","+finalOrder2+"\n"
            orderID+=1
    return output

def main(configfile,orderfile):
    with open(configfile,'r') as config:
        lines=config.readlines()
        analyseconfig(lines)
    with open(orderfile,'r') as orders:
        lines=orders.readlines()
        output=analyseorders(lines)
        txtboxOutput=open('output.txt','w')
        txtboxOutput.write(output)

if __name__ == '__main__':
    main(sys.argv[1], sys.argv[2])