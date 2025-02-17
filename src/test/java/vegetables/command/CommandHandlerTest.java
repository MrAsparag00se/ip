package vegetables.command;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import vegetables.exception.VeggieException;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.task.Task;
public class CommandHandlerTest {

    @Test
    void executeCommand_helpCommand_returnsHelpMessage() {
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
    void executeCommand_listWhenNoTasks_returnsNoTasksMessage() {
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
    void executeCommand_listWithTasks_returnsFormattedTasks() {
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
                "Here are the tasks in your list:\n"
                        + "1.[T][ ] Read book\n"
                        + "2.[D][ ] Submit report (by: 2023-10-10)\n";

        assertEquals(expectedOutput, result);
    }

    @Test
    void executeCommand_addValidTodo_returnsSuccessMessage() {
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
    void executeCommand_addDuplicateTodo_returnsErrorMessage() {
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
    void executeCommand_addEmptyTodo_returnsErrorForEmptyDescription() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);

        when(mockTaskManager.taskExists("")).thenReturn(false);
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());

        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String result = handler.executeCommand("todo");
        assertEquals("Error: Task description cannot be empty!", result);

        verify(mockTaskManager, never()).addToDoTask("");
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_validInput_addsTaskAndReturnsSuccess() {
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
    void handleAddDeadline_missingByKeyword_returnsFormatError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String invalidInput = "deadline Finish assignment";
        String result = handler.executeCommand(invalidInput);

        assertEquals("Error adding deadline task: Correct format: deadline [Task description] "
                + "/by [yyyy-MM-dd HH:mm]", result);
        try {
            verify(mockTaskManager, never()).addDeadlineTask(anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleAddDeadline_invalidDateFormat_returnsParseError() {
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
    void handleAddDeadline_pastDeadline_returnsPastDateError() {
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
    void handleAddDeadline_duplicateTask_returnsDuplicateError() {
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
    void handleAddDeadline_emptyDescription_handlesGracefully() {
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
    void handleAddEvent_validInput_addsTaskAndReturnsSuccess() {
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
    void handleAddEvent_missingFromOrTo_returnsFormatError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String invalidInput = "event Project workshop /to 2024-12-01 14:00";
        String result = handler.executeCommand(invalidInput);

        assertEquals("Error adding event task: Correct format: event [Task description] "
                + "/from [Start time] /to [End time]", result);
        try {
            verify(mockTaskManager, never()).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_invalidDateFormat_returnsParseError() {
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
    void handleAddEvent_pastEventTime_returnsPastTimeError() {
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
    void handleAddEvent_startAfterEnd_returnsTimeOrderError() {
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
    void handleAddEvent_duplicateTask_returnsDuplicateError() {
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
    void handleAddEvent_eventClash_returnsWarning() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Mock clash detection for future dates
        when(mockTaskManager.checkEventClash(any(), any()))
                .thenReturn(new StringBuilder("Warning: Clashes with existing event 'Project Review'"));
        when(mockTaskManager.getTasks()).thenReturn(new ArrayList<>());

        String validInput = "event Team meeting /from 2030-12-01 14:00 /to 2030-12-01 16:00";
        String result = handler.executeCommand(validInput);

        assertEquals("Event added with a warning:\nWarning: "
                + "Clashes with existing event 'Project Review'\nNew event added: "
                + "Team meeting\nNow you have 0 tasks in the list.", result);
        try {
            verify(mockTaskManager).addEventTask(anyString(), anyString(), anyString());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void handleAddEvent_emptyDescription_returnsError() {
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

    @Test
    void handleFindTask_validSubstring_returnsMatchingTasks() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Mock tasks containing "Thi"
        ArrayList<Task> mockTasks = new ArrayList<>();
        Task task1 = mock(Task.class);
        when(task1.toString()).thenReturn("[T][ ] This is cold");
        Task task2 = mock(Task.class);
        when(task2.toString()).thenReturn("[T][ ] This is hot");
        Task task3 = mock(Task.class);
        when(task3.toString()).thenReturn("[E][ ] Thicken (from: 2024-01-01 10:00 to: 2024-01-01 12:00)");
        mockTasks.add(task1);
        mockTasks.add(task2);
        mockTasks.add(task3);

        // Stub the search for "Thi"
        when(mockTaskManager.findTasksBySubstring("Thi")).thenReturn(mockTasks);

        String result = handler.executeCommand("find Thi");

        String expectedOutput =
                "Here are the matching tasks in your list:\n"
                        + "1.[T][ ] This is cold\n"
                        + "2.[T][ ] This is hot\n"
                        + "3.[E][ ] Thicken (from: 2024-01-01 10:00 to: 2024-01-01 12:00)\n";

        assertEquals(expectedOutput, result);
    }

    @Test
    void handleFindTask_noMatches_returnsNoTasksMessage() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Stub empty search results
        when(mockTaskManager.findTasksBySubstring("XYZ")).thenReturn(new ArrayList<>());

        String result = handler.executeCommand("find XYZ");

        assertEquals("No matching tasks found.\n", result);
    }

    @Test
    void handleFindTask_emptyKeyword_returnsErrorMessage() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        String result = handler.executeCommand("find");

        assertEquals("Error: Please provide a keyword to search. Correct format: find [keyword]", result);
    }

    @Test
    void handleDeleteTask_validTaskNumber_deletesTaskAndReturnsUpdatedList() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Use a mutable list to track tasks dynamically
        List<Task> tasks = new ArrayList<>();
        Task task1 = mock(Task.class);
        Task task2 = mock(Task.class);
        tasks.add(task1); // Original index 0 (task 1)
        tasks.add(task2); // Original index 1 (task 2)

        // Stub getTasks() to return the current state of the list
        when(mockTaskManager.getTasks()).thenAnswer(invocation -> new ArrayList<>(tasks));

        // When deleteTask is called, remove the task from the list
        try {
            doAnswer(invocation -> {
                int taskNumber = invocation.getArgument(0); // 1-based index
                tasks.remove(taskNumber - 1); // Convert to 0-based index
                return null;
            }).when(mockTaskManager).deleteTask(anyInt());
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        // Stub toString() for the remaining task
        when(task2.toString()).thenReturn("[T][ ] Remaining Task");

        // Execute command: delete task 1 (original task1)
        String result = handler.executeCommand("delete 1");

        // Verify the response message shows the updated list with 1 task (task2)
        assertEquals(
                "Task deleted.\nHere are the tasks in your list:\n1.[T][ ] Remaining Task\n",
                result
        );

        // Verify the saved tasks list has size 1 (task2)
        verify(mockTaskStorage).saveTasks(argThat(savedTasks ->
                savedTasks.size() == 1 && savedTasks.get(0) == task2
        ));
    }

    @Test
    void handleDeleteTask_outOfBoundsTaskNumber_returnsError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // TaskManager throws exception for invalid index
        try {
            doThrow(new IndexOutOfBoundsException("Invalid task index: 5"))
                    .when(mockTaskManager).deleteTask(5);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        String result = handler.executeCommand("delete 5");

        assertEquals("Error: Invalid task index: 5", result);
        try {
            verify(mockTaskManager).deleteTask(5);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleMarkTask_validTaskNumber_marksTaskAndReturnsUpdatedList() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Mock tasks
        ArrayList<Task> tasks = new ArrayList<>();
        Task task = mock(Task.class);
        tasks.add(task);
        when(task.toString()).thenReturn("[T][X] Completed task"); // Marked as done

        when(mockTaskManager.getTasks()).thenReturn(tasks);

        String result = handler.executeCommand("mark 1");

        assertEquals(
                "Task marked as done.\nHere are the tasks in your list:\n1.[T][X] Completed task\n",
                result
        );
        try {
            verify(mockTaskManager).markTaskAsDone(1);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage).saveTasks(tasks);
    }


    @Test
    void handleMarkTask_outOfBoundsTaskNumber_returnsError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Simulate an out-of-bounds exception
        try {
            doThrow(new IndexOutOfBoundsException("Task index out of bounds"))
                    .when(mockTaskManager).markTaskAsDone(1);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        String result = handler.executeCommand("mark 1");

        assertEquals("Error: Task index out of bounds", result);
        try {
            verify(mockTaskManager).markTaskAsDone(1);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }

    @Test
    void handleUnmarkTask_validTaskNumber_unmarksTaskAndReturnsUpdatedList() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Mock task list with one task (unmarked after unmarking)
        ArrayList<Task> tasks = new ArrayList<>();
        Task task = mock(Task.class);
        tasks.add(task);
        when(task.toString()).thenReturn("[T][ ] Read book"); // Unmarked state
        when(mockTaskManager.getTasks()).thenReturn(tasks);

        String result = handler.executeCommand("unmark 1");

        assertEquals(
                "Task marked as not done.\nHere are the tasks in your list:\n1.[T][ ] Read book\n",
                result
        );
        try {
            verify(mockTaskManager).unmarkTask(1);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage).saveTasks(tasks);
    }

    @Test
    void handleUnmarkTask_outOfBoundsTaskNumber_returnsError() {
        TaskManager mockTaskManager = mock(TaskManager.class);
        TaskStorage mockTaskStorage = mock(TaskStorage.class);
        CommandHandler handler = new CommandHandler(mockTaskManager, mockTaskStorage);

        // Simulate out-of-bounds exception
        try {
            doThrow(new IndexOutOfBoundsException("Task index out of bounds"))
                    .when(mockTaskManager).unmarkTask(5);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }

        String result = handler.executeCommand("unmark 5");

        assertEquals("Error: Task index out of bounds", result);
        try {
            verify(mockTaskManager).unmarkTask(5);
        } catch (VeggieException e) {
            throw new RuntimeException(e);
        }
        verify(mockTaskStorage, never()).saveTasks(any(ArrayList.class));
    }
}
