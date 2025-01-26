package vegetables.task;

import vegetables.exception.VeggieException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Represents a 'Deadline' task.
 * The deadline is stored as a LocalDateTime object.
 */
public class Deadline extends Task {
    private final LocalDateTime by;
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
