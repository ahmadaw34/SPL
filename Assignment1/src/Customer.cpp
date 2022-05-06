#include "../include/Customer.h"
#include <vector>
#include <algorithm> 
#include <iostream>
using namespace std;

Customer::Customer(std::string c_name, int c_id):name(c_name),id(c_id){}

std::string Customer::getName() const{return name;}

int Customer::getId() const{return id;}
Customer::~Customer(){}
//---------------------------------------------------------------------------------
SweatyCustomer::SweatyCustomer(std::string name, int id):Customer(name,id){}

std::vector<int> SweatyCustomer::order(const std::vector<Workout> &workout_options)
{
    std::vector<int> swt;
    size_t size=workout_options.size();
    for(size_t i=0;i<size;i++)
    {
        
        if(workout_options[i].getType()==CARDIO)
        {
            swt.push_back(workout_options[i].getId());
        }
    }
    return swt;
}

std::string SweatyCustomer::toString() const{return this->getName()+"swt";}
SweatyCustomer::~SweatyCustomer(){}
//----------------------------------------------------------------------------------
CheapCustomer::CheapCustomer(std::string name, int id):Customer(name,id){}

std::vector<int> CheapCustomer::order(const std::vector<Workout> &workout_options)
{
    Workout w(workout_options.at(0));
    std::vector<int> chp=vector<int>();
    for(Workout wk:workout_options)if(wk.getPrice()<w.getPrice())Workout w(wk);
    chp.push_back(w.getId());
    return chp;
}

std::string CheapCustomer::toString() const{return this->getName()+"chp";}
CheapCustomer::~CheapCustomer(){}
//----------------------------------------------------------------------------------
HeavyMuscleCustomer::HeavyMuscleCustomer(std::string name, int id):Customer(name,id){}

std::vector<int> HeavyMuscleCustomer::order(const std::vector<Workout> &workout_options)
{
    std::vector<int> mcl=vector<int>();
    std::vector<Workout> pricev;
    for(Workout wk:workout_options)
    {
        if((wk.getType()!=MIXED)&(wk.getType()!=CARDIO))
        {
            pricev.push_back(wk);
        }
    }
    for(Workout w:pricev){mcl.push_back(w.getId());}
    sort(mcl.begin(),mcl.end());
    pricev.clear();
    return mcl;
}

std::string HeavyMuscleCustomer::toString() const{return this->getName()+"mcl";}
HeavyMuscleCustomer::~HeavyMuscleCustomer(){}
//---------------------------------------------------------------------------------
FullBodyCustomer::FullBodyCustomer(std::string name, int id):Customer(name,id){}

std::vector<int> FullBodyCustomer::order(const std::vector<Workout> &workout_options)
{
    std::vector<int> fbd=vector<int>();
    std::vector<int> prices1=vector<int>();
    std::vector<int> prices2=vector<int>();
    std::vector<int> prices3=vector<int>();
    for(Workout w:workout_options)
    {
        if(w.getType()==CARDIO)prices1.push_back(w.getPrice());
        if(w.getType()==MIXED)prices2.push_back(w.getPrice());
        if(w.getType()==ANAEROBIC)prices3.push_back(w.getPrice());
    }
    int price1=workout_options.at(0).getPrice();
    int price2=workout_options.at(0).getPrice();
    int price3=workout_options.at(0).getPrice();
    for(Workout w:workout_options)
    {
        if((price1>w.getPrice())&(w.getType()==CARDIO))price1=w.getPrice();
        if((price1<w.getPrice())&(w.getType()==MIXED))price2=w.getPrice();
        if((price1>w.getPrice())&(w.getType()==ANAEROBIC))price3=w.getPrice();
    }
    for(Workout w:workout_options)
    {
        if((w.getType()==CARDIO)&(price1==w.getPrice()))fbd.push_back(w.getId());
        if((w.getType()==MIXED)&(price2==w.getPrice()))fbd.push_back(w.getId());
        if((w.getType()==ANAEROBIC)&(price3==w.getPrice()))fbd.push_back(w.getId());
    }
    prices1.clear();
    prices2.clear();
    prices3.clear();
    return fbd;
}

std::string FullBodyCustomer::toString() const{return this->getName()+"fbd";}
FullBodyCustomer::~FullBodyCustomer(){}