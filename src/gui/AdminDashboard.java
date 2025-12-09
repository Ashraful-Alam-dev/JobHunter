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

    private final JobService jobService;
    private final AuthService authService;
    private final User currentUser; // logged-in admin

    private JTable jobTable;

    public AdminDashboard(AuthService authService, User currentUser) {
        this.authService = authService;
        this.currentUser = currentUser;
        this.jobService = new JobService(authService.getFileService());

        setTitle("Admin Dashboard");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        add(createTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 16));

        tabs.add("Job Management", wrapPanel(createJobPanel()));

        JPanel userPanel = new JPanel(new FlowLayout());
        JButton btnViewUsers = new JButton("View All Users");
        btnViewUsers.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnViewUsers.addActionListener(e ->
                new UserListView(authService, currentUser).setVisible(true));
        userPanel.add(btnViewUsers);

        tabs.add("User Management", wrapPanel(userPanel));

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel title = new JLabel(getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnLogout.addActionListener(e -> {
            new LoginForm(authService).setVisible(true);
            dispose();
        });
        right.add(btnLogout);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        return topBar;
    }

    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        jobTable = new JTable();
        jobTable.setRowHeight(24);
        jobTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        ((DefaultTableCellRenderer) jobTable.getTableHeader().getDefaultRenderer())
                .setFont(new Font("SansSerif", Font.BOLD, 16));

        loadJobs();

        JPanel btnPanel = new JPanel();
        JButton btnApprove = new JButton("Approve Job");
        JButton btnReject = new JButton("Reject Job");
        JButton btnDelete = new JButton("Delete Job");
        JButton btnViewJob = new JButton("View Job Details"); // NEW

        btnApprove.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnReject.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnDelete.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnViewJob.setFont(new Font("SansSerif", Font.PLAIN, 16));

        btnApprove.addActionListener(e -> updateJobStatus("Approved"));
        btnReject.addActionListener(e -> updateJobStatus("Rejected"));
        btnDelete.addActionListener(e -> deleteSelectedJob());

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

    private void loadJobs() {
        List<Job> jobs = jobService.getAllJobs();

        String[] cols = {"ID", "Recruiter", "Title", "Description", "Company", "Salary Range", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
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

    private JPanel wrapPanel(JPanel p) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }
}
