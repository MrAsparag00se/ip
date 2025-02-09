package vegetables.task;

/**
 * Represents a "ToDo" task, which has no associated time constraints.
 * This class is a subclass of the abstract Task class.
 */
public class ToDo extends Task {
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
        assert toString().matches("^T \\[[ X]\\] .+") : "toString() format is incorrect: " + toString();
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
        assert toFileString().matches("^TODO \\| [X0] \\| .+")
                : "toFileString() format is incorrect: " + toFileString();
        return "TODO | " + (isDone ? "X" : "0") + " | " + description;
    }
}

