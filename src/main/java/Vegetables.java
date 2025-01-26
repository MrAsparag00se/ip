import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

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

        // Load tasks from file
        ArrayList<Task> tasks = loadTasks();

        // ASCII Art for "VEGETABLES"
        String veggieLogo =
                " _  _  ____  ___  ____  ____   __    ____  __    ____  ___ \n"
                        + "( \\/ )( ___)/ __)( ___)(_  _) /__\\  (  _ \\(  )  ( ___)/ __)\n"
                        + " \\  /  )__)( (_-. )__)   )(  /(__)\\  ) _ < )(__  )__) \\__ \\\n"
                        + "  \\/  (____)\\___/(____) (__)(__)(__)(____/(____)(____)(___/ \n";

        // Print greeting and ASCII Art
        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Vegetables");
        System.out.println(veggieLogo);
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        // Infinite loop to process user input
        while (true) {
            // Read user input
            String userInput = scanner.nextLine();

            // Handle 'help' command to display available commands
            if (userInput.equalsIgnoreCase("help")) {
                displayHelp();
            }
            // Handle 'list' command to display all tasks
            else if (userInput.equalsIgnoreCase("list")) {
                listTasks(tasks);
            }
            // Handle task addition (ToDo, Deadline, Event)
            else if (userInput.startsWith("todo")) {
                handleAddToDo(userInput, tasks);
            }
            else if (userInput.startsWith("deadline")) {
                handleAddDeadline(userInput, tasks);
            }
            else if (userInput.startsWith("event")) {
                handleAddEvent(userInput, tasks);
            }
            // Handle 'mark' command to mark tasks as done
            else if (userInput.startsWith("mark")) {
                handleMarkTask(userInput, tasks);
            }
            // Handle 'unmark' command to unmark tasks
            else if (userInput.startsWith("unmark")) {
                handleUnmarkTask(userInput, tasks);
            }
            else if (userInput.startsWith("find")) { // Add to help
                handleFindTask(userInput, tasks);
            }

            // Handle 'delete' command to remove tasks
            else if (userInput.startsWith("delete")) {
                handleDeleteTask(userInput, tasks);
            }
            // Handle 'bye' command to exit the program
            else if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                saveTasks(tasks);  // Save tasks when exiting
                break; // Break the loop and exit the program
            }
            // Handle unrecognized commands
            else {
                System.out.println("____________________________________________________________");
                System.out.println(" Unrecognised command! Please try again.");
                System.out.println("____________________________________________________________");
            }
        }

        // Close scanner
        scanner.close();
    }

    // Display help information
    private static void displayHelp() {
        System.out.println("____________________________________________________________");
        System.out.println(" Available Commands:");
        System.out.println(" - todo [Task description]: Adds a task without a deadline.");
        System.out.println(" - deadline [Task description] /by [Date/time]: Adds a task with a deadline.");
        System.out.println(" - event [Task description] /from [Start time] /to [End time]: Adds an event task.");
        System.out.println(" - list: Displays all tasks in the list.");
        System.out.println(" - mark [Task number]: Marks a task as done.");
        System.out.println(" - unmark [Task number]: Unmarks a task as not done.");
        System.out.println(" - delete [Task number]: Deletes a task from the list.");
        System.out.println(" - bye: Exits the program.");
        System.out.println("____________________________________________________________");
    }

    // List all tasks
    private static void listTasks(ArrayList<Task> tasks) {
        System.out.println("____________________________________________________________");
        if (tasks.size() == 0) {
            System.out.println("No tasks added.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + "." + tasks.get(i).toString());
            }
        }
        System.out.println("____________________________________________________________");
    }

    // Add a todo task
    private static void handleAddToDo(String userInput, ArrayList<Task> tasks) {
        try {
            if (userInput.length() <= 5) {
                throw new VeggieException("Correct format: todo [Task description]");
            }
            String taskDescription = userInput.substring(5).trim();
            Task newTask = new ToDo(taskDescription);  // Create the ToDo task
            if (newTask != null) {
                tasks.add(newTask);  // Add the task to the list
                System.out.println("____________________________________________________________");
                System.out.println(" Got it. I've added this task:");
                System.out.println("   " + newTask);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println("____________________________________________________________");
            }
        } catch (VeggieException e) {
            System.out.println("____________________________________________________________");
            System.out.println(e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    // Add a deadline task
    private static void handleAddDeadline(String userInput, ArrayList<Task> tasks) {
        try {
            if (!userInput.contains("/by")) {
                throw new VeggieException("Correct format: deadline [Task description] /by [yyyy-MM-dd HH:mm]");
            }
            String[] parts = userInput.split("/by");
            String taskDescription = parts[0].substring(9).trim();
            String by = parts[1].trim();
            Task newTask = new Deadline(taskDescription, by);
            tasks.add(newTask);
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + newTask);
            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
            saveTasks(tasks);  // Save after adding task
        } catch (VeggieException e) {
            System.out.println("____________________________________________________________");
            System.out.println(e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    // Add an event task
    private static void handleAddEvent(String userInput, ArrayList<Task> tasks) {
        try {
            if (!userInput.contains("/from") || !userInput.contains("/to")) {
                throw new VeggieException("Correct format: event [Task description] /from [Start time] /to [End time]");
            }
            String[] parts = userInput.split("/from");
            String taskDescription = parts[0].substring(6).trim();
            String from = parts.length > 1 ? parts[1].split("/to")[0].trim() : "";
            String to = parts.length > 1 ? parts[1].split("/to")[1].trim() : "";
            Task newTask = new Event(taskDescription, from, to);
            tasks.add(newTask);
            System.out.println("____________________________________________________________");
            System.out.println(" Got it. I've added this task:");
            System.out.println("   " + newTask);
            System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
            System.out.println("____________________________________________________________");
            saveTasks(tasks);  // Save after adding task
        } catch (VeggieException e) {
            System.out.println("____________________________________________________________");
            System.out.println(e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    // Mark a task as done
    private static void handleMarkTask(String userInput, ArrayList<Task> tasks) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new VeggieException("Task number out of range.");
            }
            Task taskToMark = tasks.get(taskNumber - 1);  // Correct task retrieval
            if (taskToMark != null) {
                taskToMark.markAsDone();
                System.out.println("____________________________________________________________");
                System.out.println(" Nice! I've marked this task as done:");
                System.out.println("   " + taskToMark);
                System.out.println("____________________________________________________________");
                saveTasks(tasks);  // Save after marking as done
            } else {
                throw new VeggieException("Task is null.");
            }
        } catch (Exception e) {
            System.out.println("____________________________________________________________");
            System.out.println(" Error: " + e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    // Unmark a task
    private static void handleUnmarkTask(String userInput, ArrayList<Task> tasks) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new VeggieException("Task number out of range.");
            }
            Task taskToUnmark = tasks.get(taskNumber - 1);
            if (taskToUnmark != null) {
                taskToUnmark.markAsNotDone();
                System.out.println("____________________________________________________________");
                System.out.println(" OK, I've marked this task as not done yet:");
                System.out.println("   " + taskToUnmark);
                System.out.println("____________________________________________________________");
                saveTasks(tasks);  // Save after unmarking task
            } else {
                throw new VeggieException("Task is null.");
            }
        } catch (Exception e) {
            System.out.println("____________________________________________________________");
            System.out.println(" Error: " + e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    // Search tasks by keyword
    private static void handleFindTask(String userInput, ArrayList<Task> tasks) {
        try {
            if (userInput.length() <= 5) {
                throw new VeggieException("Please provide a keyword to search. Correct format: find [keyword]");
            }

            String keyword = userInput.substring(5).trim().toLowerCase(); // Extract the keyword
            ArrayList<Task> matchingTasks = new ArrayList<>(); // List for storing matching tasks

            // Iterate over tasks and check for the keyword
            for (Task task : tasks) {
                if (task.description.toLowerCase().contains(keyword)) {
                    matchingTasks.add(task);
                }
            }

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

    // Delete a task
    private static void handleDeleteTask(String userInput, ArrayList<Task> tasks) {
        try {
            int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
            if (taskNumber < 1 || taskNumber > tasks.size()) {
                throw new VeggieException("Task number out of range.");
            }
            Task taskToDelete = tasks.get(taskNumber - 1);
            if (taskToDelete != null) {
                tasks.remove(taskNumber - 1);
                System.out.println("____________________________________________________________");
                System.out.println(" Got it. I've deleted this task:");
                System.out.println("   " + taskToDelete);
                System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                System.out.println("____________________________________________________________");
                saveTasks(tasks);  // Save after deleting task
            } else {
                throw new VeggieException("Task is null.");
            }
        } catch (Exception e) {
            System.out.println("____________________________________________________________");
            System.out.println(" Error: " + e.getMessage());
            System.out.println("____________________________________________________________");
        }
    }

    // Save tasks to the file
    private static void saveTasks(ArrayList<Task> tasks) {
        try {
            // Create the file object and ensure that the parent directories exist
            File file = new File(FILE_PATH);
            file.getParentFile().mkdirs();  // Create parent directories if they don't exist

            // Now write the tasks to the file
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            for (Task task : tasks) {
                if (task != null) {
                    writer.write(task.toFileString());
                    writer.newLine();
                } else {
                    System.out.println("Warning: Encountered a null task while saving.");
                }
            }
            writer.close();
        } catch (IOException e) {
            System.out.println("Error saving tasks to file: " + e.getMessage());
        }
    }

    // Load tasks from file
    private static ArrayList<Task> loadTasks() {
        ArrayList<Task> tasks = new ArrayList<>();
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                return tasks;  // Return empty list if the file doesn't exist
            }
            Scanner fileScanner = new Scanner(file);
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                Task task = Task.fromFileString(line);
                tasks.add(task);
            }
            fileScanner.close();
        } catch (FileNotFoundException | VeggieException e) {
            System.out.println("____________________________________________________________");
            System.out.println(" Error loading tasks: " + e.getMessage());
            System.out.println("____________________________________________________________");
        }
        return tasks;
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
