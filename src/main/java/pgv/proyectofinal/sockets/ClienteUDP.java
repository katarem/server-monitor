package pgv.proyectofinal.sockets;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;
import pgv.proyectofinal.hw.PcStats;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.Objects;

public class ClienteUDP extends Thread{

    private DatagramSocket clientSocket;

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
                Thread.sleep(1000);
            }
        } catch (IOException | InterruptedException e) {
           e.printStackTrace();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClienteUDP that = (ClienteUDP) o;
        return port == that.port && seguirArrancando == that.seguirArrancando && Objects.equals(clientSocket, that.clientSocket) && Objects.equals(ip, that.ip) && Objects.equals(info, that.info);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clientSocket, ip, port, seguirArrancando, info);
    }

    private String getDatosPc(){
        String usoCPU = String.format("%.2f",info.getCpuUsage());
        if(info.isDangerousRamUsage()) return String.format("%d;ALERTA;DEMASIADA RAM EN USO %.2fGB/%.2fGB",hashCode(),info.getReadableRam(),info.getReadableMAXRam());
        if(info.isDangerousCpuUsage()) return String.format("%d;ALERTA;USO EN CPU ALTO %s",hashCode(),usoCPU + "%");
        return String.format("%d;INFO;%s",hashCode(),info);
    }
}
