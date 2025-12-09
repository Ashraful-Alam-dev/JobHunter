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
        getContentPane().setBackground(new Color(245, 247, 250));

        initUI(oldTitle, oldDesc, oldCompany, oldSalary);
    }

    private void initUI(String oldTitle, String oldDesc, String oldCompany, String oldSalary) {

        JPanel panel = new JPanel(new GridLayout(9, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(Color.WHITE);

        JLabel lblTitle = new JLabel("Job Title:");
        lblTitle.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblTitle);

        titleField = new JTextField(oldTitle);
        titleField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        titleField.setBackground(new Color(250, 250, 250));
        panel.add(titleField);

        JLabel lblDesc = new JLabel("Job Description:");
        lblDesc.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblDesc);

        descArea = new JTextArea(oldDesc);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 15));
        descArea.setBackground(new Color(250, 250, 250));
        JScrollPane descScroll = new JScrollPane(descArea);
        panel.add(descScroll);

        JLabel lblCompany = new JLabel("Company Name (optional):");
        lblCompany.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblCompany);

        companyField = new JTextField(oldCompany);
        companyField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        companyField.setBackground(new Color(250, 250, 250));
        panel.add(companyField);

        JLabel lblSalary = new JLabel("Salary Range (required):");
        lblSalary.setFont(new Font("SansSerif", Font.PLAIN, 15));
        panel.add(lblSalary);

        salaryField = new JTextField(oldSalary);
        salaryField.setFont(new Font("SansSerif", Font.PLAIN, 15));
        salaryField.setBackground(new Color(250, 250, 250));
        panel.add(salaryField);

        JButton btnSave = new JButton("Save Changes");
        btnSave.setFont(new Font("SansSerif", Font.PLAIN, 16));
        btnSave.setBackground(new Color(173, 216, 230));
        btnSave.setForeground(Color.BLACK);
        btnSave.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btnSave.setFocusPainted(false);
        btnSave.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
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
