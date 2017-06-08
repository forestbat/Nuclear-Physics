package org.halvors.quantum.common.accelerator;

import net.minecraft.entity.player.InventoryPlayer;
import org.halvors.quantum.common.transform.vector.Vector3;
import org.halvors.quantum.lib.gui.GuiContainerBase;
import universalelectricity.api.energy.UnitDisplay;

public class GuiAccelerator extends GuiContainerBase {
    private TileAccelerator tileEntity;

    private int containerWidth;
    private int containerHeight;

    public GuiAccelerator(InventoryPlayer par1InventoryPlayer, TileAccelerator tileEntity) {
        super(new ContainerAccelerator(par1InventoryPlayer, tileEntity));
        this.tileEntity = tileEntity;
    }

    /** Draw the foreground layer for the GuiContainer (everything in front of the items) */
    @Override
    public void drawGuiContainerForegroundLayer(int x, int y) {
        fontRendererObj.drawString(tileEntity.getInventoryName(), 40, 10, 4210752);

        String status = null;
        Vector3 position = new Vector3(tileEntity);
        position.translate(tileEntity.getDirection().getOpposite());

        if (!EntityParticle.canRenderAcceleratedParticle(tileEntity.getWorld(), position)) {
            status = "\u00a74Fail to emit; try rotating.";
        } else if (tileEntity.entityParticle != null && tileEntity.velocity > 0) {
            status = "\u00a76Accelerating";
        } else {
            status = "\u00a72Idle";
        }

        fontRendererObj.drawString("Velocity: " + Math.round((this.tileEntity.velocity / TileAccelerator.clientParticleVelocity) * 100) + "%", 8, 27, 4210752);
        fontRendererObj.drawString("Energy Used:", 8, 38, 4210752);
        fontRendererObj.drawString(UnitDisplay.getDisplay(this.tileEntity.totalEnergyConsumed, UnitDisplay.Unit.JOULES), 8, 49, 4210752);
        fontRendererObj.drawString(UnitDisplay.getDisplay(TileAccelerator.energyPerTick * 20, UnitDisplay.Unit.WATT), 8, 60, 4210752);
        fontRendererObj.drawString(UnitDisplay.getDisplay(this.tileEntity.getVoltageInput(null), UnitDisplay.Unit.VOLTAGE), 8, 70, 4210752);
        fontRendererObj.drawString("Antimatter: " + this.tileEntity.antimatter + " mg", 8, 80, 4210752);
        fontRendererObj.drawString("Status:", 8, 90, 4210752);
        fontRendererObj.drawString(status, 8, 100, 4210752);
        fontRendererObj.drawString("Buffer: " + UnitDisplay.getDisplayShort(this.tileEntity.getEnergyHandler().getEnergy(), UnitDisplay.Unit.JOULES) + "/" + UnitDisplay.getDisplayShort(tileEntity.getEnergyHandler().getEnergyCapacity(), UnitDisplay.Unit.JOULES), 8, 110,
                4210752);
        fontRendererObj.drawString("Facing: " + this.tileEntity.getDirection().getOpposite(), 100, 123, 4210752);
    }

    /** Draw the background layer for the GuiContainer (everything behind the items) */
    @Override
    protected void drawGuiContainerBackgroundLayer(float par1, int x, int y) {
        super.drawGuiContainerBackgroundLayer(par1, x, y);

        this.drawSlot(131, 25);
        this.drawSlot(131, 50);
        this.drawSlot(131, 74);
        this.drawSlot(105, 74);
    }
}