package bgu.spl.net.impl.objects;
import bgu.spl.net.api.User;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

public class Messages {
    private ConcurrentHashMap<String, User> users;
    private ArrayList<User> list_of_users;
    private String[] censoredWords = {"abd" , "hoho"};

    private static class SingletonHolder{
        private static Messages messages = new Messages();
    }

    public static Messages getInstance(){
        return SingletonHolder.messages;
    }


    private Messages(){
        users = new ConcurrentHashMap<>();
        list_of_users = new ArrayList<>();
    }

    public String register(String username, String password, String dateOfBirth){
        if(!users.containsKey(username)){
            users.putIfAbsent(username, new User(username, password, dateOfBirth));
            list_of_users.add(users.get(username));
            return "10 1";
        }
        return "11 1";
    }

    public String login(String username, String password, String capcha, int connectionId){
        if(!users.containsKey(username) || capcha.equals("0")) {
            return "11 2";
        }
        User user = users.get(username);
        if(!user.getPassword().equals(password) || user.isLoggedIn()) {
            return "11 2";
        }
        user.setLoggedIn();
        ConnectionsImpl.getInstance().addConnection(username, connectionId);
        return "10 2";
    }

    public String logout(String username){
        if(!users.containsKey(username))
            return "11 3";
        users.get(username).setLoggedOut();
        ConnectionsImpl.getInstance().removeConnection(username);
        return "10 3";
    }

    public String follow(String follow, String destUsername, String srcUsername){
        User srcUser = users.get(srcUsername);
        User destUser = users.get(destUsername);
        if(destUser != null) {
            if (follow.equals("0")) { //follow
                if (srcUser.follow(destUser) && destUser.addFollower(srcUser))
                    return "10 4";
            } else { //unfollow
                if (srcUser.unfollow(destUser) && destUser.removeFollower(srcUser))
                    return "10 4";
            }
        }
        return "11 4";
    }

    public String post(String username, String content) {
        User user = users.get(username);
        if(user == null || !user.isLoggedIn())
            return "11 5";
        String[] words = content.split(" ");
        String tmp;
        user.addPostOrPm(content);
        user.increasePosts();
        LinkedBlockingQueue<User> followers = user.getFollowersList();
        ConnectionsImpl connections = ConnectionsImpl.getInstance();
        for (String word : words) {
            if (word.charAt(0) == '@') {
                tmp = word.substring(1);
                if (users.containsKey(tmp)) {
                    if (users.get(tmp).isLoggedIn() && !users.get(tmp).isBlocked(user)
                            && !user.isBlocked(users.get(tmp))) {
                        connections.send(connections.getConnectionId(tmp), "9 1 " + username + " " + content);
                    } else {
                        users.get(tmp).addUnseenPost("9 1 " + username + " " + content);
                    }
                }
            }
        }
        for(User other : followers) {
            if(!other.isBlocked(user) && !user.isBlocked(other)) {
                if (other.isLoggedIn())
                    connections.send(connections.getConnectionId(other.getUsername()), "9 1 " + username + " " + content);
                else
                    other.addUnseenPost("9 1 " + username + " " + content);
            }
        }
        return "10 5";
    }

    public String pm(String srcUsername, String destUsername, String content){
        User user = users.get(srcUsername);
        User other = users.get(destUsername);
        if(user == null || other == null || !user.isFollowing(other)
                || !user.isLoggedIn() || user.isBlocked(other) || other.isBlocked(user))
            return "11 6";
        content = filter(content);
        users.get(srcUsername).addPostOrPm(content);
        ConnectionsImpl connections = ConnectionsImpl.getInstance();
        if(other.isLoggedIn())
            connections.send(connections.getConnectionId(other.getUsername()), "9 0 " + srcUsername + " " + content);
        else
            other.addUnseenPost("9 0 " + srcUsername + " " + content);
        return "10 6";
    }

    public void logstat(String username,ConnectionsImpl<String> connections,
                          int connectionID) {
        User user = users.get(username);
        if (user != null && user.isLoggedIn()) {
            int size = users.size();
            User other;
            for (int i = 0; i < size; i++) {
                other = list_of_users.get(i);
                if (other.isLoggedIn() && !user.isBlocked(other) && !other.isBlocked(user)){
                    connections.send(connectionID, "10 8 " + other);
                }
            }
        }else {
            connections.send(connectionID, "11 7");
        }
    }

    public void stat(String username, String listOfUsers , ConnectionsImpl<String> connections,
                        int connectionID) {
        User user = users.get(username);
        if (user != null && user.isLoggedIn()) {
            String[] users = listOfUsers.split("\\|");
            int size = users.length;
            User other;
            for (int i = 0; i < size; i++) {
                if (this.users.get(users[i]) == null) {
                    connections.send(connectionID, "11 8");
                    return;
                }
            }

            for (int i = 0; i < size; i++) {
                other = this.users.get(users[i]);
                if (!user.isBlocked(other) && !other.isBlocked(user)) {
                    connections.send(connectionID, "10 8 " + other);
                } else {
                    connections.send(connectionID, "11 8");
                }
            }
        } else {
            connections.send(connectionID, "11 8");
        }
    }

    public String block(String srcUsername, String destUsername) {
        User srcUser = users.get(srcUsername);
        User destUser = users.get(destUsername);
        if (srcUser == null || destUser == null || !srcUser.isLoggedIn())
            return "11 12";
        srcUser.block(destUser);
        destUser.block(srcUser);
        return "10 12";
    }

    private String filter(String content){
        int size = censoredWords.length;
        for(int i = 0; i < size; i++){
            content = content.replaceAll(censoredWords[i], "<filtered>");
        }
        return content;
    }

    public LinkedBlockingQueue<String> getUnseenMessages(String username){
        return users.get(username).getUnseenMessages();
    }
}
