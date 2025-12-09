package gui;

import services.AuthService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SignupForm extends JFrame {

    private final AuthService authService;

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextField descriptionField;

    private JComboBox<String> roleBox;

    public SignupForm(AuthService authService) {
        this.authService = authService;

        setTitle("JobHunter - Create Account");
        setSize(600, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(12, 1, 10, 10)); // bigger gaps
        panel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50)); // more padding

        Font labelFont = new Font("SansSerif", Font.BOLD, 18);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);
        Font buttonFont = new Font("SansSerif", Font.BOLD, 16);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(labelFont);
        nameField = new JTextField();
        nameField.setFont(fieldFont);
        panel.add(lblName);
        panel.add(nameField);

        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(labelFont);
        usernameField = new JTextField();
        usernameField.setFont(fieldFont);
        panel.add(lblUsername);
        panel.add(usernameField);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        emailField = new JTextField();
        emailField.setFont(fieldFont);
        panel.add(lblEmail);
        panel.add(emailField);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(labelFont);
        passwordField = new JPasswordField();
        passwordField.setFont(fieldFont);
        panel.add(lblPassword);
        panel.add(passwordField);

        JLabel lblContact = new JLabel("Contact No.:");
        lblContact.setFont(labelFont);
        contactField = new JTextField();
        contactField.setFont(fieldFont);
        panel.add(lblContact);
        panel.add(contactField);

        JLabel lblDescription = new JLabel("Description:");
        lblDescription.setFont(labelFont);
        descriptionField = new JTextField();
        descriptionField.setFont(fieldFont);
        panel.add(lblDescription);
        panel.add(descriptionField);

        JLabel lblRole = new JLabel("Select Role:");
        lblRole.setFont(labelFont);
        roleBox = new JComboBox<>(new String[]{"Recruiter", "Applicant"});
        roleBox.setFont(fieldFont);
        panel.add(lblRole);
        panel.add(roleBox);

        JButton btnSignup = new JButton("Create Account");
        btnSignup.setFont(buttonFont);
        btnSignup.setBackground(new Color(62, 180, 137)); // green color
        btnSignup.setForeground(Color.WHITE);
        btnSignup.setFocusPainted(false);
        btnSignup.addActionListener(this::handleSignup);

        JButton btnBack = new JButton("Back to Login");
        btnBack.setFont(buttonFont);
        btnBack.setBackground(new Color(230, 230, 230)); // light gray
        btnBack.setForeground(Color.BLACK);
        btnBack.setFocusPainted(false);
        btnBack.addActionListener(e -> {
            new LoginForm(authService).setVisible(true);
            dispose();
        });

        panel.add(btnSignup);
        panel.add(btnBack);

        add(panel, BorderLayout.CENTER);
    }

    private void handleSignup(ActionEvent e) {

        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String contact = contactField.getText().trim();
        String description = descriptionField.getText().trim();
        String role = roleBox.getSelectedItem().toString();

        if (username.isEmpty() || password.isEmpty() || name.isEmpty() ||
                email.isEmpty() || contact.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "All fields are required!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (password.length() < 4) {
            JOptionPane.showMessageDialog(this,
                    "Password must be at least 4 characters!",
                    "Weak Password",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = authService.signup(username, password, role, name, email, contact, description);

        if (!success) {
            JOptionPane.showMessageDialog(this,
                    "Username already exists!",
                    "Signup Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this,
                "Account created successfully!\nPlease login now.");

        new LoginForm(authService).setVisible(true);
        dispose();
    }
}