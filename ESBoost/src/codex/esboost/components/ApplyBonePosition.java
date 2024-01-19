/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class ApplyBonePosition implements EntityComponent {
    
    public static final boolean ENTITY_TO_BONE = true, BONE_TO_ENTITY = false;
    
    private final boolean direction;

    public ApplyBonePosition(boolean direction) {
        this.direction = direction;
    }

    public boolean isDirection() {
        return direction;
    }
    @Override
    public String toString() {
        return "ApplyBonePosition{" + "direction=" + direction + '}';
    }
    
}
