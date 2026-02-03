package com.example.incidentmanagerai;

import com.example.incidentmanagerai.dto.IncidentRequest;
import com.example.incidentmanagerai.model.Priority;
import com.example.incidentmanagerai.model.Status;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.Set;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc
class IncidentControllerTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper om;

    @Test
    void create_list_update_delete_flow() throws Exception {
        IncidentRequest req = new IncidentRequest();
        req.setTitle("Problème SMTP");
        req.setDescription("Les emails ne partent plus depuis 09:00.");
        req.setPriority(Priority.MEDIUM);
        req.setStatus(Status.OPEN);

        // create
        String created = mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Problème SMTP")))
                .andReturn().getResponse().getContentAsString();

        long id = om.readTree(created).get("id").asLong();

        // list contains it
        mvc.perform(get("/api/incidents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem((int) id)));

        // update
        req.setStatus(Status.RESOLVED);
        req.setSolution("Redémarrer le service SMTP, vérifier les identifiants et le quota.");

        mvc.perform(put("/api/incidents/" + id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("RESOLVED")))
                .andExpect(jsonPath("$.solution", containsString("Redémarrer")));

        // delete
        mvc.perform(delete("/api/incidents/" + id))
                .andExpect(status().isNoContent());

        // get -> 404
        mvc.perform(get("/api/incidents/" + id))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicates_endpoint_returns_array() throws Exception {
        mvc.perform(get("/api/incidents/duplicates"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }

    @Test
    void suggestions_should_not_include_self() throws Exception {
        IncidentRequest req = new IncidentRequest();
        req.setTitle("Incident A");
        req.setDescription("Description A avec quelques mots communs.");
        req.setPriority(Priority.MEDIUM);
        req.setStatus(Status.RESOLVED);
        req.setSolution("Solution A");


        String created = mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long id = om.readTree(created).get("id").asLong();

        mvc.perform(get("/api/incidents/" + id + "/suggestions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].resolvedIncidentId", not(hasItem((int) id))));
    }
    @Test
    void suggestions_should_only_return_resolved_with_solution() throws Exception {

        IncidentRequest openNoSolution = new IncidentRequest();
        openNoSolution.setTitle("Erreur 500 API");
        openNoSolution.setDescription("Erreur interne serveur sur endpoint incidents.");
        openNoSolution.setPriority(Priority.CRITICAL);
        openNoSolution.setStatus(Status.OPEN);
        openNoSolution.setSolution(null);

        String openCreated = mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(openNoSolution)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long openId = om.readTree(openCreated).get("id").asLong();

        //Incident RESOLVED mais sans solution ne doit jamais être proposé
        IncidentRequest resolvedBlankSolution = new IncidentRequest();
        resolvedBlankSolution.setTitle("Erreur 500 similaire");
        resolvedBlankSolution.setDescription("Erreur interne serveur similaire.");
        resolvedBlankSolution.setPriority(Priority.CRITICAL);
        resolvedBlankSolution.setStatus(Status.RESOLVED);
        resolvedBlankSolution.setSolution("   "); // blank

        mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(resolvedBlankSolution)))
                .andExpect(status().isCreated());


        IncidentRequest resolvedWithSolution = new IncidentRequest();
        resolvedWithSolution.setTitle("API renvoie 500");
        resolvedWithSolution.setDescription("Erreur interne serveur sur endpoint incidents. Logs montrent NullPointerException.");
        resolvedWithSolution.setPriority(Priority.CRITICAL);
        resolvedWithSolution.setStatus(Status.RESOLVED);
        resolvedWithSolution.setSolution("Corriger la NPE, ajouter un null-check, déployer le correctif.");

        String solvedCreated = mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(resolvedWithSolution)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long solvedId = om.readTree(solvedCreated).get("id").asLong();

        // Seuils: si besoin, baisse suggestion-threshold dans application.yml pour éviter un faux négatif.
        // Appel suggestions sur l'incident OPEN: s'il y a des suggestions, elles doivent venir du RESOLVED+solution uniquement
        mvc.perform(get("/api/incidents/" + openId + "/suggestions"))
                .andExpect(status().isOk())
                // Ne doit JAMAIS contenir l'incident OPEN lui-même
                .andExpect(jsonPath("$[*].resolvedIncidentId", not(hasItem((int) openId))))
                // Ne doit pas contenir un incident résolu mais sans solution car on ne connait pas son id donc on vérifie par contenu
                .andExpect(jsonPath("$[*].solution", everyItem(not(isEmptyOrNullString()))));

    }

    @Test
    void duplicates_should_not_contain_self_pairs_or_reverse_duplicates() throws Exception {
        IncidentRequest a = new IncidentRequest();
        a.setTitle("Erreur 500 sur l'API");
        a.setDescription("Erreur interne serveur sur /api/incidents. Code 500.");
        a.setPriority(Priority.CRITICAL);
        a.setStatus(Status.OPEN);

        IncidentRequest b = new IncidentRequest();
        b.setTitle("API renvoie 500");
        b.setDescription("Erreur interne serveur sur endpoint incidents. Code 500.");
        b.setPriority(Priority.CRITICAL);
        b.setStatus(Status.OPEN);

        String createdA = mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(a)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idA = om.readTree(createdA).get("id").asLong();

        String createdB;
        createdB = mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(b)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        long idB = om.readTree(createdB).get("id").asLong();

        String json = mvc.perform(get("/api/incidents/duplicates"))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();


        var arr = om.readTree(json);

        if (arr.isArray() && arr.size() > 0) {
            Set<String> seen = new HashSet<>();
            for (var node : arr) {
                long A = node.get("incidentIdA").asLong();
                long B = node.get("incidentIdB").asLong();

                // A != B
                org.junit.jupiter.api.Assertions.assertNotEquals(A, B, "Duplicate pair should not be self-pair");


                long min = Math.min(A, B);
                long max = Math.max(A, B);
                String key = min + "-" + max;

                org.junit.jupiter.api.Assertions.assertFalse(seen.contains(key), "Reverse/duplicate pair detected: " + key);
                seen.add(key);
            }
        }
    }

    @Test
    void create_should_return_400_when_title_or_description_missing() throws Exception {
        // Missing title
        String payload1 = """
        {
          "title": "",
          "description": "Desc ok",
          "priority": "MEDIUM",
          "status": "OPEN"
        }
        """;

        mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload1))
                .andExpect(status().isBadRequest());

        // Missing description
        String payload2 = """
        {
          "title": "Titre ok",
          "description": "",
          "priority": "MEDIUM",
          "status": "OPEN"
        }
        """;

        mvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payload2))
                .andExpect(status().isBadRequest());
    }


}
