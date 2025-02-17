package vegetables.manager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import vegetables.exception.VeggieException;
import vegetables.task.Deadline;
import vegetables.task.Event;
import vegetables.task.Task;
import vegetables.task.ToDo;

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
     * Checks if a task with the given description already exists in the task list.
     *
     * @param description The description of the task to check for duplicates.
     * @return true if a task with the same description exists, false otherwise.
     */
    public boolean taskExists(String description) {
        for (Task task : tasks) {
            if (task.getDescription().equalsIgnoreCase(description)) {
                return true;
            }
        }
        return false;
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
     * @throws VeggieException If any field is empty or, format is incorrect.
     */
    public void addEventTask(String description, String from, String to) throws VeggieException {
        if (description == null || description.trim().isEmpty()) {
            throw new VeggieException("Event description cannot be empty.");
        }

        if (from == null || from.trim().isEmpty() || to == null || to.trim().isEmpty()) {
            throw new VeggieException("Both start time (/from) and end time (/to) must be provided.");
        }

        if (!from.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}") || !to.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}")) {
            throw new VeggieException("Invalid time format. Correct format: yyyy-MM-dd HH:mm");
        }

        Task newTask = new Event(description, from, to);
        tasks.add(newTask);
    }

    /**
     * Checks if the new event clashes with any existing events.
     *
     * @param newFrom The start time of the new event.
     * @param newTo The end time of the new event.
     * @return A StringBuilder containing any warnings about overlapping events.
     */
    public StringBuilder checkEventClash(LocalDateTime newFrom, LocalDateTime newTo) {
        StringBuilder warningMessage = new StringBuilder();

        for (Task task : tasks) {
            if (task instanceof Event) {
                Event existingEvent = (Event) task;
                LocalDateTime existingFrom = LocalDateTime.parse(existingEvent.getFrom(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                LocalDateTime existingTo = LocalDateTime.parse(existingEvent.getTo(),
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

                // Check if there's an overlap
                if ((newFrom.isBefore(existingTo) && newTo.isAfter(existingFrom))) {
                    warningMessage.append("Warning: The event \"")
                            .append(existingEvent.getDescription())
                            .append("\" overlaps with the new event.\n");
                }
            }
        }
        return warningMessage.length() > 0 ? warningMessage : null;
    }

    /**
     * Marks a task as done by updating its status.
     * <p>
     * The task number is 1-based, meaning the first task in the list has taskNumber = 1.
     * If the provided task number is out of range, an exception is thrown.
     * </p>
     *
     * @param taskNumber The number of the task to be marked as done (1-based index).
     * @return A confirmation message indicating the task has been marked as done.
     * @throws VeggieException If the task number is out of range or invalid.
     */
    public String markTaskAsDone(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        Task task = tasks.get(taskNumber - 1);
        task.markAsDone();
        return "Task marked as done: " + task;
    }

    /**
     * Unmarks a task by updating its status to "not done".
     * <p>
     * The task number is 1-based, meaning the first task in the list has taskNumber = 1.
     * If the provided task number is out of range, an exception is thrown.
     * </p>
     *
     * @param taskNumber The number of the task to be unmarked (1-based index).
     * @return A confirmation message indicating the task has been marked as not done.
     * @throws VeggieException If the task number is out of range or invalid.
     */
    public String unmarkTask(int taskNumber) throws VeggieException {
        if (taskNumber < 1 || taskNumber > tasks.size()) {
            throw new VeggieException("Task number out of range.");
        }
        Task task = tasks.get(taskNumber - 1);
        task.markAsNotDone(); // Unmark the task
        return "Task marked as not done: " + task; // Return message
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
     * Finds tasks in the list that match a given substring in their description.
     *
     * @param keyword The keyword to search for in task descriptions.
     * @return A list of tasks whose descriptions contain the substring.
     */
    public ArrayList<Task> findTasksBySubstring(String keyword) {
        ArrayList<Task> matchingTasks = new ArrayList<>();
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

