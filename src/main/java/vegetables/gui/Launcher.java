package vegetables.gui;

import javafx.application.Application;

/**
 * The entry point of the application.
 * <p>
 * This class serves as a workaround for launching JavaFX applications
 * when using certain build tools that require a separate main class.
 * It delegates the application startup to {@link Main}.
 * </p>
 */
public class Launcher {
    /**
     * Starts the JavaFX application.
     * Calls {@link Application#launch(Class, String...)} with {@link Main} as the application class.
     *
     * @param args Command-line arguments passed to the application.
     */
    public static void main(String[] args) {
        Application.launch(vegetables.gui.Main.class, args);
    }
}
