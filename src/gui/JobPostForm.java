package gui;

import models.User;
import services.JobService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class JobPostForm extends JFrame {

    private User recruiter;
    private JobService jobService;

    private JTextField titleField;
    private JTextArea descArea;

    public JobPostForm(User recruiter, JobService jobService) {
        this.recruiter = recruiter;
        this.jobService = jobService;

        setTitle("Post New Job");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI();
    }

    private void initUI() {
        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panel.add(new JLabel("Job Title:"));
        titleField = new JTextField();
        panel.add(titleField);

        panel.add(new JLabel("Job Description:"));
        descArea = new JTextArea(5, 20);
        panel.add(new JScrollPane(descArea));

        JButton btnPost = new JButton("Post Job");
        btnPost.addActionListener(this::postJob);

        panel.add(btnPost);

        add(panel, BorderLayout.CENTER);
    }

    private void postJob(ActionEvent e) {
        String title = titleField.getText().trim();
        String desc = descArea.getText().trim();

        if (title.isEmpty() || desc.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Fields cannot be empty!");
            return;
        }

        jobService.postJob(recruiter.getUserId(), title, desc);

        JOptionPane.showMessageDialog(this, "Job posted! Pending approval.");
        dispose();
    }
}
