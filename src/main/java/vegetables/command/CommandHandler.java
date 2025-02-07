package vegetables.command;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

import vegetables.exception.VeggieException;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.task.Task;


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
     * Executes a command based on the user input.
     * <p>
     * This method interprets the user's input and executes the corresponding command.
     * It supports the following commands:
     * </p>
     * <ul>
     *     <li><b>"help"</b> - Displays a list of available commands.</li>
     *     <li><b>"list"</b> - Lists all tasks.</li>
     *     <li><b>"todo [description]"</b> - Adds a new to-do task.</li>
     *     <li><b>"deadline [description] /by [date]"</b> - Adds a new deadline task.</li>
     *     <li><b>"event [description] /at [date]"</b> - Adds a new event task.</li>
     *     <li><b>"mark [task number]"</b> - Marks a task as completed.</li>
     *     <li><b>"unmark [task number]"</b> - Marks a task as incomplete.</li>
     *     <li><b>"find [keyword]"</b> - Searches for tasks containing the given keyword.</li>
     *     <li><b>"delete [task number]"</b> - Removes a task from the list.</li>
     *     <li><b>"bye"</b> - Saves tasks and exits the application.</li>
     * </ul>
     * <p>
     * If the input does not match any known command, an error message is returned.
     * </p>
     *
     * @param userInput The command input provided by the user.
     * @return A response message indicating the result of executing the command.
     */

    // Issue with 'wrong' indentation of cae/default statements that cannot be fixed.
    @SuppressWarnings("CheckStyle")

    public String executeCommand(String userInput) {
        Command command = Command.fromInput(userInput);
        return switch (command) {
            case HELP -> displayHelp();
            case LIST -> listTasks();
            case TODO -> handleAddToDo(userInput);
            case DEADLINE -> handleAddDeadline(userInput);
            case EVENT -> handleAddEvent(userInput, taskManager);
            case MARK -> handleMarkTask(userInput);
            case UNMARK -> handleUnmarkTask(userInput);
            case FIND -> handleFindTask(userInput);
            case DELETE -> handleDeleteTask(userInput);
            case BYE -> {
                handleExit();
                yield "";
            }
            default -> "Unrecognised command!";
        };
    }
    private void handleExit() {
        taskStorage.saveTasks(taskManager.getTasks());
        String result = "Bye. Hope to see you again soon!";
        System.out.println(result);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        System.exit(0);
    }
    private String displayHelp() {
        return " Available Commands:\n"
                + " - todo [Task description]: Adds a task without a deadline.\n"
                + " - deadline [Task description] /by [Date/time]: Adds a task with a deadline.\n"
                + " - event [Task description] /from [Start time] /to [End time]: Adds an event task.\n"
                + " - list: Displays all tasks in the list.\n"
                + " - mark [Task number]: Marks a task as done.\n"
                + " - unmark [Task number]: Unmarks a task as not done.\n"
                + " - find [Keyword]: Finds a task by its keyword.\n"
                + " - delete [Task number]: Deletes a task from the list.\n"
                + " - bye: Exits the program.\n";
    }
    private String listTasks() {
        StringBuilder result = new StringBuilder();
        ArrayList<Task> tasks = taskManager.getTasks();
        if (tasks.isEmpty()) {
            result.append("No tasks added.\n");
        } else {
            result.append("Here are the tasks in your list:\n");
            for (int i = 0; i < tasks.size(); i++) {
                // Add 1 for 1-indexing
                result.append((i + 1) + "." + tasks.get(i) + "\n");
            }
        }
        return result.toString();
    }
    private String handleAddToDo(String userInput) {
        String taskDescription = "";

        // Extract description only if input is longer than "todo "
        if (userInput.length() > 4) {
            taskDescription = userInput.substring(5).trim();
        }

        if (taskManager.taskExists(taskDescription)) {
            return "Duplicate task detected! Task already exists.";
        }

        taskManager.addToDoTask(taskDescription);
        taskStorage.saveTasks(taskManager.getTasks());
        return "Got it. I've added this task: " + taskDescription;
    }

    private String handleAddDeadline(String userInput) {
        try {
            if (!userInput.contains("/by")) {
                throw new VeggieException("Correct format: deadline [Task description] /by [yyyy-MM-dd HH:mm]");
            }

            String[] parts = userInput.split("/by");
            // Handle missing "/by" value
            if (parts.length < 2) {
                throw new VeggieException("Missing deadline date. Use: /by [yyyy-MM-dd HH:mm]");
            }

            String taskDescription = parts[0].substring(9).trim();
            String by = parts[1].trim();

            // Validate description is not empty
            if (taskDescription.isEmpty()) {
                throw new VeggieException("Task description cannot be empty!");
            }

            // Validate date format
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime deadlineDateTime = LocalDateTime.parse(by, formatter);

            if (deadlineDateTime.isBefore(LocalDateTime.now())) {
                return "Error: Deadline cannot be in the past!";
            }

            if (taskManager.taskExists(taskDescription)) {
                return "Duplicate task detected! Task already exists.";
            }

            taskManager.addDeadlineTask(taskDescription, by);
            taskStorage.saveTasks(taskManager.getTasks());
            return "Got it. I've added this deadline task: " + taskDescription;
        } catch (DateTimeParseException e) {
            return "Error: Invalid time or time format. Use: yyyy-MM-dd HH:mm";
        } catch (VeggieException | IndexOutOfBoundsException e) {
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

            if (taskDescription.isEmpty()) {
                throw new VeggieException("Task description cannot be empty!");
            }

            String from = parts.length > 1 ? parts[1].split("/to")[0].trim() : "";
            String to = parts.length > 1 ? parts[1].split("/to")[1].trim() : "";

            // Convert start and end time strings to LocalDateTime
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime fromDateTime = LocalDateTime.parse(from, formatter);
            LocalDateTime toDateTime = LocalDateTime.parse(to, formatter);

            // Check for event clash using the abstracted method
            StringBuilder warningMessage = taskManager.checkEventClash(fromDateTime, toDateTime);

            if (fromDateTime.isBefore(LocalDateTime.now()) || toDateTime.isBefore(LocalDateTime.now())) {
                return "Error: Event times cannot be in the past!";
            }
            if (fromDateTime.isAfter(toDateTime)) {
                return "Error: Start time cannot be after end time!";
            }

            if (taskManager.taskExists(taskDescription)) {
                return "Duplicate task detected! Task already exists.";
            }

            taskManager.addEventTask(taskDescription, from, to);
            taskStorage.saveTasks(taskManager.getTasks());

            if (warningMessage != null) {
                return "Event added with a warning:\n" + warningMessage
                        + "\nNew event added: " + taskDescription
                        + "\nNow you have " + taskManager.getTasks().size() + " tasks in the list.";
            } else {
                return "Got it. I've added this event task:\n" + taskDescription
                        + "\nNow you have " + taskManager.getTasks().size() + " tasks in the list.";
            }

        } catch (DateTimeParseException e) {
            return "Error: Invalid time or time format. Use: yyyy-MM-dd HH:mm";
        } catch (VeggieException e) {
            return "Error adding event task: " + e.getMessage();
        }
    }

    private String handleMarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.markTaskAsDone(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks());
            return "Task marked as done.\n" + listTasks(); // Return the updated task list
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleUnmarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.unmarkTask(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks());
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

            String keyword = userInput.substring(5).trim();

            // Delegate the task searching to TaskManager
            ArrayList<Task> matchingTasks = taskManager.findTasksBySubstring(keyword);

            StringBuilder result = new StringBuilder();
            if (matchingTasks.isEmpty()) {
                result.append("No matching tasks found.\n");
            } else {
                result.append("Here are the matching tasks in your list:\n");
                for (int i = 0; i < matchingTasks.size(); i++) {
                    result.append((i + 1) + "." + matchingTasks.get(i) + "\n");
                }
            }
            return result.toString();
        } catch (VeggieException e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleDeleteTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.deleteTask(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks());
            return "Task deleted.\n" + listTasks(); // Return the updated task list
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
