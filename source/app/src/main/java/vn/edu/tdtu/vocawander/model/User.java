package vn.edu.tdtu.vocawander.model;


public class User {

    private String name, email, password, avatar, phone;

    public User() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public User(String name, String email, String password, String avatar) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    public User(String name, String email, String password, String avatar, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.phone = phone;
    }

    public String getPhone() { return this.phone; }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getAvatar() {
        return avatar;
    }
}