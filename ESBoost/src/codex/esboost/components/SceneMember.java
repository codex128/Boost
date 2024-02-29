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
public class SceneMember implements EntityComponent {
    
    private final EntityId scene;

    public SceneMember(EntityId scene) {
        this.scene = scene;
    }

    public EntityId getScene() {
        return scene;
    }
    
}
