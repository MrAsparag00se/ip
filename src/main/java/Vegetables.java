import java.util.Scanner;

public class Vegetables {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Array to store up to 100 tasks
        String[] tasks = new String[100];
        int taskCount = 0; // Counter for the number of tasks

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
                    for (int i = 0; i < taskCount; i++) {
                        System.out.println((i + 1) + ". " + tasks[i]);
                    }
                }
                System.out.println("____________________________________________________________");
            }
            // Handle task addition
            else if (!userInput.equalsIgnoreCase("bye")) {
                if (taskCount < 100) {
                    tasks[taskCount] = userInput;
                    taskCount++;
                    System.out.println("____________________________________________________________");
                    System.out.println(" added: " + userInput);
                    System.out.println("____________________________________________________________");
                } else {
                    System.out.println("____________________________________________________________");
                    System.out.println(" Task list is full. Cannot add more tasks.");
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
