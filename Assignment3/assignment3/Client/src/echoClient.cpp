#include "../include/connectionHandler.h"
#include <stdlib.h>
#include <mutex>
#include <thread>
#include <vector>

/**
* This code assumes that the server replies the exact text the client sent it (as opposed to the practical session example)
*/

using namespace std;
bool connected = true;
bool disconnect = false;
vector<string> analyseCommand(string &line){
    vector<string> commandline;
    string temp;
    char delimiter = ' ';
    long Size = line.length();
    for(int i = 0; i < Size; i++){
        if(line[i] == delimiter){
            commandline.push_back(temp);
            temp.clear();
        }else{
            temp.push_back(line[i]);
        }
    }
    commandline.push_back(temp);
    return commandline;
}

short bytesToShort(char* bytesArr)
{
    short result = (short)((bytesArr[0] & 0xff) << 8);
    result += (short)(bytesArr[1] & 0xff);
    return result;
}

void shortToBytes(short num, char* bytesArr)
{
    bytesArr[0] = ((num >> 8) & 0xFF);
    bytesArr[1] = (num & 0xFF);
}
//-------------------------------------------------------------------------------------

class KeyboardReader{

private:
    ConnectionHandler * connectionHandler;
public:
    KeyboardReader(ConnectionHandler &connectionHandler) : connectionHandler(&connectionHandler){}
    void run() {
        while (1) {
            if(disconnect){
                break;
            }
            while (connected) {
                string line;
                getline(cin, line);
                vector<string> commandline = analyseCommand(line);
                if (commandline[0] == "REGISTER") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(1, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    connectionHandler->sendFrameAscii(commandline[1], '\0');
                    connectionHandler->sendFrameAscii(commandline[2], '\0');
                    connectionHandler->sendFrameAscii(commandline[3], ';');
                    free(opcode);
                } else if (commandline[0] == "LOGIN") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(2, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    connectionHandler->sendFrameAscii(commandline[1], '\0');
                    connectionHandler->sendFrameAscii(commandline[2], '\0');
                    connectionHandler->sendFrameAscii(commandline[3], ';');
                    free(opcode);
                } else if (commandline[0] == "LOGOUT") {
                    connected = false;
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(3, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    char nkodabsek = ';';
                    connectionHandler->sendBytes(&nkodabsek, 1);
                    free(opcode);
                } else if (commandline[0] == "FOLLOW") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(4, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    connectionHandler->sendFrameAscii(commandline[1], '\0');
                    connectionHandler->sendFrameAscii(commandline[2], ';');
                    free(opcode);
                } else if (commandline[0] == "POST") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(5, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    string post = "";
                    int commandlinesize = commandline.size();
                    for (int i = 1; i < commandlinesize - 1; i++) {
                        post += commandline[i] + " ";
                    }
                    post += commandline[commandlinesize - 1];
                    connectionHandler->sendFrameAscii(post, ';');
                    free(opcode);
                } else if (commandline[0] == "PM") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(6, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    connectionHandler->sendFrameAscii(commandline[1], '\0');
                    string post = "";
                    int commandlinesize = commandline.size();
                    for (int i = 2; i < commandlinesize - 1; i++) {
                        post += commandline[i] + " ";
                    }
                    post += commandline[commandlinesize - 1];
                    connectionHandler->sendFrameAscii(post, ';');
                    free(opcode);
                } else if (commandline[0] == "LOGSTAT") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(7, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    char nkodabsek = ';';
                    connectionHandler->sendBytes(&nkodabsek, 1);
                    free(opcode);
                } else if (commandline[0] == "STAT") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(8, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    connectionHandler->sendFrameAscii(commandline[1], ';');
                    free(opcode);
                } else if (commandline[0] == "BLOCK") {
                    char *opcode = (char *) (malloc(2));
                    shortToBytes(12, opcode);
                    connectionHandler->sendBytes(opcode, 2);
                    connectionHandler->sendFrameAscii(commandline[1], ';');
                    free(opcode);
                } else {
                    cout << "Error in messaging" << endl;
                }
            }
        }
    }
};

//-------------------------------------------------------------------------------------

class SocketReader{

private:
    ConnectionHandler * connectionHandler;
public:
    SocketReader(ConnectionHandler &connectionHandler) : connectionHandler(&connectionHandler){}
    void run() {
        while (connected) {
            char replyBytes[4];
            connectionHandler->getBytes(replyBytes,4);
            char opcodeAsBytes[2] = {replyBytes[0], replyBytes[1]};
            char messageOpcodeAsBytes[2] = {replyBytes[2],replyBytes[3]};
            short opcode = bytesToShort(opcodeAsBytes);
            short messageOpcode = bytesToShort(messageOpcodeAsBytes);
            string content;
            connectionHandler->getFrameAscii(content,';');
            if(!content.empty()){
                content.pop_back();
            }
            if(opcode == 9){
                if(messageOpcode == 1){
                    cout<<"NOTIFICATION Public ";
                }else if(messageOpcode == 0){
                    cout<<"NOTIFICATION PM ";
                }
                cout<<content<<endl;

            }else if (opcode == 10){
                cout << "ACK " << messageOpcode << " ";
                if(!content.empty()){
                    cout << content << endl;
                }else{
                    cout << endl;
                }
                if(messageOpcode == 3){
                    connected = false;
                    disconnect = true;
                }
            }else if (opcode == 11){
                cout << "ERROR " << messageOpcode << endl;
                connected = true;
            }else{
                cout << "wrong received message" <<endl;
                break;
            }
        }
    }
};

//-------------------------------------------------------------------------------------

int main (int argc, char *argv[]) {
    if (argc < 3) {
        cerr << "Usage: " << argv[0] << " host port" << endl << endl;
        return -1;
    }
    string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        cerr << "Cannot connect to " << host << ":" << port << endl;
        return -1;
    }

    KeyboardReader keyboardReader(connectionHandler);
    SocketReader socketReader(connectionHandler);

    thread keyboardListener(&KeyboardReader::run,keyboardReader);
    thread socketListener(&SocketReader::run,socketReader);

    keyboardListener.join();
    socketListener.join();
    
    connectionHandler.close();
    return 0;
}
