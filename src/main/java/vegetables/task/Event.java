package vegetables.task;

/**
 * Represents an 'Event' task.
 * An event has a start time and an end time.
 */
public class Event extends Task {
    private final String from;
    private final String to;

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
     * Getter for from property of Event.
     * Only for use by testers.
     * @return from.
     */
    public String getFrom() {
        return from;
    }

    /**
     * Getter for to property of Event.
     * Only for use by testers.
     * @return to.
     */
    public String getTo() {
        return to;
    }

    /**
     * Returns representation of the Event, including type, completion status, description, start, and end time.
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
