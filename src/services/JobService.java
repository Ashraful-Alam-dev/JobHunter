package services;

import models.Job;
import utils.FileService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobService {

    private final FileService fileService;
    private static final String JOB_FILE = "jobs.txt";

    public JobService(FileService fileService) {
        this.fileService = fileService;
        // Ensure file exists
        fileService.createIfNotExists(JOB_FILE);
    }

    // ------------------------------
    // Get All Jobs
    // ------------------------------
    public List<Job> getAllJobs() {
        List<String> lines = fileService.readAllLines(JOB_FILE);
        List<Job> jobs = new ArrayList<>();

        for (String line : lines) {
            Job job = Job.fromLine(line);
            if (job != null) jobs.add(job);
        }

        return jobs;
    }

    // ------------------------------
    // Save All Jobs
    // ------------------------------
    private void saveAll(List<Job> jobs) {
        List<String> lines = new ArrayList<>();
        for (Job j : jobs) {
            lines.add(j.toLine());
        }
        fileService.writeAllLines(JOB_FILE, lines);
    }

    // ------------------------------
    // Post Job
    // ------------------------------
    public boolean postJob(String recruiterId, String title, String description) {
        String jobId = UUID.randomUUID().toString();
        String status = "Pending";
        Job job = new Job(jobId, recruiterId, title, description, status);

        fileService.appendLine(JOB_FILE, job.toLine());
        return true;
    }

    // ------------------------------
    // Update Job
    // ------------------------------
    public boolean updateJob(String jobId, String newTitle, String newDesc) {
        List<Job> jobs = getAllJobs();
        boolean updated = false;

        for (Job j : jobs) {
            if (j.getJobId().equals(jobId)) {
                j.setTitle(newTitle);
                j.setDescription(newDesc);
                j.setStatus("Pending"); // reset approval on update
                updated = true;
                break;
            }
        }

        if (updated) saveAll(jobs);
        return updated;
    }

    // ------------------------------
    // Delete Job
    // ------------------------------
    public boolean deleteJob(String jobId) {
        List<Job> jobs = getAllJobs();
        boolean removed = jobs.removeIf(j -> j.getJobId().equals(jobId));

        if (removed) saveAll(jobs);
        return removed;
    }

    // ------------------------------
    // Admin Approve Job
    // ------------------------------
    public boolean approveJob(String jobId) {
        return updateStatus(jobId, "Approved");
    }

    // ------------------------------
    // Admin Reject Job
    // ------------------------------
    public boolean rejectJob(String jobId) {
        return updateStatus(jobId, "Rejected");
    }

    public boolean updateStatus(String jobId, String status) {
        List<Job> jobs = getAllJobs();
        boolean updated = false;

        for (Job j : jobs) {
            if (j.getJobId().equals(jobId)) {
                j.setStatus(status);
                updated = true;
                break;
            }
        }

        if (updated) saveAll(jobs);
        return updated;
    }

    // ------------------------------
    // Get All Approved Jobs
    // ------------------------------
    public List<Job> getApprovedJobs() {
        List<Job> result = new ArrayList<>();
        for (Job j : getAllJobs()) {
            if ("Approved".equalsIgnoreCase(j.getStatus())) {
                result.add(j);
            }
        }
        return result;
    }

    // ------------------------------
    // Get Jobs By Recruiter
    // ------------------------------
    public List<Job> getJobsByRecruiter(String recruiterId) {
        List<Job> result = new ArrayList<>();
        for (Job j : getAllJobs()) {
            if (j.getRecruiterId().equals(recruiterId)) {
                result.add(j);
            }
        }
        return result;
    }
}
