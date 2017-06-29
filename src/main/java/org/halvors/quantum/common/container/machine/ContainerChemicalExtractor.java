package org.halvors.quantum.common.container.machine;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotFurnace;
import net.minecraft.item.ItemStack;
import org.halvors.quantum.Quantum;
import org.halvors.quantum.common.container.ContainerQuantum;
import org.halvors.quantum.common.container.slot.SlotEnergyItem;
import org.halvors.quantum.common.container.slot.SlotSpecific;
import org.halvors.quantum.common.tile.machine.TileChemicalExtractor;

public class ContainerChemicalExtractor extends ContainerQuantum {
    public ContainerChemicalExtractor(InventoryPlayer inventoryPlayer, TileChemicalExtractor tile) {
        super(inventoryPlayer, tile);

        // Battery
        addSlotToContainer(new SlotEnergyItem(tile, 0, 80, 50));

        // Process Input (Uranium)
        addSlotToContainer(new SlotSpecific(tile, 1, 53, 25, new ItemStack(Quantum.blockUraniumOre)));

        // Process Output
        addSlotToContainer(new SlotFurnace(inventoryPlayer.player, tile, 2, 107, 25));

        // Fluid input fill
        addSlotToContainer(new Slot(tile, 3, 25, 19));

        // Fluid input drain
        addSlotToContainer(new Slot(tile, 4, 25, 50));

        // Fluid output drain
        addSlotToContainer(new Slot(tile, 5, 135, 19));

        addPlayerInventory(inventoryPlayer.player);
    }
}