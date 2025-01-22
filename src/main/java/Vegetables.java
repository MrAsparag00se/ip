import java.util.ArrayList;
import java.util.Scanner;

public class Vegetables {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        ArrayList<Task> tasks = new ArrayList<>();

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

        while (true) {
            System.out.print("> "); // Prompt for user input
            String userInput = scanner.nextLine().trim();

            if (userInput.equalsIgnoreCase("help")) {
                printHelp();
            } else if (userInput.equalsIgnoreCase("list")) {
                printTaskList(tasks);
            } else if (userInput.startsWith("todo")) {
                handleToDoCommand(userInput, tasks);
            } else if (userInput.startsWith("deadline")) {
                handleDeadlineCommand(userInput, tasks);
            } else if (userInput.startsWith("event")) {
                handleEventCommand(userInput, tasks);
            } else if (userInput.startsWith("mark")) {
                handleMarkCommand(userInput, tasks);
            } else if (userInput.startsWith("unmark")) {
                handleUnmarkCommand(userInput, tasks);
            } else if (userInput.startsWith("delete")) {
                handleDeleteCommand(userInput, tasks);
            } else if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break;
            } else {
                System.out.println("____________________________________________________________");
                System.out.println(" Unrecognized command! Type 'help' for available commands.");
                System.out.println("____________________________________________________________");
            }
        }
        scanner.close();
    }

    private static void printHelp() {
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

    private static void printTaskList(ArrayList<Task> tasks) {
        System.out.println("____________________________________________________________");
        if (tasks.isEmpty()) {
            System.out.println("No tasks added.");
        } else {
            System.out.println("Here are the tasks in your list:");
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
        System.out.println("____________________________________________________________");
    }

    private static void handleToDoCommand(String input, ArrayList<Task> tasks) {
        if (input.length() <= 5) {
            printError("Correct format: todo [Task description]");
            return;
        }
        String description = input.substring(5).trim();
        addTask(tasks, new Task(description, TaskType.TODO));
    }

    private static void handleDeadlineCommand(String input, ArrayList<Task> tasks) {
        if (!input.contains("/by")) {
            printError("Correct format: deadline [Task description] /by [Date/time]");
            return;
        }
        String[] parts = input.split("/by");
        String description = parts[0].substring(9).trim();
        String by = parts[1].trim();
        addTask(tasks, new Task(description, TaskType.DEADLINE, by));
    }

    private static void handleEventCommand(String input, ArrayList<Task> tasks) {
        if (!input.contains("/from") || !input.contains("/to")) {
            printError("Correct format: event [Task description] /from [Start time] /to [End time]");
            return;
        }
        String[] parts = input.split("/from");
        String description = parts[0].substring(6).trim();
        String[] times = parts[1].split("/to");
        String from = times[0].trim();
        String to = times[1].trim();
        String additionalInfo = from + " " + to;
        addTask(tasks, new Task(description, TaskType.EVENT, additionalInfo));
    }

    private static void handleMarkCommand(String input, ArrayList<Task> tasks) {
        handleTaskStateChange(input, tasks, true);
    }

    private static void handleUnmarkCommand(String input, ArrayList<Task> tasks) {
        handleTaskStateChange(input, tasks, false);
    }

    private static void handleTaskStateChange(String input, ArrayList<Task> tasks, boolean markAsDone) {
        try {
            int taskNumber = Integer.parseInt(input.split(" ")[1]);
            Task task = getTaskByNumber(tasks, taskNumber);
            if (markAsDone) {
                task.markAsDone();
                System.out.println(" Nice! I've marked this task as done:");
            } else {
                task.markAsNotDone();
                System.out.println(" OK, I've marked this task as not done yet:");
            }
            System.out.println("   " + task);
        } catch (Exception e) {
            printError("Invalid task number.");
        }
    }

    private static void handleDeleteCommand(String input, ArrayList<Task> tasks) {
        try {
            int taskNumber = Integer.parseInt(input.split(" ")[1]);
            Task task = tasks.remove(taskNumber - 1);
            System.out.println(" Noted. I've removed this task:");
            System.out.println("   " + task);
        } catch (Exception e) {
            printError("Invalid task number.");
        }
    }

    private static Task getTaskByNumber(ArrayList<Task> tasks, int number) throws VeggieException {
        if (number < 1 || number > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        return tasks.get(number - 1);
    }

    private static void addTask(ArrayList<Task> tasks, Task task) {
        tasks.add(task);
        System.out.println("____________________________________________________________");
        System.out.println(" Got it. I've added this task:");
        System.out.println("   " + task);
        System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
        System.out.println("____________________________________________________________");
    }

    private static void printError(String message) {
        System.out.println("____________________________________________________________");
        System.out.println(" Error: " + message);
        System.out.println("____________________________________________________________");
    }
}

// Enum for Task Types
enum TaskType {
    TODO, DEADLINE, EVENT
}

// Task class with TaskType Enum
class Task {
    private final String description;
    private final TaskType taskType;
    private boolean isDone;
    private final String additionalInfo;  // For deadline or event specific information

    // Constructor for ToDo tasks
    public Task(String description, TaskType taskType) {
        this(description, taskType, "");
    }

    // Constructor for Deadline or Event tasks with additional info (e.g., date or time)
    public Task(String description, TaskType taskType, String additionalInfo) {
        this.description = description;
        this.taskType = taskType;
        this.additionalInfo = additionalInfo;
        this.isDone = false;
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }

    public String getStatusIcon() {
        return isDone ? "[X]" : "[ ]";
    }

    @Override
    public String toString() {
        return switch (taskType) {
            case TODO -> "[T]" + getStatusIcon() + " " + description;
            case DEADLINE -> "[D]" + getStatusIcon() + " " + description + " (by: " + additionalInfo + ")";
            case EVENT -> {
                // Event task, split additionalInfo into 'from' and 'to'
                String[] times = additionalInfo.split(" ");
                yield "[E]" + getStatusIcon() + " " + description + " (from: " + times[0] + " to: " + times[1] + ")";
            }
        };
    }
}

class VeggieException extends Exception {
    public VeggieException(String message) {
        super(message);
    }
}
