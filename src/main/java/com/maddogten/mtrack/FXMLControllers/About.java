package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.Strings;
import com.maddogten.mtrack.util.Variables;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;

/*
      About is the About Stage that displays the program name, version, and other related information.
      Has the MTrack logo in the background.
 */

@SuppressWarnings("WeakerAccess")
public class About implements Initializable {
    private static final Logger log = Logger.getLogger(About.class.getName());

    @SuppressWarnings("unused")
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
    @FXML
    private Tooltip versionNumberTooltip;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.finest("About has been opened.");
        programName.setText(Strings.ProgramTitle);
        programName.setAlignment(Pos.CENTER);
        versionNumber.setText(Strings.MTrackVersion);
        versionNumber.setAlignment(Pos.CENTER);
        if (Variables.showInternalVersion) versionNumberTooltip.textProperty().bind(Strings.InternalVersion);
        else versionNumber.setTooltip(null);
        codedBy.setText(Strings.CodedBy);
        codedBy.setAlignment(Pos.CENTER);
        codedUsing.textProperty().bind(Strings.CodedUsingFull);
        codedUsing.setAlignment(Pos.CENTER);
        codedWith.textProperty().bind(Strings.JavaVersionFull);
        codedWith.setAlignment(Pos.CENTER);
        close.textProperty().bind(Strings.Close);
        close.setOnAction(e -> {
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.close();
        });
        // Allows the undecorated stage to be moved.
        new MoveStage().moveStage(pane);
    }
}
