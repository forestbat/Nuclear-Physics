package org.halvors.nuclearphysics.common.capabilities.energy;

public class EnergyStorage extends net.minecraftforge.energy.EnergyStorage {
    public EnergyStorage(int capacity) {
        super(capacity, capacity, capacity);
    }

    public EnergyStorage(int capacity, int maxTransfer) {
        super(capacity, maxTransfer);
    }

    public EnergyStorage(int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
    }

    public void setEnergyStored(int energy) {
        this.energy = energy;
    }
}
