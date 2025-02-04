package vegetables.command;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.task.Task;

import java.util.ArrayList;

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
}