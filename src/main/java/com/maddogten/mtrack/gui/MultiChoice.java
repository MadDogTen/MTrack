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
import javafx.scene.control.CheckBox;
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

public class MultiChoice {
    private static final Logger log = Logger.getLogger(MultiChoice.class.getName());

    @SuppressWarnings("SameParameterValue")
    public StringProperty multipleButtons(StringProperty[] messages, StringProperty[] choices, StringProperty[] tooltips, Stage parentStage) {
        log.fine("dualChoiceButtons has been opened.");

        Stage multipleButtons = new Stage();
        multipleButtons.initOwner(parentStage);
        multipleButtons.initStyle(StageStyle.UNDECORATED);
        multipleButtons.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(multipleButtons);

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
                multipleButtons.close();
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
                vBox.setAlignment(Pos.CENTER);
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
        exit.setOnAction(e -> multipleButtons.close());

        mainLayout.getChildren().add(exit);
        mainLayout.setPadding(new Insets(6, 6, 6, 6));
        mainLayout.setSpacing(3);
        mainLayout.setAlignment(Pos.CENTER);

        Platform.runLater(() -> new MoveStage().moveStage(mainLayout, parentStage));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        multipleButtons.setScene(scene);
        multipleButtons.show();
        multipleButtons.hide();
        if (multipleButtons.getOwner() != null) {
            multipleButtons.setX(multipleButtons.getOwner().getX() + (multipleButtons.getOwner().getWidth() / 2) - (multipleButtons.getWidth() / 2));
            multipleButtons.setY(multipleButtons.getOwner().getY() + (multipleButtons.getOwner().getHeight() / 2) - (multipleButtons.getHeight() / 2));
        }
        multipleButtons.showAndWait();
        log.fine("dualChoiceButtons has been closed.");
        return answer[0];
    }

    public ArrayList<String> multipleCheckbox(StringProperty[] messages, StringProperty[] choices, StringProperty[] tooltips, StringProperty isAll, boolean onlyOneAllowed, Stage parentStage) {
        log.fine("dualChoiceButtons has been opened.");

        Stage multipleCheckbox = new Stage();
        multipleCheckbox.initOwner(parentStage);
        multipleCheckbox.initStyle(StageStyle.UNDECORATED);
        multipleCheckbox.initModality(Modality.APPLICATION_MODAL);
        GenericMethods.setIcon(multipleCheckbox);

        Label label = new Label();
        label.textProperty().bind(messages[0]);
        label.setPadding(new Insets(0, 0, 4, 0));

        ArrayList<String> result = new ArrayList<>();
        ArrayList<CheckBox> checkBoxes = new ArrayList<>(choices.length);
        HBox layout = new HBox();
        int i = 0;
        for (StringProperty checkBoxText : choices) {
            CheckBox checkBox = new CheckBox();
            checkBox.textProperty().bind(checkBoxText);
            checkBox.setOnAction(e -> {
                if (onlyOneAllowed || (!isAll.getValue().isEmpty() && checkBoxText == isAll)) {
                    checkBoxes.forEach(checkBox1 -> {
                        if (checkBox1 != checkBox) checkBox1.setSelected(false);
                    });
                } else if (!isAll.getValue().isEmpty() && checkBoxText != isAll) {
                    for (CheckBox checkBox1 : checkBoxes) {
                        log.info(checkBox1.textProperty().getValue());
                        if (checkBox1.textProperty().getValue().matches(isAll.getValue())) {
                            checkBox1.setSelected(false);
                            break;
                        }
                    }
                }
            });
            checkBoxes.add(i, checkBox);
            layout.getChildren().add(i, checkBoxes.get(i));
            i++;
        }
        checkBoxes.get(0).setSelected(true);

        if (tooltips != null && tooltips.length == checkBoxes.size()) {
            checkBoxes.forEach(button -> {
                int index = checkBoxes.indexOf(button);
                if (tooltips[index] != null && !tooltips[index].getValue().isEmpty())
                    button.setTooltip(new Tooltip(tooltips[checkBoxes.indexOf(button)].getValue()));
            });
        }

        layout.setSpacing(3);
        layout.setAlignment(Pos.CENTER);

        VBox mainLayout = new VBox();
        if (messages.length == 1) mainLayout.getChildren().addAll(label, layout);
        else {
            if (messages.length == checkBoxes.size()) {
                VBox vBox = new VBox();
                checkBoxes.forEach(button -> {
                    int index = checkBoxes.indexOf(button);
                    if (messages[index] != null && !messages[index].getValue().isEmpty()) {
                        Label label1 = new Label();
                        label1.textProperty().bind(messages[index]);
                        vBox.getChildren().add(index, label1);
                    }
                });
                mainLayout.getChildren().addAll(vBox, layout);
            } else mainLayout.getChildren().addAll(label, layout);
        }
        Button submit = new Button();
        submit.textProperty().bind(Strings.Submit);
        submit.setOnAction(e -> {
            checkBoxes.forEach(checkBox -> {
                if (checkBox.isSelected()) result.add(checkBox.textProperty().getValue());
            });
            if (result.isEmpty())
                new MessageBox().message(new StringProperty[]{Strings.YouMustSelectACheckbox}, multipleCheckbox);
            else multipleCheckbox.close();
        });
        Button exit = new Button(Strings.EmptyString, new ImageView("/image/UI/ExitButtonSmall.png"));
        exit.setOnAction(e -> multipleCheckbox.close());

        HBox buttonsHBox = new HBox();
        buttonsHBox.setAlignment(Pos.CENTER);
        buttonsHBox.setSpacing(3);
        buttonsHBox.getChildren().addAll(submit, exit);

        mainLayout.getChildren().add(buttonsHBox);
        mainLayout.setPadding(new Insets(6, 6, 6, 6));
        mainLayout.setSpacing(3);
        mainLayout.setAlignment(Pos.CENTER);

        Platform.runLater(() -> new MoveStage().moveStage(mainLayout, parentStage));

        Scene scene = new Scene(mainLayout);
        scene.getStylesheets().add("/gui/GenericStyle.css");

        multipleCheckbox.setScene(scene);
        multipleCheckbox.show();
        multipleCheckbox.hide();
        if (multipleCheckbox.getOwner() != null) {
            multipleCheckbox.setX(multipleCheckbox.getOwner().getX() + (multipleCheckbox.getOwner().getWidth() / 2) - (multipleCheckbox.getWidth() / 2));
            multipleCheckbox.setY(multipleCheckbox.getOwner().getY() + (multipleCheckbox.getOwner().getHeight() / 2) - (multipleCheckbox.getHeight() / 2));
        }
        multipleCheckbox.showAndWait();
        log.fine("dualChoiceButtons has been closed.");

        return result;
    }
}
