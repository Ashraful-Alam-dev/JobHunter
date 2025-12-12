package gui;

import models.Application;
import models.Job;
import models.User;
import services.AuthService;
import services.ApplicationService;
import services.JobService;
import utils.FileService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class RecruiterDashboard extends JFrame {

    private User recruiter;
    private JobService jobService;
    private AuthService authService;
    private ApplicationService applicationService;

    private JTable jobTable;  // Table showing recruiter's jobs
    private JTable appTable;  // Table showing applications to recruiter's jobs

    private final Color HEADER_COLOR = new Color(173, 216, 230); // Soft blue header
    private final Color TABLE_BG = new Color(245, 247, 250);     // Background

    public RecruiterDashboard(User recruiter, AuthService authService) {
        this.recruiter = recruiter;
        this.authService = authService;

        FileService fs = authService.getFileService();
        this.jobService = new JobService(fs);
        this.applicationService = new ApplicationService(fs);

        setTitle("Recruiter Dashboard");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(TABLE_BG);

        initUI();
    }

    // Initialize main UI components
    private void initUI() {
        add(createTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 16));
        tabs.setBackground(HEADER_COLOR);
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        tabs.add("My Jobs", wrapPanel(createJobPanel()));
        tabs.add("Applications", wrapPanel(createApplicationPanel()));

        add(tabs, BorderLayout.CENTER);
    }

    // Top bar with title, profile & logout buttons
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        topBar.setBackground(Color.WHITE);

        JLabel title = new JLabel(getTitle());
        title.setFont(new Font("SansSerif", Font.BOLD, 22));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        left.setBackground(Color.WHITE);
        left.add(title);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        right.setBackground(Color.WHITE);

        JButton btnProfile = new JButton("Manage Profile");
        btnProfile.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnProfile.setBackground(HEADER_COLOR);
        btnProfile.setForeground(Color.BLACK);
        btnProfile.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnProfile.setFocusPainted(false);
        btnProfile.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnProfile.addActionListener(e -> new ProfileForm(recruiter, authService).setVisible(true));
        right.add(btnProfile);

        JButton btnLogout = new JButton("Logout");
        btnLogout.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnLogout.setBackground(HEADER_COLOR);
        btnLogout.setForeground(Color.BLACK);
        btnLogout.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnLogout.setFocusPainted(false);
        btnLogout.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnLogout.addActionListener(e -> {
            new LoginForm(authService).setVisible(true);
            dispose();
        });
        right.add(btnLogout);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        return topBar;
    }

    // Panel showing recruiter's posted jobs
    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TABLE_BG);

        jobTable = new JTable();
        styleTable(jobTable);

        loadJobs(); // Populate jobs table

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(TABLE_BG);

        JButton btnPost = new JButton("Post Job");
        JButton btnUpdate = new JButton("Update Job");
        JButton btnDelete = new JButton("Delete Job");
        JButton btnViewJob = new JButton("View Job Details");

        // Style buttons
        for (JButton b : new JButton[]{btnPost, btnUpdate, btnDelete, btnViewJob}) {
            b.setFont(new Font("SansSerif", Font.PLAIN, 16));
            b.setBackground(HEADER_COLOR);
            b.setForeground(Color.BLACK);
            b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        // Button actions
        btnPost.addActionListener(e -> {
            JobPostForm postForm = new JobPostForm(recruiter, jobService);
            postForm.setVisible(true);
            postForm.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosed(WindowEvent e) {
                    loadJobs(); // Refresh table after posting
                }
            });
        });
        btnUpdate.addActionListener(e -> updateSelectedJob());
        btnDelete.addActionListener(e -> deleteSelectedJob());
        btnViewJob.addActionListener(e -> {
            int row = jobTable.getSelectedRow();
            if (row == -1) {
                JOptionPane.showMessageDialog(this, "Select a job first!");
                return;
            }
            String details = "ID: " + jobTable.getValueAt(row, 0) +
                    "\nTitle: " + jobTable.getValueAt(row, 1) +
                    "\nDescription: " + jobTable.getValueAt(row, 2) +
                    "\nCompany: " + jobTable.getValueAt(row, 3) +
                    "\nSalary: " + jobTable.getValueAt(row, 4) +
                    "\nStatus: " + jobTable.getValueAt(row, 5);
            JOptionPane.showMessageDialog(this, details, "Job Details", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(btnPost);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);
        btnPanel.add(btnViewJob);

        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Populate job table with recruiter's jobs
    private void loadJobs() {
        List<Job> jobs = jobService.getJobsByRecruiter(recruiter.getUserId());

        String[] cols = {"ID", "Title", "Description", "Company", "Salary Range", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Job j : jobs) {
            model.addRow(new Object[]{
                    j.getJobId(),
                    j.getTitle(),
                    j.getDescription(),
                    j.getCompanyName(),
                    j.getSalaryRange(),
                    j.getStatus()
            });
        }

        jobTable.setModel(model);
    }

    // Update selected job
    private void updateSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) return;

        new JobUpdateForm(
                jobTable.getValueAt(row, 0).toString(),
                jobTable.getValueAt(row, 1).toString(),
                jobTable.getValueAt(row, 2).toString(),
                jobTable.getValueAt(row, 3).toString(),
                jobTable.getValueAt(row, 4).toString(),
                jobService
        ).setVisible(true);
    }

    // Delete selected job
    private void deleteSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) return;

        String jobId = jobTable.getValueAt(row, 0).toString();

        if (jobService.deleteJob(jobId)) {
            JOptionPane.showMessageDialog(this, "Job deleted!");
            loadJobs();
            loadApplications();
        }
    }

    // Panel showing applications for recruiter's jobs
    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TABLE_BG);

        appTable = new JTable();
        styleTable(appTable);

        loadApplications();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(TABLE_BG);

        JButton btnAccept = new JButton("Accept");
        JButton btnCancel = new JButton("Cancel");
        JButton btnViewApp = new JButton("View Applicant/Application Details");

        // Style buttons
        for (JButton b : new JButton[]{btnAccept, btnCancel, btnViewApp}) {
            b.setFont(new Font("SansSerif", Font.PLAIN, 16));
            b.setBackground(HEADER_COLOR);
            b.setForeground(Color.BLACK);
            b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        btnAccept.addActionListener(e -> updateApplicationStatus("Accepted"));
        btnCancel.addActionListener(e -> updateApplicationStatus("Cancelled"));

        btnViewApp.addActionListener(e -> {
            int row = appTable.getSelectedRow();
            if (row == -1) return;

            String appId = appTable.getValueAt(row, 0).toString();
            String jobTitle = appTable.getValueAt(row, 1).toString();
            String applicantName = appTable.getValueAt(row, 2).toString();
            String status = appTable.getValueAt(row, 3).toString();

            User applicant = authService.getAllUsers().stream()
                    .filter(u -> u.getName().equals(applicantName))
                    .findFirst()
                    .orElse(null);

            String details = "Application ID: " + appId +
                    "\nJob Title: " + jobTitle +
                    "\nStatus: " + status;

            if (applicant != null) {
                details += "\n\nApplicant Details:\n" +
                        "Name: " + applicant.getName() +
                        "\nEmail: " + applicant.getEmail() +
                        "\nContact: " + applicant.getContact() +
                        "\nDescription: " + applicant.getDescription();
            }

            JOptionPane.showMessageDialog(this, details, "Application & Applicant Details", JOptionPane.INFORMATION_MESSAGE);
        });

        btnPanel.add(btnAccept);
        btnPanel.add(btnCancel);
        btnPanel.add(btnViewApp);

        panel.add(new JScrollPane(appTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Load applications for all recruiter's jobs
    private void loadApplications() {
        List<Job> jobs = jobService.getJobsByRecruiter(recruiter.getUserId());
        List<Application> allApps = applicationService.getAllApplications();

        String[] cols = {"Application ID", "Job Title", "Applicant Name", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            public boolean isCellEditable(int row, int column) { return false; }
        };

        for (Job j : jobs) {
            for (Application a : allApps) {
                if (a.getJobId().equals(j.getJobId())) {
                    User applicant = authService.getAllUsers().stream()
                            .filter(u -> u.getUserId().equals(a.getApplicantId()))
                            .findFirst()
                            .orElse(null);

                    String applicantName = applicant != null ? applicant.getName() : a.getApplicantId();

                    model.addRow(new Object[]{
                            a.getApplicationId(),
                            j.getTitle(),
                            applicantName,
                            a.getStatus()
                    });
                }
            }
        }

        appTable.setModel(model);
    }

    // Update status of selected application
    private void updateApplicationStatus(String newStatus) {
        int row = appTable.getSelectedRow();
        if (row == -1) return;

        String appId = appTable.getValueAt(row, 0).toString();

        if (applicationService.updateStatus(appId, newStatus)) {
            JOptionPane.showMessageDialog(this, "Application status updated → " + newStatus);
            loadApplications();
        }
    }

    // Utility: Wrap panel with padding
    private JPanel wrapPanel(JPanel p) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.setBackground(TABLE_BG);
        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }

    // Utility: Apply consistent styling to tables
    private void styleTable(JTable table) {
        table.setRowHeight(26);
        table.setFont(new Font("SansSerif", Font.PLAIN, 16));
        table.setShowGrid(true);
        table.setGridColor(new Color(220, 220, 220));
        table.setBackground(Color.WHITE);
        table.setSelectionBackground(new Color(220, 240, 255));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        table.getTableHeader().setBackground(HEADER_COLOR);
        table.getTableHeader().setForeground(Color.BLACK);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));
    }
}
