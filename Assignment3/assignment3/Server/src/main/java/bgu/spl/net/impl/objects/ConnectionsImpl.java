package bgu.spl.net.impl.objects;

import bgu.spl.net.srv.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ConnectionsImpl<T> implements Connections<T> {
    private ConcurrentHashMap<String, Integer> users; //changed from T to String
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> activeConnections;
    private AtomicInteger connectionId;

    private static ConnectionsImpl connections = new ConnectionsImpl<>();
    public static ConnectionsImpl getInstance(){
        if(connections == null)
            connections = new ConnectionsImpl();
        return connections;
    }

    public ConnectionsImpl(){
        users = new ConcurrentHashMap<>();
        activeConnections = new ConcurrentHashMap<>();
        connectionId = new AtomicInteger(0);
    }

    public int connect(bgu.spl.net.srv.ConnectionHandler<T> connectionHandler){
        activeConnections.putIfAbsent(connectionId.intValue(), connectionHandler);
        connectionHandler.setId(connectionId.intValue());
        return connectionId.getAndIncrement();
    }

    @Override
    public boolean send(int connectionId, T msg) {
        if(activeConnections.containsKey(connectionId)){
            activeConnections.get(connectionId).send(msg);
            return true;
        }
        return false;
    }

    @Override
    public void broadcast(T msg) {

    }

    @Override //after a successful logout delete the connection
    public void disconnect(int connectionId) {
        activeConnections.remove(connectionId);
    }

    public void addConnection(String username, int id){
        users.putIfAbsent(username, id);
    }

    public void removeConnection(String username){
        users.remove(username);
    }

    public int getConnectionId(String username){
        return users.get(username);
    }
}
