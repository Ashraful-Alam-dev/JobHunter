package models;

public class User {

    private String id;
    private String username;
    private String password;
    private String role;       // Admin / Recruiter / Applicant
    private String name;
    private String email;
    private String contact;

    public User(String id, String username, String password,
                String role, String name, String email, String contact) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
        this.name = name;
        this.email = email;
        this.contact = contact;
    }

    // Convert file line to a User object
    public static User fromLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 7) return null;

        return new User(
                p[0],  // id
                p[1],  // username
                p[2],  // password
                p[3],  // role
                p[4],  // name
                p[5],  // email
                p[6]   // contact
        );
    }

    // Convert User object to file line
    public String toLine() {
        return String.join("|",
                id, username, password, role, name, email, contact
        );
    }

    // ------------------------
    // Getters
    // ------------------------
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getRole() { return role; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getContact() { return contact; }

    // ------------------------
    // Setters
    // ------------------------
    public void setId(String id) { this.id = id; }
    public void setPassword(String password) { this.password = password; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setContact(String contact) { this.contact = contact; }
}
