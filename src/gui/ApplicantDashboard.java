package gui;

import models.Application;
import models.Job;
import models.User;
import services.ApplicationService;
import services.AuthService;
import services.JobService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class ApplicantDashboard extends JFrame {

    private final AuthService authService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final User applicant;

    private JTable jobTable;
    private JTable applicationTable;

    public ApplicantDashboard(User applicant, AuthService authService) {
        this.applicant = applicant;
        this.authService = authService;
        this.jobService = new JobService(authService.getFileService());
        this.applicationService = new ApplicationService(authService.getFileService());

        setTitle("Applicant Dashboard");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        JTabbedPane tabs = new JTabbedPane();

        // Profile Tab
        JButton btnProfile = new JButton("Manage Profile");
        btnProfile.addActionListener(e -> new ProfileForm(applicant, authService).setVisible(true));
        JPanel profilePanel = new JPanel();
        profilePanel.add(btnProfile);
        tabs.add("Profile", profilePanel);

        // View & Apply Jobs Tab
        JPanel jobPanel = new JPanel(new BorderLayout());
        jobTable = new JTable();
        loadJobs();

        JButton btnApply = new JButton("Apply for Selected Job");
        btnApply.addActionListener(e -> applyForJob());

        JButton btnViewRecruiter = new JButton("View Recruiter Profile");
        btnViewRecruiter.addActionListener(e -> viewRecruiterProfile());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnApply);
        btnPanel.add(btnViewRecruiter);

        jobPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        jobPanel.add(btnPanel, BorderLayout.SOUTH);
        tabs.add("Jobs", jobPanel);

        // Check Application Status
        JPanel appPanel = new JPanel(new BorderLayout());
        applicationTable = new JTable();
        loadApplications();
        JButton btnRefresh = new JButton("Refresh Status");
        btnRefresh.addActionListener(e -> loadApplications());
        JPanel refreshPanel = new JPanel();
        refreshPanel.add(btnRefresh);
        appPanel.add(new JScrollPane(applicationTable), BorderLayout.CENTER);
        appPanel.add(refreshPanel, BorderLayout.SOUTH);
        tabs.add("My Applications", appPanel);

        add(tabs);
    }

    private void loadJobs() {
        List<Job> jobs = jobService.getApprovedJobs();
        String[] cols = {"Job ID", "Title", "Description", "Recruiter ID"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Job j : jobs) {
            model.addRow(new Object[]{j.getJobId(), j.getTitle(), j.getDescription(), j.getRecruiterId()});
        }
        jobTable.setModel(model);
    }

    private void applyForJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job to apply!");
            return;
        }
        String jobId = jobTable.getValueAt(row, 0).toString();

        boolean success = applicationService.applyForJob(jobId, applicant.getUserId());
        if (success) {
            JOptionPane.showMessageDialog(this, "Applied successfully!");
            loadApplications();
        }
    }

    private void viewRecruiterProfile() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job first!");
            return;
        }
        String recruiterId = jobTable.getValueAt(row, 3).toString();
        User recruiter = authService.getAllUsers().stream()
                .filter(u -> u.getUserId().equals(recruiterId))
                .findFirst()
                .orElse(null);

        if (recruiter != null) {
            JOptionPane.showMessageDialog(this,
                    "Name: " + recruiter.getName() +
                            "\nEmail: " + recruiter.getEmail() +
                            "\nContact: " + recruiter.getContact() +
                            "\nDescription: " + recruiter.getDescription(),
                    "Recruiter Profile",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void loadApplications() {
        List<Application> apps = applicationService.getApplicationsByApplicant(applicant.getUserId());
        String[] cols = {"Application ID", "Job ID", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0);
        for (Application a : apps) {
            model.addRow(new Object[]{a.getApplicationId(), a.getJobId(), a.getStatus()});
        }
        applicationTable.setModel(model);
    }
}
