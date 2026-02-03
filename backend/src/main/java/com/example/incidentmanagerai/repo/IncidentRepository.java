package com.example.incidentmanagerai.repo;

import com.example.incidentmanagerai.model.Incident;
import com.example.incidentmanagerai.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
    List<Incident> findByStatus(Status status);
}
