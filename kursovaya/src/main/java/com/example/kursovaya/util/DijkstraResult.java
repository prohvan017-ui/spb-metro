package com.example.kursovaya.util;

/**
 * Класс для хранения результатов выполнения алгоритма Дейкстры.
 * Содержит массивы расстояний и предшественников.
 *
 * @author Student
 * @version 1.0
 */
public class DijkstraResult {
    private final int[] dist;
    private final int[] prev;

    /**
     * Создает объект с результатами алгоритма Дейкстры
     *
     * @param dist массив расстояний от начальной вершины
     * @param prev массив предшественников для восстановления пути
     */
    public DijkstraResult(int[] dist, int[] prev) {
        this.dist = dist;
        this.prev = prev;
    }

    /**
     * Возвращает массив расстояний
     *
     * @return массив расстояний
     */
    public int[] getDist() {
        return dist;
    }

    /**
     * Возвращает массив предшественников
     *
     * @return массив предшественников
     */
    public int[] getPrev() {
        return prev;
    }
}