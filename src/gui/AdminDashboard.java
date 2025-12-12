package gui;

import models.Job;
import models.User;
import services.AuthService;
import services.JobService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final JobService jobService;     // Service to manage jobs
    private final AuthService authService;   // Service for authentication/user data
    private final User currentUser;          // Currently logged-in admin

    private JTable jobTable;                 // Table displaying all jobs

    public AdminDashboard(AuthService authService, User currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.jobService = new JobService(authService.getFileService());

        setTitle("Admin Dashboard");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        getContentPane().setBackground(new Color(245, 247, 250));

        initUI();
    }

    // Initialize main UI components
    private void initUI() {
        add(createTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tabs.setBackground(new Color(220, 220, 220));
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        tabs.add("Job Management", wrapPanel(createJobPanel())); // Jobs tab

        // User management tab with button
        JPanel userPanel = new JPanel(new FlowLayout());
        userPanel.setBackground(Color.WHITE);

        JButton btnViewUsers = new JButton("View All Users");
        btnViewUsers.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnViewUsers.setBackground(new Color(235, 235, 235));
        btnViewUsers.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnViewUsers.setFocusPainted(false);
        btnViewUsers.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnViewUsers.addActionListener(e ->
                new UserListView(authService, currentUser).setVisible(true));

        userPanel.add(btnViewUsers);
        tabs.add("User Management", wrapPanel(userPanel));

        add(tabs, BorderLayout.CENTER);
    }

    // Top bar with dashboard title and logout button
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBar.setBackground(Color.WHITE);

        JLabel title = new JLabel(getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(new Color(40, 40, 40));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setBackground(Color.WHITE);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Color.WHITE);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnLogout.setBackground(new Color(230, 230, 230));
        btnLogout.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btnLogout.addActionListener(e -> {
            new LoginForm(authService).setVisible(true); // Return to login
            dispose();
        });

        right.add(btnLogout);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        return topBar;
    }

    // Panel displaying all jobs with management buttons
    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);

        jobTable = new JTable();
        jobTable.setRowHeight(26);
        jobTable.setFont(new Font("SansSerif", Font.PLAIN, 15));
        jobTable.setShowGrid(true);
        jobTable.setGridColor(new Color(215, 215, 215));
        jobTable.setIntercellSpacing(new Dimension(6, 6));
        jobTable.setBackground(Color.WHITE);
        jobTable.setForeground(Color.BLACK);

        // Custom header styling
        JTableHeaderRenderer headerRenderer = new JTableHeaderRenderer();
        jobTable.getTableHeader().setDefaultRenderer(headerRenderer);
        jobTable.getTableHeader().setPreferredSize(new Dimension(0, 32));

        loadJobs(); // Populate table

        // Buttons for managing selected job
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 12));
        btnPanel.setBackground(Color.WHITE);
        btnPanel.setBorder(BorderFactory.createEmptyBorder(10, 5, 10, 5));

        JButton btnApprove = styledButton("Approve Job");
        JButton btnReject = styledButton("Reject Job");
        JButton btnDelete = styledButton("Delete Job");
        JButton btnViewJob = styledButton("View Job Details");

        btnApprove.addActionListener(e -> updateJobStatus("Approved"));
        btnReject.addActionListener(e -> updateJobStatus("Rejected"));
        btnDelete.addActionListener(e -> deleteSelectedJob());

        // View job details in a dialog
        btnViewJob.addActionListener(e -> {
            int row = jobTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a job first!");
                return;
            }

            String details = "Job ID: " + jobTable.getValueAt(row, 0) +
                    "\nRecruiter ID: " + jobTable.getValueAt(row, 1) +
                    "\nTitle: " + jobTable.getValueAt(row, 2) +
                    "\nDescription: " + jobTable.getValueAt(row, 3) +
                    "\nCompany: " + jobTable.getValueAt(row, 4) +
                    "\nSalary Range: " + jobTable.getValueAt(row, 5) +
                    "\nStatus: " + jobTable.getValueAt(row, 6);

            JOptionPane.showMessageDialog(this, details, "Job Details", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(btnApprove);
        btnPanel.add(btnReject);
        btnPanel.add(btnDelete);
        btnPanel.add(btnViewJob);

        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Utility to create styled buttons
    private JButton styledButton(String text) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 15));
        b.setBackground(new Color(235, 235, 235));
        b.setForeground(Color.BLACK);
        b.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 18));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    // Custom table header renderer
    private static class JTableHeaderRenderer extends DefaultTableCellRenderer {
        public JTableHeaderRenderer() {
            setFont(new Font("SansSerif", Font.BOLD, 15));
            setHorizontalAlignment(CENTER);
            setBackground(new Color(230, 230, 230));
            setForeground(Color.BLACK);
        }
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            return this;
        }
    }

    // Load all jobs into table
    private void loadJobs() {
        List<Job> jobs = jobService.getAllJobs();

        String[] cols = {"ID", "Recruiter", "Title", "Description", "Company", "Salary Range", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int r, int c) { return false; }
        };

        for (Job j : jobs) {
            model.addRow(new Object[]{
                    j.getJobId(),
                    j.getRecruiterId(),
                    j.getTitle(),
                    j.getDescription(),
                    j.getCompanyName(),
                    j.getSalaryRange(),
                    j.getStatus()
            });
        }

        jobTable.setModel(model);
    }

    // Update status of selected job
    private void updateJobStatus(String newStatus) {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select job first!");
            return;
        }

        String jobId = jobTable.getValueAt(row, 0).toString();

        if (jobService.updateStatus(jobId, newStatus)) {
            JOptionPane.showMessageDialog(this, "Status updated → " + newStatus);
            loadJobs();
        }
    }

    // Delete selected job
    private void deleteSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job!");
            return;
        }

        String jobId = jobTable.getValueAt(row, 0).toString();

        if (jobService.deleteJob(jobId)) {
            JOptionPane.showMessageDialog(this, "Job deleted.");
            loadJobs();
        }
    }

    // Wrap panel with padding and background
    private JPanel wrapPanel(JPanel p) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.setBackground(new Color(245, 247, 250));
        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }
}
