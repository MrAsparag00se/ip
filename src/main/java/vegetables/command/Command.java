package vegetables.command;

/**
 * Represents the various commands that can be issued in the Vegetables chatbot.
 * Each command corresponds to a specific user action.
 */
public enum Command {
    HELP,
    LIST,
    TODO,
    DEADLINE,
    EVENT,
    MARK,
    UNMARK,
    FIND,
    DELETE,
    BYE,
    UNKNOWN;

    /**
     * Parses a string input and returns the corresponding {@code Command} enum.
     * If the input does not match any known command, {@code UNKNOWN} is returned.
     *
     * @param input The user input string.
     * @return The corresponding {@code Command} enum, or {@code UNKNOWN} if no match is found.
     */
    public static Command fromInput(String input) {
        if (input == null || input.trim().isEmpty()) {
            return UNKNOWN;
        }
        String lowerInput = input.trim().toLowerCase();
        if (lowerInput.startsWith("todo")) {
            return TODO;
        } else if (lowerInput.startsWith("deadline")) {
            return DEADLINE;
        } else if (lowerInput.startsWith("event")) {
            return EVENT;
        } else if (lowerInput.startsWith("mark")) {
            return MARK;
        } else if (lowerInput.startsWith("unmark")) {
            return UNMARK;
        } else if (lowerInput.startsWith("find")) {
            return FIND;
        } else if (lowerInput.startsWith("delete")) {
            return DELETE;
        } else if (lowerInput.equals("help")) {
            return HELP;
        } else if (lowerInput.equals("list")) {
            return LIST;
        } else if (lowerInput.equals("bye")) {
            return BYE;
        } else {
            return UNKNOWN;
        }
    }
}
