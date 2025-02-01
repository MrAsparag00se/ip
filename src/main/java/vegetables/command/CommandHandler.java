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
    public String executeCommand(String userInput) {
        String result;
        if (userInput.equalsIgnoreCase("help")) {
            result = displayHelp();
        } else if (userInput.equalsIgnoreCase("list")) {
            result = listTasks();
        } else if (userInput.startsWith("todo")) {
            result = handleAddToDo(userInput);
        } else if (userInput.startsWith("deadline")) {
            result = handleAddDeadline(userInput);
        } else if (userInput.startsWith("event")) {
            result = handleAddEvent(userInput, taskManager);
        } else if (userInput.startsWith("mark")) {
            result = handleMarkTask(userInput);
        } else if (userInput.startsWith("unmark")) {
            result = handleUnmarkTask(userInput);
        } else if (userInput.startsWith("find")) {
            result = handleFindTask(userInput);
        } else if (userInput.startsWith("delete")) {
            result = handleDeleteTask(userInput);
        } else if (userInput.equalsIgnoreCase("bye")) {
            taskStorage.saveTasks(taskManager.getTasks());
            result = "Bye. Hope to see you again soon!";
        } else {
            result = "Unrecognised command!";
        }
        return result;
    }

    private String displayHelp() {
        return "____________________________________________________________\n"
                + " Available Commands:\n"
                + " - todo [Task description]: Adds a task without a deadline.\n"
                + " - deadline [Task description] /by [Date/time]: Adds a task with a deadline.\n"
                + " - event [Task description] /from [Start time] /to [End time]: Adds an event task.\n"
                + " - list: Displays all tasks in the list.\n"
                + " - mark [Task number]: Marks a task as done.\n"
                + " - unmark [Task number]: Unmarks a task as not done.\n"
                + " - find [Keyword]: Finds a task by its keyword.\n"
                + " - delete [Task number]: Deletes a task from the list.\n"
                + " - bye: Exits the program.\n"
                + "____________________________________________________________";
    }

    private String listTasks() {
        StringBuilder result = new StringBuilder("____________________________________________________________\n");
        ArrayList<Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            result.append("No tasks added.\n");
        } else {
            result.append("Here are the tasks in your list:\n");
            for (int i = 0; i < tasks.size(); i++) {
                result.append((i + 1) + "." + tasks.get(i) + "\n");
            }
        }
        result.append("____________________________________________________________");
        return result.toString();
    }

    private String handleAddToDo(String userInput) {
        String taskDescription = userInput.substring(5).trim();
        taskManager.addToDoTask(taskDescription);
        taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding
        return "Got it. I've added this task: " + taskDescription;
    }

    private String handleAddDeadline(String userInput) {
        try {
            if (!userInput.contains("/by")) {
                throw new VeggieException("Correct format: deadline [Task description] /by [yyyy-MM-dd HH:mm]");
            }

            String[] parts = userInput.split("/by");
            String taskDescription = parts[0].substring(9).trim();  // Ensure this is the right way to extract description
            String by = parts[1].trim();

            // Attempt to add the deadline task
            taskManager.addDeadlineTask(taskDescription, by);
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding
            return "Got it. I've added this deadline task: " + taskDescription;
        } catch (VeggieException e) {
            return "Error adding deadline task: " + e.getMessage();
        }
    }

    private String handleAddEvent(String userInput, TaskManager taskManager) {
        try {
            if (!userInput.contains("/from") || !userInput.contains("/to")) {
                throw new VeggieException("Correct format: event [Task description] /from [Start time] /to [End time]");
            }

            String[] parts = userInput.split("/from");
            String taskDescription = parts[0].substring(6).trim();
            String from = parts.length > 1 ? parts[1].split("/to")[0].trim() : "";
            String to = parts.length > 1 ? parts[1].split("/to")[1].trim() : "";

            taskManager.addEventTask(taskDescription, from, to);
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after adding

            return "Got it. I've added this task:\n" + taskDescription
                    + "\nNow you have " + taskManager.getTasks().size() + " tasks in the list.";

        } catch (VeggieException e) {
            return "Error adding event task: " + e.getMessage();
        }
    }

    private String handleMarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.markTaskAsDone(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after marking
            return "Task marked as done.\n" + listTasks(); // Return the updated task list
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleUnmarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.unmarkTask(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after unmarking
            return "Task marked as not done.\n" + listTasks(); // Return the updated task list
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleFindTask(String userInput) {
        try {
            if (userInput.length() <= 5) {
                throw new VeggieException("Please provide a keyword to search. Correct format: find [keyword]");
            }

            String keyword = userInput.substring(5).trim(); // Extract the keyword

            // Delegate the task searching to TaskManager
            ArrayList<Task> matchingTasks = taskManager.findTasksByDescription(keyword);

            StringBuilder result = new StringBuilder("____________________________________________________________\n");
            if (matchingTasks.isEmpty()) {
                result.append("No matching tasks found.\n");
            } else {
                result.append("Here are the matching tasks in your list:\n");
                for (int i = 0; i < matchingTasks.size(); i++) {
                    result.append((i + 1) + "." + matchingTasks.get(i) + "\n");
                }
            }
            result.append("____________________________________________________________");
            return result.toString();
        } catch (VeggieException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleDeleteTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.deleteTask(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks()); // Save tasks after deleting
            return "Task deleted.\n" + listTasks(); // Return the updated task list
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}