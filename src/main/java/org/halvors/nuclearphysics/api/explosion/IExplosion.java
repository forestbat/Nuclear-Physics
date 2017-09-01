package org.halvors.nuclearphysics.api.explosion;

/** The actual explosion interface. Extends Explosion.java.
 *
 * @author Calclavia */
public interface IExplosion {
    /** Called to initiate the explosion. */
    void explode();

    /** @return The radius of effect of the explosion. */
    float getRadius();

    /** @return The energy emitted by this explosive. In Joules and approximately based off of a real
     * life equivalent. */
    long getEnergy();
}