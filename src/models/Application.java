package models;

public class Application {

    private String applicationId;
    private String jobId;       // internal storage
    private String applicantId;
    private String status;

    public Application(String applicationId, String jobId,
                       String applicantId, String status) {
        this.applicationId = applicationId;
        this.jobId = jobId;
        this.applicantId = applicantId;
        this.status = status;
    }

    public String getApplicationId() { return applicationId; }
    public String getJobId() { return jobId; }
    public String getApplicantId() { return applicantId; }
    public String getStatus() { return status; }

    public void setStatus(String status) { this.status = status; }

    public String toLine() {
        return applicationId + "|" + jobId + "|" + applicantId + "|" + status;
    }

    public static Application fromLine(String line) {
        try {
            String[] p = line.split("\\|");
            return new Application(p[0], p[1], p[2], p[3]);
        } catch (Exception e) {
            return null;
        }
    }
}
