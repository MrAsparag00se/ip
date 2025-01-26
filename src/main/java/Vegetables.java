import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;


public class TaskManager {
    private ArrayList<Task> tasks;

    public TaskManager(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    public void addToDoTask(String description) {
        Task newTask = new ToDo(description);
        tasks.add(newTask);
    }

    public void addDeadlineTask(String description, String deadline) {
        try {
            Task newTask = new Deadline(description, deadline);
            tasks.add(newTask);
        } catch (VeggieException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public void addEventTask(String description, String from, String to) {
        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
    }

    public void markTaskAsDone(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        tasks.get(taskNumber - 1).markAsDone();
    }

    public void unmarkTask(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        tasks.get(taskNumber - 1).markAsNotDone();
    }

    public void deleteTask(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        tasks.remove(taskNumber - 1);
    }

    public ArrayList<Task> getTasks() {
        return tasks;
    }
}

public class TaskStorage {
    private static final String FILE_PATH = "./tasklist/veggied.txt";

    public void saveTasks(ArrayList<Task> tasks) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (Task task : tasks) {
                writer.write(task.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    public ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try (Scanner fileScanner = new Scanner(new File(FILE_PATH))) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                try {
                    Task task = Task.fromFileString(line);
                    tasks.add(task);
                } catch (VeggieException e) {
                    System.out.println("Error parsing task: " + e.getMessage());
                }
            }
        } catch (FileNotFoundException e) {
            System.out.println("Error loading tasks: " + e.getMessage());
        }
        return tasks;
    }
}


public class CommandHandler {
    private TaskManager taskManager;
    private TaskStorage taskStorage;

    public CommandHandler(TaskManager taskManager, TaskStorage taskStorage) {
        this.taskManager = taskManager;
        this.taskStorage = taskStorage;
    }

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
            handleAddEvent(userInput);
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
        System.out.println("Got it. I've added this task: " + taskDescription);
    }

    private void handleAddDeadline(String userInput) {
        String[] parts = userInput.split("/by");
        String taskDescription = parts[0].substring(9).trim();
        String by = parts[1].trim();
        taskManager.addDeadlineTask(taskDescription, by);
        System.out.println("Got it. I've added this deadline task: " + taskDescription);
    }

    private void handleAddEvent(String userInput) {
        String[] parts = userInput.split("/from");
        String taskDescription = parts[0].substring(6).trim();
        String from = parts[1].split("/to")[0].trim();
        String to = parts[1].split("/to")[1].trim();
        taskManager.addEventTask(taskDescription, from, to);
        System.out.println("Got it. I've added this event task: " + taskDescription);
    }

    private void handleMarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.markTaskAsDone(taskNumber);
            System.out.println("Task marked as done.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleUnmarkTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.unmarkTask(taskNumber);
            System.out.println("Task marked as not done.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void handleFindTask(String userInput) {
        // Implement your find task logic here
    }

    private void handleDeleteTask(String userInput) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            taskManager.deleteTask(taskNumber);
            System.out.println("Task deleted.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}

/**
 * Abstract class representing a task with a description and a completion status.
 * Provides methods for marking a task as done or not done, converting tasks to a file string,
 * and reconstructing tasks from a file string.
 */
abstract class Task {
    protected String description;
    protected boolean isDone;

    /**
     * Constructs a new Task with the specified description.
     * The task is initially marked as not done.
     *
     * @param description The description of the task.
     */
    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    /**
     * Converts the task to a string representation suitable for saving to a file.
     *
     * @return A string representation of the task in the file's format.
     */
    public abstract String toFileString(); // Save task to file

     /**
     * Marks the task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks the task as not done.
     */
    public void markAsNotDone() {
        isDone = false;
    }

    /**
     * Returns a string representation of the task, including its completion status and description.
     *
     * @return A string representation of the task.
     */
    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    /**
     * Reconstructs a task from its string representation in file format.
     * The format of the string is expected to be "TYPE | STATUS | DESCRIPTION [| ADDITIONAL INFO...]".
     *
     * @param taskString The string representation of the task.
     * @return A reconstructed Task object.
     * @throws VeggieException If the string format is invalid or the task type is unrecognized.
     */
    public static Task fromFileString(String taskString) throws VeggieException {
        String[] parts = taskString.split(" \\| ");
        String taskType = parts[0];
        boolean isDone = parts[1].equals("X");
        String description = parts[2];
        switch (taskType) {
            case "TODO":
                return new ToDo(description, isDone);
            case "DEADLINE":
                return new Deadline(description, parts[3], isDone);
            case "EVENT":
                return new Event(description, parts[3], parts[4], isDone);
            default:
                return null;
        }
    }
}

/**
 * Represents a "ToDo" task, which has no associated time constraints.
 * This class is a subclass of the abstract Task class.
 */
class ToDo extends Task {
    /**
     * Constructs a new ToDo task with the specified description.
     * The task is initially marked as not done.
     *
     * @param description The description of the ToDo task.
     */
    public ToDo(String description) {
        super(description);
    }

    /**
     * Constructs a new ToDo task with the specified description and completion status.
     *
     * @param description The description of the ToDo task.
     * @param isDone      The completion status of the ToDo task. {@code true} if the task is done,
     *                    {@code false} otherwise.
     */
    public ToDo(String description, boolean isDone) {
        super(description);
        this.isDone = isDone;
    }

    /**
     * Returns a string representation of the ToDo task, including its type, completion status, and description.
     * The format is: "T [status] description".
     *
     * @return A string representation of the ToDo task.
     */
    @Override
    public String toString() {
        return "T [" + (isDone ? "X" : " ") + "] " + description;
    }

    /**
     * Converts the ToDo task to a string representation suitable for saving to a file.
     * The format is: "TODO | status | description".
     *
     * - `status`: "X" if the task is done, "0" otherwise.
     *
     * @return A string representation of the ToDo task in file format.
     */
    @Override
    public String toFileString() {
        return "TODO | " + (isDone ? "X" : "0") + " | " + description;
    }
}

/**
 * Represents a 'Deadline' task.
 * The deadline is stored as a LocalDateTime object.
 */
class Deadline extends Task {
    private LocalDateTime by;
    private static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    /**
     * Constructs a new Deadline task with the specified description and deadline.
     *
     * @param description The description of the deadline task.
     * @param by          The deadline in the format "yyyy-MM-dd HH:mm" (e.g., "2025-01-22 18:00").
     * @throws VeggieException If the provided deadline is not in the expected format.
     */
    public Deadline(String description, String by) throws VeggieException {
        super(description);
        try {
            this.by = LocalDateTime.parse(by, inputFormatter);
        } catch (DateTimeParseException e) {
            throw new VeggieException("Invalid date format! Use yyyy-MM-dd HH:mm (e.g., 2023-01-22 18:00)");
        }
    }

    /**
     * Constructs a new Deadline task with the specified description, deadline, and completion status.
     *
     * @param description The description of the deadline task.
     * @param by          The deadline in the format "yyyy-MM-dd HH:mm".
     * @param isDone      The completion status of the deadline task. {@code true} if the task is done, {@code false} otherwise.
     * @throws VeggieException If the provided deadline is not in the expected format.
     */
    public Deadline(String description, String by, boolean isDone) throws VeggieException {
        this(description, by);
        this.isDone = isDone;
    }

    /**
     * Returns a string representation of the Deadline task, including its type, completion status, description, and deadline.
     * The format is: "D [status] description (by: formatted deadline)".
     *
     * @return A string representation of the Deadline task.
     */
    @Override
    public String toString() {
        return "D [" + (isDone ? "X" : " ") + "] " + description + " (by: " + by.format(displayFormatter) + ")";
    }

    /**
     * Converts the Deadline task to a string representation suitable for saving to a file.
     * The format is: "DEADLINE | status | description | deadline".
     *
     * - `status`: "X" if the task is done, "0" otherwise.
     *
     * @return A string representation of the Deadline task in file format.
     */
    @Override
    public String toFileString() {
        return "DEADLINE | " + (isDone ? "X" : "0") + " | " + description + " | " + by.format(inputFormatter);
    }
}

/**
 * Represents an 'Event' task.
 * An event has a start time and an end time.
 */
class Event extends Task {
    private String from;
    private String to;

    /**
     * Constructs a new Event task with the specified description, start time, and end time.
     *
     * @param description The description of the event.
     * @param from        The start time of the event.
     * @param to          The end time of the event.
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Constructs a new Event task with the specified description, start time, end time, and completion status.
     *
     * @param description The description of the event.
     * @param from        The start time of the event.
     * @param to          The end time of the event.
     * @param isDone      The completion status of the event. {@code true} if the task is done, {@code false} otherwise.
     */
    public Event(String description, String from, String to, boolean isDone) {
        super(description);
        this.from = from;
        this.to = to;
        this.isDone = isDone;
    }

    /**
     * Returns a string representation of the Event task, including its type, completion status, description, start time, and end time.
     * The format is: "E [status] description from: start time to: end time".
     *
     * @return A string representation of the Event task.
     */
    @Override
    public String toString() {
        return "E [" + (isDone ? "X" : " ") + "] " + description + " from: " + from + " to: " + to;
    }

    /**
     * Converts the Event task to a string representation suitable for saving to a file.
     * The format is: "EVENT | status | description | start time | end time".
     *
     * - `status`: "X" if the task is done, "0" otherwise.
     *
     * @return A string representation of the Event task in file format.
     */
    @Override
    public String toFileString() {
        return "EVENT | " + (isDone ? "X" : "0") + " | " + description + " | " + from + " | " + to;
    }
}

/**
 * Custom exception class for input validation.
 * This exception is used to handle errors specific to the application's logic.
 */
class VeggieException extends Exception {
    /**
     * Constructs a new {@code VeggieException} with the specified detail message.
     * The message provides additional information about the exception cause.
     *
     * @param message the detail message describing the exception.
     */
    public VeggieException(String message) {
        super(message);
    }
}

/**
 * Runs the Vegetables program, that supports creating, listing,
 * marking, unmarking, deleting, and finding tasks.
 * Tasks can be of type ToDo, Deadline, or Event.
 * The program uses a file to persist tasks between sessions.
 */
public class Vegetables {
    private static final String FILE_PATH = "./tasklist/veggied.txt";  // File path for storing tasks

    /**
     * Entry point of the Vegetables program.
     * Displays a welcome message and runs a command loop to process user input.
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
