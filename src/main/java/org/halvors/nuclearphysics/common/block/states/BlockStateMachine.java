package org.halvors.nuclearphysics.common.block.states;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.IStringSerializable;
import org.halvors.nuclearphysics.common.NuclearPhysics;
import org.halvors.nuclearphysics.common.block.machine.BlockMachine;
import org.halvors.nuclearphysics.common.tile.particle.TileParticleAccelerator;
import org.halvors.nuclearphysics.common.tile.particle.TileQuantumAssembler;
import org.halvors.nuclearphysics.common.tile.process.TileChemicalExtractor;
import org.halvors.nuclearphysics.common.tile.process.TileGasCentrifuge;
import org.halvors.nuclearphysics.common.tile.process.TileNuclearBoiler;
import org.halvors.nuclearphysics.common.tile.reactor.fusion.TilePlasmaHeater;

import javax.annotation.Nonnull;

public class BlockStateMachine extends BlockStateFacing {
    public static final PropertyEnum<EnumMachine> TYPE = PropertyEnum.create("type", EnumMachine.class);

    public BlockStateMachine(BlockMachine block) {
        super(block, TYPE);
    }

    public enum EnumMachine implements IStringSerializable {
        CHEMICAL_EXTRACTOR("chemical_extractor", TileChemicalExtractor.class, EnumBlockRenderType.ENTITYBLOCK_ANIMATED),
        GAS_CENTRIFUGE("gas_centrifuge", TileGasCentrifuge.class, EnumBlockRenderType.ENTITYBLOCK_ANIMATED),
        NUCLEAR_BOILER("nuclear_boiler", TileNuclearBoiler.class, EnumBlockRenderType.ENTITYBLOCK_ANIMATED),
        PARTICLE_ACCELERATOR("particle_accelerator", TileParticleAccelerator.class, EnumBlockRenderType.MODEL),
        PLASMA_HEATER("plasma_heater", TilePlasmaHeater.class, EnumBlockRenderType.ENTITYBLOCK_ANIMATED),
        QUANTUM_ASSEMBLER("quantum_assembler", TileQuantumAssembler.class, EnumBlockRenderType.ENTITYBLOCK_ANIMATED);

        private String name;
        private Class<? extends TileEntity> tileClass;
        private EnumBlockRenderType renderType;

        EnumMachine(String name, Class<? extends TileEntity> tileClass, EnumBlockRenderType renderType) {
            this.name = name;
            this.tileClass = tileClass;
            this.renderType = renderType;
        }

        @Override
        @Nonnull
        public String getName() {
            return name;
        }

        public Class<? extends TileEntity> getTileClass() {
            return tileClass;
        }

        public TileEntity getTileAsInstance() {
            try {
                return tileClass.newInstance();
            } catch (Exception e) {
                NuclearPhysics.getLogger().error("Unable to indirectly create tile entity.");
                e.printStackTrace();

                return null;
            }
        }

        public EnumBlockRenderType getRenderType() {
            return renderType;
        }
    }
}