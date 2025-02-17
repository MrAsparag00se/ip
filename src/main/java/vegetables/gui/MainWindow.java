package vegetables.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

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

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = vegetablesGui.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getVeggieDialog(response, veggieImage)
        );
        userInput.clear();
    }

}
