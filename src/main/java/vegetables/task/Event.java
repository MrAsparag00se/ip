package vegetables.task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

import vegetables.exception.VeggieException;

/**
 * Represents an 'Event' task.
 * An event has a start time and an end time.
 */
public class Event extends Task {
    private static final DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static final DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("MMM dd yyyy, h:mm a");

    private final LocalDateTime from;
    private final LocalDateTime to;

    /**
     * Constructs a new Event task with the specified description, start time, and end time.
     *
     * @param description The description of the event.
     * @param from        The start time of the event in "yyyy-MM-dd HH:mm" format.
     * @param to          The end time of the event in "yyyy-MM-dd HH:mm" format.
     * @throws VeggieException If the date format is incorrect.
     */
    public Event(String description, String from, String to) throws VeggieException {
        super(description);
        // Assertions to check validity
        assert description != null && !description.isEmpty() : "Event description cannot be null or empty";
        assert from != null && !from.isEmpty() : "Start time cannot be null or empty";
        assert to != null && !to.isEmpty() : "End time cannot be null or empty";

        try {
            this.from = LocalDateTime.parse(from, inputFormatter);
            this.to = LocalDateTime.parse(to, inputFormatter);

            assert this.from.isBefore(this.to) : "Start time cannot be after end time";

        } catch (DateTimeParseException e) {
            throw new VeggieException("Invalid date format! Use yyyy-MM-dd HH:mm (e.g., 2023-01-22 18:00)");
        }
        // Ensure the times are valid after parsing
        assert this.from != null && this.to != null : "Parsed from/to times cannot be null";

    }

    /**
     * Constructs a new Event task with the specified description, start time, end time, and completion status.
     *
     * @param description The description of the event.
     * @param from        The start time of the event in "yyyy-MM-dd HH:mm" format.
     * @param to          The end time of the event in "yyyy-MM-dd HH:mm" format.
     * @param isDone      The completion status of the event.
     * @throws VeggieException If the date format is incorrect.
     */
    public Event(String description, String from, String to, boolean isDone) throws VeggieException {
        this(description, from, to);
        this.isDone = isDone;
    }

    /**
     * Getter for the start time of the event.
     *
     * @return The start time as a formatted string.
     */
    public String getFrom() {
        return from.format(displayFormatter);
    }

    /**
     * Getter for the end time of the event.
     *
     * @return The end time as a formatted string.
     */
    public String getTo() {
        return to.format(displayFormatter);
    }

    /**
     * Returns a formatted string representation of the Event task.
     * The format is: "E [status] description (from: formatted start time to: formatted end time)".
     *
     * @return A string representation of the Event task.
     */
    @Override
    public String toString() {
        return "E [" + (isDone ? "X" : " ") + "] " + description
                + " (from: " + from.format(displayFormatter)
                + " to: " + to.format(displayFormatter) + ")";
    }

    /**
     * Converts the Event task to a string representation suitable for saving to a file.
     * The format is: "EVENT | status | description | formatted start time | formatted end time".
     *
     * - `status`: "X" if the task is done, "0" otherwise.
     *
     * @return A string representation of the Event task in file format.
     */
    @Override
    public String toFileString() {
        return "EVENT | " + (isDone ? "X" : "0") + " | " + description + " | "
                + from.format(inputFormatter) + " | "
                + to.format(inputFormatter);
    }
}
