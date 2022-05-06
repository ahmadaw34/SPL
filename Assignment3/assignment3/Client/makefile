

CFLAGS:=-c -Wall -Weffc++ -g -std=c++11 -Iinclude
LDFLAGS:=-lboost_system -lboost_thread -pthread

all: bin/echoClient

bin/echoClient: bin/connectionHandler.o bin/echoClient.o
	@echo 'Building target: echoClient'
	@echo 'Invoking: C++ Linker'
	g++ -o bin/echoClient bin/connectionHandler.o bin/echoClient.o $(LDFLAGS)
	@echo 'Finished building target: echoClient'
	@echo ' '

bin/echoClient.o: src/echoClient.cpp
	g++ $(CFLAGS) -o bin/echoClient.o src/echoClient.cpp

bin/connectionHandler.o: src/connectionHandler.cpp
	g++ $(CFLAGS) -o bin/connectionHandler.o src/connectionHandler.cpp

.PHONY: clean
clean:
	rm -f bin/*


