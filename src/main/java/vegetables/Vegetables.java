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
     * Displays a welcome message and continuously processes user input
     * until the user exits the program.
     * Initializes task storage, task manager, and command handler.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        TaskStorage taskStorage = new TaskStorage();
        ArrayList<Task> tasks = taskStorage.loadTasks();
        TaskManager taskManager = new TaskManager(tasks);

        String veggieLogo =
                " _  _  ____  ___  ____  ____   __    ____  __    ____  ___ \n"
                        + "( \\/ )( ___)/ __)( ___)(_  _) /__\\  (  _ \\(  )  ( ___)/ __)\n"
                        + " \\  /  )__)( (_-. )__)   )(  /(__)\\  ) _ < )(__  )__) \\__ \\\n"
                        + "  \\/  (____)\\___/(____) (__)(__)(__)(____/(____)(____)(___/ \n";

        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Vegetables");
        System.out.println(veggieLogo);
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

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
