package pgv.proyectofinal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.ui.UIController;

@Slf4j
public class App extends Application {

    public static Stage stage;
    public static Scene loginScene, mailScene;

    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;

        App.stage.setTitle("SERVER MONITOR");

        UIController i = new UIController();

        App.loginScene = new Scene(i.getView());

        App.stage.setScene(App.loginScene);

        App.stage.show();
        log.info("App started!");
    }

    public static void mailScreen(){
        App.stage.setScene(App.mailScene);
        App.stage.show();
    }



    public static void main(String[] args) {
        launch(args);
    }
}
