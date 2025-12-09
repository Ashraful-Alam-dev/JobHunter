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
        setSize(600, 450);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        Font labelFont = new Font("SansSerif", Font.PLAIN, 16);
        Font fieldFont = new Font("SansSerif", Font.PLAIN, 16);

        JLabel lblName = new JLabel("Full Name:");
        lblName.setFont(labelFont);
        panel.add(lblName);
        nameField = new JTextField(user.getName());
        nameField.setFont(fieldFont);
        panel.add(nameField);

        JLabel lblEmail = new JLabel("Email:");
        lblEmail.setFont(labelFont);
        panel.add(lblEmail);
        emailField = new JTextField(user.getEmail());
        emailField.setFont(fieldFont);
        panel.add(emailField);

        JLabel lblContact = new JLabel("Contact:");
        lblContact.setFont(labelFont);
        panel.add(lblContact);
        contactField = new JTextField(user.getContact());
        contactField.setFont(fieldFont);
        panel.add(contactField);

        JLabel lblDesc = new JLabel("Description:");
        lblDesc.setFont(labelFont);
        panel.add(lblDesc);
        descriptionField = new JTextField(user.getDescription());
        descriptionField.setFont(fieldFont);
        panel.add(descriptionField);

        JButton btnSave = new JButton("Save");
        btnSave.setFont(new Font("SansSerif", Font.BOLD, 16));
        btnSave.addActionListener(e -> {
            boolean ok = authService.updateProfile(
                    user.getUserId(),
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
