package models;

public class Job {

    private String jobId;
    private String recruiterId;
    private String title;
    private String description;
    private String companyName;   // optional
    private String salaryRange;   // required
    private String status;        // Pending, Approved, Rejected

    public Job(String jobId, String recruiterId, String title, String description,
               String companyName, String salaryRange, String status) {

        this.jobId = jobId;
        this.recruiterId = recruiterId;
        this.title = title;
        this.description = description;
        this.companyName = companyName == null ? "" : companyName;
        this.salaryRange = salaryRange;
        this.status = status;
    }

    public static Job fromLine(String line) {
        String[] p = line.split("\\|");

        if (p.length != 7) return null;

        return new Job(
                p[0],
                p[1],
                p[2],
                p[3],
                p[4],
                p[5],
                p[6]
        );
    }

    public String toLine() {
        return String.join("|",
                jobId,
                recruiterId,
                title,
                description,
                companyName,
                salaryRange,
                status
        );
    }

    public String getJobId() { return jobId; }
    public String getRecruiterId() { return recruiterId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCompanyName() { return companyName; }
    public String getSalaryRange() { return salaryRange; }
    public String getStatus() { return status; }

    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }
    public void setSalaryRange(String salaryRange) { this.salaryRange = salaryRange; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", recruiterId='" + recruiterId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", companyName='" + companyName + '\'' +
                ", salaryRange='" + salaryRange + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
