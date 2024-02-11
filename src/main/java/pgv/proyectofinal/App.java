package pgv.proyectofinal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.ui.MainController;

@Slf4j
public class App extends Application {
    public static Stage stage;
    public static Scene loginScene, mailScene;
    public static int port;

    public static Alert alerta;

    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;

        App.stage.setTitle("SERVER MONITOR");

        MainController mailController = new MainController();
        App.stage.setScene(mailScene);
        alerta = new Alert(Alert.AlertType.WARNING);
        mailScene = new Scene(mailController.getView());
        App.stage.setScene(App.mailScene);
        App.stage.show();
        log.info("App started!");
    }
    public static void showAlerta(String title, String mensaje){
        alerta.setTitle(title);
        alerta.setContentText(mensaje);
        alerta.show();
    }



    public static void main(String[] args) {
        App.port = Integer.parseInt(args[5]);
        launch(args);
    }
}
