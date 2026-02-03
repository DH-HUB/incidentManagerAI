package com.example.incidentmanagerai.dto;

public class SolutionSuggestion {
    private Long resolvedIncidentId;
    private String title;
    private double similarity;
    private String solution;

    public SolutionSuggestion(Long resolvedIncidentId, String title, double similarity, String solution) {
        this.resolvedIncidentId = resolvedIncidentId;
        this.title = title;
        this.similarity = similarity;
        this.solution = solution;
    }

    public Long getResolvedIncidentId() { return resolvedIncidentId; }
    public String getTitle() { return title; }
    public double getSimilarity() { return similarity; }
    public String getSolution() { return solution; }
}
