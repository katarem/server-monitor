package pgv.proyectofinal.sockets;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import pgv.proyectofinal.hw.PcStats;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

public class ClienteUDP extends Thread{

    private DatagramSocket clientSocket;
    private static int NUMERO_CLIENTES = 0;

    private int id;
    private InetAddress ip;
    private int port;

    private boolean seguirArrancando = true;

    private PcStats info;

    public ClienteUDP(@NonNull String ip,@NonNull int port){
        try {
            this.setDaemon(true);
            this.setName("cliente-udp");
            this.ip = InetAddress.getByName(ip);
            this.port = port;
            NUMERO_CLIENTES++;
            this.id = NUMERO_CLIENTES;

            info = new PcStats();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void kill(){
        seguirArrancando = false;
    }

    @Override
    public void run() {
        try {
            clientSocket = new DatagramSocket();
            while(seguirArrancando){
                String datosPc = getDatosPc();
                byte[] sendData = datosPc.getBytes();

                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip,port);
                clientSocket.send(sendPacket);
                Thread.sleep(500);
            }
        } catch (IOException | InterruptedException e) {
           e.printStackTrace();
        }
    }

    private String getDatosPc(){
        if(info.isDangerousRamUsage()) return String.format("%d;ALERTA;DEMASIADA RAM EN USO %.2fGB/%.2fGB",id,info.getCurrentRam(),info.getMAX_RAM());
        if(info.isDangerousCpuUsage()) return String.format("%d;ALERTA;USO EN CPU ALTO %s",id,info.getCpuUsage() + "%");
        return String.format("%d;INFO;%s",id,info);
    }
}
