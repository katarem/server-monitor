package pgv.proyectofinal.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.sockets.ClienteUDP;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
@Getter
public class ServerComponentController implements Initializable {

    private int id;

    @FXML
    private TitledPane view;

    @FXML
    private Label osLabel, cpuLabel, ramLabel;

    private ClienteUDP cliente;

    public ServerComponentController(){
        try{
            FXMLLoader l = new FXMLLoader(getClass().getResource("/ServerComponentView.fxml"));
            l.setController(this);
            l.load();
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }

    public void setCliente(String data){
        var cleandata = data.split("|");
        view.setText(cleandata[0]);
        osLabel.setText(cleandata[1]);
        cpuLabel.setText(cleandata[2]);
        ramLabel.setText(cleandata[3]);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
