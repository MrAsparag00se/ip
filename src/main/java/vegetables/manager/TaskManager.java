package vegetables.manager;

import vegetables.task.Task;
import vegetables.task.ToDo;
import vegetables.task.Deadline;
import vegetables.task.Event;
import vegetables.exception.VeggieException;
import java.util.ArrayList;

/**
 * TaskManager is responsible for managing a list of tasks. It supports adding various
 * types of tasks (To-Do, Deadline, Event), marking tasks as done or not done, deleting tasks,
 * and searching tasks by their description. It also ensures the validity of inputs such as deadlines
 * and event times.
 */
public class TaskManager {
    private final ArrayList<Task> tasks;

    /**
     * Constructs a TaskManager instance with an initial list of tasks.
     *
     * @param tasks The list of tasks to initialize the TaskManager with.
     */
    public TaskManager(ArrayList<Task> tasks) {
        this.tasks = tasks;
    }

    /**
     * Adds a To-Do task to the task list with the provided description.
     *
     * @param description The description of the To-Do task to be added.
     */
    public void addToDoTask(String description) {
        Task newTask = new ToDo(description);
        tasks.add(newTask);
    }

    /**
     * Adds a Deadline task to the task list with the provided description and deadline.
     * The deadline must be in the format yyyy-MM-dd HH:mm.
     *
     * @param description The description of the Deadline task to be added.
     * @param deadline The deadline for the task, in the format yyyy-MM-dd HH:mm.
     * @throws VeggieException If the description is empty or the deadline format is incorrect.
     */
    public void addDeadlineTask(String description, String deadline) throws VeggieException {
        if (description == null || description.trim().isEmpty()) {
            throw new VeggieException("Task description cannot be empty.");
        }

        // Validate the deadline format (e.g., a simple check for a date pattern, you could refine this)
        if (!deadline.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            throw new VeggieException("Invalid deadline format. Use: yyyy-MM-dd HH:mm");
        }

        Task newTask = new Deadline(description, deadline);
        tasks.add(newTask);
    }

    /**
     * Adds an Event task to the task list with the provided description, start time, and end time.
     * Both start time and end time must be in the format yyyy-MM-dd HH:mm.
     *
     * @param description The description of the Event task to be added.
     * @param from The start time of the event, in the format yyyy-MM-dd HH:mm.
     * @param to The end time of the event, in the format yyyy-MM-dd HH:mm.
     * @throws VeggieException If the description, start time, or end time is empty, or if the date/time format is incorrect.
     */
    public void addEventTask(String description, String from, String to) throws VeggieException {
        // Check if the description, from, or to is missing
        if (description == null || description.trim().isEmpty()) {
            throw new VeggieException("Event description cannot be empty.");
        }

        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            throw new VeggieException("Both start time (/from) and end time (/to) must be provided.");
        }

        // Optionally, validate date formats for 'from' and 'to'
        if (!from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}") || !to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            throw new VeggieException("Invalid time format. Correct format: yyyy-MM-dd HH:mm");
        }

        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
    }

    /**
     * Marks a task as done by updating its status.
     *
     * @param taskNumber The number of the task to be marked as done. The task number starts from 1.
     * @throws VeggieException If the task number is out of range or invalid.
     */
    public void markTaskAsDone(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone(); // Mark the task as done

        // Output in the desired format
        System.out.println("____________________________________________________________");
        System.out.println("Nice! I've marked this task as done:");
        System.out.println("  " + task); // Task's toString will show the updated status
        System.out.println("____________________________________________________________");
    }

    /**
     * Unmarks a task by updating its status to "not done".
     *
     * @param taskNumber The number of the task to be unmarked. The task number starts from 1.
     * @throws VeggieException If the task number is out of range or invalid.
     */
    public void unmarkTask(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone(); // Unmark the task

        // Output in the desired format
        System.out.println("____________________________________________________________");
        System.out.println("OK, I've marked this task as not done yet:");
        System.out.println("  " + task); // Task's toString will show the updated status
        System.out.println("____________________________________________________________");
    }

    /**
     * Deletes a task from the task list by its number.
     *
     * @param taskNumber The number of the task to be deleted. The task number starts from 1.
     * @throws VeggieException If the task number is out of range or invalid.
     */
    public void deleteTask(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        tasks.remove(taskNumber - 1);
    }

    /**
     * Finds tasks in the list that match a given keyword in their description.
     *
     * @param keyword The keyword to search for in task descriptions.
     * @return A list of tasks whose descriptions contain the keyword.
     */
    public ArrayList<Task> findTasksByDescription(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();  // List for storing matching tasks
        for (Task task : tasks) {
            if (task.getDescription().toLowerCase().contains(keyword.toLowerCase())) {
                matchingTasks.add(task);
            }
        }
        return matchingTasks;
    }

    /**
     * Returns the list of all tasks managed by the TaskManager.
     *
     * @return The list of tasks.
     */
    public ArrayList<Task> getTasks() {
        return tasks;
    }
}
