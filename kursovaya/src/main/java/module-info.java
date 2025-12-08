/**
 * Модуль spbmetro.main содержит приложение для поиска маршрутов
 * в метрополитене Санкт-Петербурга.
 *
 * @author Student
 * @version 1.0
 */
module spbmetro.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires com.fasterxml.jackson.databind;
    requires org.apache.logging.log4j;

    opens com.example.kursovaya.model to javafx.fxml, com.fasterxml.jackson.databind;

    exports com.example.kursovaya.model;

}