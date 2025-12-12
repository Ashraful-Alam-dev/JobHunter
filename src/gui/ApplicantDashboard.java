package gui;

import models.Application;
import models.Job;
import models.User;
import services.AuthService;
import services.ApplicationService;
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

    private JTable jobTable;   // Table showing available jobs
    private JTable appTable;   // Table showing applicant's applications

    // Theme colors
    private final Color HEADER_COLOR = new Color(212, 161, 171);
    private final Color TABLE_BG = new Color(245, 247, 250);

    public ApplicantDashboard(User applicant, AuthService authService) {
        this.applicant = applicant;
        this.authService = authService;
        this.jobService = new JobService(authService.getFileService());
        this.applicationService = new ApplicationService(authService.getFileService());

        setTitle("Applicant Dashboard");
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
        tabs.setUI(new javax.swing.plaf.basic.BasicTabbedPaneUI() {
            @Override
            protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                              int x, int y, int w, int h, boolean isSelected) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(isSelected ? new Color(225, 185, 190) : HEADER_COLOR);
                g2.fillRect(x, y, w, h);
            }
        });
        tabs.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));

        tabs.add("Jobs", wrapPanel(createJobPanel()));
        tabs.add("My Applications", wrapPanel(createApplicationPanel()));

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
        btnProfile.addActionListener(e -> new ProfileForm(applicant, authService).setVisible(true));
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

    // Panel listing all approved jobs
    private JPanel createJobPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TABLE_BG);

        jobTable = new JTable();
        jobTable.setRowHeight(26);
        jobTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        jobTable.setShowGrid(true);
        jobTable.setGridColor(new Color(220, 220, 220));
        jobTable.setBackground(Color.WHITE);
        jobTable.setSelectionBackground(new Color(220, 240, 255));

        jobTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        jobTable.getTableHeader().setBackground(HEADER_COLOR);
        jobTable.getTableHeader().setForeground(Color.BLACK);
        jobTable.getTableHeader().setPreferredSize(new Dimension(0, 32));

        loadJobs(); // Populate table

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(TABLE_BG);

        JButton btnApply = new JButton("Apply for Selected Job");
        JButton btnViewJob = new JButton("View Recruiter/Job Details");

        // Style buttons
        for (JButton b : new JButton[]{btnApply, btnViewJob}) {
            b.setFont(new Font("SansSerif", Font.PLAIN, 16));
            b.setBackground(HEADER_COLOR);
            b.setForeground(Color.BLACK);
            b.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
            b.setFocusPainted(false);
            b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }

        btnApply.addActionListener(e -> applyForJob());
        btnViewJob.addActionListener(e -> viewSelectedJobOrRecruiter());

        btnPanel.add(btnApply);
        btnPanel.add(btnViewJob);

        panel.add(new JScrollPane(jobTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Panel showing applicant's submitted applications
    private JPanel createApplicationPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(TABLE_BG);

        appTable = new JTable();
        appTable.setRowHeight(26);
        appTable.setFont(new Font("SansSerif", Font.PLAIN, 16));
        appTable.setShowGrid(true);
        appTable.setGridColor(new Color(220, 220, 220));
        appTable.setBackground(Color.WHITE);
        appTable.setSelectionBackground(new Color(220, 240, 255));

        appTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 16));
        appTable.getTableHeader().setBackground(HEADER_COLOR);
        appTable.getTableHeader().setForeground(Color.BLACK);
        appTable.getTableHeader().setPreferredSize(new Dimension(0, 32));

        loadApplications();

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 10));
        btnPanel.setBackground(TABLE_BG);

        JButton btnViewApp = new JButton("View Application / Job Details");
        btnViewApp.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnViewApp.setBackground(HEADER_COLOR);
        btnViewApp.setForeground(Color.BLACK);
        btnViewApp.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnViewApp.setFocusPainted(false);
        btnViewApp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnViewApp.addActionListener(e -> viewSelectedApplication());

        btnPanel.add(btnViewApp);

        panel.add(new JScrollPane(appTable), BorderLayout.CENTER);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    // Load all approved jobs into table
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

    // Load all applications submitted by this applicant
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
        appTable.setModel(model);
    }

    // Apply for selected job
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

    // View details of selected job + recruiter
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

    // View details of selected application + associated job
    private void viewSelectedApplication() {
        int row = appTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select an application first!");
            return;
        }

        String appId = appTable.getValueAt(row, 0).toString();
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

    // Wrap panel to add consistent padding
    private JPanel wrapPanel(JPanel p) {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        wrapper.setBackground(TABLE_BG);
        wrapper.add(p, BorderLayout.CENTER);
        return wrapper;
    }
}
