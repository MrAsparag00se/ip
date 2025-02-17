package vegetables;

import java.util.ArrayList;
import java.util.Scanner;

import vegetables.command.CommandHandler;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.task.Task;

/**
 * Runs the Vegetables program, that supports creating, listing,
 * marking, unmarking, deleting, and finding tasks.
 * Tasks can be of type ToDo, Deadline, or Event.
 * The program uses a file to persist tasks between sessions.
 */
public class Vegetables {
    /**
     * Starts the Vegetables program.
     * Processes user input until the user exits the program.
     * Initializes task storage, task manager, and command handler.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        TaskStorage taskStorage = new TaskStorage();
        ArrayList<Task> tasks = taskStorage.loadTasks();
        TaskManager taskManager = new TaskManager(tasks);

        CommandHandler commandHandler = new CommandHandler(taskManager, taskStorage);

        while (true) {
            String userInput = scanner.nextLine();

            commandHandler.executeCommand(userInput);
            if (userInput.equalsIgnoreCase("bye")) {
                break; // Exit the program
            }
        }
        scanner.close();
    }
}
