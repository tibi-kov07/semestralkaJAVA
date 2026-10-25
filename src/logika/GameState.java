package logika;

import java.io.Serializable;

public class GameState implements Serializable {
    private static final long serialVersionUID = 1L;

    private boolean electricPanelOpen = false;
    private boolean powerRestored = false;
    private boolean boilerSolved = false;
    private boolean exitCodeObtained = false;

    public boolean isElectricPanelOpen() { return electricPanelOpen; }
    public void setElectricPanelOpen(boolean electricPanelOpen) { this.electricPanelOpen = electricPanelOpen; }

    public boolean isPowerRestored() { return powerRestored; }
    public void setPowerRestored(boolean powerRestored) { this.powerRestored = powerRestored; }

    public boolean isBoilerSolved() { return boilerSolved; }
    public void setBoilerSolved(boolean boilerSolved) { this.boilerSolved = boilerSolved; }

    public boolean isExitCodeObtained() { return exitCodeObtained; }
    public void setExitCodeObtained(boolean exitCodeObtained) { this.exitCodeObtained = exitCodeObtained; }

}
