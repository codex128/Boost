/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;

/**
 * Indicates that this entity represents a scene which
 * can be split down into other entities.
 * <p>
 * This should not be confused with {@link SceneId}, which indicates
 * a scene/group an entity is part of.
 * 
 * @author codex
 */
public class Scene implements EntityComponent {
    
    public static final Scene IMPL = new Scene();
    
}
