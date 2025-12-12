package services;

import models.Application;
import utils.FileService;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ApplicationService {

    private FileService fileService;
    private static final String APP_FILE = "applications.txt";

    public ApplicationService(FileService fileService) {
        this.fileService = fileService;
        fileService.createIfNotExists(APP_FILE); // ensure storage file exists
    }

    public List<Application> getAllApplications() {
        List<String> lines = fileService.readAllLines(APP_FILE);
        List<Application> apps = new ArrayList<>();

        // Convert text lines into Application objects
        for (String line : lines) {
            Application a = Application.fromLine(line);
            if (a != null) apps.add(a);
        }
        return apps;
    }

    private void saveAll(List<Application> apps) {
        List<String> lines = new ArrayList<>();

        // Convert objects back to savable lines
        for (Application a : apps) lines.add(a.toLine());

        fileService.writeAllLines(APP_FILE, lines);
    }

    public boolean applyForJob(String jobId, String applicantId) {
        String applicationId = UUID.randomUUID().toString();
        String status = "Pending";

        // Create and save a new job application
        Application app = new Application(applicationId, jobId, applicantId, status);
        fileService.appendLine(APP_FILE, app.toLine());
        return true;
    }

    public List<Application> getApplicationsByApplicant(String applicantId) {
        List<Application> result = new ArrayList<>();

        // Filter applications belonging to the given applicant
        for (Application a : getAllApplications()) {
            if (a.getApplicantId().equals(applicantId))
                result.add(a);
        }
        return result;
    }

    public boolean updateStatus(String applicationId, String newStatus) {
        List<Application> apps = getAllApplications();
        boolean found = false;

        // Update status of matching application
        for (Application a : apps) {
            if (a.getApplicationId().equals(applicationId)) {
                a.setStatus(newStatus);
                found = true;
                break;
            }
        }

        if (found) saveAll(apps); // persist changes
        return found;
    }
}
