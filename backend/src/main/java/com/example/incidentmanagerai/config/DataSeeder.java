package com.example.incidentmanagerai.config;

import com.example.incidentmanagerai.model.Incident;
import com.example.incidentmanagerai.model.Priority;
import com.example.incidentmanagerai.model.Status;
import com.example.incidentmanagerai.repo.IncidentRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Quelques données de démo au démarrage (facultatif).
 * Supprimez ce fichier si vous voulez une base vide.
 */
@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(IncidentRepository repo) {
        return args -> {
            if (repo.count() > 0) return;

            Incident a = new Incident("Erreur 500 sur l'API", "Les utilisateurs reçoivent une erreur 500 sur /api/incidents.", Priority.CRITICAL);
            a.setStatus(Status.OPEN);

            Incident b = new Incident("API renvoie 500", "Erreur interne serveur sur endpoint incidents. Logs montrent NullPointerException.", Priority.CRITICAL);
            b.setStatus(Status.OPEN);

            Incident c = new Incident("Login impossible", "Plusieurs utilisateurs ne peuvent pas se connecter. Message: invalid token.", Priority.MEDIUM);
            c.setStatus(Status.RESOLVED);
            c.setSolution("Vérifier la configuration OAuth, régénérer les secrets, invalider les anciens tokens.");

            repo.save(a);
            repo.save(b);
            repo.save(c);
        };
    }
}
