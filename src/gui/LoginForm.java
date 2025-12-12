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
        getContentPane().setBackground(new Color(245, 247, 250));

        setLayout(new BorderLayout());
        initUI(); // setup all UI components
    }

    private void initUI() {

        // panel with grid layout for form inputs and buttons
        JPanel panel = new JPanel(new GridLayout(6, 1, 15, 15));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        panel.setBackground(Color.WHITE);
        panel.setOpaque(true);

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        // username label and field
        JLabel lblUser = new JLabel("Username:");
        lblUser.setFont(labelFont);
        lblUser.setForeground(new Color(50, 50, 50));
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        usernameField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // password label and field
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(labelFont);
        lblPass.setForeground(new Color(50, 50, 50));
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));

        // login button
        JButton btnLogin = new JButton("Login");
        btnLogin.setFont(buttonFont);
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setBackground(new Color(62, 180, 137)); // green button
        btnLogin.setFocusPainted(false);
        btnLogin.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnLogin.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(this::handleLogin); // handle login click

        // signup button to open SignupForm
        JButton btnSignup = new JButton("Create Account");
        btnSignup.setFont(buttonFont);
        btnSignup.setBackground(new Color(230, 230, 230));
        btnSignup.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnSignup.setFocusPainted(false);
        btnSignup.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        btnSignup.addActionListener(e -> {
            SignupForm signup = new SignupForm(authService);
            signup.setVisible(true);
            dispose(); // close login form
        });

        // add components to panel
        panel.add(lblUser);
        panel.add(usernameField);
        panel.add(lblPass);
        panel.add(passwordField);
        panel.add(btnLogin);
        panel.add(btnSignup);

        add(panel, BorderLayout.CENTER);
    }

    // validate login credentials
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

        openDashboard(user.getRole(), user); // open corresponding dashboard
    }

    // open dashboard based on user role
    private void openDashboard(String role, User user) {
        switch (role.toLowerCase()) {
            case "admin" -> new AdminDashboard(authService, user).setVisible(true);
            case "recruiter" -> new RecruiterDashboard(user, authService).setVisible(true);
            case "applicant" -> new ApplicantDashboard(user, authService).setVisible(true);
            default -> JOptionPane.showMessageDialog(this, "Unknown role: " + role);
        }

        dispose(); // close login form after opening dashboard
    }
}
