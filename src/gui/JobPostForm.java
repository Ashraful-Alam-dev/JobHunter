package gui;

import models.User;
import services.JobService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JobPostForm extends JFrame {

    private final User recruiter;
    private final JobService jobService;

    private JTextField titleField;
    private JTextArea descArea;
    private JTextField companyField;
    private JTextField salaryField;

    public JobPostForm(User recruiter, JobService jobService) {
        this.recruiter = recruiter;
        this.jobService = jobService;

        setTitle("Post New Job");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 247, 250));

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Job Title:");
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblTitle);

        titleField = new JTextField();
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        titleField.setBackground(new Color(250, 250, 250));
        panel.add(titleField);

        JLabel lblDesc = new JLabel("Job Description:");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblDesc);

        descArea = new JTextArea(5, 20);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descArea.setBackground(new Color(250, 250, 250));
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll);

        JLabel lblCompany = new JLabel("Company Name (optional):");
        lblCompany.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblCompany);

        companyField = new JTextField();
        companyField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        companyField.setBackground(new Color(250, 250, 250));
        panel.add(companyField);

        JLabel lblSalary = new JLabel("Salary Range (required):");
        lblSalary.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblSalary);

        salaryField = new JTextField();
        salaryField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        salaryField.setBackground(new Color(250, 250, 250));
        panel.add(salaryField);

        JButton btnPost = new JButton("Post Job");
        btnPost.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnPost.setBackground(new Color(173, 216, 230));
        btnPost.setForeground(Color.BLACK);
        btnPost.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnPost.setFocusPainted(false);
        btnPost.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnPost.addActionListener(this::postJob);
        panel.add(btnPost);

        add(panel, BorderLayout.CENTER);
    }

    private void postJob(ActionEvent e) {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();
        String company = companyField.getText().trim();
        String salary = salaryField.getText().trim();

        if (title.isEmpty() || desc.isEmpty() || salary.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Title, Description, and Salary are required!");
            return;
        }

        jobService.postJob(recruiter.getUserId(), title, desc, company, salary);

        JOptionPane.showMessageDialog(this, "Job posted! Pending approval.");
        dispose();
    }
}
