package vegetables.gui;

import vegetables.task.Task;
import vegetables.manager.TaskManager;
import vegetables.storage.TaskStorage;
import vegetables.command.CommandHandler;
import java.util.ArrayList;

public class VegetablesGUI {
    private final CommandHandler commandHandler;

    public VegetablesGUI() {
        TaskStorage taskStorage = new TaskStorage();
        ArrayList<Task> tasks = taskStorage.loadTasks();
        TaskManager taskManager = new TaskManager(tasks);
        this.commandHandler = new CommandHandler(taskManager, taskStorage);
    }

    public String getResponse(String input) {
        return commandHandler.executeCommand(input);
    }
}