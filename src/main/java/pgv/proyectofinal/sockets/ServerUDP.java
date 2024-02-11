package pgv.proyectofinal.sockets;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.App;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

@Slf4j
public class ServerUDP extends Task<Void> {

    private DatagramSocket serverSocket;
    private int port;
    private boolean seguirArrancando = true;
    public ServerUDP(@NonNull int port){
        try {
            this.port = port;
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            log.error(e.getLocalizedMessage());
        }
    }



    private void procesarMensaje(String mensaje){
        var contenidoMensaje = mensaje.split(",");
        var idServidor = contenidoMensaje[0];
        var tipoMensaje = contenidoMensaje[1];
        log.info("TIPO MENSAJE " + tipoMensaje);

        if(tipoMensaje.equals("ALERTA")){
            var mensajeAlerta = contenidoMensaje[2];
            Platform.runLater(() -> {
                App.showAlerta("ALERTA EN SERVIDOR " + idServidor, mensajeAlerta);
            });
        }
        else log.info(mensaje);
    }


    @Override
    protected Void call() throws Exception {
        try {
            log.info("estoy escuchando mensajes...");
            while(seguirArrancando){
                byte[] receiveData = new byte[1024];
                DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivedPacket);
                String mensajeRecibido = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                procesarMensaje(mensajeRecibido);
                //Thread.sleep(500);
            }
        } catch (IOException  e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }
}