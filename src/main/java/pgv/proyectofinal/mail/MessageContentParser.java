package pgv.proyectofinal.mail;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

public class MessageContentParser {

	private MessageContentParser() {}
	
	public static String getTextFromMessage(Message message) throws MessagingException, IOException {
	    if (message.isMimeType("text/plain")) {
	        return message.getContent().toString();
	    } 
	    if (message.isMimeType("multipart/*")) {
	        MimeMultipart mimeMultipart = (MimeMultipart) message.getContent();
	        return getTextFromMimeMultipart(mimeMultipart);
	    }
	    return "contenido desconocido";
	}

	private static String getTextFromMimeMultipart(MimeMultipart mimeMultipart)  throws MessagingException, IOException{
	    StringBuilder result = new StringBuilder();
	    for (int i = 0; i < mimeMultipart.getCount(); i++) {
	        BodyPart bodyPart = mimeMultipart.getBodyPart(i);
	        if (bodyPart.isMimeType("text/plain")) {
	            return result.toString() + bodyPart.getContent();
	        }
	        result.append(parseBodyPart(bodyPart));
	    }
	    return result.toString();
	}

	public static String parseBodyPart(BodyPart bodyPart) throws MessagingException, IOException {
	    if (bodyPart.isMimeType("text/html")) {
	        return "contenido HTML";
	    }
	    if (bodyPart.getContent() instanceof MimeMultipart){
	        return getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
	    }

	    return "";
	}


}
