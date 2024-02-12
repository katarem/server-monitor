package pgv.proyectofinal.hw;


import lombok.Getter;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

import java.util.Arrays;

@Getter
public class PcStats {
    private final SystemInfo info;
    private final int FACTOR_CONVERSION = 1000000000;
    private final double MAX_RAM;
    private final double UMBRAL_RAM;
    private final int UMBRAL_CPU_USAGE = 70;
    private double currentFreq;
    private double currentRam;
    private double cpuUsage;
    private final String os;
    private final String pcName;

    public PcStats(){
        this.info = new SystemInfo();
        this.MAX_RAM = info.getHardware().getMemory().getTotal();
        this.UMBRAL_RAM = (MAX_RAM * 70)/100;

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

    public double getReadableMAXRam(){
        return getMAX_RAM() / FACTOR_CONVERSION;
    }

    public double getReadableRam(){
        return getCurrentRam() / FACTOR_CONVERSION;
    }



    @Override
    public String toString() {
        var usoCPU = String.format("%.2f",getCpuUsage()/FACTOR_CONVERSION);
        return String.format("%s;%s;%.2fGHz;%s;%.2fGB/%.2fGB",pcName,os,getCurrentFreq(),(usoCPU + "%"),(getCurrentRam()/FACTOR_CONVERSION),(MAX_RAM/FACTOR_CONVERSION));
    }
}
