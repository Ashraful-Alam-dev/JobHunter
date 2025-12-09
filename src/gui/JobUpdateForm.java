package gui;

import services.JobService;

import javax.swing.*;
import java.awt.*;

public class JobUpdateForm extends JFrame {

    private JobService jobService;

    private String jobId;
    private JTextField titleField;
    private JTextArea descArea;
    private JTextField companyField;
    private JTextField salaryField;

    public JobUpdateForm(String jobId, String oldTitle, String oldDesc,
                         String oldCompany, String oldSalary, JobService jobService) {

        this.jobId = jobId;
        this.jobService = jobService;

        setTitle("Update Job");
        setSize(400, 450);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        initUI(oldTitle, oldDesc, oldCompany, oldSalary);
    }

    private void initUI(String oldTitle, String oldDesc, String oldCompany, String oldSalary) {

        JPanel panel = new JPanel(new GridLayout(8, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));

        panel.add(new JLabel("Job Title:"));
        titleField = new JTextField(oldTitle);
        panel.add(titleField);

        panel.add(new JLabel("Job Description:"));
        descArea = new JTextArea(oldDesc);
        panel.add(new JScrollPane(descArea));

        panel.add(new JLabel("Company Name (optional):"));
        companyField = new JTextField(oldCompany);
        panel.add(companyField);

        panel.add(new JLabel("Salary Range (required):"));
        salaryField = new JTextField(oldSalary);
        panel.add(salaryField);

        JButton btnSave = new JButton("Save Changes");
        btnSave.addActionListener(e -> {
            if (titleField.getText().trim().isEmpty() ||
                    descArea.getText().trim().isEmpty() ||
                    salaryField.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(this, "Title, Description, and Salary are required!");
                return;
            }

            if (jobService.updateJob(jobId,
                    titleField.getText().trim(),
                    descArea.getText().trim(),
                    companyField.getText().trim(),
                    salaryField.getText().trim())) {

                JOptionPane.showMessageDialog(this, "Job updated! Requires re-approval.");
                dispose();
            }
        });

        panel.add(btnSave);
        add(panel, BorderLayout.CENTER);
    }
}
