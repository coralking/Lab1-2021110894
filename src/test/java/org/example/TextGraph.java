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

    String queryBridgeWords(String word1, String word2) {
        // 如果word1和word2不存在
        if (!graph.containsKey(word1) || !graph.containsKey(word2)) {
            if (!graph.containsKey(word1) && !graph.containsKey(word2))
                return "No " + "\"" + word1 + "\"" + " and " + "\"" + word2 + "\"" + " in the graph!";
            else {
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

            if (successorsOfBridgeWord != null
                    && successorsOfBridgeWord.stream().anyMatch(e -> e.vertex.equals(word2))) {
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
}
