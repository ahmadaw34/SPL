#include "../include/Action.h"
#include<iostream>
#include<string>
using namespace std;

BaseAction::BaseAction():errorMsg(""),status(ActionStatus()){}

ActionStatus BaseAction::getStatus() const{return status;}

std::string BaseAction::getErrorMsg() const{return errorMsg;}

void BaseAction::complete(){status=ActionStatus::COMPLETED;}
void BaseAction::error(std::string errorMsg)
{
    status=ERROR;
    cout<<errorMsg<<endl;
}
BaseAction::~BaseAction(){}
//-----------------------------------------------------------------------------
OpenTrainer::OpenTrainer(int id, std::vector<Customer *> &customersList):trainerId(id),
customers(customersList){}

void OpenTrainer::act(Studio &studio)
{
    Trainer* t=studio.getTrainer(trainerId);
    if((t==nullptr) | (t->isOpen()))
    {
        string errorMsg="Workout session does not Sexist or is already open."; 
        this->error(errorMsg);
        return;
    }
    t->openTrainer();
    int numofcustomers=0;
    for(Customer* c:customers)
    {
        if(numofcustomers<=t->getCapacity())
        {
            t->addCustomer(c);
            numofcustomers++;
            c=nullptr;

        }
        else break;
    }
    this->complete();
    t=nullptr;
}

std::string OpenTrainer::toString() const
{
    if(this->getStatus()==COMPLETED)return "open"+std::to_string(trainerId)+"Completed";
    else return "open"+std::to_string(trainerId)+"Error";
}

OpenTrainer::~OpenTrainer()
{
    int customersnum=customers.size();
    for(int i=0;i<customersnum;i++)
    {
        customers[i]=nullptr;
        delete customers[i];
    }
    customers.clear();
}
//-----------------------------------------------------------------------------
Order::Order(int id):trainerId(id){}

void Order::act(Studio &studio)
{
    Trainer* t=studio.getTrainer(trainerId);
    if(t==nullptr or !t->isOpen())
    {
        string errormsg="Trainer does not exist or is not open";
        this->error(errormsg);
        return;
    }
    vector<Customer*>customers=t->getCustomers();
    for(Customer* c:customers)
    {
        if(c!=nullptr)
        {
            t->order(c->getId(),c->order(studio.getWorkoutOptions()),studio.getWorkoutOptions());
        }
        c=nullptr;
    }
    std::vector<OrderPair> pairs=t->getOrders();
    for(OrderPair op:pairs)
    {
        Customer* cus=t->getCustomer(op.first);
        cout<<cus->getName()<<" Is Doing "<<op.second.getName()<<endl;
        cus=nullptr;
    }
    this->complete();
    pairs.clear();
     t=nullptr;
}
std::string Order::toString() const
{
    if(this->getStatus()==COMPLETED)return "order"+std::to_string(trainerId)+"Completed";
    else return "order"+std::to_string(trainerId)+"Error";   
}

Order::~Order(){}
//-------------------------------------------------------------------------------
MoveCustomer::MoveCustomer(int src, int dst, int customerId):
srcTrainer(src),dstTrainer(dst),id(customerId){}
void MoveCustomer::act(Studio &studio)
{
    Trainer* src=studio.getTrainer(srcTrainer);
    Trainer* dst=studio.getTrainer(dstTrainer);
    Customer* c=src->getCustomer(id);
    std::string errormsg="";
    if((dst==nullptr) | (src==nullptr) | (c==nullptr))
    {
        errormsg="Cannot move customer";
        this->error(errormsg);
        return;
    }
    int size=dst->getCustomers().size();
    if(dst->getCapacity()==size)
    {
        errormsg="Cannot move customer";
        this->error(errormsg);
        return;
    }
    if((!dst->isOpen()) | (!src->isOpen()))
    {
        errormsg="Cannot move customer";
        this->error(errormsg);
        return;
    }
    if(dst->getCapacity()==size)
    {
        errormsg="Cannot move customer";
        this->error(errormsg);
        return;
    }
    if(this->getStatus()==ERROR)
    {
        errormsg="Cannot move customer";
        this->error(errormsg);
        return;
    }
    Customer* ccc=src->getCustomer(id);
    string str=ccc->toString();
    string type=str.substr(str.length()-3,str.length()-1);
    Customer* c1;
    vector<Customer *> dstCustomers=dst->getCustomers();
    if(type=="swt")
    {
        c1=new SweatyCustomer(ccc->getName(),dstCustomers.size());
    }
    else if(type=="mcl")
    {
        c1=new HeavyMuscleCustomer(ccc->getName(),dstCustomers.size());
    }
    else if(type=="chp")
    {
        c1=new CheapCustomer(ccc->getName(),dstCustomers.size());
    }
    else 
    {
        c1=new FullBodyCustomer(ccc->getName(),dstCustomers.size());
    }
    ccc=nullptr;
    dst->addCustomer(c1);
    std::vector<OrderPair> op;
    std::vector<OrderPair> orders=src->getOrders();
    for(OrderPair p:orders)if(p.first==id)op.push_back(p);
    orders.clear();
    std::vector <int> opids;
    std::vector <Workout> opop;
    for(OrderPair p:op)
    {
        opids.push_back(p.first);
        opop.push_back(p.second);
    }
    vector<Customer*> customers=dst->getCustomers();
    int csize=customers.size();
    dst->order(csize,opids,opop);
    src->removeCustomer(id);
    complete();
    opop.clear();
    opids.clear();
    for(int i=0;i<csize;i++)
    {
        customers[i]=nullptr;
    }
    customers.clear();
    src=nullptr;
    dst=nullptr;
    c1=nullptr;
    c=nullptr;
}

std::string MoveCustomer::toString() const
{
    if(this->getStatus()==COMPLETED)return "move"+std::to_string(srcTrainer)+std::to_string(dstTrainer)+std::to_string(id)+"Completed";
    else return "move"+std::to_string(srcTrainer)+std::to_string(dstTrainer)+std::to_string(id)+"Error";
}

MoveCustomer::~MoveCustomer(){}
//---------------------------------------------------------------------------------
Close::Close(int id):trainerId(id){}
void Close::act(Studio &studio)
{
    Trainer* t=studio.getTrainer(trainerId);
    if((t==nullptr) | (!t->isOpen()))
    {
        string errormsg="Trainer does not exist or is not open";
        this->error(errormsg);
        return;
    }
    t->closeTrainer();
    cout<<"Trainer "<<trainerId<<" closed. Salary "<<t->getSalary()<<" NIS"<<endl;
    complete();
    t=nullptr;
}
std::string Close::toString() const
{
    if(this->getStatus()==COMPLETED)return "close"+std::to_string(trainerId)+"Completed";
    else return "close"+std::to_string(trainerId)+"Error";
}

Close::~Close(){}
//-----------------------------------------------------------------------------------
CloseAll::CloseAll(){}
void CloseAll::act(Studio &studio)
{
    for(int i=0;i<studio.getNumOfTrainers();i++)
    {
        Trainer* t=studio.getTrainer(i);
        if(t->isOpen())
        {
            Close* c=new Close(i);
            c->act(studio);
            delete c;
            c=nullptr;
        }
        t=nullptr;
    }
    complete();
}
std::string CloseAll::toString() const
{
    if(this->getStatus()==COMPLETED)return "closeall Completed";
    else return "closeall Error";

}
CloseAll::~CloseAll(){}
//----------------------------------------------------------------------------------
PrintWorkoutOptions::PrintWorkoutOptions(){}
void PrintWorkoutOptions::act(Studio &studio)
{
    std::vector<Workout> wk=studio.getWorkoutOptions();
    for(Workout w:wk)
    {
        string type;
        if(w.getType()==0)type="ANAEROBIC";
        if(w.getType()==1)type="MIXED";
        else type="CARDIO";
        cout<<w.getName()+","<<type<<","<<w.getPrice()<<endl;
    }
    complete();
    wk.clear();
}
std::string PrintWorkoutOptions::toString() const
{
    if(this->getStatus()==COMPLETED)return "workout_options Completed";
    else return "workout_options Error";

}
PrintWorkoutOptions::~PrintWorkoutOptions(){}
//-----------------------------------------------------------------------------------
PrintTrainerStatus::PrintTrainerStatus(int id):trainerId(id){}
void PrintTrainerStatus::act(Studio &studio)
{
    Trainer* t=studio.getTrainer(trainerId);
    if(!t->isOpen())
    {
        std::cout<<"Trainer"<<trainerId<<"status:closed"<<endl;
        return;
    }
    cout<<"Trainer"<<trainerId<<"status:open"<<endl;
    cout<<"Customers:"<<endl;
    std::vector<Customer*> c=t->getCustomers();
    for(Customer* cc:c)
    {
        cout<<cc->getId()<<" "<<cc->getName()<<endl;
        cc=nullptr;
    }
    cout<<"Orders:"<<endl;
    vector<OrderPair> &orders=t->getOrders();
    for(OrderPair op:orders){cout<<op.second.getName()<<" "<<op.second.getPrice()<<"NIS "<<op.first<<endl;}
    cout<<"Current Trainer’s Salary:"<<t->getSalary()<<"NIS"<<endl;
    complete();
    t=nullptr;
}
std::string PrintTrainerStatus::toString() const
{
    if(this->getStatus()==COMPLETED)return "status"+std::to_string(trainerId)+"Completed";
    else return "status"+std::to_string(trainerId)+"Error";
}
PrintTrainerStatus::~PrintTrainerStatus(){}
//-----------------------------------------------------------------------------------
PrintActionsLog::PrintActionsLog(){}
void PrintActionsLog::act(Studio &studio)
{
    vector<BaseAction*> logs=studio.getActionsLog();
    for(BaseAction* ba: logs)
    {
        cout<<ba->toString()<<endl;
        ba=nullptr;
    }
    complete();
    logs.clear();
}
std::string PrintActionsLog::toString() const
{
    if(this->getStatus()==COMPLETED)return "log Completed";
    else return "log Error";
}
PrintActionsLog::~PrintActionsLog(){}
//-------------------------------------------------------------------------------------
BackupStudio::BackupStudio(){}
void BackupStudio::act(Studio &studio)
{
    backup=new Studio(studio);
    this->complete();
}
std::string BackupStudio::toString() const
{
    if(this->getStatus()==COMPLETED)return "backup Completed";
    else return "backup Error";
}
BackupStudio::~BackupStudio(){}
//-------------------------------------------------------------------------------------
RestoreStudio::RestoreStudio(){}
void RestoreStudio::act(Studio &studio)
{
    if(backup==nullptr)
    {
        cout<<"No backup available"<<endl;
        return;
    }
    studio=*(backup);
    this->complete();
}
std::string RestoreStudio::toString() const
{
    if(this->getStatus()==COMPLETED)return "restore Completed";
    else return "restore Error";
}
RestoreStudio::~RestoreStudio(){}