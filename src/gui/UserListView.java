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
    private final User currentUser;
    private JTable userTable;

    public UserListView(AuthService authService, User currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;

        setTitle("User List");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        initUI();
        loadUsers();
    }

    private void initUI() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        userTable = new JTable();
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        userTable.setRowHeight(24);

        JScrollPane scrollPane = new JScrollPane(userTable);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel();
        btnPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnRefresh.addActionListener(e -> loadUsers());
        btnPanel.add(btnRefresh);

        JButton btnDelete = new JButton("Delete Selected User");
        btnDelete.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnDelete.addActionListener(e -> deleteSelectedUser());
        btnPanel.add(btnDelete);

        JButton btnViewUser = new JButton("View User Details"); // NEW
        btnViewUser.setFont(new Font("SansSerif", Font.BOLD, 12));
        btnViewUser.addActionListener(e -> {
            int row = userTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a user!");
                return;
            }

            String details = "User ID: " + userTable.getValueAt(row, 0) +
                    "\nUsername: " + userTable.getValueAt(row, 1) +
                    "\nRole: " + userTable.getValueAt(row, 2) +
                    "\nName: " + userTable.getValueAt(row, 3) +
                    "\nEmail: " + userTable.getValueAt(row, 4) +
                    "\nContact: " + userTable.getValueAt(row, 5);

            JOptionPane.showMessageDialog(this, details, "User Details", JOptionPane.INFORMATION_MESSAGE);
        });
        btnPanel.add(btnViewUser);

        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private void loadUsers() {
        List<User> users = authService.getAllUsers();

        String[] columns = {"User ID", "Username", "Role", "Name", "Email", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };

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

    private void deleteSelectedUser() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user to delete!");
            return;
        }

        String userId = userTable.getValueAt(row, 0).toString();
        String username = userTable.getValueAt(row, 1).toString();

        if (userId.equals(currentUser.getUserId())) {
            JOptionPane.showMessageDialog(this, "You cannot delete your own account!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete user \"" + username + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                FileService fs = authService.getFileService();
                List<User> users = authService.getAllUsers();
                boolean removed = users.removeIf(u -> u.getUserId().equals(userId));

                if (removed) {
                    List<String> lines = users.stream()
                            .map(User::toLine)
                            .toList();
                    fs.writeAllLines("users.txt", lines);

                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
            }
        }
    }
}
