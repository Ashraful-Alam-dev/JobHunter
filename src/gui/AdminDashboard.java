package gui;

import models.Job;
import services.AuthService;
import services.JobService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final JobService jobService;
    private final AuthService authService;

    private JTable jobTable;

    // Constructor accepts existing AuthService
    public AdminDashboard(AuthService authService) {
        this.authService = authService;
        this.jobService = new JobService(authService.getFileService());

        setTitle("Admin Dashboard");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        // Jobs Tab
        tabs.add("Job Management", createJobPanel());

        // User List Tab -> Open UserListView on button click
        JPanel userPanel = new JPanel(new FlowLayout());
        JButton btnViewUsers = new JButton("View All Users");
        btnViewUsers.addActionListener(e -> {
            // Only admins can see this
            new UserListView(authService).setVisible(true);
        });
        userPanel.add(btnViewUsers);

        tabs.add("User Management", userPanel);

        add(tabs);
    }

    // -----------------------------------------
    // JOB PANEL
    // -----------------------------------------
    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        jobTable = new JTable();
        loadJobs();

        JPanel btnPanel = new JPanel();

        JButton btnVerify = new JButton("Verify Job");
        JButton btnApprove = new JButton("Approve Job");
        JButton btnDelete = new JButton("Delete Job");

        btnVerify.addActionListener(e -> updateJobStatus("Verified"));
        btnApprove.addActionListener(e -> updateJobStatus("Approved"));
        btnDelete.addActionListener(e -> deleteSelectedJob());

        btnPanel.add(btnVerify);
        btnPanel.add(btnApprove);
        btnPanel.add(btnDelete);

        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadJobs() {
        List<Job> jobs = jobService.getAllJobs();

        String[] cols = {"ID", "Recruiter", "Title", "Description", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Job j : jobs) {
            model.addRow(new Object[]{
                    j.getJobId(),
                    j.getRecruiterId(),
                    j.getTitle(),
                    j.getDescription(),
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
            JOptionPane.showMessageDialog(this, "Status updated -> " + newStatus);
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
}
