package org.halvors.nuclearphysics.common.effect.poison;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.BlockPos;
import org.halvors.nuclearphysics.common.effect.potion.PotionRadiation;

public class PoisonRadiation extends Poison {
    private static final PoisonRadiation instance = new PoisonRadiation("radiation");
    private static final DamageSource damageSource = new DamageSource("radiation").setDamageBypassesArmor();

    public PoisonRadiation(String name) {
        super(name);
    }

    @Override
    protected void doPoisonEntity(BlockPos pos, EntityLivingBase entity, int amplifier) {
        // TODO: Add option to disable poisoning?
        entity.addPotionEffect(new PotionEffect(PotionRadiation.getInstance(), 300 * (amplifier + 1), amplifier));
    }

    public static PoisonRadiation getInstance() {
        return instance;
    }

    public static DamageSource getDamageSource() {
        return damageSource;
    }
}
