package vegetables.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import vegetables.exception.VeggieException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.task.Task;

public class CommandHandlerTest {

    @Test
    void executeCommand_HelpCommand_ReturnsHelpMessage() {
        // Mock dependencies
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        CommandHandler commandHandler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String expected = " Available Commands:\n"
                + " - todo [Task description]: Adds a task without a deadline.\n"
                + " - deadline [Task description] /by [Date/time]: Adds a task with a deadline.\n"
                + " - event [Task description] /from [Start time] /to [End time]: Adds an event task.\n"
                + " - list: Displays all tasks in the list.\n"
                + " - mark [Task number]: Marks a task as done.\n"
                + " - unmark [Task number]: Unmarks a task as not done.\n"
                + " - find [Keyword]: Finds a task by its keyword.\n"
                + " - delete [Task number]: Deletes a task from the list.\n"
                + " - bye: Exits the program.\n";

        String actual = commandHandler.executeCommand("help");

        assertEquals(expected, actual);
    }

    @Test
    void executeCommand_ListWhenNoTasks_ReturnsNoTasksMessage() {
        // Mock dependencies
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        // Stub TaskManager to return an empty list
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());

        CommandHandler commandHandler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Execute "list" command
        String result = commandHandler.executeCommand("list");

        // Verify output
        assertEquals("No tasks added.\n", result);
    }

    @Test
    void executeCommand_ListWithTasks_ReturnsFormattedTasks() {
        // Mock dependencies
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        // Create mocked tasks with controlled toString() output
        Task task1 = mock(Task.class);
        when(task1.toString()).thenReturn("[T][ ] Read book");
        Task task2 = mock(Task.class);
        when(task2.toString()).thenReturn("[D][ ] Submit report (by: 2023-10-10)");

        // Stub TaskManager to return a list of tasks
        ArrayList<Task> tasks = new ArrayList<>();
        tasks.add(task1);
        tasks.add(task2);
        when(mockTaskManager.getTasks()).thenReturn(tasks);

        CommandHandler commandHandler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Execute "list" command
        String result = commandHandler.executeCommand("list");

        // Verify output format
        String expectedOutput =
                "Here are the tasks in your list:\n" +
                        "1.[T][ ] Read book\n" +
                        "2.[D][ ] Submit report (by: 2023-10-10)\n";

        assertEquals(expectedOutput, result);
    }

    @Test
    void executeCommand_AddValidTodo_ReturnsSuccessMessage() {
        // Mock dependencies
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        // Stub task existence check and task list
        when(mockTaskManager.taskExists("Read book")).thenReturn(false);
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());

        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Execute command
        String result = handler.executeCommand("todo Read book");

        // Verify interactions and output
        assertEquals("Got it. I've added this task: Read book", result);
        verify(mockTaskManager).addToDoTask("Read book");
        verify(mockTaskStorage).saveTasks(any(ArrayList.class));
    }

    @Test
    void executeCommand_AddDuplicateTodo_ReturnsErrorMessage() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        // Stub task exists
        when(mockTaskManager.taskExists("Read book")).thenReturn(true);

        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String result = handler.executeCommand("todo Read book");

        assertEquals("Duplicate task detected! Task already exists.", result);
        verify(mockTaskManager, never()).addToDoTask(anyString());
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void executeCommand_AddEmptyTodo_ReturnsSuccessWithEmptyDescription() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        // Allow empty task
        when(mockTaskManager.taskExists("")).thenReturn(false);
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());

        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String result = handler.executeCommand("todo");

        assertEquals("Got it. I've added this task: ", result);
        verify(mockTaskManager).addToDoTask("");
        verify(mockTaskStorage).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_ValidInput_AddsTaskAndReturnsSuccess() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use a date far in the future
        String validInput = "deadline Submit report /by 2030-01-01 12:00";
        String expectedDescription = "Submit report";
        String expectedBy = "2030-01-01 12:00";

        when(mockTaskManager.taskExists(expectedDescription)).thenReturn(false);
        try {
            doNothing().when(mockTaskManager).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        String result = handler.executeCommand(validInput);

        assertEquals("Got it. I've added this deadline task: Submit report", result);
        try {
            verify(mockTaskManager).addDeadlineTask(expectedDescription, expectedBy);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddDeadline_MissingByKeyword_ReturnsFormatError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String invalidInput = "deadline Finish assignment";
        String result = handler.executeCommand(invalidInput);

        assertEquals("Error adding deadline task: Correct format: deadline [Task description] /by [yyyy-MM-dd HH:mm]", result);
        try {
            verify(mockTaskManager, never()).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_InvalidDateFormat_ReturnsParseError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String invalidInput = "deadline Submit report /by 2024/12/31 23:59"; // Wrong format
        String result = handler.executeCommand(invalidInput);

        assertEquals("Error: Invalid time or time format. Use: yyyy-MM-dd HH:mm", result);
        try {
            verify(mockTaskManager, never()).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_PastDeadline_ReturnsPastDateError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use a past date
        String pastDate = LocalDateTime.now().minusDays(1)
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String input = "deadline Submit report /by " + pastDate;

        String result = handler.executeCommand(input);

        assertEquals("Error: Deadline cannot be in the past!", result);
        try {
            verify(mockTaskManager, never()).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_DuplicateTask_ReturnsDuplicateError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use a FUTURE date to bypass deadline validation
        String validInput = "deadline Submit report /by 2030-12-31 23:59";
        String expectedDescription = "Submit report";

        // Stub task existence check
        when(mockTaskManager.taskExists(expectedDescription)).thenReturn(true);
        // Suppress exception for method call
        try {
            doNothing().when(mockTaskManager).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        String result = handler.executeCommand(validInput);

        assertEquals("Duplicate task detected! Task already exists.", result);
        try {
            verify(mockTaskManager, never()).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_EmptyDescription_HandlesGracefully() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String input = "deadline /by 2024-12-31 23:59";
        String result = handler.executeCommand(input);

        // Change expected result
        assertEquals("Error adding deadline task: Task description cannot be empty!", result);
        try {
            verify(mockTaskManager, never()).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_ValidInput_AddsTaskAndReturnsSuccess() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use a FUTURE date to avoid past-time validation errors
        String validInput = "event Team meeting /from 2030-12-01 14:00 /to 2030-12-01 16:00";
        String expectedDescription = "Team meeting";
        String expectedFrom = "2030-12-01 14:00";
        String expectedTo = "2030-12-01 16:00";

        // Stub dependencies
        when(mockTaskManager.taskExists(expectedDescription)).thenReturn(false);
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());
        try {
            doNothing().when(mockTaskManager).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        // Execute command
        String result = handler.executeCommand(validInput);

        // Assertions
        assertEquals("Got it. I've added this event task:\nTeam meeting\nNow you have 0 tasks in the list.", result);

        // Verify interactions
        try {
            verify(mockTaskManager).addEventTask(expectedDescription, expectedFrom, expectedTo);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddEvent_MissingFromOrTo_ReturnsFormatError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String invalidInput = "event Project workshop /to 2024-12-01 14:00";
        String result = handler.executeCommand(invalidInput);

        assertEquals("Error adding event task: Correct format: event [Task description] /from [Start time] /to [End time]", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_InvalidDateFormat_ReturnsParseError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String invalidInput = "event Conference /from 2024/12/01 09:00 /to 2024/12/01 18:00";
        String result = handler.executeCommand(invalidInput);

        assertEquals("Error: Invalid time or time format. Use: yyyy-MM-dd HH:mm", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_PastEventTime_ReturnsPastTimeError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use a past date
        String pastDate = LocalDateTime.now().minusDays(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String input = "event Retrospective /from " + pastDate + " /to " + pastDate;

        String result = handler.executeCommand(input);

        assertEquals("Error: Event times cannot be in the past!", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_StartAfterEnd_ReturnsTimeOrderError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Future dates with start after end
        String input = "event Workshop /from 2030-12-01 17:00 /to 2030-12-01 15:00";
        String result = handler.executeCommand(input);

        assertEquals("Error: Start time cannot be after end time!", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_DuplicateTask_ReturnsDuplicateError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String validInput = "event Team meeting /from 2030-12-01 14:00 /to 2030-12-01 16:00";
        when(mockTaskManager.taskExists("Team meeting")).thenReturn(true);
        try {
            doNothing().when(mockTaskManager).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        String result = handler.executeCommand(validInput);

        assertEquals("Duplicate task detected! Task already exists.", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_EventClash_ReturnsWarning() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Mock clash detection for future dates
        when(mockTaskManager.checkEventClash(any(), any()))
                .thenReturn(new StringBuilder("Warning: Clashes with existing event 'Project Review'"));
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());

        String validInput = "event Team meeting /from 2030-12-01 14:00 /to 2030-12-01 16:00";
        String result = handler.executeCommand(validInput);

        assertEquals("Event added with a warning:\nWarning: Clashes with existing event 'Project Review'\nNew event added: Team meeting\nNow you have 0 tasks in the list.", result);
        try {
            verify(mockTaskManager).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_EmptyDescription_ReturnsError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use future dates to bypass time validation
        String input = "event /from 2030-12-01 09:00 /to 2030-12-01 18:00";
        String result = handler.executeCommand(input);

        assertEquals("Error adding event task: Task description cannot be empty!", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }
}