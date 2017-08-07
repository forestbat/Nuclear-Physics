package org.halvors.quantum.common.block.states;

import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockStateContainer;
import org.halvors.quantum.common.block.BlockRadioactiveGrass;
import org.halvors.quantum.common.block.BlockRadioactiveGrass.EnumRadioactive;

public class BlockStateRadioactive extends BlockStateContainer {
    public static final PropertyEnum<EnumRadioactive> TYPE = PropertyEnum.create("type", EnumRadioactive.class);

    public BlockStateRadioactive(BlockRadioactiveGrass block) {
        super(block, TYPE);
    }
}