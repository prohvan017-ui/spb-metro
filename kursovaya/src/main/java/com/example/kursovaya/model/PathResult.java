package com.example.kursovaya.model;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

/**
 * Класс для хранения результатов поиска пути.
 * Содержит маршрут (список станций) и общее время в пути.
 *
 * @author Student
 * @version 1.0
 */
public class PathResult {
    private static final Logger logger = LogManager.getLogger(PathResult.class);

    private final List<Station> path;
    private final int totalTime;

    /**
     * Создает объект с результатами пути
     *
     * @param path список станций в порядке следования
     * @param totalTime общее время в пути в минутах
     */
    public PathResult(List<Station> path, int totalTime) {
        this.path = path;
        this.totalTime = totalTime;
        logger.debug("Created PathResult: {} stations, time {} minutes",
                path.size(), totalTime);
    }

    /**
     * Возвращает маршрут (список станций)
     *
     * @return список станций
     */
    public List<Station> getPath() {
        return path;
    }

    /**
     * Возвращает общее время в пути
     *
     * @return время в минутах
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Выводит информацию о маршруте в консоль
     */
    public void printPath() {
        logger.info("Printing path information to console");
        System.out.println("Кратчайший маршрут:");
        System.out.println("Общее время: " + totalTime + " минут");
        System.out.println("Маршрут:");

        Station prev = null;
        for (Station station : path) {
            if (prev != null && prev.getLine() != station.getLine()) {
                System.out.println("  -> Пересадка на линию " + station.getLine());
                logger.trace("Transfer to line {}", station.getLine());
            }
            System.out.println("  - " + station.getName() + " (линия " + station.getLine() + ")");
            prev = station;
        }
        logger.debug("Path information printed to console");
    }
}