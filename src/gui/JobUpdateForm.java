package gui;

import services.JobService;

import javax.swing.*;
import java.awt.*;

public class JobUpdateForm extends JFrame {

    private JobService jobService;

    private String jobId;
    private JTextField titleField;
    private JTextArea descArea;

    public JobUpdateForm(String jobId, String oldTitle, String oldDesc, JobService jobService) {
        this.jobId = jobId;
        this.jobService = jobService;

        setTitle("Update Job");
        setSize(400, 350);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI(oldTitle, oldDesc);
    }

    private void initUI(String oldTitle, String oldDesc) {

        JPanel panel = new JPanel(new GridLayout(5, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panel.add(new JLabel("Job Title:"));
        titleField = new JTextField(oldTitle);
        panel.add(titleField);

        panel.add(new JLabel("Job Description:"));
        descArea = new JTextArea(oldDesc);
        panel.add(new JScrollPane(descArea));

        JButton btnSave = new JButton("Save Changes");
        btnSave.addActionListener(e -> {
            if (jobService.updateJob(jobId, titleField.getText(), descArea.getText())) {
                JOptionPane.showMessageDialog(this, "Job updated! Requires re-approval.");
                dispose();
            }
        });

        panel.add(btnSave);
        add(panel, BorderLayout.CENTER);
    }
}
