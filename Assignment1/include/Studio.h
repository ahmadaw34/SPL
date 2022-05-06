#ifndef STUDIO_H_
#define STUDIO_H_

#include <vector>
#include <string>
#include <iostream>
#include "Workout.h"
#include "Trainer.h"
#include "Action.h"
#include "Customer.h"


class BaseAction;
class Studio{
		
public:
     Studio();
     Studio(const std::string &configFilePath);
     void start();
     int getNumOfTrainers() const;
     Trainer* getTrainer(int tid);
     const std::vector<BaseAction*>& getActionsLog() const; // Return a reference to the history of actions
     std::vector<Workout>& getWorkoutOptions();
     //Rule of 5
     Studio(const Studio& other);// Copy Constructor
     Studio(Studio&& other);// Move Constructor
     Studio& operator=(const Studio& other);// Copy Assignment
     Studio& operator=(Studio&& other);// Move Assignment
     virtual ~Studio();// Destructor
     void clear();

 private:
     bool open;
     std::vector<Trainer*> trainers;
     std::vector<Workout> workout_options;
     std::vector<BaseAction*> actionsLog;
};

#endif
