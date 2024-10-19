/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories.designs;

import codex.esboost.components.EnvMapSize;
import codex.esboost.components.InfluenceCone;
import codex.esboost.components.LightInfo;
import codex.esboost.components.LightPower;
import codex.esboost.components.ProbeInfo;
import codex.esboost.factories.AbstractDesign;
import codex.esboost.factories.Prefab;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;
import com.simsilica.es.Name;

/**
 *
 * @author codex
 */
public class LightDesign extends AbstractDesign {
    
    private final int lightType;

    public LightDesign(int lightType) {
        this.lightType = lightType;
    }
    
    @Override
    public void create() {
        main = ed.createEntity();
        ed.setComponents(main, new EntityComponent[] {
            new Name("light"),
            new LightInfo(lightType, ColorRGBA.White),
            new LightPower(10f),
            new InfluenceCone(),
            new EnvMapSize(256),
        });
    }
    @Override
    public void create(EntityId customer) {
        create();
    }
    @Override
    public void create(Spatial customer) {
        create();
    }
    
    public void setColor(ColorRGBA color) {
        ed.setComponent(main, new LightInfo(lightType, color));
    }
    public void setPower(float power) {
        ed.setComponent(main, new LightPower(power));
    }
    public void setAngles(float innerAngle, float outerAngle) {
        ed.setComponent(main, new InfluenceCone(innerAngle, outerAngle));
    }
    public void setProbe(Prefab prefab) {
        ed.setComponent(main, new ProbeInfo(prefab));
    }
    public void setEnvMapSize(int size) {
        ed.setComponent(main, new EnvMapSize(size));
    }
    
    public int getType() {
        return lightType;
    }
    
}
