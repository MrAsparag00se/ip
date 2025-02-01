package vegetables.command;

import vegetables.task.Task;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.exception.VeggieException;
import java.util.ArrayList;

/**
 * CommandHandler is responsible for processing and executing user commands
 * related to task management. It interacts with the TaskManager and TaskStorage
 * to perform operations such as adding tasks, listing tasks, marking tasks,
 * and saving/loading tasks to/from a file.
 */
public class CommandHandler {
    private final TaskManager taskManager;
    private final TaskStorage taskStorage;

    /**
     * Constructs a CommandHandler instance with the specified TaskManager and TaskStorage.
     *
     * @param taskManager The TaskManager that handles task-related operations.
     * @param taskStorage The TaskStorage used for saving and loading tasks from a file.
     */
    public CommandHandler(TaskManager taskManager, TaskStorage taskStorage) {
        this.taskManager = taskManager;
        this.taskStorage = taskStorage;
    }

    /**
     * Executes a command based on the user input. The method handles various
     * commands such as "help", "list", "todo", "deadline", "event", "mark", "unmark",
     * "find", "delete", and "bye".
     *
     * @param userInput The command input provided by the user.
     */
    public void executeCommand(String userInput) {
        if (userInput.equalsIgnoreCase("help")) {
            displayHelp();
        } else if (userInput.equalsIgnoreCase("list")) {
            listTasks();
        } else if (userInput.startsWith("todo")) {
            handleAddToDo(userInput);
        } else if (userInput.startsWith("deadline")) {
            handleAddDeadline(userInput);
        } else if (userInput.startsWith("event")) {
            handleAddEvent(userInput, taskManager);
        } else if (userInput.startsWith("mark")) {
            handleMarkTask(userInput);
        } else if (userInput.startsWith("unmark")) {
            handleUnmarkTask(userInput);
        } else if (userInput.startsWith("find")) {
            handleFindTask(userInput);
        } else if (userInput.startsWith("delete")) {
            handleDeleteTask(userInput);
        } else if (userInput.equalsIgnoreCase("bye")) {
            System.out.println("Bye. Hope to see you again soon!");
            taskStorage.saveTasks(taskManager.getTasks());
        } else {
            System.out.println("Unrecognised command!");
        }
    }

    private void displayHelp() {
        System.out.println("____________________________________________________________");
        System.out.println(" Available Commands:");
        System.out.println(" - todo [Task description]: Adds a task without a deadline.");
        System.out.println(" - deadline [Task description] /by [Date/time]: Adds a task with a deadline.");
        System.out.println(" - event [Task description] /from [Start time] /to [End time]: Adds an event task.");
        System.out.println(" - list: Displays all tasks in the list.");
        System.out.println(" - mark [Task number]: Marks a task as done.");
        System.out.println(" - unmark [Task number]: Unmarks a task as not done.");
        System.out.println(" - find [Keyword]: Finds a task by its keyword.");
        System.out.println(" - delete [Task number]: Deletes a task from the list.");
        System.out.println(" - bye: Exits the program.");
        System.out.println("____________________________________________________________");
    }

    private void listTasks() {
        System.out.println("____________________________________________________________");
        ArrayList<Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks added.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i));
            }
        }
        System.out.println("____________________________________________________________");
    }

    private void handleAddToDo(String userInput) {
        String taskDescription = userInput.substring(5).trim();
        taskManager.addToDoTask(taskDescription);
        System.out.println("Got it. I've added this task: " + taskDescription);        taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding
        taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding
    }

    private void handleAddDeadline(String userInput) {
        try {
            if (!userInput.contains("/by")) {
                throw new VeggieException("Correct format: deadline [Task description] /by [yyyy-MM-dd HH:mm]");
            }

            String[] parts = userInput.split("/by");
            String taskDescription = parts[0].substring(9).trim();  // Ensure this is the right way to extract description
            String by = parts[1].trim();

            // Attempt to add the deadline task
            taskManager.addDeadlineTask(taskDescription, by);
            System.out.println("Got it. I've added this deadline task: " + taskDescription);
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding
        } catch (VeggieException e) {
            // Handle exception gracefully and print the message
            System.out.println("Error adding deadline task: " + e.getMessage());
        }
    }

    // Update the method to accept TaskManager
    private void handleAddEvent(String userInput, TaskManager taskManager) {
        try {
            if (!userInput.contains("/from") || !userInput.contains("/to")) {
                throw new VeggieException("Correct format: event [Task description] /from [Start time] /to [End time]");
            }

            String[] parts = userInput.split("/from");
            String taskDescription = parts[0].substring(6).trim();
            String from = parts.length > 1 ? parts[1].split("/to")[0].trim() : "";
            String to = parts.length > 1 ? parts[1].split("/to")[1].trim() : "";

            // Now we use taskManager to add the event task
            taskManager.addEventTask(taskDescription, from, to);

            // Confirm the task was added
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + taskDescription);
            System.out.println(" Now you have " + taskManager.getTasks().size() + " tasks in the list.");
            System.out.println("____________________________________________________________");

            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding
        } catch (VeggieException e) {
            System.out.println("____________________________________________________________");
            System.out.println(e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    private void handleMarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.markTaskAsDone(taskNumber);
            System.out.println("Task marked as done.");
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after marking
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleUnmarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.unmarkTask(taskNumber);
            System.out.println("Task marked as not done.");
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after unmarking
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleFindTask(String userInput) {
        try {
            if (userInput.length() <= 5) {
                throw new VeggieException("Please provide a keyword to search. Correct format: find [keyword]");
            }

            String keyword = userInput.substring(5).trim(); // Extract the keyword

            // Delegate the task searching to TaskManager
            ArrayList<Task> matchingTasks = taskManager.findTasksByDescription(keyword);

            // Print matching tasks
            System.out.println("____________________________________________________________");
            if (matchingTasks.isEmpty()) {
                System.out.println("No matching tasks found.");
            } else {
                System.out.println("Here are the matching tasks in your list:");
                for (int i = 0; i < matchingTasks.size(); i++) {
                    System.out.println((i + 1) + "." + matchingTasks.get(i).toString());
                }
            }
            System.out.println("____________________________________________________________");

        } catch (VeggieException e) {
            System.out.println("____________________________________________________________");
            System.out.println(e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    private void handleDeleteTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.deleteTask(taskNumber);
            System.out.println("Task deleted.");
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after deleting
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /*
    * EDIT THIS COMMENT:
    * GUI-Specific processing
    * */
    public String processGuiCommand(String input) {
        try {
            if (input.equalsIgnoreCase("list")) {
                return formatTasksForGui(taskManager.getTasks());
            } else if (input.startsWith("todo")) {
                return addTodoForGui(input);
            }
            // Add other command cases
            return "Command processed: " + input;
        } finally {
            taskStorage.saveTasks(taskManager.getTasks());
        }
    }

    private String formatTasksForGui(ArrayList<Task> tasks) {
        if (tasks.isEmpty()) return "No tasks yet!";

        StringBuilder sb = new StringBuilder("Your tasks:\n");
        for (int i = 0; i < tasks.size(); i++) {
            sb.append(i + 1).append(". ").append(tasks.get(i)).append("\n");
        }
        return sb.toString();
    }

    private String addTodoForGui(String input) {
        String description = input.substring(5).trim();
        taskManager.addToDoTask(description);
        return "Added todo: " + description;
    }


}