package com.example.kursovaya.model;

/**
 * Класс, представляющий ребро графа (соединение между станциями).
 * Ребро имеет начальную и конечную вершины, а также вес (время перемещения).
 *
 * @author Student
 * @version 1.0
 */
public class Edge {
    private final int from;
    private final int to;
    private final int weight;

    /**
     * Создает новое ребро графа
     *
     * @param from ID начальной вершины (станции)
     * @param to ID конечной вершины (станции)
     * @param weight вес ребра (время в минутах)
     */
    public Edge(int from, int to, int weight) {
        this.from = from;
        this.to = to;
        this.weight = weight;
    }

    /**
     * Возвращает ID начальной вершины
     *
     * @return ID начальной вершины
     */
    public int getFrom() {
        return from;
    }

    /**
     * Возвращает ID конечной вершины
     *
     * @return ID конечной вершины
     */
    public int getTo() {
        return to;
    }

    /**
     * Возвращает вес ребра (время перемещения)
     *
     * @return вес в минутах
     */
    public int getWeight() {
        return weight;
    }
}