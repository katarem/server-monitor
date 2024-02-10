package pgv.proyectofinal.ui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.App;
import pgv.proyectofinal.mail.MailClient;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

@Slf4j
public class UIController implements Initializable {
    @FXML
    private BorderPane bPane;
    @FXML
    private TextField userField;
    @FXML
    private PasswordField passField;

    @FXML
    private Label clientResponse;


    public UIController(){
        try{
            FXMLLoader l = new FXMLLoader(getClass().getResource("/UI.fxml"));
            l.setController(this);
            l.load();
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {



    }

    @FXML
    public void login(){

        var correo = userField.getText();
        var password = passField.getText();

        Platform.runLater(() -> {
            MailClient client = new MailClient(correo,password);
            if(client.checkConnection()){
                clientResponse.setStyle("-fx-text-fill: green;");
                clientResponse.setText("Login exitoso");
                App.mailScreen(correo,password);
            }
            else {
                clientResponse.setStyle("-fx-text-fill: red;");
                clientResponse.setText("Login fallido");
            }
        });
    }

    public BorderPane getView(){
        return bPane;
    }


}
