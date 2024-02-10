package pgv.proyectofinal.asyncWorkers;

import javafx.concurrent.Task;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.mail.Mail;
import pgv.proyectofinal.mail.MailClient;

import javax.mail.Message;
import javax.mail.MessagingException;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class MailTask extends Task<List<Mail>> {

    private MailClient client;

    public MailTask(MailClient client){
        this.client = client;
    }

    @Override
    protected List<Mail> call() throws Exception {
        return client.receiveMail(50);
    }
}
