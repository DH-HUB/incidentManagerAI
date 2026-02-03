package com.example.incidentmanagerai.controller;

import com.example.incidentmanagerai.dto.DuplicatePair;
import com.example.incidentmanagerai.dto.IncidentRequest;
import com.example.incidentmanagerai.dto.SolutionSuggestion;
import com.example.incidentmanagerai.model.Incident;
import com.example.incidentmanagerai.service.IncidentService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/incidents")
@Validated
public class IncidentController {

    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @GetMapping
    public List<Incident> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Incident> get(@PathVariable Long id) {
        return service.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Incident> create(@Valid @RequestBody IncidentRequest req) {
        Incident created = service.create(req);
        return ResponseEntity.created(URI.create("/api/incidents/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Incident> update(@PathVariable Long id, @Valid @RequestBody IncidentRequest req) {
        return service.update(id, req)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/duplicates")
    public List<DuplicatePair> duplicates() {
        return service.detectDuplicates();
    }

    @GetMapping("/{id}/suggestions")
    public List<SolutionSuggestion> suggestions(@PathVariable Long id) {
        return service.suggestSolutions(id);
    }
}
