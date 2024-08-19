/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.light.SpotLight;
import com.jme3.math.FastMath;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class InfluenceCone implements EntityComponent {
    
    private final float innerAngle;
    private final float outerAngle;

    public InfluenceCone() {
        this(FastMath.HALF_PI-0.01f, FastMath.HALF_PI-0.001f);
    }
    public InfluenceCone(float innerAngle, float outerAngle) {
        this.innerAngle = innerAngle;
        this.outerAngle = outerAngle;
    }
    public InfluenceCone(double innerAngle, double outerAngle) {
        this((float)innerAngle, (float)outerAngle);
    }

    public float getInnerAngle() {
        return innerAngle;
    }
    public float getOuterAngle() {
        return outerAngle;
    }
    public void applyToSpotLight(SpotLight light) {
        light.setSpotInnerAngle(innerAngle);
        light.setSpotOuterAngle(outerAngle);
    }
    @Override
    public String toString() {
        return "InfluenceCone{" + "innerAngle=" + innerAngle + ", outerAngle=" + outerAngle + '}';
    }
    
}
