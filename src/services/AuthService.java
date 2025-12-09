package services;

import models.User;
import utils.FileService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AuthService {

    private final FileService fileService;
    private final String USERS_FILE = "users.txt";

    public AuthService(FileService fileService) {
        this.fileService = fileService;
    }

    // -----------------------------
    // LOGIN
    // -----------------------------
    public Optional<User> login(String username, String password) {
        List<String> lines = fileService.readAllLines(USERS_FILE);

        for (String line : lines) {
            User user = User.fromLine(line);
            if (user != null) {
                if (user.getUsername().equals(username) &&
                    user.getPassword().equals(password)) {

                    return Optional.of(user);
                }
            }
        }

        return Optional.empty();
    }

    // -----------------------------
    // SIGNUP (Recruiter / Applicant only)
    // -----------------------------
    public boolean signup(User newUser) {

        List<String> lines = fileService.readAllLines(USERS_FILE);

        // Check if username already exists
        for (String line : lines) {
            User existing = User.fromLine(line);
            if (existing != null && existing.getUsername().equals(newUser.getUsername())) {
                return false; // username already taken
            }
        }

        // Generate new unique user ID
        int maxId = 0;
        for (String line : lines) {
            User u = User.fromLine(line);
            if (u != null) {
                try {
                    int id = Integer.parseInt(u.getId());
                    if (id > maxId) maxId = id;
                } catch (NumberFormatException ignored) {}
            }
        }

        newUser.setId(String.valueOf(maxId + 1));

        // Save user to file
        fileService.appendLine(USERS_FILE, newUser.toLine());
        return true;
    }

    // (Optional helper)
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        for (String line : fileService.readAllLines(USERS_FILE)) {
            User u = User.fromLine(line);
            if (u != null) users.add(u);
        }
        return users;
    }
}

