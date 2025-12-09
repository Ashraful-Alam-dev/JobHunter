package models;

public class Job {

    private String jobId;
    private String recruiterId;
    private String title;
    private String description;
    private String status; // Pending, Verified, Approved

    public Job(String jobId, String recruiterId, String title, String description, String status) {
        this.jobId = jobId;
        this.recruiterId = recruiterId;
        this.title = title;
        this.description = description;
        this.status = status;
    }

    // Create a Job object from a line in the file
    public static Job fromLine(String line) {
        String[] p = line.split("\\|");
        if (p.length < 5) return null;

        return new Job(
                p[0], // jobId
                p[1], // recruiterId
                p[2], // title
                p[3], // description
                p[4]  // status
        );
    }

    // Convert a Job object to a line for file storage
    public String toLine() {
        return String.join("|",
                jobId, recruiterId, title, description, status
        );
    }

    // -------------------------
    // Getters
    // -------------------------
    public String getJobId() { return jobId; }
    public String getRecruiterId() { return recruiterId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getStatus() { return status; }

    // -------------------------
    // Setters
    // -------------------------
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setStatus(String status) { this.status = status; }

    // -------------------------
    // Optional: for debugging
    // -------------------------
    @Override
    public String toString() {
        return "Job{" +
                "jobId='" + jobId + '\'' +
                ", recruiterId='" + recruiterId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
