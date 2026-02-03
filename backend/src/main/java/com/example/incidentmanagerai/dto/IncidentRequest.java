package com.example.incidentmanagerai.dto;

import com.example.incidentmanagerai.model.Priority;
import com.example.incidentmanagerai.model.Status;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class IncidentRequest {

    @NotBlank
    @Size(max = 200)
    private String title;

    @NotBlank
    @Size(max = 4000)
    private String description;

    private Priority priority = Priority.MEDIUM;

    private Status status = Status.OPEN;

    @Size(max = 4000)
    private String solution;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Priority getPriority() { return priority; }
    public void setPriority(Priority priority) { this.priority = priority; }

    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public String getSolution() { return solution; }
    public void setSolution(String solution) { this.solution = solution; }
}
