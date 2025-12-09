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
import java.util.List;

public class RecruiterDashboard extends JFrame {

    private User recruiter;
    private JobService jobService;
    private AuthService authService;
    private ApplicationService applicationService;

    private JTable jobTable;
    private JTable appTable;

    public RecruiterDashboard(User recruiter, AuthService authService) {
        this.recruiter = recruiter;
        this.authService = authService;

        FileService fs = authService.getFileService();
        this.jobService = new JobService(fs);
        this.applicationService = new ApplicationService(fs);

        setTitle("Recruiter Dashboard");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        // Jobs tab
        tabs.add("My Jobs", createJobPanel());

        // Applications tab
        tabs.add("Applications", createApplicationPanel());

        add(tabs);
    }

    // -----------------------------
    // JOB PANEL
    // -----------------------------
    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        jobTable = new JTable();
        loadJobs();

        JPanel btnPanel = new JPanel();
        JButton btnPost = new JButton("Post Job");
        JButton btnUpdate = new JButton("Update Job");
        JButton btnDelete = new JButton("Delete Job");

        btnPost.addActionListener(e -> new JobPostForm(recruiter, jobService).setVisible(true));
        btnUpdate.addActionListener(e -> updateSelectedJob());
        btnDelete.addActionListener(e -> deleteSelectedJob());

        btnPanel.add(btnPost);
        btnPanel.add(btnUpdate);
        btnPanel.add(btnDelete);

        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadJobs() {
        List<Job> jobs = jobService.getJobsByRecruiter(recruiter.getUserId());

        String[] cols = {"ID", "Title", "Description", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

        for (Job j : jobs) {
            model.addRow(new Object[]{
                    j.getJobId(),
                    j.getTitle(),
                    j.getDescription(),
                    j.getStatus()
            });
        }

        jobTable.setModel(model);
    }

    private void updateSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job!");
            return;
        }

        String jobId = jobTable.getValueAt(row, 0).toString();
        String title = jobTable.getValueAt(row, 1).toString();
        String desc = jobTable.getValueAt(row, 2).toString();

        new JobUpdateForm(jobId, title, desc, jobService).setVisible(true);
    }

    private void deleteSelectedJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job!");
            return;
        }

        String jobId = jobTable.getValueAt(row, 0).toString();

        if (jobService.deleteJob(jobId)) {
            JOptionPane.showMessageDialog(this, "Job deleted!");
            loadJobs();
            loadApplications(); // refresh applications too
        }
    }

    // -----------------------------
    // APPLICATION PANEL
    // -----------------------------
    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        appTable = new JTable();
        loadApplications();

        JPanel btnPanel = new JPanel();
        JButton btnViewProfile = new JButton("View Applicant Profile");
        JButton btnAccept = new JButton("Accept");
        JButton btnCancel = new JButton("Cancel");

        btnViewProfile.addActionListener(e -> viewApplicantProfile());
        btnAccept.addActionListener(e -> updateApplicationStatus("Accepted"));
        btnCancel.addActionListener(e -> updateApplicationStatus("Cancelled"));

        btnPanel.add(btnViewProfile);
        btnPanel.add(btnAccept);
        btnPanel.add(btnCancel);

        panel.add(new JScrollPane(appTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadApplications() {
        List<Job> jobs = jobService.getJobsByRecruiter(recruiter.getUserId());
        List<Application> allApps = applicationService.getAllApplications();

        String[] cols = {"Application ID", "Job Title", "Applicant Name", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);

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

    private void viewApplicantProfile() {
        int row = appTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first!");
            return;
        }

        String applicantName = appTable.getValueAt(row, 2).toString();
        User applicant = authService.getAllUsers().stream()
                .filter(u -> u.getName().equals(applicantName))
                .findFirst()
                .orElse(null);

        if (applicant != null) {
            JOptionPane.showMessageDialog(this,
                    "Name: " + applicant.getName() +
                            "\nEmail: " + applicant.getEmail() +
                            "\nContact: " + applicant.getContact() +
                            "\nDescription: " + applicant.getDescription(),
                    "Applicant Profile",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void updateApplicationStatus(String newStatus) {
        int row = appTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first!");
            return;
        }

        String appId = appTable.getValueAt(row, 0).toString();

        if (applicationService.updateStatus(appId, newStatus)) {
            JOptionPane.showMessageDialog(this, "Application status updated -> " + newStatus);
            loadApplications();
        }
    }
}
