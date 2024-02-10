package pgv.proyectofinal.mail;

import lombok.extern.slf4j.Slf4j;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

@Slf4j
public class MailClient {
    private Properties props;
    private Session session;

    private final String mail;
    private final String pwd;

    public MailClient(String mail, String pwd){
        this.mail = mail;
        this.pwd = pwd;
    }

    private void setPropsServerSMTP(){
        props = System.getProperties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.host","smtp.gmail.com");
        props.put("mail.smtp.port","587");
        props.put("mail.smtp.starttls.enable", "true");
        session = Session.getInstance(props);
    }
    
    private void setPropsServerIMAP() {
    	props = System.getProperties();
    	props.put("mail.store.protocol", "imaps");
        props.put("mail.imaps.host", "imap.gmail.com");
        props.put("mail.imaps.port", "993");
        props.put("mail.imaps.starttls.enable", "true");
        session = Session.getInstance(props);
    }

    private Transport connectSMTPServer() throws MessagingException{
        Transport t = session.getTransport("smtp");
        t.connect(props.getProperty("mail.smtp.host"), mail, pwd);
        return t;
    }

    private Message createMessageCore(String dest, String asunto) throws AddressException, MessagingException{
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(mail));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
        message.setSubject(asunto);
        return message;
    }

    private Message createTextMessage(String dest, String asunto, String textMessage) throws AddressException, MessagingException, IOException{
        Message message = createMessageCore(dest, asunto);

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(textMessage);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        message.setContent(multipart);
        return message;
    }

    private Message createMessageWithFile(String destinatario, String asunto,
                                           String textoMensaje, String pathFichero) 
            throws MessagingException, AddressException, IOException {
        Message mensaje = createMessageCore(destinatario, asunto);

        // Cuerpo del mensaje
        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(textoMensaje);

        // Adjunto del mensaje
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        mimeBodyPart.attachFile(new File(pathFichero));

        // Composición del mensaje (Cuerpo + Adjunto)
        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);
        multipart.addBodyPart(mimeBodyPart);

        mensaje.setContent(multipart);
        return mensaje;
    }

    public void sendTextMessage(String dest, String asunto, String messageText) throws AddressException, MessagingException, IOException{
        setPropsServerSMTP();
        Message message = createTextMessage(dest, asunto, messageText);
        Transport t = connectSMTPServer();
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }

    public void sendTextWithFile(String dest, String asunto, String messageText, String filePath) throws MessagingException{
        setPropsServerSMTP();
        Message message = createMessageCore(dest, asunto);
        Transport t = connectSMTPServer();
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }    
    
    public boolean checkConnection() {
    	try {
    		setPropsServerIMAP();
    		Store store = session.getStore("imaps");
			store.connect(mail,pwd);
			store.close();
			return true;
		} catch (MessagingException e) {
			return false;
		}
    }
    
    
	public List<Mail> receiveMail(int numeroCorreos) {
		try {
			setPropsServerIMAP();
			Store store = session.getStore("imaps");
			store.connect(mail, pwd);

			Folder folder = store.getFolder("inbox");
			folder.open(Folder.READ_ONLY);
			
			var messages = Arrays.asList(folder.getMessages());
            Collections.reverse(messages);
			ArrayList<Mail> mails = new ArrayList<>();

            //DEBUG ONLY
            int contador = 0;


			log.info("comienzo a serializar correos");
			for (Message message : messages) {
				
				if(mails.size()==numeroCorreos) break; // para cuentas con demasiados correos
				
				var from = Arrays.toString(message.getFrom());
				var subject = message.getSubject();
				var content = MessageContentParser.getTextFromMessage(message);
				var date = message.getReceivedDate();
				
				var correo = new Mail(from,subject,content,date);
				mails.add(correo);
                contador++;
				log.info("correo añadido " + contador);
			}

            log.info("termine de serializar correos");
			return mails;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

}
