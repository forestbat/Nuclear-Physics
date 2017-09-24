package org.halvors.nuclearphysics.common.effect.potion;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import org.halvors.nuclearphysics.common.effect.poison.PoisonRadiation;

import javax.annotation.Nonnull;

public class PotionRadiation extends Potion {
    private static final PotionRadiation instance = new PotionRadiation();

    public PotionRadiation() {
        super(true, 0x4e9331);

        setPotionName("potion.radiation");
        setIconIndex(6, 0);
    }

    @Override
    public void performEffect(@Nonnull EntityLivingBase entity, int amplifier) {
        if (entity.getEntityWorld().rand.nextFloat() > 0.9 - amplifier * 0.07) {
            entity.attackEntityFrom(PoisonRadiation.getDamageSource(), 1);

            if (entity instanceof EntityPlayer) {
                ((EntityPlayer) entity).addExhaustion(0.01F * (amplifier + 1));
            }
        }
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return duration % 10 == 0;
    }

    public static PotionRadiation getInstance() {
        return instance;
    }
}
