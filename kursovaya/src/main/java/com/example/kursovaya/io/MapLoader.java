package com.example.kursovaya.io;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.kursovaya.model.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;

/**
 * Утилитарный класс для загрузки данных метрополитена из JSON файла.
 * Использует библиотеку Jackson для парсинга JSON.
 * Класс содержит только статические методы и не предназначен для создания экземпляров.
 *
 * @author Student
 * @version 1.0
 */
public class MapLoader {
    private static final Logger logger = LogManager.getLogger(MapLoader.class);

    /**
     * Приватный конструктор для предотвращения создания экземпляров класса.
     * Выбрасывает исключение, если попытаться создать объект через рефлексию.
     */
    private MapLoader() {
        throw new IllegalStateException("MapLoader is a utility class and cannot be instantiated");
    }

    /**
     * Загружает карту метрополитена из JSON файла в ресурсах
     *
     * @param filename имя файла в директории resources (например, "map.json")
     * @return объект MetroMap с загруженными данными
     * @throws RuntimeException если файл не найден или содержит ошибки
     */
    public static MetroMap load(String filename) {
        logger.info("Loading metro map from file: {}", filename);
        MetroMap metroMap = null;

        try {
            InputStream in = MapLoader.class.getClassLoader().getResourceAsStream(filename);
            if (in == null) {
                logger.error("File {} not found in resources!", filename);
                throw new RuntimeException("File " + filename + " not found in resources!");
            }

            logger.debug("File found, starting JSON parsing");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(in);

            JsonNode stationsNode = root.get("stations");
            JsonNode linesNode = root.get("lines");
            JsonNode connectionsNode = root.get("connections");

            // Count stations
            int stationCount = 0;
            for (JsonNode line : stationsNode) {
                stationCount += line.size();
            }

            logger.info("Creating MetroMap with {} stations", stationCount);
            metroMap = new MetroMap(stationCount);

            // Load lines
            int linesLoaded = 0;
            for (JsonNode line : linesNode) {
                int num = line.get("number").asInt();
                String name = line.get("name").asText();
                String color = line.get("color").asText();

                metroMap.addLine(new MetroLine(num, name, color));
                linesLoaded++;
                logger.trace("Loaded line: {} - {} ({})", num, name, color);
            }
            logger.debug("Loaded {} metro lines", linesLoaded);

            // Load stations
            int id = 0;
            int currentLine = 1;
            int stationsLoaded = 0;

            for (JsonNode lineStations : stationsNode) {
                for (JsonNode stationName : lineStations) {
                    String name = stationName.asText();
                    metroMap.addStation(new Station(name, currentLine));
                    stationsLoaded++;
                    logger.trace("Loaded station: {} (line {})", name, currentLine);
                    id++;
                }
                currentLine++;
            }
            logger.debug("Loaded {} stations", stationsLoaded);

            // Load connections
            int connectionsLoaded = 0;
            for (JsonNode conn : connectionsNode) {
                int from = conn.get("from").asInt();
                int to = conn.get("to").asInt();
                int weight = conn.get("weight").asInt();

                metroMap.addConnection(from, to, weight);
                connectionsLoaded++;
                logger.trace("Loaded connection: {} -> {} ({} min)", from, to, weight);
            }
            logger.debug("Loaded {} connections", connectionsLoaded);

            logger.info("Metro map successfully loaded: {} lines, {} stations, {} connections",
                    linesLoaded, stationsLoaded, connectionsLoaded);

        } catch (Exception e) {
            logger.error("Error reading file {}: {}", filename, e.getMessage(), e);
            throw new RuntimeException("Error reading " + filename + ": " + e.getMessage(), e);
        }

        return metroMap;
    }
}