package gui;

import models.User;
import services.AuthService;
import utils.FileService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class UserListView extends JFrame {

    private final AuthService authService;
    private JTable userTable;

    public UserListView(AuthService authService) {
        this.authService = authService;

        setTitle("User List");
        setSize(700, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadUsers();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout());

        // Table
        userTable = new JTable();
        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Refresh Button
        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.addActionListener(e -> loadUsers());
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnRefresh);
        panel.add(btnPanel, BorderLayout.SOUTH);

        add(panel);
    }

    private void loadUsers() {
        List<User> users = authService.getAllUsers();

        String[] columns = {"User ID", "Username", "Role", "Name", "Email", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getUserId(),
                    u.getUsername(),
                    u.getRole(),
                    u.getName(),
                    u.getEmail(),
                    u.getContact()
            });
        }

        userTable.setModel(model);
    }

    // Optional main method for testing independently
    public static void main(String[] args) {
        FileService fs = new FileService("data");
        AuthService authService = new AuthService(fs);
        SwingUtilities.invokeLater(() -> new UserListView(authService).setVisible(true));
    }
}
