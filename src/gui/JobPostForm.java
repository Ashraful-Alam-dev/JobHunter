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

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panel.add(new JLabel("Job Title:"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Job Description:"));
        descArea = new JTextArea(5, 20);
        panel.add(new JScrollPane(descArea));

        panel.add(new JLabel("Company Name (optional):"));
        companyField = new JTextField();
        panel.add(companyField);

        panel.add(new JLabel("Salary Range (required):"));
        salaryField = new JTextField();
        panel.add(salaryField);

        JButton btnPost = new JButton("Post Job");
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
