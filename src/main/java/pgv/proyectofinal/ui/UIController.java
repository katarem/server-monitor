package pgv.proyectofinal.ui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
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

        MailClient client = new MailClient();
        if(client.checkConnection(correo,password)) //App.mailScreen();
            log.info("Login success");
        log.error("No se pudo logear");
    }

    public BorderPane getView(){
        return bPane;
    }


}
