import java.util.Scanner;

public class Vegetables {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Array to store tasks, allowing up to 100 tasks
        Task[] tasks = new Task[100];
        int taskCount = 0; // Counter for number of tasks

        // ASCII Art for "VEGETABLES"
        String veggieLogo =
                "      (`-.      ('-.                ('-.   .-') _      ('-.    .-. .-')               ('-.    .-')    \n"
                        + "    _(OO  )_  _(  OO)             _(  OO) (  OO) )    ( OO ).-.\\  ( OO )            _(  OO)  ( OO ).  \n"
                        + ",--(_/   ,. \\(,------. ,----.    (,------./     '._   / . --. / ;-----.\\  ,--.     (,------.(_)---\\_) \n"
                        + "\\   \\   /(__/ |  .---''  .-./-')  |  .---'|'--...__)  | \\-.  \\  | .-.  |  |  |.-')  |  .---'/    _ |  \n"
                        + " \\   \\ /   /  |  |    |  |_( O- ) |  |    '--.  .--'.-'-'  |  | | '-' /_) |  | OO ) |  |    \\  :` `.  \n"
                        + "  \\   '   /, (|  '--. |  | .--, \\(|  '--.    |  |    \\| |_.'  | | .-. `.  |  |`-' |(|  '--.  '..`''.) \n"
                        + "   \\     /__) |  .--'(|  | '. (_/ |  .--'    |  |     |  .-.  | | |  \\  |(|  '---.' |  .--' .-._)   \\ \n"
                        + "    \\   /     |  `---.|  '--'  |  |  `---.   |  |     |  | |  | | '--'  / |      |  |  `---.\\       / \n"
                        + "     `-'      `------' `------'   `------'   `--'     `--' `--' `------'  `------'  `------' `-----'   \n";

        // Print greeting and ASCII Art
        System.out.println("____________________________________________________________");
        System.out.println(" Hello! I'm Vegetables");
        System.out.println(veggieLogo);
        System.out.println(" What can I do for you?");
        System.out.println("____________________________________________________________");

        // Infinite loop to process user input
        while (true) {
            // Read user input
            String userInput = scanner.nextLine();

            // Handle 'list' command to display all tasks
            if (userInput.equalsIgnoreCase("list")) {
                System.out.println("____________________________________________________________");
                if (taskCount == 0) {
                    System.out.println("No tasks added.");
                } else {
                    System.out.println("Here are the tasks in your list:");
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + "." + tasks[i].getStatusIcon() + " " + tasks[i].getDescription());
                    }
                }
                System.out.println("____________________________________________________________");
            }
            // Handle task addition
            else if (userInput.startsWith("add ")) {
                if (taskCount < 100) {
                    String taskDescription = userInput.substring(4);
                    tasks[taskCount] = new Task(taskDescription);
                    taskCount++;
                    System.out.println("____________________________________________________________");
                    System.out.println(" added: " + taskDescription);
                    System.out.println("____________________________________________________________");
                } else {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Task list is full. Cannot add more tasks.");
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle marking a task as done
            else if (userInput.startsWith("mark ")) {
                int taskNumber = Integer.parseInt(userInput.substring(5));
                if (taskNumber >= 1 && taskNumber <= taskCount) {
                    tasks[taskNumber - 1].markAsDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" Nice! I've marked this task as done:");
                    System.out.println("   " + tasks[taskNumber - 1].getStatusIcon() + " " + tasks[taskNumber - 1].getDescription());
                    System.out.println("____________________________________________________________");
                } else {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Task number is out of range.");
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle unmarking a task
            else if (userInput.startsWith("unmark ")) {
                int taskNumber = Integer.parseInt(userInput.substring(7));
                if (taskNumber >= 1 && taskNumber <= taskCount) {
                    tasks[taskNumber - 1].markAsNotDone();
                    System.out.println("____________________________________________________________");
                    System.out.println(" OK, I've marked this task as not done yet:");
                    System.out.println("   " + tasks[taskNumber - 1].getStatusIcon() + " " + tasks[taskNumber - 1].getDescription());
                    System.out.println("____________________________________________________________");
                } else {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Task number is out of range.");
                    System.out.println("____________________________________________________________");
                }
            }
            // Handle 'bye' command to exit the program
            else if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("____________________________________________________________");
                System.out.println(" Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break; // Break the loop and exit the program
            }
        }

        // Close scanner
        scanner.close();
    }
}

// Task class to represent each task
class Task {
    protected String description;
    protected boolean isDone;

    public Task(String description) {
        this.description = description;
        this.isDone = false; // New tasks are not done by default
    }

    public String getDescription() {
        return description;
    }

    public String getStatusIcon() {
        return (isDone ? "[X]" : "[ ]"); // Return [X] if done, [ ] if not done
    }

    public void markAsDone() {
        isDone = true;
    }

    public void markAsNotDone() {
        isDone = false;
    }
}
