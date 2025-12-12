package utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    // Folder where application data files are stored
    private final Path dataFolder;

    public FileService(String folderName) {
        this.dataFolder = Paths.get(folderName);

        // Create data folder if it doesn't exist
        try {
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readAllLines(String fileName) {
        Path filePath = dataFolder.resolve(fileName);

        // Return empty list if file not found
        if (!Files.exists(filePath)) {
            return new ArrayList<>();
        }

        try {
            return Files.readAllLines(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void appendLine(String fileName, String line) {
        Path filePath = dataFolder.resolve(fileName);

        // Append new line to the file (create file if needed)
        try (BufferedWriter bw = Files.newBufferedWriter(
                filePath,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        )) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeAllLines(String fileName, List<String> lines) {
        Path filePath = dataFolder.resolve(fileName);

        // Overwrite file with given lines
        try {
            Files.write(
                    filePath,
                    lines,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createIfNotExists(String fileName) {
        Path filePath = dataFolder.resolve(fileName);

        // Create empty file if missing
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
