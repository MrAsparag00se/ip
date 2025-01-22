import java.util.ArrayList;
import java.util.Scanner;
import java.io.*;

// Main class for managing tasks
public class Vegetables {
    private static final String FILE_PATH = "./data/duke.txt";  // File path for storing tasks

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
                throw new VeggieException("Correct format: deadline [Task description] /by [Date/time]");
            }
            String[] parts = userInput.split("/by");
            String taskDescription = parts[0].substring(9).trim();
            String by = parts.length > 1 ? parts[1].trim() : "";
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
        } catch (FileNotFoundException e) {
            System.out.println("____________________________________________________________");
            System.out.println(" Error loading tasks: " + e.getMessage());
            System.out.println("____________________________________________________________");
        }
        return tasks;
    }
}

// Task class and its subclasses
abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false;
    }

    public abstract String toFileString(); // Save task to file

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    @Override
    public String toString() {
        return "[" + (isDone ? "X" : " ") + "] " + description;
    }

    public static Task fromFileString(String taskString) {
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

class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }

    // Add constructor with isDone parameter
    public ToDo(String description, boolean isDone) {
        super(description);
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return "T [" + (isDone ? "X" : " ") + "] " + description;
    }

    @Override
    public String toFileString() {
        return "TODO | " + (isDone ? "X" : "0") + " | " + description;
    }
}

class Deadline extends Task {
    private String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    // Add constructor with isDone parameter
    public Deadline(String description, String by, boolean isDone) {
        super(description);
        this.by = by;
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return "D [" + (isDone ? "X" : " ") + "] " + description + " by: " + by;
    }


    @Override
    public String toFileString() {
        return "DEADLINE | " + (isDone ? "X" : "0") + " | " + description + " | " + by;
    }
}

class Event extends Task {
    private String from;
    private String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    // Add constructor with isDone parameter
    public Event(String description, String from, String to, boolean isDone) {
        super(description);
        this.from = from;
        this.to = to;
        this.isDone = isDone;
    }

    @Override
    public String toString() {
        return "E [" + (isDone ? "X" : " ") + "] " + description + " from: " + from + " to: " + to;
    }


    @Override
    public String toFileString() {
        return "EVENT | " + (isDone ? "X" : "0") + " | " + description + " | " + from + " | " + to;
    }
}

// Custom exception class for input validation
class VeggieException extends Exception {
    public VeggieException(String message) {
        super(message);
    }
}
