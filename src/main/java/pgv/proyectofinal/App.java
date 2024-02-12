package pgv.proyectofinal;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.sockets.ServerUDP;
import pgv.proyectofinal.ui.MainController;

@Slf4j
public class App extends Application {
    public static Stage stage;
    public static Scene loginScene, mainScene;
    public static int port;

    public static Alert alerta;

    @Override
    public void start(Stage stage) throws Exception {
        App.stage = stage;

        App.stage.setTitle("SERVER MONITOR");

        MainController mailController = new MainController();
        App.stage.setScene(mainScene);
        alerta = new Alert(Alert.AlertType.WARNING);
        mainScene = new Scene(mailController.getView());
        ServerUDP server = new ServerUDP(1234, mailController);
        Thread serverThread = new Thread(server);
        serverThread.setName("server-udp");
        serverThread.setDaemon(true);
        serverThread.start();

        App.stage.setOnCloseRequest(e -> {
            server.kill();
        });




        App.stage.setScene(App.mainScene);
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
