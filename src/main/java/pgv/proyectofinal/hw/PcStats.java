package pgv.proyectofinal.hw;


import lombok.Getter;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Arrays;

@Getter
public class PcStats {
    private SystemInfo info;
    private final int FACTOR_CONVERSION = 1000000000;
    private double MAX_RAM;
    private double UMBRAL_RAM;
    private final int UMBRAL_CPU_USAGE = 70;
    private double currentFreq;
    private double currentRam;
    private double cpuUsage;
    private String os;
    private String pcName;

    public PcStats(){
        this.info = new SystemInfo();
        this.MAX_RAM = info.getHardware().getMemory().getTotal();
        this.UMBRAL_RAM = (MAX_RAM * 7)/10;

        this.pcName = info.getHardware().getComputerSystem().getModel().equals("System Product Name") ? "PC Sobremesa" : info.getHardware().getComputerSystem().getModel();
        this.os = info.getOperatingSystem().getManufacturer() + " " + info.getOperatingSystem().getFamily() + " " + info.getOperatingSystem().getVersionInfo().getVersion();
    }


    public boolean isDangerousCpuUsage(){
        return getCpuUsage() >= UMBRAL_CPU_USAGE;
    }

    public boolean isDangerousRamUsage(){
        return getCurrentRam() >= UMBRAL_RAM;
    }

    public double getCpuUsage() {
        var loadAverage = info.getHardware().getProcessor().getSystemCpuLoadTicks();
        cpuUsage = 100 * (1 - (double)loadAverage[CentralProcessor.TickType.IDLE.getIndex()]);
        return cpuUsage;
    }

    public double getCurrentRam() {
        var disponible = info.getHardware().getMemory().getAvailable();
        currentRam = MAX_RAM - disponible;
        return currentRam;
    }

    public double getCurrentFreq() {
        var freqs = info.getHardware().getProcessor().getCurrentFreq();
        currentFreq = Arrays.stream(freqs).average().getAsDouble() / FACTOR_CONVERSION;
        return currentFreq;
    }

    @Override
    public String toString() {
        return String.format("%s;%s;%.2fGHz;%s;%.2fGB/%.2fGB",pcName,os,getCurrentFreq(),(getCpuUsage()/FACTOR_CONVERSION) + "%",(getCurrentRam()/FACTOR_CONVERSION),(MAX_RAM/FACTOR_CONVERSION));
    }
}
