package org.halvors.quantum.common.item.armor;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemArmor;
import org.halvors.quantum.common.Quantum;
import org.halvors.quantum.common.Reference;

public class ItemArmorQuantum extends ItemArmor {
    public ItemArmorQuantum(String name, ArmorMaterial armorMaterial, EntityEquipmentSlot entityEquipmentSlot) {
        super(armorMaterial, 0, entityEquipmentSlot);

        setUnlocalizedName(name);
        setRegistryName(Reference.ID, name);
        //setTextureName(Reference.PREFIX + name);
        setCreativeTab(Quantum.getCreativeTab());
    }
}
