package services;

import models.User;
import utils.FileService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AuthService {

    private final FileService fileService;
    private static final String USER_FILE = "users.txt";

    public AuthService(FileService fs) {
        this.fileService = fs;
        fileService.createIfNotExists(USER_FILE);
    }

    public FileService getFileService() {
        return fileService;
    }

    public List<User> getAllUsers() {
        List<String> lines = fileService.readAllLines(USER_FILE);
        List<User> users = new ArrayList<>();

        for (String line : lines) {
            User u = User.fromLine(line);
            if (u != null) users.add(u);
        }
        return users;
    }

    private void saveAll(List<User> users) {
        List<String> lines = new ArrayList<>();
        for (User u : users) lines.add(u.toLine());
        fileService.writeAllLines(USER_FILE, lines);
    }

    public boolean signup(String username, String password, String role,
                          String name, String email, String contact, String description) {

        for (User u : getAllUsers()) {
            if (u.getUsername().equalsIgnoreCase(username))
                return false;
        }

        String userId = UUID.randomUUID().toString();
        User newUser = new User(userId, username, password, role, name, email, contact, description);

        fileService.appendLine(USER_FILE, newUser.toLine());
        return true;
    }

    public User login(String username, String password) {
        for (User u : getAllUsers()) {
            if (u.getUsername().equals(username) &&
                    u.getPassword().equals(password))
                return u;
        }
        return null;
    }

    // -------------------------
    // Update profile method
    // -------------------------
    public boolean updateProfile(String userId, String newName, String newEmail, String newContact, String newDescription) {
        List<User> users = getAllUsers();
        boolean updated = false;

        for (User u : users) {
            if (u.getUserId().equals(userId)) {
                u.setName(newName);
                u.setEmail(newEmail);
                u.setContact(newContact);
                u.setDescription(newDescription);
                updated = true;
                break;
            }
        }

        if (updated) {
            saveAll(users);
        }

        return updated;
    }
}
