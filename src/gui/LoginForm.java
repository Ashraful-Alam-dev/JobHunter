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
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15)); // slightly more spacing
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(labelFont);
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);

        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(labelFont);
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);

        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(buttonFont);
        btnLogin.addActionListener(this::handleLogin);

        JButton btnSignup = new JButton("Create Account");
        btnSignup.setFont(buttonFont);
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
            case "admin" -> new AdminDashboard(authService, user).setVisible(true);
            case "recruiter" -> new RecruiterDashboard(user, authService).setVisible(true);
            case "applicant" -> new ApplicantDashboard(user, authService).setVisible(true);
            default -> JOptionPane.showMessageDialog(this, "Unknown role: " + role);
        }

        dispose();
    }
}
