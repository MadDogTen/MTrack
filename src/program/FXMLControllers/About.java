package program.FXMLControllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import program.input.MoveWindow;
import program.util.Strings;

import java.net.URL;
import java.util.ResourceBundle;

public class About implements Initializable {

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

        programName.setText(Strings.ProgramTitle);
        programName.setAlignment(Pos.CENTER);

        versionNumber.setText(Strings.MTrackVersion);
        versionNumber.setAlignment(Pos.CENTER);

        codedBy.setText(Strings.CodedBy);
        codedBy.setAlignment(Pos.CENTER);

        codedUsing.setText(Strings.CodedUsing);
        codedUsing.setAlignment(Pos.CENTER);

        codedWith.setText(Strings.codedWith + " " + Strings.javaVersion);
        codedWith.setAlignment(Pos.CENTER);

        close.setOnAction(e -> {
            Stage stage = (Stage) pane.getScene().getWindow();
            stage.close();
        });

        new MoveWindow().moveWindow(pane);

    }
}
