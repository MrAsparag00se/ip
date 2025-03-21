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
        assert taskManager != null : "TaskManager should not be null";
        assert taskStorage != null : "TaskStorage should not be null";

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
    public String executeCommand(String userInput) {
        assert userInput != null : "User input should not be null";

        Command command = Command.fromInput(userInput);
        assert command != null : "Command should not be null";

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
        assert taskManager.getTasks() != null : "Task list should not be null after saving";
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
        assert tasks != null : "Task list should not be null";
        if (tasks.isEmpty()) {
            result.append("No tasks added.\n");
        } else {
            result.append("🌅 Here are the crops we've harvested so far: 🌾\n");
            for (int i = 0; i < tasks.size(); i++) {
                assert tasks.get(i) != null : "Task should not be null";
                // Add 1 for 1-indexing
                result.append((i + 1) + "." + tasks.get(i) + "\n");
            }
        }
        return result.toString();
    }

    private String handleAddToDo(String userInput) {
        assert userInput.startsWith("todo") : "Invalid ToDo command format";
        String taskDescription = "";

        if (userInput.length() > 4) {
            taskDescription = userInput.substring(5).trim();
        }
        if (taskDescription.isEmpty()) {
            return "Error: Task description cannot be empty!";
        }
        if (taskManager.taskExists(taskDescription)) {
            return "Duplicate task detected! Task already exists.";
        }
        taskManager.addToDoTask(taskDescription);
        taskStorage.saveTasks(taskManager.getTasks());
        return "\uD83C\uDF3B Great! You've planted a new to-do task: " + taskDescription;
    }

    private String handleAddDeadline(String userInput) {
        try {
            if (!userInput.contains("/by")) {
                throw new VeggieException("Correct format: deadline [Task description] /by [yyyy-MM-dd HH:mm]");
            }
            String[] parts = userInput.split("/by");
            if (parts.length < 2) {
                throw new VeggieException("Missing deadline date. Use: /by [yyyy-MM-dd HH:mm]");
            }

            String taskDescription = parts[0].substring(9).trim();
            String by = parts[1].trim();
            if (taskDescription.isEmpty()) {
                throw new VeggieException("Task description cannot be empty!");
            }

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

            return "\uD83C\uDF3E Great! You've planted a new deadline task: " + taskDescription;
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
                return "\uD83C\uDF3B Great! You've planted a new event task: \n" + taskDescription
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
            return "✅ This task is fully grown! It's time to harvest it. Task marked as done. "
                    + "🌾\n" + listTasks();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleUnmarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.unmarkTask(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks());
            return "🌱 Oops! Looks like this task still needs some more time in the soil. "
                    + "Task marked as not done. 🌾\n" + listTasks();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String handleFindTask(String userInput) {
        try {
            assert userInput != null : "User input should not be null";
            assert userInput.startsWith("find") : "Command should start with 'find'";

            if (userInput.length() <= 5) {
                throw new VeggieException("Please provide a keyword to search. Correct format: find [keyword]");
            }
            String keyword = userInput.substring(5).trim();
            assert !keyword.isEmpty() : "Keyword should not be empty after trimming";

            // Delegate the task searching to TaskManager
            ArrayList<Task> matchingTasks = taskManager.findTasksBySubstring(keyword);
            assert matchingTasks != null : "findTasksBySubstring() should not return null";

            StringBuilder result = new StringBuilder();
            if (matchingTasks.isEmpty()) {
                result.append("No matching tasks found.\n");
            } else {
                result.append("\uD83D\uDD0D Searching through the garden beds "
                        + "for your tasks... Here's what I found!\n");
                for (int i = 0; i < matchingTasks.size(); i++) {
                    assert matchingTasks.get(i) != null : "Task in the matching list should not be null";
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
            if (userInput.split(" ").length < 2 || userInput.split(" ")[1].isEmpty()) {
                return "Error: Please specify a task number to delete.";
            }

            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            assert taskNumber > 0 : "Task number should be positive";

            taskManager.deleteTask(taskNumber);
            taskStorage.saveTasks(taskManager.getTasks());
            return "🌿 Weeding time! The task has been pulled from the garden. 🧑‍🌾\n"
                    + listTasks();
        } catch (NumberFormatException e) {
            return "Error: Task number must be a valid integer.";
        } catch (IndexOutOfBoundsException e) {
            return "Error: Invalid task index: " + userInput.split(" ")[1]; // Capture the task number in the error
        } catch (VeggieException e) {
            return "Error: " + e.getMessage();
        } catch (Exception e) {
            return "Error: An unexpected error occurred.";
        }
    }
}
