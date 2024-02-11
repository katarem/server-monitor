package pgv.proyectofinal;

import lombok.extern.slf4j.Slf4j;
import pgv.proyectofinal.sockets.ClienteUDP;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;

@Slf4j
public class AppClient {
    public static void main(String[] args) {
        try{
            ClienteUDP cliente = new ClienteUDP(args[3],Integer.parseInt(args[5]));
            cliente.start();
            cliente.join();
        } catch(InterruptedException e){
            log.error(e.getLocalizedMessage());
        }
    }
}
