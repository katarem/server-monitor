package pgv.proyectofinal.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.sockets.ServerUDP;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class MainController implements Initializable {

    @FXML
    private BorderPane view;
    @FXML
    private Accordion servidoresContainer;


    ServerUDP server = new ServerUDP(1234);

    public void addClient(){
        TitledPane newClientPane = new TitledPane();



    }

    public MainController(){
        try{
            FXMLLoader l = new FXMLLoader(getClass().getResource("/MailView.fxml"));
            l.setController(this);
            l.load();
        } catch(IOException e){
            log.error(e.getLocalizedMessage());
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Thread serverThread = new Thread(server);
        serverThread.setName("server-udp");
        serverThread.setDaemon(true);
        serverThread.start();
    }
    public BorderPane getView() {
        return view;
    }
}
