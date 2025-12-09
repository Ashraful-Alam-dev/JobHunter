package utils;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileService {

    private final Path dataFolder;

    public FileService(String folderName) {
        this.dataFolder = Paths.get(folderName);

        try {
            if (!Files.exists(dataFolder)) {
                Files.createDirectories(dataFolder);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------------------
    // READ ALL LINES
    // ------------------------------
    public List<String> readAllLines(String fileName) {
        Path filePath = dataFolder.resolve(fileName);

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

    // ------------------------------
    // APPEND A SINGLE LINE
    // ------------------------------
    public void appendLine(String fileName, String line) {
        Path filePath = dataFolder.resolve(fileName);

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

    // ------------------------------
    // WRITE (OVERWRITE) ALL LINES
    // ------------------------------
    public void writeAllLines(String fileName, List<String> lines) {
        Path filePath = dataFolder.resolve(fileName);

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

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
