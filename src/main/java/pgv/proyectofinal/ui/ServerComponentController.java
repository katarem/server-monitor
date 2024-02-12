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

    public ServerComponentController(){
        try{
            FXMLLoader l = new FXMLLoader(getClass().getResource("/ServerComponentView.fxml"));
            l.setController(this);
            l.load();
        } catch(IOException e){
            log.error(e.getMessage());
        }
    }

    public void setData(String data){
        var cleandata = data.split(";");
        this.id = Integer.parseInt(cleandata[0]);
        view.setText("Servidor " + cleandata[0] + cleandata[2]);
        osLabel.setText(cleandata[3]);
        cpuLabel.setText(cleandata[4] + " " + cleandata[5]);
        ramLabel.setText(cleandata[6]);
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }
}
