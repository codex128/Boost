/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.factories.Prefab;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;

/**
 *
 * @author codex
 */
public class ModelInfo implements EntityComponent {
    
    private final Prefab prefab;

    public ModelInfo(Prefab prefab) {
        this.prefab = prefab;
    }
    
    public Prefab getPrefab() {
        return prefab;
    }
    public int getId() {
        return prefab.getId();
    }
    public String getName(EntityData ed) {
        return prefab.getName(ed);
    }
    
    public static ModelInfo create(String name, EntityData ed) {
        return new ModelInfo(Prefab.create(name, ed));
    }
    
}
