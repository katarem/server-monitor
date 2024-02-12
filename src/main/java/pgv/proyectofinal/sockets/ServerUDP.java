package pgv.proyectofinal.sockets;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.App;
import pgv.proyectofinal.ui.MainController;
import pgv.proyectofinal.ui.ServerComponentController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

@Slf4j
public class ServerUDP extends Task<Void> {

    private DatagramSocket serverSocket;

    private boolean seguirArrancando = true;

    private MainController controller;

    public ServerUDP(@NonNull int port, MainController controller){
        try {
            serverSocket = new DatagramSocket(port);
            this.controller = controller;
        } catch (SocketException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    public ServerUDP(@NonNull int port){
        try {
            serverSocket = new DatagramSocket(port);
        } catch (SocketException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private boolean procesarMensaje(String mensaje){
        if(mensaje == null || mensaje.isBlank()) return false;
        log.info(mensaje);
        controller.procesarClient(mensaje);
        return true;
    }

    public void kill(){
        this.seguirArrancando = false;
    }

    @Override
    protected Void call() throws Exception {
        try {
            while(seguirArrancando){
                byte[] receiveData = new byte[2048];
                DatagramPacket receivedPacket = new DatagramPacket(receiveData, receiveData.length);
                serverSocket.receive(receivedPacket);
                String mensajeRecibido = new String(receivedPacket.getData(), 0, receivedPacket.getLength());
                procesarMensaje(mensajeRecibido);
                Thread.sleep(1000);
            }
        } catch (IOException  e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }





}