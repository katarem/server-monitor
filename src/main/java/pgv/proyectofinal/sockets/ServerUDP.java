package pgv.proyectofinal.sockets;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.Accordion;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.App;
import pgv.proyectofinal.ui.ServerComponentController;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

@Slf4j
public class ServerUDP extends Task<Void> {

    private DatagramSocket serverSocket;
    private int port;

    private int N_Clientes = 0;

    private boolean seguirArrancando = true;

    private Accordion servidoresContainer;

    private ArrayList<ServerComponentController> clientes;

    public ServerUDP(@NonNull int port, Accordion servidoresContainer){
        try {
            this.port = port;
            serverSocket = new DatagramSocket(port);
            this.servidoresContainer = servidoresContainer;
        } catch (SocketException e) {
            log.error(e.getLocalizedMessage());
        }
    }

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

        if(tipoMensaje.equals("ALERTA")){
            var mensajeAlerta = contenidoMensaje[2];
            Platform.runLater(() -> {
                App.showAlerta("ALERTA EN SERVIDOR " + idServidor, mensajeAlerta);
            });
        }
        else {
            if(Integer.parseInt(idServidor) > N_Clientes) appendCliente(mensaje);
            else showCliente(Integer.parseInt(idServidor), mensaje);
        }
    }

    private void appendCliente(String data){
        ServerComponentController controller = new ServerComponentController();
        controller.setData(data);
        servidoresContainer.getPanes().add(controller.getView());
        clientes.add(controller);

    }

    private void showCliente(int id, String data){
        var cliente = clientes.stream().filter(c -> c.getId() == id).findAny();
        if(cliente.isPresent()){
            var index = servidoresContainer.getPanes().indexOf(cliente.get().getView());
            servidoresContainer.getPanes().remove(cliente.get().getView());
            cliente.get().setData(data);
            servidoresContainer.getPanes().add(index, cliente.get().getView());
        }
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
            }
        } catch (IOException  e) {
            log.error(e.getLocalizedMessage());
        }
        return null;
    }





}