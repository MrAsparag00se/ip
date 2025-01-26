package vegetables.task;

import vegetables.exception.VeggieException;

/**
 * Abstract class representing a task with a description and a completion status.
 * Provides methods for marking a task as done or not done, converting tasks to a file string,
 * and reconstructing tasks from a file string.
 */
public abstract class Task {
    protected String description;
    protected boolean isDone;

    public String getDescription() {
        return description;
    }

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
        return switch (taskType) {
            case "TODO" -> new ToDo(description, isDone);
            case "DEADLINE" -> new Deadline(description, parts[3], isDone);
            case "EVENT" -> new Event(description, parts[3], parts[4], isDone);
            default -> null;
        };
    }
}
