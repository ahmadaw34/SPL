#include "../include/Studio.h"
using namespace std;
#include <string>
#include <iostream>
#include <fstream>
#include <sstream>

Studio::Studio()
{
    open=false;
    trainers=vector<Trainer*>();
    workout_options=vector<Workout>();
    actionsLog=vector<BaseAction*>();
}

Studio::Studio(const std::string &configFilePath)
{
    open=false;
    trainers=vector<Trainer*>();
    workout_options=vector<Workout>();
    actionsLog=vector<BaseAction*>();
    std::ifstream file;
    file.open(configFilePath);
    if(!file)
    {
        std::cerr<<"cant open confug file"<<endl;
        exit(1);
    }
    int counter=0,id=0;
    for(std::string chars;getline(file,chars);)
    {
        if(!((chars[0]=='#') | (chars[0]=='\0')))
        {
            string copy=chars;
            int length=copy.length();
            if(counter==2)
            {
                int i=0;
                while(i<length)
                {
                    string str="";
                    while((copy[i]!=',')&(i<length))
                    {
                        str.push_back(copy[i]);
                        i=i+1;
                    }
                    Trainer* t=new Trainer(stoi(str));
                    trainers.push_back(t);
                    i=i+1;
                }
            }
            if(counter==3)
            {
               int i=0;
                string str="";
                while((copy[i]!=',')&(i<length))
                {
                    str.push_back(copy[i]);
                    i=i+1;
                }
                string name=str;
                str="";
                i=i+2;
                while((copy[i]!=',')&(i<length))
                {
                    str.push_back(copy[i]);
                    i=i+1;   
                }
                WorkoutType type;
                if(str=="Anaerobic")type=ANAEROBIC;
                else if(str=="Mixed")type=MIXED;
                else type=CARDIO;
                str="";
                i=i+2;
                while((copy[i]!=',')&(i<length))
                {
                    str.push_back(copy[i]);
                    i=i+1;   
                }
                int price=stoi(str);
                Workout w(id,name,price,type);
                workout_options.push_back(w);
                id=id+1;
            }  
        }
        if((chars[0]=='#')&(counter!=3))counter++; 
    }
}

void Studio::start()
{
    cout<<"Studio is now open!"<<endl;
    open=true;
    string input;
    bool flag=true;
    while(flag)
    {
        getline(cin,input);
        string str="";
        int x=0;
        int length=input.length();
        while((input[x]!=' ')&(x<length))
        {
            str.push_back(input[x]);
            x=x+1;
        }
        if(str=="open")
        {        
            str="";
            x=x+1;
            while((input[x]!=' ')&(x<length))
            {
                str.push_back(input[x]);
                x=x+1;
            }
            int trainerId=stoi(str);
            vector<Customer*> c;                
            string str1="";
            int count=0;
            x=x+1;
            while(x<length)
            {
                string cname="";
                while((x<length)&(input[x]!=','))
                {
                    cname.push_back(input[x]);
                    x=x+1;
                }
                string strategy=""; 
                x=x+1;
                for(int i=0;i<3;i=i+1)
                {
                    strategy.push_back(input[x]);
                    x=x+1;
                }
                if(strategy=="swt")
                {
                    Customer* newc=new SweatyCustomer(cname,count);
                    c.push_back(newc);
                }
                if(strategy=="chp")
                {
                    Customer* newc=new CheapCustomer(cname,count);
                    c.push_back(newc);
                }
                if(strategy=="mcl")
                {
                    Customer* newc=new HeavyMuscleCustomer(cname,count);
                    c.push_back(newc);
                }
                if(strategy=="fbd")
                {
                    Customer* newc=new FullBodyCustomer(cname,count);
                    c.push_back(newc);
                }
                count=count+1;
                x=x+1;
            }
            BaseAction* ot=new OpenTrainer(trainerId,c);
            ot->act(*this);
            actionsLog.push_back(ot);
        }
        if(str=="order")
        {
            str="";
            x=x+1;
            while(x!=length)
            {
                str.push_back(input[x]);
                x=x+1;
            }
            BaseAction* o=new Order(stoi(str));
            o->act(*this);
            actionsLog.push_back(o);
        }
        if(str=="move")
        {
            int count=1,src,dst,cId;str="";
            x=x+1;
            while(x!=length)
            {
                if(input[x]!=' '){str.push_back(input[x]);}
                else
                {
                    if(count==1)
                    { 
                        src=stoi(str); 
                        count=count+1;
                        str="";
                    }
                    else
                    {
                        if(count==2)
                        {
                            dst=stoi(str); 
                            count=count+1; 
                            str="";
                        }
                        else 
                        {
                            cId=stoi(str); 
                            count=count+1; 
                            str="";
                        }
                    }    
                }
                x=x+1;  
            }
            BaseAction* mc=new MoveCustomer(src,dst,cId);
            mc->act(*this);
            actionsLog.push_back(mc);
        }
        if(str=="close")
        {
            x=x+1;
            str="";
            while(x!=length)
            {
                str.push_back(input[x]);
                x=x+1;
            }
            BaseAction* c=new Close(stoi(str));
            c->act(*this);
            actionsLog.push_back(c);
        }
        if(str=="closeall")
        {
            BaseAction* ca=new CloseAll();
            ca->act(*this);
            actionsLog.push_back(ca);
            flag=false;
            open=false;
        }
        if(str=="workout_options")
        {
           BaseAction* pwk=new PrintWorkoutOptions();
           pwk->act(*this);
           actionsLog.push_back(pwk);
        }
        if(str=="status")
        {
            x=x+1;
            str="";
            while(x<length)
            {
                str.push_back(input[x]);
                x=x+1;
            }
            BaseAction* ts=new PrintTrainerStatus(stoi(str));
            ts->act(*this);
            actionsLog.push_back(ts);
        }
        if(str=="log")
        {
            BaseAction* log=new PrintActionsLog();
            log->act(*this);
            actionsLog.push_back(log);
        }
        if(str=="backup")
        {
            BaseAction* back=new BackupStudio();
            back->act(*this);
            actionsLog.push_back(back);
        }
        if(str=="restore")
        {
            BaseAction* restore=new RestoreStudio();
            restore->act(*this);
            actionsLog.push_back(restore);
        }
    }
}

int Studio::getNumOfTrainers() const{return trainers.size();}

Trainer* Studio::getTrainer(int tid)
{
    int trainerssize=trainers.size();
    if(tid<trainerssize)return trainers[tid];
    else return nullptr;
}

const std::vector<BaseAction*>& Studio::getActionsLog() const{return actionsLog;}

std::vector<Workout>& Studio::getWorkoutOptions(){return workout_options;}

// Copy Constructor
Studio::Studio(const Studio& other)
{
    open=other.open;
    for(Trainer* t:other.trainers)
    {
        trainers.push_back(t);
        t=nullptr;
        //delete t;
    }
    for(Workout w:other.workout_options)workout_options.push_back(w);
    for(BaseAction* action:other.actionsLog)
    {
        actionsLog.push_back(action);
        action=nullptr;
       // delete action;
    }
}

// Copy Assignment
Studio& Studio::operator=(const Studio& other) {
    if (this != &other) {
        clear();
        open=other.open;
        for(Trainer* t:other.trainers)
        {
            trainers.push_back(t);
            t=nullptr;
        }
        for(Workout w:other.workout_options)workout_options.push_back(w);
        for(BaseAction* action:other.actionsLog)
        {
            actionsLog.push_back(action);
            action=nullptr;
        }
    }

    return *this;
}

// Move Constructor
Studio::Studio(Studio&& other)
{
    open=other.open;
    for(Trainer* t:other.trainers)trainers.push_back(t);
    for(Workout w:other.workout_options)workout_options.push_back(w);
    for(BaseAction* action:other.actionsLog)actionsLog.push_back(action);
    other.clear();        
}

// Move Assignment
Studio& Studio::operator=(Studio&& other) {
    
    if (this != &other) {
        clear();
        open=other.open;
        for(Trainer* t:other.trainers)trainers.push_back(t);
        for(Workout w:other.workout_options)workout_options.push_back(w);
        for(BaseAction* action:other.actionsLog)actionsLog.push_back(action);
    }
    other.clear();
    return *this;
}

void Studio::clear()
{
    int trainersnum=trainers.size();
    for(int i=0;i<trainersnum;i++)if(trainers[i]!=nullptr)delete trainers[i];
    trainers.clear();
    int actionsnum=actionsLog.size();
    for(int i=0;i<actionsnum;i++)if(actionsLog[i]!=nullptr)delete actionsLog[i];
    actionsLog.clear();
    workout_options.clear();
    open=false;
}

// Destructor
Studio::~Studio() { clear(); }
