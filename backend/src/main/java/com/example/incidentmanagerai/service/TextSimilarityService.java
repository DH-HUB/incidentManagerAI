package com.example.incidentmanagerai.service;

import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Vectorisation TF-IDF simplifiée + cosinus.
 * - Corpus = l'ensemble des textes fournis
 * - Tokenisation = split sur caractères non-alphanumériques + min longueur
 */
@Service
public class TextSimilarityService {

    public double cosineSimilarity(String a, String b, List<String> corpus) {
        Map<String, Integer> vocab = buildVocab(corpus);
        double[] va = tfidfVector(a, corpus, vocab);
        double[] vb = tfidfVector(b, corpus, vocab);
        return cosine(va, vb);
    }

    public Map<String, Integer> buildVocab(List<String> corpus) {
        LinkedHashSet<String> set = new LinkedHashSet<>();
        for (String doc : corpus) {
            set.addAll(tokens(doc));
        }
        Map<String, Integer> vocab = new LinkedHashMap<>();
        int i = 0;
        for (String t : set) vocab.put(t, i++);
        return vocab;
    }

    public double[] tfidfVector(String doc, List<String> corpus, Map<String, Integer> vocab) {
        List<String> toks = tokens(doc);
        Map<String, Long> tf = toks.stream().collect(Collectors.groupingBy(x -> x, Collectors.counting()));

        // document frequency
        Map<String, Integer> df = new HashMap<>();
        for (String t : vocab.keySet()) df.put(t, 0);

        for (String d : corpus) {
            Set<String> dtoks = new HashSet<>(tokens(d));
            for (String t : dtoks) {
                if (df.containsKey(t)) df.put(t, df.get(t) + 1);
            }
        }

        int N = Math.max(corpus.size(), 1);
        double[] v = new double[vocab.size()];
        for (Map.Entry<String, Integer> e : vocab.entrySet()) {
            String term = e.getKey();
            int idx = e.getValue();
            double termFreq = tf.getOrDefault(term, 0L);
            if (termFreq == 0) {
                v[idx] = 0;
                continue;
            }
            double idf = Math.log((N + 1.0) / (df.getOrDefault(term, 0) + 1.0)) + 1.0;
            v[idx] = termFreq * idf;
        }
        // L2 normalize
        double norm = 0;
        for (double x : v) norm += x * x;
        norm = Math.sqrt(norm);
        if (norm > 0) {
            for (int i = 0; i < v.length; i++) v[i] /= norm;
        }
        return v;
    }

    private List<String> tokens(String text) {
        if (text == null) return Collections.emptyList();
        String[] raw = text.toLowerCase(Locale.ROOT).split("[^a-z0-9àâäéèêëîïôöùûüç]+");
        ArrayList<String> out = new ArrayList<>();
        for (String r : raw) {
            if (r == null) continue;
            String t = r.trim();
            if (t.length() >= 3) out.add(t);
        }
        return out;
    }

    private double cosine(double[] a, double[] b) {
        if (a.length != b.length) return 0;
        double dot = 0, na = 0, nb = 0;
        for (int i = 0; i < a.length; i++) {
            dot += a[i] * b[i];
            na += a[i] * a[i];
            nb += b[i] * b[i];
        }
        if (na == 0 || nb == 0) return 0;
        return dot / (Math.sqrt(na) * Math.sqrt(nb));
    }
}
