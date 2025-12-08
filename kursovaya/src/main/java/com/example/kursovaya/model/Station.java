package com.example.kursovaya.model;

import java.util.Objects;

/**
 * Класс, представляющий станцию метрополитена.
 * Каждая станция имеет уникальное название и принадлежит к определенной линии.
 *
 * @author Student
 * @version 1.0
 */
public class Station {
    private final String name;
    private final int line;

    /**
     * Создает новую станцию метро
     *
     * @param name название станции
     * @param line номер линии (1-5 для СПб метро)
     */
    public Station(String name, int line) {
        this.name = name;
        this.line = line;
    }

    /**
     * Возвращает название станции
     *
     * @return название станции
     */
    public String getName() {
        return name;
    }

    /**
     * Возвращает номер линии, на которой находится станция
     *
     * @return номер линии
     */
    public int getLine() {
        return line;
    }

    /**
     * Возвращает строковое представление станции
     *
     * @return строка в формате "Название (Линия X)"
     */
    @Override
    public String toString() {
        return name + " (Линия " + line + ")";
    }

    /**
     * Сравнивает станции по названию и линии
     *
     * @param o объект для сравнения
     * @return true если станции одинаковые
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Station)) return false;
        Station station = (Station) o;
        return line == station.line && Objects.equals(name, station.name);
    }

    /**
     * Возвращает хэш-код станции
     *
     * @return хэш-код
     */
    @Override
    public int hashCode() {
        return Objects.hash(name, line);
    }
}