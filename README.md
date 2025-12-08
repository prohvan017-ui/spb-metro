spb-metro

Программа для поиска кратчайшего пути в метрополитене Санкт-Петербурга.

Возможности

- Поиск оптимального маршрута между любыми двумя станциями
- Отображение времени в пути и количества пересадок
- Просмотр информации о матрице смежности
- Список всех станций с группировкой по линиям
- Логирование операций в файл

Требования

- Java 21 или выше
- JavaFX 21

Установка и запуск

Windows

1. Скачайте файлы spbmetro-1.0-fat.jar и run.bat из репозитория по пути kursovaya/build/libs
2. Скачайте Java JDK 21.0.8 (https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) для Windows x64 Installer
3. Скачайте SDK JavaFX 21.0.2 (https://jdk.java.net/javafx21/) для Windows/x64
4. Распакуйте JavaFX в папку с spbmetro-1.0-fat.jar
5. Вы можете запустить файл через консоль, открытой в папке с программой, командой
java --module-path "openjfx-21.0.2_windows-x64_bin-sdk\javafx-sdk-21.0.2\lib" --add-modules javafx.controls -jar spbmetro-1.0-fat.jar
или Вы можете запустить скрипт run.bat для быстрого запуска программы.

Linux/Mac

1. Скачайте файлы spbmetro-1.0-fat.jar и run.sh из репозитория по пути kursovaya/build/libs
2. Скачайте Java JDK 21.0.8 (https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html) для вашей операционной системы.
3. Скачайте SDK JavaFX 21.0.2 (https://jdk.java.net/javafx21/) для вашей операционной системы.
4. Распакуйте JavaFX в директорию с spbmetro-1.0-fat.jar
5. Вы можете запустить файл через консоль, открытой в директории с программой, командой
java --module-path "javafx-sdk-21.0.2/lib" --add-modules javafx.controls -jar spbmetro-1.0-fat.jar
или Вы можете запустить скрипт run.sh для быстрого запуска программы. Для запуска скрипта необходимо сделать его исполняемым: "chmod +x run.sh" и запустить "./run.sh".


