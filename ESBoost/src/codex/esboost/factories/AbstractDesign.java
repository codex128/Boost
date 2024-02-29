/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityData;
import com.simsilica.es.EntityId;
import com.simsilica.sim.SimTime;
import com.simsilica.state.GameSystemsState;

/**
 *
 * @author codex
 */
public abstract class AbstractDesign implements Design {
    
    protected DesignTools tools;
    protected GameSystemsState systems;
    protected EntityData ed;
    protected SimTime time;
    protected Factory<Design> designFactory;
    protected EntityId main;
    
    @Override
    public void setDesignTools(DesignTools tools) {
        this.tools = tools;
        if (this.tools != null) {
            this.systems = this.tools.systems;
            this.ed = this.tools.ed;
            this.time = this.tools.time;
            this.designFactory = this.tools.designFactory;
        } else {
            this.systems = null;
            this.ed = null;
            this.time = null;
            this.designFactory = null;
        }
    }
    @Override
    public DesignTools getDesignTools() {
        return tools;
    }
    @Override
    public EntityId getMainEntity() {
        return main;
    }
    
}
