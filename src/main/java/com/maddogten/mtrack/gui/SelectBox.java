package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.logging.Logger;

public class SelectBox {
    private static final Logger log = Logger.getLogger(ConfirmBox.class.getName());
    private Stage selectStage;

    public String select(String message, String[] buttonsText, Stage oldStage) {
        log.finest("SelectBox has been opened.");
        selectStage = new Stage();
        GenericMethods.setIcon(selectStage);
        selectStage.initOwner(oldStage);
        selectStage.initStyle(StageStyle.UNDECORATED);
        selectStage.initModality(Modality.APPLICATION_MODAL);
        Label label = new Label();
        label.setText(message);
        ArrayList<Button> buttons = new ArrayList<>();
        for (String aString : buttonsText) buttons.add(new Button(aString));
        Button close = new Button(Strings.ExitButtonText);
        final String[] answer = new String[1];
        HBox layout2 = new HBox();
        buttons.forEach(aButton -> {
            aButton.setOnAction(e -> {
                answer[0] = aButton.getText();
                selectStage.close();
            });
            layout2.getChildren().add(aButton);
        });
        close.setOnAction(e -> {
            answer[0] = Strings.EmptyString;
            selectStage.close();
        });
        layout2.getChildren().add(close);
        layout2.setAlignment(Pos.CENTER);
        layout2.setPadding(new Insets(4, 6, 6, 6));
        VBox layout = new VBox();
        layout.getChildren().addAll(label, layout2);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(6, 0, 0, 0));
        Scene scene = new Scene(layout);
        selectStage.setScene(scene);
        Platform.runLater(() -> {
            selectStage.setX(selectStage.getOwner().getX() + (selectStage.getOwner().getWidth() / 2) - (selectStage.getWidth() / 2));
            selectStage.setY(selectStage.getOwner().getY() + (selectStage.getOwner().getHeight() / 2) - (selectStage.getHeight() / 2));
            new MoveStage().moveStage(layout, oldStage);
        });
        selectStage.showAndWait();
        selectStage = null;
        return answer[0];
    }
}