import java.util.Scanner;

public class Vegetables {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

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

            // Echo user input
            System.out.println("____________________________________________________________");
            System.out.println(userInput);
            System.out.println("____________________________________________________________");

            // Exit when the user types 'bye'
            if (userInput.equalsIgnoreCase("bye")) {
                System.out.println("Bye. Hope to see you again soon!");
                System.out.println("____________________________________________________________");
                break; // Break the loop and exit the program
            }
        }

        // Close scanner
        scanner.close();
    }
}
