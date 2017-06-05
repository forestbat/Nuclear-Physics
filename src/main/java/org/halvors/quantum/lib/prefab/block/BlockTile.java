package org.halvors.quantum.lib.prefab.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public abstract class BlockTile extends BlockAdvanced implements ITileEntityProvider {
    public BlockTile(Material material) {
        super(material);

        this.isBlockContainer = true;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int par6) {
        dropEntireInventory(world, x, y, z, block, par6);

        super.breakBlock(world, x, y, z, block, par6);
        world.removeTileEntity(x, y, z);
    }

    @Override
    public boolean onBlockEventReceived(World par1World, int par2, int par3, int par4, int par5, int par6) {
        super.onBlockEventReceived(par1World, par2, par3, par4, par5, par6);
        TileEntity tileentity = par1World.getTileEntity(par2, par3, par4);

        return tileentity != null && tileentity.receiveClientEvent(par5, par6);
    }

    public void dropEntireInventory(World world, int x, int y, int z, Block block, int par6) {
        TileEntity tileEntity = world.getTileEntity(x, y, z);

        if (tileEntity != null) {
            if (tileEntity instanceof IInventory) {
                IInventory inventory = (IInventory) tileEntity;

                for (int i = 0; i < inventory.getSizeInventory(); i++) {
                    ItemStack var7 = inventory.getStackInSlot(i);

                    if (var7 != null) {
                        Random random = new Random();
                        float var8 = random.nextFloat() * 0.8F + 0.1F;
                        float var9 = random.nextFloat() * 0.8F + 0.1F;
                        float var10 = random.nextFloat() * 0.8F + 0.1F;

                        while (var7.stackSize > 0) {
                            int var11 = random.nextInt(21) + 10;

                            if (var11 > var7.stackSize) {
                                var11 = var7.stackSize;
                            }

                            var7.stackSize -= var11;

                            EntityItem var12 = new EntityItem(world, x + var8, y + var9, z + var10, new ItemStack(var7.getItem(), var11, var7.getMetadata()));

                            if (var7.hasTagCompound()) {
                                var12.getEntityItem().setTagCompound((NBTTagCompound)var7.getTagCompound().copy());
                            }

                            float var13 = 0.05F;
                            var12.motionX = ((float)random.nextGaussian() * var13);
                            var12.motionY = ((float)random.nextGaussian() * var13 + 0.2F);
                            var12.motionZ = ((float)random.nextGaussian() * var13);
                            world.spawnEntityInWorld(var12);

                            if (var7.stackSize <= 0) {
                                inventory.setInventorySlotContents(i, null);
                            }
                        }
                    }
                }
            }
        }
    }

}
