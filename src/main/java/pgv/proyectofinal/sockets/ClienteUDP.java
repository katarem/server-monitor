package pgv.proyectofinal.sockets;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;

@Slf4j
public class ClienteUDP extends Thread{

    private DatagramSocket clientSocket;
    private static int NUMERO_CLIENTES = 0;

    private int id;
    private InetAddress ip;
    private int port;

    private boolean seguirArrancando = true;
    SystemInfo info;
    final int FACTOR_CONVERSION = 1000000000;
    private double ramTotal;
    private double freqTotal;
    private double UMBRAL_RAM;
    private double UMBRAL_FREQ;
    private String os;
    private String pcName;

    public ClienteUDP(@NonNull String ip,@NonNull int port){
        try {
            this.setDaemon(true);
            this.setName("cliente-udp");
            this.ip = InetAddress.getByName(ip);
            this.port = port;
            NUMERO_CLIENTES++;
            this.id = NUMERO_CLIENTES;
            info = new SystemInfo();
            pcName = info.getHardware().getComputerSystem().getModel().equals("System Product Name") ? "PC Sobremesa" : info.getHardware().getComputerSystem().getModel();
            os = info.getOperatingSystem().getManufacturer() + " " + info.getOperatingSystem().getFamily() + " " + info.getOperatingSystem().getVersionInfo().getVersion();
            freqTotal = (double)info.getHardware().getProcessor().getMaxFreq();
            ramTotal = (double)info.getHardware().getMemory().getTotal()/FACTOR_CONVERSION;
            UMBRAL_RAM = ((ramTotal*7)/10);
            UMBRAL_FREQ = ((freqTotal*7)/10);
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }


    public void kill(){
        seguirArrancando = false;
    }

    @Override
    public void run() {
        try {
            clientSocket = new DatagramSocket();
            log.info("empiezo a mandar mensajes...");
            while(seguirArrancando){
                String datosPc = getDatosPc();
                byte[] sendData = datosPc.getBytes();
                DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, ip,port);
                clientSocket.send(sendPacket);
                Thread.sleep(2500);
            }
        } catch (IOException | InterruptedException e) {
            log.error(e.getLocalizedMessage());
        }
    }

    private String getDatosPc(){
        var freq = Arrays.stream(info.getHardware().getProcessor().getCurrentFreq()).average().getAsDouble() / FACTOR_CONVERSION;

        var ramDisponible = info.getHardware().getMemory().getAvailable() / FACTOR_CONVERSION;
        double ramUso = ramTotal - ramDisponible;
        if(ramUso >= UMBRAL_RAM) return String.format("%d,ALERTA,DEMASIADA RAM EN USO %.2fGB/%.2fGB",id,ramUso,ramTotal);
        if(freq >= UMBRAL_FREQ) return String.format("%d,ALERTA,FRECUENCIA EN CPU ALTA %.2fGHz/%.2fGHz",id,freq,freqTotal);
        return String.format("%d|INFO|%s|%s|%.2fGHz|%.2fGB/%.2fGB",id, pcName,os, freq, ramUso, ramTotal);
    }

}
