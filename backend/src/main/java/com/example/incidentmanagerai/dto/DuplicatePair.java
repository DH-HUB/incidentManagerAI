package com.example.incidentmanagerai.dto;

public class DuplicatePair {
    private Long incidentIdA;
    private Long incidentIdB;
    private double similarity;

    public DuplicatePair(Long incidentIdA, Long incidentIdB, double similarity) {
        this.incidentIdA = incidentIdA;
        this.incidentIdB = incidentIdB;
        this.similarity = similarity;
    }

    public Long getIncidentIdA() { return incidentIdA; }
    public Long getIncidentIdB() { return incidentIdB; }
    public double getSimilarity() { return similarity; }
}
