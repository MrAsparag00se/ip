package vegetables.gui;

import java.io.IOException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The main entry point for the JavaFX application.
 * <p>
 * This class initializes and launches the GUI for the Vegetables application.
 * It loads the {@code MainWindow.fxml} file and sets up the primary stage.
 * </p>
 */
public class Main extends Application {
    private final VegetablesGui vegetablesGui = new VegetablesGui();

    /**
     * Starts the JavaFX application by setting up the primary stage.
     * <p>
     * Loads the {@code MainWindow.fxml} layout, sets the scene, and initializes the
     * {@link VegetablesGui} instance for handling interactions.
     * </p>
     *
     * @param stage The primary stage for the JavaFX application.
     */
    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/MainWindow.fxml"));
            AnchorPane ap = fxmlLoader.load();
            Scene scene = new Scene(ap);
            stage.setScene(scene);
            fxmlLoader.<MainWindow>getController().setVegetablesGui(vegetablesGui);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
