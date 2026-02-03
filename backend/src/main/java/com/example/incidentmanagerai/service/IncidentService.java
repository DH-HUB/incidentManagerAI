package com.example.incidentmanagerai.service;

import com.example.incidentmanagerai.dto.DuplicatePair;
import com.example.incidentmanagerai.dto.IncidentRequest;
import com.example.incidentmanagerai.dto.SolutionSuggestion;
import com.example.incidentmanagerai.model.Incident;
import com.example.incidentmanagerai.model.Status;
import com.example.incidentmanagerai.repo.IncidentRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class IncidentService {

    private final IncidentRepository repo;
    private final TextSimilarityService similarityService;

    @Value("${app.similarity.duplicate-threshold:0.82}")
    private double duplicateThreshold;

    @Value("${app.similarity.suggestion-threshold:0.62}")
    private double suggestionThreshold;

    @Value("${app.similarity.max-suggestions:5}")
    private int maxSuggestions;

    public IncidentService(IncidentRepository repo, TextSimilarityService similarityService) {
        this.repo = repo;
        this.similarityService = similarityService;
    }

    public List<Incident> findAll() {
        return repo.findAll().stream()
                .sorted(Comparator.comparing(Incident::getUpdatedAt).reversed())
                .collect(Collectors.toList());
    }

    public Optional<Incident> findById(Long id) {
        return repo.findById(id);
    }

    @Transactional
    public Incident create(IncidentRequest req) {
        Incident i = new Incident();
        i.setTitle(req.getTitle());
        i.setDescription(req.getDescription());
        i.setPriority(req.getPriority());
        i.setStatus(req.getStatus());
        i.setSolution(req.getSolution());
        return repo.save(i);
    }

    @Transactional
    public Optional<Incident> update(Long id, IncidentRequest req) {
        return repo.findById(id).map(existing -> {
            existing.setTitle(req.getTitle());
            existing.setDescription(req.getDescription());
            existing.setPriority(req.getPriority());
            existing.setStatus(req.getStatus());
            existing.setSolution(req.getSolution());
            return repo.save(existing);
        });
    }

    public void delete(Long id) {
        repo.deleteById(id);
    }

    public List<DuplicatePair> detectDuplicates() {
        List<Incident> incidents = repo.findAll();
        List<String> corpus = incidents.stream()
                .map(i -> (i.getTitle() + " " + i.getDescription()).trim())
                .collect(Collectors.toList());

        List<DuplicatePair> out = new ArrayList<>();
        for (int i = 0; i < incidents.size(); i++) {
            for (int j = i + 1; j < incidents.size(); j++) {
                Incident a = incidents.get(i);
                Incident b = incidents.get(j);
                String ta = (a.getTitle() + " " + a.getDescription()).trim();
                String tb = (b.getTitle() + " " + b.getDescription()).trim();
                double sim = similarityService.cosineSimilarity(ta, tb, corpus);
                if (sim >= duplicateThreshold) {
                    out.add(new DuplicatePair(a.getId(), b.getId(), sim));
                }
            }
        }
        out.sort(Comparator.comparing(DuplicatePair::getSimilarity).reversed());
        return out;
    }

    public List<SolutionSuggestion> suggestSolutions(Long incidentId) {
        Incident target = repo.findById(incidentId)
                .orElseThrow(() -> new NoSuchElementException("Incident not found"));

        List<Incident> resolved = repo.findByStatus(Status.RESOLVED).stream()
                .filter(i -> i.getSolution() != null && !i.getSolution().isBlank())
                .collect(Collectors.toList());

        if (resolved.isEmpty()) return Collections.emptyList();

        // corpus = target + resolved (title+desc)
        List<String> corpus = new ArrayList<>();
        corpus.add((target.getTitle() + " " + target.getDescription()).trim());
        for (Incident r : resolved) {
            corpus.add((r.getTitle() + " " + r.getDescription()).trim());
        }

        String tText = (target.getTitle() + " " + target.getDescription()).trim();

        List<SolutionSuggestion> suggestions = new ArrayList<>();
        for (Incident r : resolved) {


            if (r.getId() != null && r.getId().equals(target.getId())) {
                continue;
            }

            String rText = (r.getTitle() + " " + r.getDescription()).trim();
            double sim = similarityService.cosineSimilarity(tText, rText, corpus);

            if (sim >= suggestionThreshold) {
                suggestions.add(new SolutionSuggestion(r.getId(), r.getTitle(), sim, r.getSolution()));
            }
        }

        suggestions.sort(Comparator.comparing(SolutionSuggestion::getSimilarity).reversed());
        if (suggestions.size() > maxSuggestions) {
            return suggestions.subList(0, maxSuggestions);
        }
        return suggestions;
    }
}
