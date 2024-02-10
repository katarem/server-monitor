package pgv.proyectofinal.mail;

import javax.mail.*;
import javax.mail.internet.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class MailClient {
    private Properties props;
    private Session session;
    
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

    private Transport connectSMTPServer(String email, String pwd) throws MessagingException{
        Transport t = session.getTransport("smtp");
        t.connect(props.getProperty("mail.smtp.host"), email, pwd);
        return t;
    }

    private Message createMessageCore(String source, String dest, String asunto) throws AddressException, MessagingException{
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(source));
        message.addRecipient(Message.RecipientType.TO, new InternetAddress(dest));
        message.setSubject(asunto);
        return message;
    }

    private Message createTextMessage(String source, String dest, String asunto, String textMessage) throws AddressException, MessagingException, IOException{
        Message message = createMessageCore(source, dest, asunto);

        BodyPart bodyPart = new MimeBodyPart();
        bodyPart.setText(textMessage);

        Multipart multipart = new MimeMultipart();
        multipart.addBodyPart(bodyPart);

        message.setContent(multipart);
        return message;
    }

    private Message createMessageWithFile(String emisor, String destinatario, String asunto, 
                                           String textoMensaje, String pathFichero) 
            throws MessagingException, AddressException, IOException {
        Message mensaje = createMessageCore(emisor, destinatario, asunto);

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

    public void sendTextMessage(String source, String dest, String asunto, String messageText, String mail,String pwd) throws AddressException, MessagingException, IOException{
        setPropsServerSMTP();
        Message message = createTextMessage(source, dest, asunto, messageText);
        Transport t = connectSMTPServer(mail, pwd);
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }

    public void sendTextWithFile(String source, String dest, String asunto, String messageText, String mail, String pwd, String filePath) throws MessagingException{
        setPropsServerSMTP();
        Message message = createMessageCore(source, dest, asunto);
        Transport t = connectSMTPServer(mail, pwd);
        t.sendMessage(message, message.getAllRecipients());
        t.close();
    }    
    
    public boolean checkConnection(String mail, String pwd) {
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
    
    
	public List<Mail> receiveMail(String mail, String pwd) {
		try {
			setPropsServerIMAP();
			Store store = session.getStore("imaps");
			store.connect(mail, pwd);

			Folder folder = store.getFolder("inbox");
			folder.open(Folder.READ_ONLY);
			
			Message[] messages = folder.getMessages();
			ArrayList<Mail> mails = new ArrayList<>();
			
			for (Message message : messages) {
				
				//if(mails.size()==100) break; // para cuentas con demasiados correos
				
				var from = Arrays.toString(message.getFrom());
				var to = Arrays.toString(message.getReplyTo());
				var subject = message.getSubject();
				var content = MessageContentParser.getTextFromMessage(message);
				var date = message.getReceivedDate();
				
				var correo = new Mail(from,to,subject,content,date);
				mails.add(correo);
				
			}
			return mails;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

}
