package vegetables.gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import vegetables.util.FunFactGenerator;

/**
 * The controller for the main GUI window of the Vegetables application.
 * <p>
 * This class manages user interactions, displaying dialog boxes for both user input
 * and system responses. It extends {@link ScrollPane} and contains an interactive
 * text field for user input.
 * </p>
 */
public class MainWindow extends ScrollPane {
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;
    @FXML private ScrollPane scrollPane;

    private VegetablesGui vegetablesGui;
    private final Image userImage = new Image(getClass().getResourceAsStream("/images/User.png"));
    private final Image veggieImage = new Image(getClass().getResourceAsStream("/images/Vegetables.png"));

    /**
     * Sets the instance of {@link VegetablesGui} that handles user input and responses.
     *
     * @param vegetablesGui The instance of {@link VegetablesGui} to be linked to this window.
     */
    public void setVegetablesGui(VegetablesGui vegetablesGui) {
        this.vegetablesGui = vegetablesGui;
    }

    /**
     * Initializes the GUI and displays a fun fact when the chatbot starts.
     */
    @FXML
    public void initialize() {
        showFunFact();

        // Add a listener to handle resizing dynamically if needed
        scrollPane.widthProperty().addListener((obs, oldWidth, newWidth) -> {
            // You can do any adjustments here when the width changes
            System.out.println("Window resized to width: " + newWidth);
        });

        scrollPane.heightProperty().addListener((obs, oldHeight, newHeight) -> {
            // Adjust if needed when the height changes
            System.out.println("Window resized to height: " + newHeight);
        });
    }

    private void showFunFact() {
        String funFact = "🌱 Fun Fact: " + FunFactGenerator.getRandomFunFact();
        dialogContainer.getChildren().add(DialogBox.getVeggieDialog(funFact, veggieImage));
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = vegetablesGui.getResponse(input);

        // Add user dialog (aligned to the right)
        dialogContainer.getChildren().add(DialogBox.getUserDialog(input, userImage));

        // Add Veggie dialog (aligned to the left)
        dialogContainer.getChildren().add(DialogBox.getVeggieDialog(response, veggieImage));

        userInput.clear();

        Platform.runLater(() -> scrollPane.setVvalue(1.0));
    }

}
