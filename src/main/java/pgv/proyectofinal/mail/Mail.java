package pgv.proyectofinal.mail;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Mail {

	private String fromEmail;
	private String toEmail;
	private String asunto;
	private String contenido;
	private String fecha;
	
	public Mail() {}
	public Mail(String fromEmail, String asunto, String contenido, Date fecha) {
		super();
		this.fromEmail = fromEmail;
		this.asunto = asunto;
		this.contenido = contenido;
		setFecha(fecha);
	}

	public String getFromEmail() {
		return fromEmail;
	}
	public void setFromEmail(String fromEmail) {
		this.fromEmail = fromEmail;
	}
	public String getToEmail() {
		return toEmail;
	}
	public void setToEmail(String toEmail) {
		this.toEmail = toEmail;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getContenido() {
		return contenido;
	}
	public void setContenido(String contenido) {
		this.contenido = contenido;
	}
	public String getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy - HH:mm:ss");
		this.fecha = format.format(fecha);
	}

	@Override
	public String toString() {
		String resumen = "";
		if(contenido.length()>=20) resumen = contenido.substring(0,20);
		else resumen = contenido;
		return String.format("%s - %s - %s: %s", fromEmail,asunto, fecha, resumen);
	}
	
}
