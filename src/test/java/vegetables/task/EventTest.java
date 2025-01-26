package vegetables.task;

import vegetables.manager.TaskManager;
import vegetables.exception.VeggieException;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class EventTest {

    // Scenario 1: Description is empty, should throw VeggieException
    @Test
    public void testEventEmptyDescription() {
        // Try to create an Event with an empty description
        assertThrows(VeggieException.class, () -> new Event("", "2025-01-01 10:00", "2025-01-01 12:00"));
    }

    // Scenario 2: Date format is incorrect, should throw VeggieException
    @Test
    public void testEventInvalidDateFormat() {
        // Try to create an Event with an invalid date format for the 'from' time
        assertThrows(VeggieException.class, () -> {
            new Event("Meeting", "2025-01-01 10:00", "2025-01-01 25:00");  // Invalid 'to' time
        });
    }

    // Scenario 3: All parameters are correct, Event should be added to list
    @Test
    public void testEventAddedToList() {
        // Create a new Event instance to be added
        Event eventToAdd = new Event("Meeting", "2025-01-28 10:00", "2025-01-28 11:00");

        // Add the event to the list
        TaskManager taskManager = new TaskManager(new ArrayList<>());
        try {
            taskManager.addEventTask(eventToAdd.getDescription(), eventToAdd.getFrom(), eventToAdd.getTo());
        } catch (VeggieException e) {
            System.out.println("Error: Unable to add event task. " + e.getMessage());
        }

        // Verify if the event has been added to the task list
        assertEquals(1, taskManager.getTasks().size());
        assertEquals(eventToAdd, taskManager.getTasks().get(0));  // Verify if the added event is the same as the created one
    }
}
