package pgv.proyectofinal.hardware;

import oshi.SystemInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class HardwareMonitor implements Runnable{

    SystemInfo info = new SystemInfo();
    final int FACTOR_CONVERSION = 1000000000;
    final double ramTotal = (double)info.getHardware().getMemory().getTotal()/FACTOR_CONVERSION;
    String os = info.getOperatingSystem().getManufacturer() + " " + info.getOperatingSystem().getFamily() + " " + info.getOperatingSystem().getVersionInfo().getVersion();

    @Override
    public void run() {
        try{
            while(true){
                Thread.sleep(1000);
                var freq = Arrays.stream(info.getHardware().getProcessor().getCurrentFreq()).average().getAsDouble()/FACTOR_CONVERSION;
                var ramDisponible = info.getHardware().getMemory().getAvailable()/FACTOR_CONVERSION;
                double ramUso = ramTotal - ramDisponible;
                var tempCPU = info.getHardware().getSensors().getCpuTemperature();
                System.out.printf("OS: %s\nCPU: FREQ:%.2fGHz TEMP:%.2fC\nRAM: %.2fGB/%.2fGB",os,freq,tempCPU,ramUso,ramTotal);
                clearScreen();
            }
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public static void clearScreen() {
        System.out.print("\033[H\033[2J");
    }
}
