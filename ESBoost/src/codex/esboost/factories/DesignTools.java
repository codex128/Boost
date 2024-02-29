/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityData;
import com.simsilica.sim.SimTime;
import com.simsilica.state.GameSystemsState;

/**
 * Wraps all objects necessary for {@link Design} creation
 * into one neat package.
 * 
 * @author codex
 */
public final class DesignTools {
    
    public final GameSystemsState systems;
    public final EntityData ed;
    public final SimTime time;
    public final Factory<Design> designFactory;
    
    public DesignTools(GameSystemsState systems, Factory<Design> designFactory) {
        this.systems = systems;
        ed = this.systems.get(EntityData.class, true);
        time = this.systems.getStepTime();
        this.designFactory = designFactory;
    }
    
    public final GameSystemsState getSystems() {
        return systems;
    }
    public final EntityData getEntityData() {
        return ed;
    }
    public final SimTime getTime() {
        return time;
    }
    public final Factory<Design> getDesignFactory() {
        return designFactory;
    }
    
}
