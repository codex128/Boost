/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.factories.Prefab;
import com.jme3.math.Vector3f;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class Decal implements EntityComponent {
    
    private final Prefab material;
    private final Vector3f size;
    private final EntityId scene;
    
    public Decal(Prefab material, Vector3f size) {
        this(material, size, null);
    }
    public Decal(Prefab material, Vector3f size, EntityId scene) {
        this.material = material;
        this.size = size;
        this.scene = scene;
    }

    public Prefab getMaterial() {
        return material;
    }
    public Vector3f getSize() {
        return size;
    }
    public EntityId getScene() {
        return scene;
    }
    
}
