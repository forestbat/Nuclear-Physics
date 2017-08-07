package org.halvors.quantum.common.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.IStringSerializable;
import org.halvors.quantum.common.block.states.BlockStateRadioactive;

import javax.annotation.Nonnull;
import java.util.Random;

public class BlockRadioactiveGrass extends BlockRadioactive {
    public BlockRadioactiveGrass() {
        super("radioactive_grass", Material.GRASS);

        setHardness(0.2F);

        canSpread = true;
        radius = 5;
        amplifier = 2;
        canWalkPoison = true;
        isRandomlyRadioactive = true;
        spawnParticle = true;

        setDefaultState(blockState.getBaseState().withProperty(BlockStateRadioactive.TYPE, EnumRadioactive.GRASS));
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState() {
        return new BlockStateRadioactive(this);
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public IBlockState getStateFromMeta(int metadata) {
        return getDefaultState().withProperty(BlockStateRadioactive.TYPE, EnumRadioactive.values()[metadata]);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(BlockStateRadioactive.TYPE).ordinal();
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    public enum EnumRadioactive implements IStringSerializable {
        NORMAL("normal"),
        GRASS("grass");

        private String name;

        EnumRadioactive(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name.toLowerCase();
        }
    }
}
