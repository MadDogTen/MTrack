package program.temp;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class MainSceneTest extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        primaryStage.setTitle("MTrack");

        Label label = new Label();
        label.setText("Testing");
        label.setAlignment(Pos.CENTER);
        label.autosize();

        Scene scene = new Scene(label);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
