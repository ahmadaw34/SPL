package bgu.spl.net.impl.objects;

import sun.awt.X11.XSystemTrayPeer;

import java.util.concurrent.LinkedBlockingQueue;


public class BidiMessagingProtocolImpl<T> implements BidiMessagingProtocol<T>{
    private boolean terminate;
    private int connectionId; //connected client
    private ConnectionsImpl<String> connections; //the connection itself
    private String username; //the logged in user if there's any
    private Messages messages;

    public BidiMessagingProtocolImpl() {
        username = null;
        messages = Messages.getInstance();
    }

    @Override
    public void start(int connectionId, Connections<T> connections) {
        this.connectionId = connectionId;
        this.connections = (ConnectionsImpl<String>) connections;
        this.terminate = false;
    }

    private short bytesToShort(byte[] byteArr) {
        short result = (short)((byteArr[0] & 0xff) << 8);
        result += (short)(byteArr[1] & 0xff);
        return result;
    }

    @Override
    public void process(T message) {
        String msg = message.toString();
        System.out.print("Recieved Message: ");
        System.out.println(msg);
        String[] data = msg.split(" ");
        switch (Integer.parseInt(data[0])){ //check opcode
            case 1: //register
                connections.send(connectionId, messages.register(data[1], data[2], data[3]));
                break;
            case 2:
                if(username != null){
                    connections.send(connectionId , "11 2");
                    break;
                }
                String str1 = messages.login(data[1], data[2], data[3], connectionId);
                connections.send(connectionId, str1);
                if(str1.equals("10 2")) { //if login successful
                    username = data[1];
                    LinkedBlockingQueue<String> tmp = messages.getUnseenMessages(username);
                    while(!tmp.isEmpty()){
                        connections.send(connectionId , tmp.poll());
                    }
                }
                break;
            case 3:
                if(username == null){
                    connections.send(connectionId , "11 3");
                    break;
                }
                String str2 = messages.logout(username);
                connections.send(connectionId, str2);
                if(str2.equals("10 3"))
                    username = null;
                break;
            case 4:
                if(username == null){
                    connections.send(connectionId , "11 4");
                    break;
                }
                connections.send(connectionId, messages.follow(data[1], data[2], username));
                break;
            case 5:
                if(username == null){
                    connections.send(connectionId , "11 5");
                    break;
                }
                String content1 = "";
                int size1 = data.length;
                for(int i = 1 ; i < size1  ; i++){
                    content1 += data[i] + " " ;
                }
                connections.send(connectionId, messages.post(username, content1));
                break;
            case 6:
                if(username == null){
                    connections.send(connectionId , "11 6");
                    break;
                }
                String content2 = "";
                int size2 = data.length;
                for(int i = 2 ; i < size2  ; i++){
                    content2 += data[i] + " " ;
                }
                connections.send(connectionId, messages.pm(username, data[1], content2));
                break;
            case 7:
                if(username == null){
                    connections.send(connectionId , "11 7");
                    break;
                }
                messages.logstat(username, connections , connectionId);
                break;
            case 8:
                if(username == null){
                    connections.send(connectionId , "11 8");
                    break;
                }
                messages.stat(username, data[1] , connections , connectionId);
                break;
            case 12:
                if(username == null){
                    connections.send(connectionId , "11 12");
                    break;
                }
                connections.send(connectionId, messages.block(username, data[1]));
                break;
        }
    }

    @Override
    public boolean shouldTerminate() {
        return terminate;
    }
}
