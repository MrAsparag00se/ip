import java.util.ArrayList;
import java.util.Scanner;

public class Vegetables {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // ArrayList to store tasks (allows dynamic sizing)
        ArrayList<Task> tasks = new ArrayList<>();

        // New ASCII Art for "VEGETABLES"
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
            // Handle 'list' command to display all tasks
            else if (userInput.equalsIgnoreCase("list")) {
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
            // Handle task addition (ToDo, Deadline, Event)
            else if (userInput.startsWith("todo")) {
                try {
                    if (userInput.length() <= 5) {
                        throw new VeggieException("Correct format: todo [Task description]");
                    }
                    String taskDescription = userInput.substring(5).trim();
                    tasks.add(new ToDo(taskDescription));
                    System.out.println("____________________________________________________________");
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks.get(tasks.size() - 1));
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println("____________________________________________________________");
                } catch (VeggieException e) {
                    System.out.println("____________________________________________________________");
                    System.out.println(e.getMessage());
                    System.out.println("____________________________________________________________");
                }
            }
            else if (userInput.startsWith("deadline")) {
                try {
                    if (!userInput.contains("/by")) {
                        throw new VeggieException("Correct format: deadline [Task description] /by [Date/time]");
                    }
                    String[] parts = userInput.split("/by");
                    String taskDescription = parts[0].substring(9).trim();
                    String by = parts.length > 1 ? parts[1].trim() : "";
                    tasks.add(new Deadline(taskDescription, by));
                    System.out.println("____________________________________________________________");
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks.get(tasks.size() - 1));
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println("____________________________________________________________");
                } catch (VeggieException e) {
                    System.out.println("____________________________________________________________");
                    System.out.println(e.getMessage());
                    System.out.println("____________________________________________________________");
                }
            }
            else if (userInput.startsWith("event")) {
                try {
                    if (!userInput.contains("/from") || !userInput.contains("/to")) {
                        throw new VeggieException("Correct format: event [Task description] /from [Start time] /to [End time]");
                    }
                    String[] parts = userInput.split("/from");
                    String taskDescription = parts[0].substring(6).trim();
                    String from = parts.length > 1 ? parts[1].split("/to")[0].trim() : "";
                    String to = parts.length > 1 ? parts[1].split("/to")[1].trim() : "";
                    tasks.add(new Event(taskDescription, from, to));
                    System.out.println("____________________________________________________________");
                    System.out.println(" Got it. I've added this task:");
                    System.out.println("   " + tasks.get(tasks.size() - 1));
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println("____________________________________________________________");
                } catch (VeggieException e) {
                    System.out.println("____________________________________________________________");
                    System.out.println(e.getMessage());
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle 'mark' command to mark tasks as done
            else if (userInput.startsWith("mark")) {
                try {
                    int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new VeggieException("Task number out of range.");
                    }
                    tasks.get(taskNumber - 1).markAsDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks.get(taskNumber - 1));
                    System.out.println("____________________________________________________________");
                } catch (Exception e) {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Error: " + e.getMessage());
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle 'unmark' command to unmark tasks
            else if (userInput.startsWith("unmark")) {
                try {
                    int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new VeggieException("Task number out of range.");
                    }
                    tasks.get(taskNumber - 1).markAsNotDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks.get(taskNumber - 1));
                    System.out.println("____________________________________________________________");
                } catch (Exception e) {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Error: " + e.getMessage());
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle 'delete' command to remove tasks
            else if (userInput.startsWith("delete")) {
                try {
                    int taskNumber = Integer.parseInt(userInput.split(" ")[1]);
                    if (taskNumber < 1 || taskNumber > tasks.size()) {
                        throw new VeggieException("Task number out of range.");
                    }
                    Task removedTask = tasks.remove(taskNumber - 1);
                    System.out.println("____________________________________________________________");
                    System.out.println(" Noted. I've removed this task:");
                    System.out.println("   " + removedTask);
                    System.out.println(" Now you have " + tasks.size() + " tasks in the list.");
                    System.out.println("____________________________________________________________");
                } catch (Exception e) {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Error: " + e.getMessage());
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle 'bye' command to exit the program
            else if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
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
}

// Custom exception for handling veggie-specific errors
class VeggieException extends Exception {
    public VeggieException(String message) {
        super(message);
    }
}

// Base Task class
abstract class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false; // New tasks are not done by default
    }
/*
    public String getDescription() {
        return description;
    }
*/
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
        return getStatusIcon() + " " + description;
    }
}

// ToDo class - represents a simple task
class ToDo extends Task {
    public ToDo(String description) {
        super(description);
    }
}

// Deadline class - represents a task with a deadline
class Deadline extends Task {
    protected String by;

    public Deadline(String description, String by) {
        super(description);
        this.by = by;
    }

    @Override
    public String toString() {
        return "[D]" + getStatusIcon() + " " + description + " (by: " + by + ")";
    }
}

// Event class - represents a task with a start and end time
class Event extends Task {
    private final String from;
    private final String to;

    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "[E]" + getStatusIcon() + " " + description + " (from: " + from + " to: " + to + ")";
    }
}
