package program.FXMLControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import program.io.MoveWindow;
import program.util.Strings;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

@SuppressWarnings("WeakerAccess")
public class About implements Initializable {
    private static final Logger log = Logger.getLogger(About.class.getName());

    @FXML
    private Pane pane;
    @FXML
    private Label programName;
    @FXML
    private Label versionNumber;
    @FXML
    private Label codedBy;
    @FXML
    private Label codedUsing;
    @FXML
    private Label codedWith;
    @FXML
    private Button close;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.finest("About has been opened.");
        programName.setText(Strings.ProgramTitle);
        programName.setAlignment(Pos.CENTER);

        versionNumber.setText(Strings.MTrackVersion);
        versionNumber.setAlignment(Pos.CENTER);

        codedBy.setText(Strings.CodedBy);
        codedBy.setAlignment(Pos.CENTER);

        codedUsing.setText(Strings.CodedUsing);
        codedUsing.setAlignment(Pos.CENTER);

        codedWith.setText(Strings.codedWith + ' ' + Strings.javaVersion);
        codedWith.setAlignment(Pos.CENTER);

        close.setOnAction(e -> {
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.close();
        });

        // Allow the undecorated stage to be moved.
        new MoveWindow().moveWindow(pane);
    }
}
