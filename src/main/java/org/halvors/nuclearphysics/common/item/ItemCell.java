package org.halvors.nuclearphysics.common.item;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStackSimple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.Reference;
import org.halvors.nuclearphysics.common.init.ModItems;
import org.halvors.nuclearphysics.common.utility.FluidUtility;
import org.halvors.nuclearphysics.common.utility.LanguageUtility;

import javax.annotation.Nonnull;
import java.util.List;

public class ItemCell extends ItemTooltip {
    public static final int capacity = 200;

    public ItemCell() {
        super("cell");

        setContainerItem(ModItems.itemCell);
    }

    @Override
    public void registerItemModel() {
        NuclearPhysics.getProxy().registerItemRenderer(this, 0, name);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack itemStack, EntityPlayer player, List<String> list, boolean flag) {
        FluidStack fluidStack = FluidUtil.getFluidContained(itemStack);

        if (fluidStack != null) {
            list.add(LanguageUtility.transelate(getUnlocalizedName(itemStack) + ".tooltip", fluidStack.getLocalizedName()));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(@Nonnull Item item, CreativeTabs tab, NonNullList<ItemStack> list) {
        for (EnumCell type : EnumCell.values()) {
            list.add(type == EnumCell.EMPTY ? new ItemStack(item) : FluidUtility.getFilledCell(FluidRegistry.getFluid(type.getName())));
        }
    }

    @Override
    @Nonnull
    public ICapabilityProvider initCapabilities(ItemStack stack, NBTTagCompound tag) {
        return new FluidHandlerItemStackSimple(stack, capacity);
    }

    public enum EnumCell {
        EMPTY("empty"),
        DEUTERIUM("deuterium"),
        TRITIUM("tritium"),
        WATER("water");

        private String name;

        EnumCell(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }
}