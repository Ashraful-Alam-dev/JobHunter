package gui;

import models.Application;
import models.Job;
import models.User;
import services.AuthService;
import services.ApplicationService;
import services.JobService;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
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
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        initUI();
    }

    private void initUI() {
        add(createTopBar(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(new Font("SansSerif", Font.PLAIN, 16));

        JPanel jobPanel = new JPanel(new BorderLayout());
        jobTable = new JTable();
        jobTable.setRowHeight(24);
        jobTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        ((DefaultTableCellRenderer) jobTable.getTableHeader().getDefaultRenderer())
                .setFont(new Font("SansSerif", Font.BOLD, 16));
        loadJobs();

        JButton btnApply = new JButton("Apply for Selected Job");
        btnApply.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnApply.addActionListener(e -> applyForJob());

        JButton btnViewRecruiter = new JButton("View Recruiter/Job Details");
        btnViewRecruiter.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnViewRecruiter.addActionListener(e -> viewSelectedJobOrRecruiter());

        JPanel btnPanel = new JPanel();
        btnPanel.add(btnApply);
        btnPanel.add(btnViewRecruiter);

        jobPanel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        jobPanel.add(btnPanel, BorderLayout.SOUTH);
        tabs.add("Jobs", jobPanel);

        JPanel appPanel = new JPanel(new BorderLayout());
        applicationTable = new JTable();
        applicationTable.setRowHeight(24);
        applicationTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        ((DefaultTableCellRenderer) applicationTable.getTableHeader().getDefaultRenderer())
                .setFont(new Font("SansSerif", Font.BOLD, 16));
        loadApplications();

        JButton btnViewApp = new JButton("View Application / Job Details");
        btnViewApp.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnViewApp.addActionListener(e -> viewSelectedApplication());

        JPanel appBtnPanel = new JPanel();
        appBtnPanel.add(btnViewApp);

        appPanel.add(new JScrollPane(applicationTable), BorderLayout.CENTER);
        appPanel.add(appBtnPanel, BorderLayout.SOUTH);
        tabs.add("My Applications", appPanel);

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

        JButton btnProfile = new JButton("Manage Profile");
        btnProfile.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnProfile.addActionListener(e -> new ProfileForm(applicant, authService).setVisible(true));
        right.add(btnProfile);

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

    private void loadJobs() {
        List<Job> jobs = jobService.getApprovedJobs();
        String[] cols = {"Job ID", "Title", "Description", "Company", "Salary Range", "Recruiter ID"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Job j : jobs) {
            model.addRow(new Object[]{
                    j.getJobId(),
                    j.getTitle(),
                    j.getDescription(),
                    j.getCompanyName(),
                    j.getSalaryRange(),
                    j.getRecruiterId()
            });
        }
        jobTable.setModel(model);
    }

    private void loadApplications() {
        List<Application> apps = applicationService.getApplicationsByApplicant(applicant.getUserId());
        String[] cols = {"Application ID", "Job Title", "Status"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        for (Application a : apps) {
            Job job = jobService.getAllJobs().stream()
                    .filter(j -> j.getJobId().equals(a.getJobId()))
                    .findFirst()
                    .orElse(null);
            String jobTitle = (job != null) ? job.getTitle() : a.getJobId();
            model.addRow(new Object[]{a.getApplicationId(), jobTitle, a.getStatus()});
        }
        applicationTable.setModel(model);
    }

    private void applyForJob() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job to apply!");
            return;
        }

        String jobId = jobTable.getValueAt(row, 0).toString();
        if (applicationService.applyForJob(jobId, applicant.getUserId())) {
            JOptionPane.showMessageDialog(this, "Applied successfully!");
            loadApplications();
        }
    }

    private void viewSelectedJobOrRecruiter() {
        int row = jobTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a job first!");
            return;
        }

        String jobId = jobTable.getValueAt(row, 0).toString();
        Job job = jobService.getAllJobs().stream()
                .filter(j -> j.getJobId().equals(jobId))
                .findFirst()
                .orElse(null);

        if (job != null) {
            User recruiter = authService.getAllUsers().stream()
                    .filter(u -> u.getUserId().equals(job.getRecruiterId()))
                    .findFirst()
                    .orElse(null);

            JOptionPane.showMessageDialog(this,
                    "Job Title: " + job.getTitle() +
                            "\nDescription: " + job.getDescription() +
                            "\nCompany: " + job.getCompanyName() +
                            "\nSalary: " + job.getSalaryRange() +
                            "\nStatus: " + job.getStatus() +
                            (recruiter != null ?
                                    "\n\nRecruiter Name: " + recruiter.getName() +
                                            "\nEmail: " + recruiter.getEmail() +
                                            "\nContact: " + recruiter.getContact()
                                    : ""),
                    "Job & Recruiter Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewSelectedApplication() {
        int row = applicationTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first!");
            return;
        }

        String appId = applicationTable.getValueAt(row, 0).toString();
        Application a = applicationService.getAllApplications().stream()
                .filter(app -> app.getApplicationId().equals(appId))
                .findFirst()
                .orElse(null);

        if (a != null) {
            Job job = jobService.getAllJobs().stream()
                    .filter(j -> j.getJobId().equals(a.getJobId()))
                    .findFirst()
                    .orElse(null);

            JOptionPane.showMessageDialog(this,
                    "Application ID: " + a.getApplicationId() +
                            "\nStatus: " + a.getStatus() +
                            (job != null ?
                                    "\n\nJob Title: " + job.getTitle() +
                                            "\nDescription: " + job.getDescription() +
                                            "\nCompany: " + job.getCompanyName() +
                                            "\nSalary: " + job.getSalaryRange()
                                    : ""),
                    "Application & Job Details",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
