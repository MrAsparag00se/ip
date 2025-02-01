package vegetables.gui;

import vegetables.gui.DialogBox;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;

public class MainWindow extends ScrollPane {
    @FXML private VBox dialogContainer;
    @FXML private TextField userInput;

    private VegetablesGUI vegetablesGUI;
    private Image userImage = new Image(getClass().getResourceAsStream("/images/User.png"));
    private Image veggieImage = new Image(getClass().getResourceAsStream("/images/Vegetables.png"));

    public void setVegetablesGUI(VegetablesGUI vegetablesGUI) {
        this.vegetablesGUI = vegetablesGUI;
    }

    @FXML
    private void handleUserInput() {
        String input = userInput.getText();
        String response = vegetablesGUI.getResponse(input);

        dialogContainer.getChildren().addAll(
                DialogBox.getUserDialog(input, userImage),
                DialogBox.getVeggieDialog(response, veggieImage)
        );
        userInput.clear();
    }
}