package gui;

import models.User;
import services.AuthService;

import javax.swing.*;
import java.awt.*;

public class ProfileForm extends JFrame {

    private final User user;
    private final AuthService authService;

    private JTextField nameField;
    private JTextField emailField;
    private JTextField contactField;
    private JTextField descriptionField;

    public ProfileForm(User user, AuthService authService) {
        this.user = user;
        this.authService = authService;

        setTitle("Edit Profile");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(7, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        // Name
        panel.add(new JLabel("Full Name:"));
        nameField = new JTextField(user.getName());
        panel.add(nameField);

        // Email
        panel.add(new JLabel("Email:"));
        emailField = new JTextField(user.getEmail());
        panel.add(emailField);

        // Contact
        panel.add(new JLabel("Contact:"));
        contactField = new JTextField(user.getContact());
        panel.add(contactField);

        panel.add(new JLabel("Description:"));
        contactField = new JTextField(user.getDescription());
        panel.add(descriptionField);

        // Save Button
        JButton btnSave = new JButton("Save");
        btnSave.addActionListener(e -> {
            boolean ok = authService.updateProfile(
                    user.getUserId(), // corrected getter
                    nameField.getText().trim(),
                    emailField.getText().trim(),
                    contactField.getText().trim(),
                    descriptionField.getText().trim()
            );

            if (ok) {
                JOptionPane.showMessageDialog(this, "Profile updated!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update profile.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        panel.add(btnSave);

        add(panel, BorderLayout.CENTER);
    }
}
