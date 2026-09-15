package me.dotu.JobTrackerBE.Response;

import java.time.LocalDate;

import me.dotu.JobTrackerBE.Entity.JobApplicationEntity;
import me.dotu.JobTrackerBE.Model.ApplicationStatus;

public class JobApplicationResponse {
    private Long id;
    private String companyName;
    private String roleTitle;
    private String jobUrl;
    private ApplicationStatus status;
    private LocalDate dateApplied;
    private LocalDate dateRejected;
    private String notes;
    private String source;

    public static JobApplicationResponse from(JobApplicationEntity entity) {
        JobApplicationResponse response = new JobApplicationResponse();
        response.id = entity.getId();
        response.companyName = entity.getCompanyName();
        response.roleTitle = entity.getRoleTitle();
        response.jobUrl = entity.getJobUrl();
        response.status = entity.getStatus();
        response.dateApplied = entity.getDateApplied();
        response.dateRejected = entity.getDateRejected();
        response.notes = entity.getNotes();
        response.source = entity.getSource();
        return response;
    }

    public Long getId() {
        return id;
    }

    public String getCompanyName() {
        return companyName;
    }

    public String getRoleTitle() {
        return roleTitle;
    }

    public String getJobUrl() {
        return jobUrl;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public LocalDate getDateApplied() {
        return dateApplied;
    }

    public LocalDate getDateRejected() {
        return dateRejected;
    }

    public String getNotes() {
        return notes;
    }

    public String getSource() {
        return source;
    }

}
