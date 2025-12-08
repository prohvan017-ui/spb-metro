package com.example.kursovaya.model;

import com.example.kursovaya.util.DijkstraResult;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

/**
 * Класс, представляющий граф метрополитена.
 * Использует списки смежности для хранения соединений между станциями.
 *
 * @author Student
 * @version 1.0
 */
public class Graph {
    private static final Logger logger = LogManager.getLogger(Graph.class);

    private final int vertices;
    private final List<List<Edge>> adjacency;

    /**
     * Создает новый граф с указанным количеством вершин (станций)
     *
     * @param vertices количество вершин (станций)
     */
    public Graph(int vertices) {
        logger.debug("Creating graph with {} vertices", vertices);
        this.vertices = vertices;
        adjacency = new ArrayList<>(vertices);
        for (int i = 0; i < vertices; i++) {
            adjacency.add(new ArrayList<>());
        }
    }

    /**
     * Добавляет ребро (соединение) между двумя станциями
     *
     * @param from ID начальной станции
     * @param to ID конечной станции
     * @param weight вес ребра (время в минутах)
     */
    public void addEdge(int from, int to, int weight) {
        logger.trace("Adding edge: {} -> {} (weight {})", from, to, weight);
        adjacency.get(from).add(new Edge(from, to, weight));
        adjacency.get(to).add(new Edge(to, from, weight));
    }

    /**
     * Выполняет алгоритм Дейкстры для поиска кратчайшего пути
     * Использует списки смежности и очередь с приоритетами
     *
     * @param start ID начальной станции
     * @param end ID конечной станции
     * @return результат алгоритма Дейкстры
     */
    public DijkstraResult dijkstra(int start, int end) {
        logger.debug("Running Dijkstra algorithm (adjacency lists) from {} to {}", start, end);
        long startTime = System.nanoTime();

        int[] dist = new int[vertices];
        int[] prev = new int[vertices];

        Arrays.fill(dist, Integer.MAX_VALUE);
        Arrays.fill(prev, -1);

        dist[start] = 0;

        PriorityQueue<int[]> pq = new PriorityQueue<>(Comparator.comparingInt(a -> a[1]));
        pq.add(new int[]{start, 0});

        int iterations = 0;
        while (!pq.isEmpty()) {
            int[] u = pq.poll();
            int station = u[0];
            int d = u[1];

            if (d > dist[station]) continue;
            if (station == end) {
                logger.trace("Reached destination vertex at iteration {}", iterations);
                break;
            }

            for (Edge e : adjacency.get(station)) {
                int alt = dist[station] + e.getWeight();
                if (alt < dist[e.getTo()]) {
                    dist[e.getTo()] = alt;
                    prev[e.getTo()] = station;
                    pq.add(new int[]{e.getTo(), alt});
                }
            }
            iterations++;
        }

        long endTime = System.nanoTime();
        long duration = (endTime - startTime) / 1000;

        logger.info("Dijkstra algorithm (adjacency lists) completed in {} μs, {} iterations",
                duration, iterations);

        return new DijkstraResult(dist, prev);
    }
}