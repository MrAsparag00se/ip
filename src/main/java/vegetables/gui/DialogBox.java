package vegetables.gui;

import java.io.IOException;
import java.util.Collections;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

/**
 * Represents a dialog box consisting of an ImageView to represent the speaker's face
 * and a label containing text from the speaker.
 */
public class DialogBox extends HBox {
    @FXML
    private Label dialog;
    @FXML
    private ImageView displayPicture;

    private DialogBox(String text, Image img, boolean isUserDialog) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/DialogBox.fxml"));
            fxmlLoader.setController(this);
            fxmlLoader.setRoot(this);
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        dialog.setText(text);
        displayPicture.setImage(img);

        // Set alignment to the right for user and left for system (Veggie)
        if (isUserDialog) {
            this.setAlignment(Pos.TOP_RIGHT);
        } else {
            this.setAlignment(Pos.TOP_LEFT);
        }
    }

    /**
     * Flips the dialog box such that the ImageView is on the left and text on the right.
     */
    private void flip() {
        ObservableList<Node> tmp = FXCollections.observableArrayList(this.getChildren());
        Collections.reverse(tmp);
        getChildren().setAll(tmp);
        setAlignment(Pos.TOP_LEFT);
    }

    /**
     * Creates a dialog box for the user, with the given text and image.
     *
     * @param text The text message from the user.
     * @param img  The image representing the user.
     * @return A DialogBox instance displaying the user's message.
     */
    public static DialogBox getUserDialog(String text, Image img) {
        return new DialogBox(text, img, true);
    }

    /**
     * Creates a dialog box for the system (Veggie), with the given text and image.
     * <p>
     * The dialog box is flipped so that the system's messages appear distinct from the user's.
     * </p>
     *
     * @param text The text message from the system (Veggie).
     * @param img  The image representing the system.
     * @return A DialogBox instance displaying the system's message.
     */
    public static DialogBox getVeggieDialog(String text, Image img) {
        var db = new DialogBox(text, img, false);
        db.flip();
        return db;
    }
}
