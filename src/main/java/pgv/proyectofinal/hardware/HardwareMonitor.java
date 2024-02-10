package pgv.proyectofinal.hardware;

import lombok.extern.slf4j.Slf4j;
import oshi.SystemInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

@Slf4j
public class HardwareMonitor implements Runnable{

    SystemInfo info = new SystemInfo();
    final int FACTOR_CONVERSION = 1000000000;
    final double ramTotal = (double)info.getHardware().getMemory().getTotal()/FACTOR_CONVERSION;
    String os = info.getOperatingSystem().getManufacturer() + " " + info.getOperatingSystem().getFamily() + " " + info.getOperatingSystem().getVersionInfo().getVersion();

    private boolean seguirArrancando = true;

    public void kill(){
        seguirArrancando = false;
    }

    @Override
    public void run() {
        try {
            while (seguirArrancando) {
                Thread.sleep(1000);
                var freq = Arrays.stream(info.getHardware().getProcessor().getCurrentFreq()).average().getAsDouble() / FACTOR_CONVERSION;
                var ramDisponible = info.getHardware().getMemory().getAvailable() / FACTOR_CONVERSION;
                double ramUso = ramTotal - ramDisponible;
                var tempCPU = info.getHardware().getSensors().getCpuTemperature();
                log.info(String.format("OS: %s\nCPU: FREQ:%.2fGHz TEMP:%.2fC\nRAM: %.2fGB/%.2fGB", os, freq, tempCPU, ramUso, ramTotal));
            }
        } catch (Exception e) {
            log.info(e.getMessage());
        }
    }
}
