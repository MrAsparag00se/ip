package vegetables.storage;

import vegetables.task.Task;
import vegetables.exception.VeggieException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.util.Scanner;

/**
 * The TaskStorage class is responsible for saving and loading tasks to and from a file.
 * It ensures that tasks are persisted across program executions and handles file I/O operations.
 * If the file or necessary directories don't exist, it will create them.
 */
public class TaskStorage {
    private static final String FILE_PATH = "./SHOPPING/list.txt";

    /**
     * Saves the given list of tasks to a file. If the required directories do not exist,
     * they will be created automatically. If the file already exists, it will be overwritten
     * with the current list of tasks.
     * <p>
     * Each task is written to the file using its {@link Task#toFileString()} method.
     * If a null task is encountered, a warning message is printed to the console.
     * </p>
     *
     * @param tasks The list of tasks to be saved to the file. Each task must implement
     *             the {@link Task#toFileString()} method to ensure correct formatting.
     * @return A message indicating whether the tasks were successfully saved or if an error occurred.
     */
    public String saveTasks(ArrayList<Task> tasks) {
        File file = new File(FILE_PATH);

        // Ensure the directory exists
        createParentDirectories(file);

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writeTasksToFile(writer, tasks);
        } catch (IOException e) {
            return handleSaveError(e);
        }

        return "Tasks have been successfully saved to the file.";
    }

    private void createParentDirectories(File file) {
        file.getParentFile().mkdirs();  // Create parent directories if they don't exist
    }

    private void writeTasksToFile(BufferedWriter writer, ArrayList<Task> tasks) throws IOException {
        for (Task task : tasks) {
            if (task != null) {
                writer.write(task.toFileString());
                writer.newLine();
            } else {
                System.out.println("Warning: Encountered a null task while saving.");
            }
        }
    }

    private String handleSaveError(IOException e) {
        return "Error saving tasks to file: " + e.getMessage();
    }

    /**
     * Loads tasks from a file. If the file does not exist, it returns an empty list.
     * <p>
     * Each line in the file is parsed into a {@link Task} object using {@link Task#fromFileString(String)}.
     * Any errors encountered during task parsing are logged.
     * </p>
     *
     * @return An {@link ArrayList} of tasks loaded from the file. If the file doesn't exist, returns an empty list.
     */
    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();

        // Check if the file exists and load the tasks
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return tasks;  // Return empty list if the file doesn't exist
        }

        try (Scanner fileScanner = new Scanner(file)) {
            loadTasksFromFile(fileScanner, tasks);
        } catch (FileNotFoundException e) {
            handleFileLoadingError(e);
        }

        return tasks;
    }

    private void loadTasksFromFile(Scanner fileScanner, ArrayList<Task> tasks) {
        while (fileScanner.hasNextLine()) {
            String line = fileScanner.nextLine();
            try {
                Task task = Task.fromFileString(line);  // This can throw VeggieException
                tasks.add(task);
            } catch (VeggieException e) {
                System.out.println("Error parsing task from file: " + e.getMessage());
            }
        }
    }

    private void handleFileLoadingError(FileNotFoundException e) {
        System.out.println("Error loading tasks: " + e.getMessage());
    }
}
