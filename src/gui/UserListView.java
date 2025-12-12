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

    // Theme colors
    private final Color ACCENT = new Color(52, 152, 219);  // bright blue
    private final Color BACKGROUND = new Color(245, 247, 250);
    private final Color PANEL_BG = Color.WHITE;

    public UserListView(AuthService authService, User currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;

        setTitle("User Management - JobHunter");
        setSize(820, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND);

        initUI();      // setup UI components
        loadUsers();   // load user data into table
    }

    private void initUI() {
        // main panel with padding and border layout
        JPanel panel = new JPanel(new BorderLayout(12, 12));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(PANEL_BG);

        // configure table to show users
        userTable = new JTable();
        userTable.setFont(new Font("SansSerif", Font.PLAIN, 15));
        userTable.setRowHeight(28);
        userTable.setShowGrid(true);
        userTable.setGridColor(new Color(220, 220, 220));
        userTable.setBackground(Color.WHITE);
        userTable.setSelectionBackground(new Color(220, 240, 255));
        userTable.setSelectionForeground(Color.BLACK);

        // table header styling
        userTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        userTable.getTableHeader().setBackground(new Color(245, 245, 245));
        userTable.getTableHeader().setForeground(new Color(50, 50, 50));

        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.getViewport().setBackground(Color.WHITE);

        panel.add(scrollPane, BorderLayout.CENTER);

        // bottom button panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        btnPanel.setBackground(PANEL_BG);

        JButton btnRefresh = createSoftButton("Refresh");
        JButton btnDelete = createSoftButton("Delete Selected User");
        JButton btnViewUser = createSoftButton("View User Details");

        btnRefresh.setBackground(ACCENT);
        btnRefresh.setForeground(Color.WHITE);

        // button actions
        btnRefresh.addActionListener(e -> loadUsers());       // reload table
        btnDelete.addActionListener(e -> deleteSelectedUser());// delete selected user
        btnViewUser.addActionListener(e -> showUserDetails());// show details popup

        btnPanel.add(btnRefresh);
        btnPanel.add(btnDelete);
        btnPanel.add(btnViewUser);

        panel.add(btnPanel, BorderLayout.SOUTH);
        add(panel);
    }

    private JButton createSoftButton(String text) {
        // utility to create simple styled buttons with hover effect
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.PLAIN, 15));
        btn.setBackground(new Color(230, 230, 230));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        btn.setFocusPainted(false);

        // hover effect
        btn.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(210, 210, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                btn.setBackground(new Color(230, 230, 230));
            }
        });

        return btn;
    }

    private void showUserDetails() {
        int row = userTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a user!");
            return;
        }

        // build detail message from table row
        String details =
                "User ID: " + userTable.getValueAt(row, 0) +
                        "\nName: " + userTable.getValueAt(row, 1) +
                        "\nUsername: " + userTable.getValueAt(row, 2) +
                        "\nEmail: " + userTable.getValueAt(row, 3) +
                        "\nRole: " + userTable.getValueAt(row, 4) +
                        "\nContact: " + userTable.getValueAt(row, 5);

        JOptionPane.showMessageDialog(this, details, "User Details", JOptionPane.INFORMATION_MESSAGE);
    }

    private void loadUsers() {
        // fetch all users and populate table
        List<User> users = authService.getAllUsers();

        String[] columns = {"User ID", "Name", "Username", "Email", "Role", "Contact"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            public boolean isCellEditable(int row, int column) { return false; } // table is read-only
        };

        for (User u : users) {
            model.addRow(new Object[]{
                    u.getUserId(),
                    u.getName(),
                    u.getUsername(),
                    u.getEmail(),
                    u.getRole(),
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
                "Are you sure you want to delete \"" + username + "\"?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                FileService fs = authService.getFileService();
                List<User> users = authService.getAllUsers();

                // remove user from list
                boolean removed = users.removeIf(u -> u.getUserId().equals(userId));

                if (removed) {
                    // rewrite file without deleted user
                    List<String> lines = users.stream()
                            .map(User::toLine)
                            .toList();
                    fs.writeAllLines("users.txt", lines);

                    JOptionPane.showMessageDialog(this, "User deleted successfully!");
                    loadUsers(); // refresh table
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete user!");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error deleting user: " + ex.getMessage());
            }
        }
    }
}
