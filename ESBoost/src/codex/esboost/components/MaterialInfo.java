/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.factories.Prefab;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class MaterialInfo implements EntityComponent {
    
    private final Prefab prefab;

    public MaterialInfo(Prefab prefab) {
        this.prefab = prefab;
    }

    public Prefab getPrefab() {
        return prefab;
    }

    @Override
    public String toString() {
        return "EntityMaterial{" + "prefab=" + prefab + '}';
    }
    
}
