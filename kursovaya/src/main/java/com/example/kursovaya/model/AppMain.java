package com.example.kursovaya.model;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import com.example.kursovaya.util.DijkstraResult;
import com.example.kursovaya.io.MapLoader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Основной класс приложения с графическим интерфейсом.
 * Реализует поиск кратчайшего пути в метро Санкт-Петербурга
 * с использованием JavaFX для пользовательского интерфейса.
 *
 * @author Student
 * @version 1.0
 */
public class AppMain extends Application {

    private static final Logger logger = LogManager.getLogger(AppMain.class);
    private MetroMap metroMap;

    /**
     * Конструктор класса AppMain.
     * Создает графическое приложение для поиска маршрутов в метро Санкт-Петербурга.
     * Использует JavaFX для графического интерфейса.
     */
    public AppMain() {

    }

    /**
     * Точка входа в графическое приложение.
     * Инициализирует интерфейс и загружает данные метрополитена.
     *
     * @param stage основной контейнер JavaFX
     */
    @Override
    public void start(Stage stage) {
        logger.info("Starting Saint Petersburg Metro application");

        try {
            // Загружаем карту метро
            logger.debug("Loading metro map from map.json");
            metroMap = MapLoader.load("map.json");
            logger.info("Metro map loaded successfully. Stations: {}",
                    metroMap.getStations().size());

            // UI элементы
            ComboBox<String> fromBox = new ComboBox<>();
            ComboBox<String> toBox = new ComboBox<>();

            // Кнопки
            Button findBtn = new Button("Найти маршрут");
            Button showMatrixBtn = new Button("Показать информацию о матрице");
            Button showStationsBtn = new Button("Список всех станций");

            TextArea output = new TextArea();
            output.setEditable(false);
            output.setWrapText(true);
            output.setPrefRowCount(20);

            // Список имен станций с использованием Stream API
            List<String> stationNames = metroMap.getStations().stream()
                    .map(Station::getName)
                    .sorted()
                    .collect(Collectors.toList());

            logger.debug("Created list of {} stations using Stream API", stationNames.size());

            fromBox.getItems().addAll(stationNames);
            toBox.getItems().addAll(stationNames);

            // Обработчик поиска маршрута
            findBtn.setOnAction(e -> {
                String fromName = fromBox.getValue();
                String toName = toBox.getValue();

                if (fromName == null || toName == null) {
                    output.setText("Выберите обе станции!");
                    logger.warn("Attempt to search route without selecting stations");
                    return;
                }

                logger.info("Searching route from '{}' to '{}'", fromName, toName);

                int fromId = metroMap.getStationId(fromName);
                int toId = metroMap.getStationId(toName);

                if (fromId == -1 || toId == -1) {
                    output.setText("Ошибка в именах станций.");
                    logger.error("Station not found: fromName='{}', toName='{}'", fromName, toName);
                    return;
                }

                // Замер времени выполнения
                long startTime = System.nanoTime();

                // ИСПОЛЬЗУЕМ МАТРИЦУ СМЕЖНОСТИ
                logger.debug("Running Dijkstra algorithm with adjacency matrix");
                DijkstraResult res = metroMap.dijkstraWithMatrix(fromId, toId);

                long endTime = System.nanoTime();
                long duration = (endTime - startTime) / 1000; // микросекунды

                int[] dist = res.getDist();
                int[] prev = res.getPrev();

                // Проверяем, найден ли путь
                if (dist[toId] >= Integer.MAX_VALUE / 2) {
                    output.setText("Маршрут не найден.");
                    logger.warn("Route not found from '{}' to '{}'", fromName, toName);
                    return;
                }

                // Восстановление пути
                logger.debug("Reconstructing path from algorithm results");
                List<Integer> pathIds = metroMap.reconstructPath(fromId, toId, res);

                // Если путь не восстановился
                if (pathIds.isEmpty()) {
                    output.setText("Не удалось построить маршрут.");
                    logger.error("Failed to reconstruct path from {} to {}", fromId, toId);
                    return;
                }

                logger.info("Route found: {} stations, time {} minutes, calculation time {} μs",
                        pathIds.size(), dist[toId], duration);

                // Подсчет пересадок с использованием Stream API
                int transfers = countTransfersStream(pathIds, metroMap);

                // Формируем красивый вывод
                StringBuilder sb = new StringBuilder();
                sb.append("=== РЕЗУЛЬТАТЫ ПОИСКА МАРШРУТА ===\n\n");

                // Основная информация
                sb.append("Начальная станция: ").append(fromName).append("\n");
                sb.append("Конечная станция:  ").append(toName).append("\n");
                sb.append("Общее время:       ").append(dist[toId]).append(" минут\n");
                sb.append("Время расчета:     ").append(duration).append(" мкс\n");
                sb.append("Алгоритм:          Матрица смежности\n");
                sb.append("Количество станций: ").append(pathIds.size()).append("\n");
                sb.append("Количество пересадок: ").append(transfers).append("\n\n");
                logger.debug("Number of transfers in route: {}", transfers);

                // Детальный маршрут
                sb.append("=== ДЕТАЛЬНЫЙ МАРШРУТ ===\n");

                Station previousStation = null;
                int step = 1;
                for (int id : pathIds) {
                    Station currentStation = metroMap.getStation(id);

                    if (previousStation != null &&
                            previousStation.getLine() != currentStation.getLine()) {
                        sb.append("\n").append(step).append(".  ПЕРЕСАДКА\n");
                        sb.append("   с линии ").append(previousStation.getLine())
                                .append(" на линию ").append(currentStation.getLine()).append("\n");
                        step++;
                        logger.trace("Transfer from line {} to line {}",
                                previousStation.getLine(), currentStation.getLine());
                    }

                    sb.append(step).append(". ").append(currentStation.getName())
                            .append(" (линия ").append(currentStation.getLine()).append(")\n");
                    step++;
                    previousStation = currentStation;
                }

                output.setText(sb.toString());
                logger.debug("Results displayed to user successfully");
            });

            // Обработчик кнопки "Показать информацию о матрице"
            showMatrixBtn.setOnAction(e -> {
                logger.debug("Requesting adjacency matrix information");
                int[][] matrix = metroMap.getAdjacencyMatrix();
                int totalStations = matrix.length;

                // Подсчитываем статистику с использованием Stream API
                long connections = 0;
                final int[] maxWeight = {0};

                for (int i = 0; i < totalStations; i++) {
                    final int row = i;
                    long rowConnections = IntStream.range(i + 1, totalStations)
                            .filter(j -> matrix[row][j] < Integer.MAX_VALUE / 2)
                            .peek(j -> {
                                if (matrix[row][j] > maxWeight[0]) {
                                    maxWeight[0] = matrix[row][j];
                                }
                            })
                            .count();
                    connections += rowConnections;
                }

                logger.info("Adjacency matrix: {} stations, {} connections, max weight={}",
                        totalStations, connections, maxWeight[0]);

                StringBuilder sb = new StringBuilder();
                sb.append("=== ИНФОРМАЦИЯ О МАТРИЦЕ СМЕЖНОСТИ ===\n\n");
                sb.append("Общее количество станций: ").append(totalStations).append("\n");
                sb.append("Размер матрицы:          ").append(totalStations).append(" × ").append(totalStations).append("\n");
                sb.append("Количество соединений:   ").append(connections).append("\n");
                sb.append("Максимальное время между станциями: ").append(maxWeight[0]).append(" мин\n");
                sb.append("Пустых ячеек (∞):        ").append(totalStations*totalStations - connections - totalStations).append("\n\n");

                sb.append("       ");
                for (int j = 0; j < Math.min(5, totalStations); j++) {
                    sb.append(String.format("  [%2d]  ", j));
                }
                sb.append("\n").append("       ");
                for (int j = 0; j < Math.min(5, totalStations); j++) {
                    sb.append("-------");
                }
                sb.append("\n");

                // Строки матрицы
                for (int i = 0; i < Math.min(5, totalStations); i++) {
                    sb.append(String.format("[%2d] | ", i));
                    for (int j = 0; j < Math.min(5, totalStations); j++) {
                        if (i == j) {
                            sb.append(String.format("  %2s   ", "0"));
                        } else if (matrix[i][j] >= Integer.MAX_VALUE / 2) {
                            sb.append(String.format("  %2s   ", "∞"));
                        } else {
                            sb.append(String.format("  %2d   ", matrix[i][j]));
                        }
                    }
                    sb.append("\n");
                }

                sb.append("\n=== ПРИМЕР СТАНЦИЙ ===\n");
                // Используем Stream API для отображения первых 5 станций
                IntStream.range(0, Math.min(5, totalStations))
                        .forEach(i -> sb.append(i).append(": ").append(metroMap.getStation(i)).append("\n"));

                output.setText(sb.toString());
                logger.debug("Matrix information displayed");
            });

            // Обработчик кнопки "Список всех станций"
            showStationsBtn.setOnAction(e -> {
                logger.debug("Requesting list of all stations");
                StringBuilder sb = new StringBuilder();
                sb.append("=== ВСЕ СТАНЦИИ МЕТРО СПб ===\n\n");

                List<Station> stations = metroMap.getStations();
                sb.append("Всего станций: ").append(stations.size()).append("\n\n");
                logger.info("Total stations in system: {}", stations.size());

                // Группируем по линиям с использованием Stream API
                for (int lineNum = 1; lineNum <= 5; lineNum++) {
                    final int currentLine = lineNum;
                    sb.append("--- Линия ").append(lineNum).append(" ---\n");

                    // Фильтруем станции по линии
                    List<String> lineStations = stations.stream()
                            .filter(s -> s.getLine() == currentLine)
                            .map(Station::getName)
                            .collect(Collectors.toList());

                    lineStations.forEach(name -> sb.append("  ").append(name).append("\n"));
                    sb.append("Всего на линии: ").append(lineStations.size()).append("\n\n");
                    logger.debug("Line {}: {} stations", lineNum, lineStations.size());
                }

                output.setText(sb.toString());
                logger.debug("Station list displayed");
            });

            // Layout
            HBox searchBox = new HBox(10,
                    new VBox(5, new Label("От:"), fromBox),
                    new VBox(5, new Label("До:"), toBox),
                    findBtn
            );

            HBox buttonBox = new HBox(10, showMatrixBtn, showStationsBtn);

            VBox root = new VBox(10,
                    new Label("Поиск маршрута в метро Санкт-Петербурга"),
                    searchBox,
                    buttonBox,
                    new Separator(),
                    new Label("Результат:"),
                    output
            );
            root.setPadding(new Insets(15));

            Scene scene = new Scene(root, 600, 700);
            stage.setScene(scene);
            stage.setTitle("Санкт-Петербургский метрополитен - Поиск маршрута");
            stage.setResizable(false);
            stage.setWidth(700);
            stage.setHeight(600);

            stage.setOnCloseRequest(event -> {
                logger.info("Application closing");
            });

            stage.show();
            logger.info("GUI initialized successfully");

        } catch (Exception ex) {
            logger.error("Critical error during application startup", ex);
            showErrorDialog("Критическая ошибка",
                    "Не удалось запустить приложение: " + ex.getMessage());
        }
    }

    /**
     * Подсчитывает количество пересадок в маршруте с использованием Stream API
     *
     * @param pathIds список ID станций в маршруте
     * @param metroMap карта метрополитена
     * @return количество пересадок
     */
    private int countTransfersStream(List<Integer> pathIds, MetroMap metroMap) {
        return (int) IntStream.range(1, pathIds.size())
                .filter(i -> metroMap.getStation(pathIds.get(i)).getLine()
                        != metroMap.getStation(pathIds.get(i-1)).getLine())
                .count();
    }

    /**
     * Показывает диалоговое окно с сообщением об ошибке
     *
     * @param title заголовок окна
     * @param message текст сообщения
     */
    private void showErrorDialog(String title, String message) {
        logger.error("Showing error dialog: {} - {}", title, message);
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Главный метод приложения
     *
     * @param args аргументы командной строки
     */
    public static void main(String[] args) {
        logger.info("==========================================");
        logger.info("Starting Saint Petersburg Metro application");
        logger.info("Java version: {}", System.getProperty("java.version"));
        logger.info("Working directory: {}", System.getProperty("user.dir"));
        logger.info("Command line arguments: {}", (Object) args);

        try {
            launch(args);
            logger.info("Application finished successfully");
        } catch (Exception e) {
            logger.fatal("Critical error in main() method", e);
            System.err.println("Critical error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}