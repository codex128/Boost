/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class ViewPortMember implements EntityComponent {
    
    private final EntityId[] viewPorts;

    public ViewPortMember(EntityId... viewPorts) {
        this.viewPorts = viewPorts;
    }

    public EntityId[] getViewPorts() {
        return viewPorts;
    }
    
}
