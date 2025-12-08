package com.example.kursovaya.model;

/**
 * Класс, представляющий линию метрополитена.
 * Линия имеет номер, название и цвет для отображения.
 *
 * @author Student
 * @version 1.0
 */
public class MetroLine {
    private final int number;
    private final String name;
    private final String color;

    /**
     * Создает новую линию метро
     *
     * @param number номер линии
     * @param name название линии
     * @param color цвет линии (HEX или название)
     */
    public MetroLine(int number, String name, String color) {
        this.number = number;
        this.name = name;
        this.color = color;
    }

    /**
     * Возвращает номер линии
     *
     * @return номер линии
     */
    public int getNumber() {
        return number;
    }

    /**
     * Возвращает название линии
     *
     * @return название линии
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает цвет линии
     *
     * @return цвет в формате строки
     */
    public String getColor() {
        return color;
    }
}