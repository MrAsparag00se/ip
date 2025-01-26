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
     * Saves the tasks to a file. If the file or directories don't exist, it will create them.
     * If the file already exists, it will overwrite the contents with the current tasks.
     * <p>
     * Each task is written to the file using its {@link Task#toFileString()} method.
     * </p>
     *
     * @param tasks The list of tasks to be saved to the file.
     */
    public void saveTasks(ArrayList<Task> tasks) {
        try {
            // Ensure the directory exists
            File file = new File(FILE_PATH);
            //file.getParentFile().mkdirs();  // Create parent directories if they don't exist

            // Now write the tasks to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                for (Task task : tasks) {
                    if (task != null) {
                        writer.write(task.toFileString());
                        writer.newLine();
                    } else {
                        System.out.println("Warning: Encountered a null task while saving.");
                    }
                }
            }
            System.out.println("Tasks have been successfully saved to the file.");
        } catch (IOException e) {
            System.out.println("Error saving tasks to file: " + e.getMessage());
        }
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
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return tasks;  // Return empty list if the file doesn't exist
            }

            try (Scanner fileScanner = new Scanner(file)) {
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
        } catch (FileNotFoundException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }
}
