package com.maddogten.mtrack.FXMLControllers;

import com.maddogten.mtrack.util.DeveloperStuff;
import com.maddogten.mtrack.util.GenericMethods;
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
    public void initialize(final URL location, final ResourceBundle resources) {
        log.finest("About has been opened.");
        programName.textProperty().bind(Strings.ProgramTitle);
        programName.setAlignment(Pos.CENTER);
        versionNumber.textProperty().bind(Strings.MTrackVersion);
        if (DeveloperStuff.showInternalVersion) versionNumberTooltip.textProperty().bind(Strings.InternalVersion);
        else versionNumber.setTooltip(null);
        codedBy.textProperty().bind(Strings.CodedBy);
        codedBy.setAlignment(Pos.CENTER);
        codedUsing.textProperty().bind(Strings.CodedUsingFull);
        codedUsing.setAlignment(Pos.CENTER);
        codedWith.textProperty().bind(Strings.JavaVersionFull);
        codedWith.setAlignment(Pos.CENTER);
        close.textProperty().bind(Strings.Close);
        close.setOnAction(e -> {
            Stage stage = (Stage) pane.getScene().getWindow();
            if (Variables.specialEffects) GenericMethods.fadeStageOut(stage, 2, log, this.getClass());
            stage.close();
        });
    }
}
