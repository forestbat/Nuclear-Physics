package org.halvors.nuclearphysics.common.tile.reactor;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.api.fluid.IBoilHandler;
import org.halvors.nuclearphysics.common.ConfigurationManager;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.capabilities.CapabilityBoilHandler;
import org.halvors.nuclearphysics.common.capabilities.energy.EnergyStorage;
import org.halvors.nuclearphysics.common.capabilities.fluid.GasTank;
import org.halvors.nuclearphysics.common.init.ModSoundEvents;
import org.halvors.nuclearphysics.common.multiblock.ElectricTurbineMultiBlockHandler;
import org.halvors.nuclearphysics.common.multiblock.IMultiBlockStructure;
import org.halvors.nuclearphysics.common.network.packet.PacketTileEntity;
import org.halvors.nuclearphysics.common.tile.TileGenerator;
import org.halvors.nuclearphysics.common.type.Position;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 1 cubic meter of steam = 338260 J of energy
 *
 * The front of the turbine is where the output is.
 */
public class TileElectricTurbine extends TileGenerator implements IMultiBlockStructure<TileElectricTurbine>, IBoilHandler {
    private final int energyPerSteam = 40;
    private final int defaultTorque = 5000;
    private int torque = defaultTorque;

    private final GasTank tank = new GasTank(Fluid.BUCKET_VOLUME * 16) {
        @Override
        public boolean canDrain() {
            return false;
        }
    };

    // Radius of large turbine?
    private int multiBlockRadius = 1;

    // The power of the turbine this tick. In joules/tick
    private int power = 0;

    // Max power in watts.
    private int maxPower = 128000;

    // Current rotation of the turbine in radians.
    public float rotation = 0;

    public int tier = 0; // Synced

    private float angularVelocity = 0; // Synced
    private float previousAngularVelocity = 0;

    // MutliBlock methods.
    private ElectricTurbineMultiBlockHandler multiBlock;

    public TileElectricTurbine() {
        energyStorage = new EnergyStorage(maxPower * 20);
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.getX() - multiBlockRadius, pos.getY(), pos.getZ() - multiBlockRadius, pos.getX() + 1 + multiBlockRadius, pos.getY() + 1, pos.getZ() + 1 + multiBlockRadius);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);

        multiBlockRadius = tag.getInteger("multiBlockRadius");
        getMultiBlock().readFromNBT(tag);
        CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.readNBT(tank, null, tag.getTag("tank"));
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);

        tag.setInteger("multiBlockRadius", multiBlockRadius);
        getMultiBlock().writeToNBT(tag);
        tag.setTag("tank", CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.writeNBT(tank, null));

        return tag;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
        return (capability == CapabilityEnergy.ENERGY && facing == EnumFacing.UP && getMultiBlock().isPrimary()) || ((capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || capability == CapabilityBoilHandler.BOIL_HANDLER_CAPABILITY) && facing == EnumFacing.DOWN) || super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nonnull
    public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
        if (capability == CapabilityEnergy.ENERGY && facing == EnumFacing.UP && getMultiBlock().isPrimary()) {
            return (T) energyStorage;
        } else if (facing == EnumFacing.DOWN) {
            if (capability == CapabilityBoilHandler.BOIL_HANDLER_CAPABILITY) {
                return (T) this;
            } else if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
                return (T) tank;
            }
        }

        return super.getCapability(capability, facing);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void update() {
        super.update();

        if (getMultiBlock().isConstructed()) {
            torque = defaultTorque * 500 * getArea();
        } else {
            torque = defaultTorque * 500;
        }

        getMultiBlock().update();

        if (getMultiBlock().isPrimary()) {
            if (!world.isRemote) {
                // Increase spin rate and consume steam.
                if (tank.getFluidAmount() > 0 && power < maxPower) {
                    FluidStack fluidStack = tank.drainInternal((int) Math.ceil(Math.min(tank.getFluidAmount() * 0.1, getMaxPower() / energyPerSteam)), true);

                    if (fluidStack != null) {
                        power += fluidStack.amount * energyPerSteam;
                    }
                }

                ////////////////////////////////////////////////////////////////////////////////////////////////////////

                // Set angular velocity based on power and torque.
                angularVelocity = (float) ((power * 4 * 256) / torque);

                if (world.getWorldTime() % 3 == 0 && previousAngularVelocity != angularVelocity) {
                    NuclearPhysics.getPacketHandler().sendToReceivers(new PacketTileEntity(this), this);
                    previousAngularVelocity = angularVelocity;
                }

                if (power > 0) {
                    energyStorage.receiveEnergy((int) (power * ConfigurationManager.General.turbineOutputMultiplier), false);
                }
            } else if (angularVelocity != 0) {
                if (world.getWorldTime() % 26 == 0) {
                    // TODO: Tweak this volume, i suspect it is way to loud.
                    double maxVelocity = (getMaxPower() / torque) * 4;
                    float percentage =Math.min(angularVelocity * 4 / (float) maxVelocity, 1);

                    world.playSound(null, pos, ModSoundEvents.ELECTRIC_TURBINE, SoundCategory.BLOCKS, percentage, 1);
                }

                // Update rotation.
                rotation = (float) ((rotation + angularVelocity / 20) % (Math.PI * 2));
            }
        } else if (tank.getFluidAmount() > 0) {
            int amount = getMultiBlock().get().tank.fillInternal(tank.getFluid(), false);

            getMultiBlock().get().tank.fillInternal(tank.drainInternal(amount, true), true);
        }

        if (!world.isRemote) {
            if (world.getTotalWorldTime() % 60 == 0 && getMultiBlock().isConstructed()) {
                NuclearPhysics.getPacketHandler().sendToReceivers(new PacketTileEntity(this), this);
            }

            power = 0;
        }
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public Position[] getMultiBlockVectors() {
        Set<Position> positions = new HashSet<>();

        EnumFacing dir = EnumFacing.UP;
        int xMulti = dir.getFrontOffsetX() != 0 ? 0 : 1;
        int yMulti = dir.getFrontOffsetY() != 0 ? 0 : 1;
        int zMulti = dir.getFrontOffsetZ() != 0 ? 0 : 1;

        for (int x = -multiBlockRadius; x <= multiBlockRadius; x++) {
            for (int y = -multiBlockRadius; y <= multiBlockRadius; y++) {
                for (int z = -multiBlockRadius; z <= multiBlockRadius; z++) {
                    positions.add(new Position(x * xMulti, y * yMulti, z * zMulti));
                }
            }
        }

        return positions.toArray(new Position[0]);
    }

    @Override
    public World getWorldObject() {
        return world;
    }

    @Override
    public void onMultiBlockChanged() {
        if (!world.isRemote) {
            NuclearPhysics.getPacketHandler().sendToReceivers(new PacketTileEntity(this), this);
        }

        // Notify neighbor blocks of when multiblock is formed.
        world.notifyNeighborsOfStateChange(pos, blockType);
    }

    @Override
    public Position getPosition() {
        return new Position(this);
    }

    @Override
    public ElectricTurbineMultiBlockHandler getMultiBlock() {
        if (multiBlock == null) {
            multiBlock = new ElectricTurbineMultiBlockHandler(this);
        }

        return multiBlock;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public int receiveGas(FluidStack fluidStack, boolean doTransfer) {
        return tank.fillInternal(fluidStack, doTransfer);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void handlePacketData(ByteBuf dataStream) {
        super.handlePacketData(dataStream);

        if (world.isRemote) {
            getMultiBlock().handlePacketData(dataStream);
            tier = dataStream.readInt();
            angularVelocity = dataStream.readFloat();
            tank.handlePacketData(dataStream);
        }
    }

    @Override
    public List<Object> getPacketData(List<Object> objects) {
        super.getPacketData(objects);

        getMultiBlock().getPacketData(objects);
        objects.add(tier);
        objects.add(angularVelocity);
        tank.getPacketData(objects);

        return objects;
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public EnumSet<EnumFacing> getExtractingDirections() {
        if (getMultiBlock().isPrimary()) {
            return EnumSet.of(EnumFacing.UP);
        }

        return EnumSet.noneOf(EnumFacing.class);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    private int getMaxPower() {
        if (getMultiBlock().isConstructed()) {
            return maxPower * getArea();
        }

        return maxPower;
    }

    private int getArea() {
        return (int) ((multiBlockRadius + 0.5) * 2 * (multiBlockRadius + 0.5) * 2);
    }
}
