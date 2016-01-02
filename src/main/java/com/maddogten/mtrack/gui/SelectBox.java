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

    public String select(String message, String[] buttonsText, Stage oldStage) {
        log.fine("select has been opened.");

        Stage selectStage = new Stage();
        selectStage.initOwner(oldStage);
        selectStage.initStyle(StageStyle.UNDECORATED);
        selectStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(selectStage);

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
        layout.setPadding(new Insets(6, 6, 6, 6));

        Platform.runLater(() -> new MoveStage().moveStage(layout, oldStage));

        selectStage.setScene(new Scene(layout));
        selectStage.show();
        selectStage.hide();
        selectStage.setX(selectStage.getOwner().getX() + (selectStage.getOwner().getWidth() / 2) - (selectStage.getWidth() / 2));
        selectStage.setY(selectStage.getOwner().getY() + (selectStage.getOwner().getHeight() / 2) - (selectStage.getHeight() / 2));
        selectStage.showAndWait();

        log.fine("select has been closed.");
        return answer[0];
    }
}
