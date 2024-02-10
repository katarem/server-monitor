package pgv.proyectofinal.ui;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Accordion;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.asyncWorkers.MailTask;
import pgv.proyectofinal.hardware.HardwareMonitor;
import pgv.proyectofinal.mail.Mail;
import pgv.proyectofinal.mail.MailClient;

import javax.mail.Message;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

@Slf4j
public class MailController implements Initializable {

    @FXML
    private BorderPane view;
    @FXML
    private Accordion servidoresContainer;

    @FXML
    private ListView<Mail> mailList;

    private MailClient mailClient;


    public MailController(){
        try{
            FXMLLoader l = new FXMLLoader(getClass().getResource("/MailView.fxml"));
            l.setController(this);
            l.load();
        } catch(IOException e){
            log.error(e.getLocalizedMessage());
        }
    }

    public void setUser(String mail, String pwd){
        mailClient = new MailClient(mail,pwd);

        MailTask recopilarCorreos = new MailTask(mailClient);

        recopilarCorreos.setOnSucceeded(e -> {
            try {
                var mails = recopilarCorreos.get();
                mailList.setItems(FXCollections.observableList(mails));
            } catch (InterruptedException | ExecutionException ex) {
                log.error(ex.getLocalizedMessage());
            }
        });
        recopilarCorreos.setOnFailed(e -> log.error(e.getSource().getException().getLocalizedMessage()));

        new Thread(recopilarCorreos).start();

        HardwareMonitor hwMonitor = new HardwareMonitor();

        Platform.runLater(hwMonitor);


    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }
    public BorderPane getView() {
        return view;
    }
}
