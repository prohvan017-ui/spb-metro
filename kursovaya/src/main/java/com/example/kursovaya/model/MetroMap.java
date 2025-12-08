package com.example.kursovaya.model;

import com.example.kursovaya.util.DijkstraResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Класс, представляющий карту метрополитена Санкт-Петербурга.
 * Содержит станции, линии, соединения и реализует алгоритмы поиска пути.
 * Использует как списки смежности, так и матрицу смежности.
 *
 * @author Student
 * @version 1.0
 */
public class MetroMap {
    private final Map<Integer, MetroLine> lines = new HashMap<>();
    private final List<Station> stations = new ArrayList<>();
    private final Graph graph; // Для эффективных операций со списками смежности
    private final int[][] adjacencyMatrix; // Матрица смежности
    private static final int INF = Integer.MAX_VALUE / 2; // "Бесконечность" для матрицы

    /**
     * Создает новую карту метрополитена
     *
     * @param stationCount ожидаемое количество станций
     */
    public MetroMap(int stationCount) {
        graph = new Graph(stationCount);
        adjacencyMatrix = new int[stationCount][stationCount];

        // Инициализация матрицы смежности
        for (int i = 0; i < stationCount; i++) {
            for (int j = 0; j < stationCount; j++) {
                if (i == j) {
                    adjacencyMatrix[i][j] = 0; // Расстояние до себя
                } else {
                    adjacencyMatrix[i][j] = INF; // "Бесконечность"
                }
            }
        }
    }

    /**
     * Добавляет линию метро
     *
     * @param line объект линии метро
     */
    public void addLine(MetroLine line) {
        lines.put(line.getNumber(), line);
    }

    /**
     * Добавляет станцию метро
     *
     * @param station объект станции
     */
    public void addStation(Station station) {
        stations.add(station);
    }

    /**
     * Добавляет соединение между станциями
     *
     * @param from ID начальной станции
     * @param to ID конечной станции
     * @param weight время перемещения в минутах
     */
    public void addConnection(int from, int to, int weight) {
        // Добавляем в граф (списки смежности)
        graph.addEdge(from, to, weight);

        // Добавляем в матрицу смежности (для двух направлений)
        adjacencyMatrix[from][to] = weight;
        adjacencyMatrix[to][from] = weight;
    }

    /**
     * Возвращает станцию по её ID
     *
     * @param id ID станции
     * @return объект станции
     */
    public Station getStation(int id) {
        return stations.get(id);
    }

    /**
     * Находит ID станции по её названию (без учета регистра)
     * Использует Stream API для поиска
     *
     * @param name название станции
     * @return ID станции или -1 если не найдена
     */
    public int getStationId(String name) {
        return IntStream.range(0, stations.size())
                .filter(i -> stations.get(i).getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(-1);
    }

    /**
     * Возвращает список всех станций
     *
     * @return неизменяемый список станций
     */
    public List<Station> getStations() {
        return Collections.unmodifiableList(stations);
    }

    /**
     * Возвращает объект графа для работы со списками смежности
     *
     * @return объект Graph
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Возвращает матрицу смежности графа метро
     *
     * @return двумерный массив размером [количество_станций][количество_станций]
     */
    public int[][] getAdjacencyMatrix() {
        return adjacencyMatrix;
    }

    /**
     * Возвращает размер матрицы смежности (количество станций)
     *
     * @return количество станций в системе
     */
    public int getMatrixSize() {
        return adjacencyMatrix.length;
    }

    /**
     * Выполняет алгоритм Дейкстры, используя матрицу смежности
     *
     * @param startId ID начальной станции
     * @param endId ID конечной станции
     * @return Результат алгоритма Дейкстры
     */
    public DijkstraResult dijkstraWithMatrix(int startId, int endId) {
        int n = stations.size();
        int[] dist = new int[n];
        int[] prev = new int[n];
        boolean[] visited = new boolean[n];

        // Инициализация
        for (int i = 0; i < n; i++) {
            dist[i] = INF;
            prev[i] = -1;
        }
        dist[startId] = 0;

        // Основной цикл алгоритма Дейкстры
        for (int i = 0; i < n; i++) {
            // Находим непосещенную вершину с минимальным расстоянием
            int u = -1;
            int minDist = INF;
            for (int j = 0; j < n; j++) {
                if (!visited[j] && dist[j] < minDist) {
                    minDist = dist[j];
                    u = j;
                }
            }

            // Если все вершины посещены или достигли конечной
            if (u == -1 || u == endId) break;

            visited[u] = true;

            // Обновляем расстояния до соседей (используем строку матрицы)
            for (int v = 0; v < n; v++) {
                if (!visited[v] && adjacencyMatrix[u][v] < INF) {
                    int alt = dist[u] + adjacencyMatrix[u][v];
                    if (alt < dist[v]) {
                        dist[v] = alt;
                        prev[v] = u;
                    }
                }
            }
        }

        return new DijkstraResult(dist, prev);
    }

    /**
     * Восстанавливает путь по результатам алгоритма Дейкстры
     *
     * @param startId ID начальной станции
     * @param endId ID конечной станции
     * @param result Результат алгоритма Дейкстры
     * @return Список ID станций пути в порядке следования
     */
    public List<Integer> reconstructPath(int startId, int endId, DijkstraResult result) {
        List<Integer> path = new ArrayList<>();
        int[] prev = result.getPrev();

        // Если путь не существует
        if (prev[endId] == -1 && startId != endId) {
            return path;
        }

        // Восстанавливаем путь от конца к началу
        for (int at = endId; at != -1; at = prev[at]) {
            path.add(at);
        }

        // Разворачиваем путь
        Collections.reverse(path);

        return path;
    }

    /**
     * Получает путь по названиям станций (удобный метод для использования)
     *
     * @param startName Название начальной станции
     * @param endName Название конечной станции
     * @return Объект PathResult с информацией о пути
     * @throws IllegalArgumentException если станция не найдена
     */
    public PathResult findShortestPath(String startName, String endName) {
        int startId = getStationId(startName);
        int endId = getStationId(endName);

        if (startId == -1 || endId == -1) {
            throw new IllegalArgumentException("Станция не найдена: " +
                    (startId == -1 ? startName : endName));
        }

        // Используем алгоритм Дейкстры с матрицей смежности
        DijkstraResult result = dijkstraWithMatrix(startId, endId);
        List<Integer> pathIds = reconstructPath(startId, endId, result);

        // Преобразуем ID в станции с использованием Stream API
        List<Station> pathStations = pathIds.stream()
                .map(stations::get)
                .collect(Collectors.toList());

        int totalTime = result.getDist()[endId];

        return new PathResult(pathStations, totalTime);
    }

    /**
     * Возвращает список станций на указанной линии
     * Использует Stream API для фильтрации
     *
     * @param lineNumber номер линии
     * @return список станций на линии
     */
    public List<Station> getStationsByLine(int lineNumber) {
        return stations.stream()
                .filter(station -> station.getLine() == lineNumber)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает статистику по линиям метро
     * Использует Stream API для группировки
     *
     * @return Map где ключ - номер линии, значение - количество станций
     */
    public Map<Integer, Long> getLineStatistics() {
        return stations.stream()
                .collect(Collectors.groupingBy(
                        Station::getLine,
                        Collectors.counting()
                ));
    }
}