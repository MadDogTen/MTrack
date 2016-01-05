package com.maddogten.mtrack.gui;

import com.maddogten.mtrack.io.MoveStage;
import com.maddogten.mtrack.util.GenericMethods;
import com.maddogten.mtrack.util.Strings;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.ArrayList;
import java.util.logging.Logger;

/*
      DualChoiceButtons will display two separate choices and the related buttons, then returns the string of whichever button was pressed.
      It gives the option to display a separate message per a button if wanted, otherwise displays only the first message.
 */

public class DualChoiceButtons {
    private static final Logger log = Logger.getLogger(DualChoiceButtons.class.getName());

    @SuppressWarnings("SameParameterValue")
    public StringProperty multipleButtons(StringProperty[] messages, StringProperty[] choices, StringProperty[] tooltips, Stage parentStage) {
        log.fine("dualChoiceButtons has been opened.");

        Stage dualChoiceStage = new Stage();
        dualChoiceStage.initOwner(parentStage);
        dualChoiceStage.initStyle(StageStyle.UNDECORATED);
        dualChoiceStage.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(dualChoiceStage);

        Label label = new Label();
        label.textProperty().bind(messages[0]);
        label.setPadding(new Insets(0, 0, 4, 0));

        final StringProperty[] answer = new StringProperty[]{new SimpleStringProperty(Strings.EmptyString)};
        ArrayList<Button> buttons = new ArrayList<>(choices.length);
        HBox layout = new HBox();
        int i = 0;
        for (StringProperty button : choices) {
            buttons.add(i, new Button());
            buttons.get(i).textProperty().bind(button);
            buttons.get(i).setOnAction(e -> {
                answer[0] = button;
                dualChoiceStage.close();
            });
            layout.getChildren().add(i, buttons.get(i));
            i++;
        }

        if (tooltips != null && tooltips.length == buttons.size()) {
            buttons.forEach(button -> {
                int index = buttons.indexOf(button);
                if (tooltips[index] != null && !tooltips[index].getValue().isEmpty())
                    button.setTooltip(new Tooltip(tooltips[buttons.indexOf(button)].getValue()));
            });
        }

        layout.setSpacing(3);
        layout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox();
        if (messages.length == 1) mainLayout.getChildren().addAll(label, layout);
        else {
            if (messages.length == buttons.size()) {
                VBox vBox = new VBox();
                buttons.forEach(button -> {
                    int index = buttons.indexOf(button);
                    if (messages[index] != null && !messages[index].getValue().isEmpty()) {
                        Label label1 = new Label();
                        label1.textProperty().bind(messages[index]);
                        vBox.getChildren().add(index, label1);
                    }
                });
                mainLayout.getChildren().addAll(vBox, layout);
            } else mainLayout.getChildren().addAll(label, layout);
        }
        Button exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        exit.setOnAction(e -> dualChoiceStage.close());

        mainLayout.getChildren().add(exit);
        mainLayout.setPadding(new Insets(6, 6, 6, 6));
        mainLayout.setSpacing(3);
        mainLayout.setAlignment(Pos.CENTER);

        Platform.runLater(() -> new MoveStage().moveStage(mainLayout, parentStage));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        dualChoiceStage.setScene(scene);
        dualChoiceStage.show();
        dualChoiceStage.hide();
        if (dualChoiceStage.getOwner() != null) {
            dualChoiceStage.setX(dualChoiceStage.getOwner().getX() + (dualChoiceStage.getOwner().getWidth() / 2) - (dualChoiceStage.getWidth() / 2));
            dualChoiceStage.setY(dualChoiceStage.getOwner().getY() + (dualChoiceStage.getOwner().getHeight() / 2) - (dualChoiceStage.getHeight() / 2));
        }
        dualChoiceStage.showAndWait();
        log.fine("dualChoiceButtons has been closed.");
        return answer[0];
    }
}
