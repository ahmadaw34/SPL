package bgu.spl.net.api;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class User {
    private String username;
    private String password;
    private String dateOfBirth; //changed from LocalDate to String
    private LinkedBlockingQueue<User> following;
    private LinkedBlockingQueue<User> followers;
    private List<User> blocked;
    private boolean loggedIn;
    private List<String> postsAndPms;
    private LinkedBlockingQueue<String> unseenMessages;
    private long age;
    private int numOfPosts;


    public User(String username, String password, String dateOfBirth) {
        this.username = username;
        this.password = password;
        this.dateOfBirth = dateOfBirth;
        this.blocked = new ArrayList<>();
        this.followers = new LinkedBlockingQueue<>();
        this.following = new LinkedBlockingQueue<>();
        this.loggedIn = false;
        this.postsAndPms = new ArrayList<>();
        this.unseenMessages = new LinkedBlockingQueue<>();
        this.numOfPosts = 0;
        String[] birth = dateOfBirth.split("-");
        LocalDate birthDate = LocalDate.of(Integer.parseInt(birth[2]), Integer.parseInt(birth[1]), Integer.parseInt(birth[0]));
        this.age = Period.between(birthDate, LocalDate.now()).getYears();
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public LinkedBlockingQueue<User> getFollowingList() {
        return following;
    }

    public LinkedBlockingQueue<User> getFollowersList() { return followers; }

    public void setLoggedIn(){
        loggedIn = true;
    }

    public void setLoggedOut(){
        loggedIn = false;
    }

    public boolean isLoggedIn(){
        return loggedIn;
    }

    public boolean isBlocked(User other){
        if(blocked.isEmpty())
            return false;
        return blocked.contains(other);
    }

    public boolean isFollowing(User other){
        if(following.isEmpty())
            return false;
        return following.contains(other);
    }

    public boolean follow(User user) {
        //no need to check if other exists
        if (!following.contains(user) && !blocked.contains(user)) {
            following.add(user);
            return true;
        }
        return false;
    }

    public boolean unfollow(User other){
        if(following.contains(other)){
            following.remove(other);
            return true;
        }
        return false;
    }

    public boolean addFollower(User user) {
        if (!followers.contains(user) && !blocked.contains(user)) { //added check if blocked
            followers.add(user);
            return true;
        }
        return false;
    }

    public boolean removeFollower(User other){
        if(followers.contains(other)){
            followers.remove(other);
            return true;
        }
        return false;
    }

    public void block(User other) {
        blocked.add(other);

        unfollow(other);
        other.unfollow(this);

        removeFollower(other);
        other.removeFollower(this);
    }

    public void addPostOrPm(String msg){
        postsAndPms.add(msg);
    }

    public void addUnseenPost(String post){ //????
        unseenMessages.add(post);
    }

    public void increasePosts() {
        numOfPosts++;
    }

    public int unseenPostsNum(){
        return unseenMessages.size();
    }

    public LinkedBlockingQueue<String> getUnseenMessages() {
        return unseenMessages;
    }

    @Override
    public String toString() { //check this please // checked no worries
        return username + " " + age + " " + numOfPosts + " " + followers.size() +
                    " " + following.size();
    }
}
