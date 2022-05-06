#include "../include/Trainer.h"
#include <iostream>

using namespace std;
Trainer::Trainer(int t_capacity)
{
    orderList=vector<OrderPair>();
    id=0;
    open=false;
    customersList=vector<Customer*>();
    
    capacity=t_capacity;
}

int Trainer::getId(){return id;}

int Trainer::getCapacity() const{return capacity;}

void Trainer::addCustomer(Customer* customer){customersList.push_back(customer);}

void Trainer::removeCustomer(int id)
{
    int size=customersList.size();
    for(int i=0;i<size;i++)
    {
        if(customersList[i]->getId()==id)
        {
            customersList.erase(customersList.begin()+i);
            break;
        }
        customersList[i]=nullptr;
    }
    vector<OrderPair> pairs=vector<OrderPair>();
    for(OrderPair op:orderList)pairs.push_back(op);
    while(!orderList.empty())orderList.pop_back();
    for(OrderPair op:pairs)if(op.first!=id)orderList.push_back(op);
    pairs.clear();
}

Customer* Trainer::getCustomer(int id)
{
    for(Customer* c : customersList)
    {
        int newid=c->getId(); 
        if(newid==id)return c;
        c=nullptr;
    }
return nullptr;
}

std::vector<Customer*>& Trainer::getCustomers(){return customersList;}

std::vector<OrderPair>& Trainer::getOrders(){return orderList;}

void Trainer::order(const int customer_id, const std::vector<int> workout_ids,const std::vector<Workout>& workout_options)
{
    vector<int>ids=vector<int>(workout_ids);
    int i=0;
    for(Workout w:workout_options)
    {            
       if(ids[i]==w.getId())
        {
            OrderPair op = OrderPair(customer_id,w);
            orderList.push_back(op);
            i=i+1;
        }
    }
    ids.clear();
}

void Trainer::openTrainer(){open=true;}

void Trainer::closeTrainer(){open=false;}

int Trainer::getSalary()
{
    int salary=0;
    for(OrderPair o : orderList){salary=salary+o.second.getPrice();}
    return salary;
}

bool Trainer::isOpen()
{
    if(open)return open;
    else return false;
}

// Copy Constructor
Trainer::Trainer(const Trainer& other)
{
    if(this!=&other)
    {
        clear();    
        customersList=vector<Customer*>();
        orderList=vector<OrderPair>();
        id=other.id;
        open=other.open;
        capacity=other.capacity;
        for(Customer* c:other.customersList)
        {
            customersList.push_back(c);
            c=nullptr;
        }    
        for(OrderPair op:other.orderList)orderList.push_back(op);
    }
}
// Copy Assignment
Trainer& Trainer::operator=(const Trainer& other)
{
    if (this != &other) {
        clear();
        id=other.id;
        capacity=other.capacity;
        open=other.open;
        for(Customer* c:other.customersList)
        {
            customersList.push_back(c);
            c=nullptr;
        }    
        for(OrderPair op:other.orderList)orderList.push_back(op);
    }

    return *this;
}

// Move Constructor
Trainer::Trainer(Trainer&& other)
{
    if(this!=&other)
    {
        clear();    
        customersList=vector<Customer*>();
        orderList=vector<OrderPair>();
        id=other.id;
        open=other.open;
        capacity=other.capacity;
        for(Customer* c:other.customersList)
        {
            customersList.push_back(c);
            c=nullptr;
        }    
        for(OrderPair op:other.orderList)orderList.push_back(op);
        other.clear();
    }

}
// Move Assignment
Trainer& Trainer::operator=(Trainer&& other) {
    
    if (this != &other) {
        clear();
        id=other.id;
        capacity=other.capacity;
        open=other.open;
        for(Customer* c:other.customersList)
        {
            customersList.push_back(c);
            c=nullptr;
        }    
        for(OrderPair op:other.orderList)orderList.push_back(op);
    }

    return *this;
}

void Trainer::clear()
{
    int customersnum=customersList.size();
    for(int i=0;i<customersnum;i++)if(customersList[i]!=nullptr)delete customersList[i];
    customersList.clear();
    orderList.clear();
}

// Destructor
Trainer::~Trainer() { clear(); }
