package models;

public class User {

    private String userId;
    private String username;
    private String password;
    private String role;
    private String name;
    private String email;
    private String contact;
    private String description;

    public User(String userId, String username, String password,
                String role, String name, String email, String contact, String description) {

        this.userId = userId;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.description = description;
    }

    // Getters
    public String getUserId() { return userId; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }
    public String getDescription() { return description; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setContact(String contact) { this.contact = contact; }
    public void setDescription(String description) { this.description = description; }

    public String toLine() {
        return userId + "|" + username + "|" + password + "|" +
                role + "|" + name + "|" + email + "|" + contact + "|" + description;
    }

    public static User fromLine(String line) {
        try {
            String[] p = line.split("\\|");
            return new User(p[0], p[1], p[2], p[3], p[4], p[5], p[6], p[7]);
        } catch (Exception e) {
            return null;
        }
    }
}
