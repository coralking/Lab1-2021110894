package org.example;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class TextGraph {

    private Map<String, Set<Edge>> graph;

    private static class Edge {
        String vertex;
        int weight;

        Edge(String vertex, int weight) {
            this.vertex = vertex;
            this.weight = weight;
        }
    }

    public TextGraph() {
        this.graph = new HashMap<>();
    }

    void readFile(String filePath) {
        File file = new File(filePath);
        String text = "";
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                line = line.replaceAll("[^a-zA-Z\\s]", " ");
                line = line.replaceAll("\\s+", " ");
                line = line.toLowerCase();
                text = text + line + " ";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String[] words = text.split("\\s+");
        for (int i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];
            Set<Edge> edges = graph.get(currentWord);
            if (edges == null) {
                graph.put(currentWord, new HashSet<>());
                graph.get(currentWord).add(new Edge(nextWord, 1));
            } else {
                boolean has = false;
                for (Edge edge : edges) {
                    if (edge.vertex.equals(nextWord)) {
                        edge.weight++;
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    edges.add(new Edge(nextWord, 1));
                }
            }
        }
        graph.computeIfAbsent(words[words.length - 1], k -> new HashSet<>());
    }

    String calcShortestPath(String word1, String word2) {
        word1 = word1.toLowerCase();
        word2 = word2.toLowerCase();

        if (word1.isEmpty() && word2.isEmpty()) {
            return "Both words are empty!";
        }
        if (word1.isEmpty() || word2.isEmpty()) {
            return "One word is empty!";
        }

        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            return "No " + word1 + " or " + word2 + " in the graph!";
        }

        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();
        for (String word : graph.keySet()) {
            distances.put(word, Integer.MAX_VALUE);
        }
        distances.put(word1, 0);

        Queue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(word1);

        while (!queue.isEmpty()) {
            String current = queue.poll();
            if (current.equals(word2)) {
                break;
            }

            for (Edge edge : graph.getOrDefault(current, Collections.emptySet())) {
                String neighbor = edge.vertex;
                int distanceThroughCurrent = distances.get(current) + edge.weight;

                if (distanceThroughCurrent < distances.get(neighbor)) {
                    distances.put(neighbor, distanceThroughCurrent);
                    predecessors.put(neighbor, current);
                    queue.add(neighbor);
                }
            }
        }

        StringBuilder path = new StringBuilder();
        String current = word2;
        while (current != null) {
            path.insert(0, current + "->");
            current = predecessors.get(current);
        }
        path.delete(path.length() - "->".length(), path.length());
        if (!path.toString().startsWith(word1)) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        return path.toString();
    }
}
