package gui;

import models.User;
import services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class LoginForm extends JFrame {

    private final AuthService authService;

    private JTextField usernameField;
    private JPasswordField passwordField;

    public LoginForm(AuthService authService) {
        this.authService = authService;
        setTitle("JobHunter - Login");
        setSize(400, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        JLabel lblUser = new JLabel("Username:");
        usernameField = new JTextField();

        JLabel lblPass = new JLabel("Password:");
        passwordField = new JPasswordField();

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(this::handleLogin);

        JButton btnSignup = new JButton("Create Account");
        btnSignup.addActionListener(e -> {
            SignupForm signup = new SignupForm(authService);
            signup.setVisible(true);
            dispose();
        });

        panel.add(lblUser);
        panel.add(usernameField);
        panel.add(lblPass);
        panel.add(passwordField);
        panel.add(btnLogin);
        panel.add(btnSignup);

        add(panel, BorderLayout.CENTER);
    }

    private void handleLogin(ActionEvent e) {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());

        User user = authService.login(username, password);

        if (user == null) {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password!",
                    "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Welcome, " + user.getName() + " (" + user.getRole() + ")");

        openDashboard(user.getRole(), user);
    }

    private void openDashboard(String role, User user) {
        switch (role.toLowerCase()) {
            case "admin" -> new AdminDashboard(authService).setVisible(true);
            case "recruiter" -> new RecruiterDashboard(user, authService).setVisible(true);
            case "applicant" -> new ApplicantDashboard(user, authService).setVisible(true);
            default -> JOptionPane.showMessageDialog(this, "Unknown role: " + role);
        }

        dispose();
    }
}
