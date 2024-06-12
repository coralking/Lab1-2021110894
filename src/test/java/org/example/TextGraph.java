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

    String queryBridgeWords(String word1, String word2){
        // 如果word1和word2不存在
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            if(!graph.containsKey(word1) && !graph.containsKey(word2))
                return "No " + "\"" + word1 + "\"" + " and " + "\"" + word2 + "\"" + " in the graph!";
            else{
                if(!graph.containsKey(word1))
                    return "No " + "\"" + word1 + "\"" + " in the graph!";
                else
                    return "No " + "\"" + word2 + "\"" + " in the graph!";
            }
        }
        // 查找word1的所有后继节点
        Set<Edge> successorsOfWord1 = graph.get(word1);
        Set<String> bridgeWords = new HashSet<>();
        for (Edge edge : successorsOfWord1) {
            String potentialBridgeWord = edge.vertex;
            Set<Edge> successorsOfBridgeWord = graph.get(potentialBridgeWord);
            if (successorsOfBridgeWord != null && successorsOfBridgeWord.stream().anyMatch(e -> e.vertex.equals(word2)))
            {
                //System.out.println(successorsOfBridgeWord.stream());
                bridgeWords.add(potentialBridgeWord);
            }
        }
        // 根据找到的桥接词数量返回结果
        if (bridgeWords.isEmpty()) {
            return "No bridge words from " + "\"" + word1 + "\"" + " to " + "\"" + word2 + "\"" + "!";
        } else {
            StringBuilder sb = new StringBuilder("The bridge words from " + "\"" + word1 + "\"" + " to " + "\"" +
                    word2 + "\"" + " are: ");
            for (String bridgeWord : bridgeWords) {
                sb.append(bridgeWord).append(", ");
            }
            // 移除最后的逗号和空格
            if (sb.length() > 2) {
                sb.setLength(sb.length() - 2);
            }
            return sb.toString();
        }
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
            return "No " + "\"" + word1 + "\"" + " or " + "\"" + word2 + "\"" + " in the graph!";
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
