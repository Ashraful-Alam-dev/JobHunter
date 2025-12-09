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
        setSize(450, 400);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {

        JPanel panel = new JPanel(new GridLayout(12, 1, 8, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Labels + Inputs
        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Full Name:"));
        nameField = new JTextField();
        panel.add(nameField);

        panel.add(new JLabel("Email:"));
        emailField = new JTextField();
        panel.add(emailField);

        panel.add(new JLabel("Contact No.:"));
        contactField = new JTextField();
        panel.add(contactField);

        panel.add(new JLabel("Description:"));
        descriptionField = new JTextField();
        panel.add(descriptionField);

        panel.add(new JLabel("Select Role:"));
        roleBox = new JComboBox<>(new String[]{"Recruiter", "Applicant"});
        panel.add(roleBox);

        JButton btnSignup = new JButton("Create Account");
        btnSignup.addActionListener(this::handleSignup);

        JButton btnBack = new JButton("Back to Login");
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

        // --- Validations ---
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
