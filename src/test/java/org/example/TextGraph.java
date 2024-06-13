package org.example;

import org.apache.commons.io.FilenameUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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

    void readFile(String filePath, Boolean test) {   // 读取文件并且处理语句
        File file = new File(test ? "src/test/java/org/example/"
                : "src/main/java/org/example/", FilenameUtils.getName(filePath));
        StringBuilder text = new StringBuilder();   //用来记录处理后的语句，都变为小写，且用空格分隔
        try (BufferedReader br = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            // 逐行读取文件
            while ((line = br.readLine()) != null) {
                // 使用正则表达式替换标点符号和非字母字符为空格
                line = line.replaceAll("[^a-zA-Z\\s]", " ");
                // 将换行符和回车符转换为空格
                line = line.replaceAll("\\s+", " ");
                // 将所有字母都替换为小写
                line = line.toLowerCase();
                text.append(line).append(" ");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //System.out.println(text);
        String[] words = text.toString().split("\\s+");    // 将text分割为字符串数组
        // 遍历单词数组，构建有向边
        int i;
        for (i = 0; i < words.length - 1; i++) {
            String currentWord = words[i];
            String nextWord = words[i + 1];
            Set<Edge> edges = graph.get(currentWord);
            //如果当前单词没有后继节点的set集合，则创建一个新的
            if (edges == null) {
                graph.put(currentWord, new HashSet<>());
                graph.get(currentWord).add(new Edge(nextWord, 1));
            } else {     // 如果已经有set集合，就要在set集合里寻找是否已经存在nextword，如果存在只需要将weight+1，不存在则需要新创建
                boolean has = false;
                for (Edge edge : edges) {
                    if (edge.vertex.equals(nextWord)) {
                        edge.weight++; // 增加权重
                        has = true;
                        break;
                    }
                }
                if (!has) {
                    edges.add(new Edge(nextWord, 1));
                }
            }
        }
        graph.computeIfAbsent(words[i], k -> new HashSet<>());   // 处理最后一个单词，如果已经有set集合则不需要创建，没有需要创建

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

        // 使用 HashMap 存储每个节点的最短路径估计和前驱节点
        Map<String, Integer> distances = new HashMap<>();
        Map<String, String> predecessors = new HashMap<>();

        // 初始化所有节点的最短路径估计为无穷大，源节点为 0
        for (String word : graph.keySet()) {
            distances.put(word, Integer.MAX_VALUE);
        }
        distances.put(word1, 0);

        // 使用 PriorityQueue 存储所有待处理的节点
        Queue<String> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));
        queue.add(word1);

        // Dijkstra 算法主循环
        while (!queue.isEmpty()) {
            String current = queue.poll();

            if (current.equals(word2)) {
                break; // 找到目标节点，结束循环
            }

            for (Edge edge : graph.getOrDefault(current, Collections.emptySet())) {
                String neighbor = edge.vertex;
                int distanceThroughCurrent = distances.get(current) + edge.weight;

                // 如果通过当前节点到达邻居节点的路径更短，则更新该路径
                if (distanceThroughCurrent < distances.get(neighbor)) {
                    distances.put(neighbor, distanceThroughCurrent);
                    predecessors.put(neighbor, current); // 更新前驱节点

                    // 重新将邻居节点加入队列，因为我们更新了它的最短路径估计
                    queue.add(neighbor);
                }
            }
        }

        // 回溯构建最短路径字符串
        StringBuilder path = new StringBuilder();
        String current = word2;
        while (current != null) {
            path.insert(0, current + "->");
            current = predecessors.get(current);
        }

        // 移除最后的 "->" 并添加源节点
        path.delete(path.length() - "->".length(), path.length());

        // 如果源节点和目标节点相同，或者没有路径连接它们
        if (!path.toString().startsWith(word1)) {
            return "No path from \"" + word1 + "\" to \"" + word2 + "\"!";
        }

        return path.toString();
    }
}
