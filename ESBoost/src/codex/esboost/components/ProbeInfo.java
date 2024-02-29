/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.factories.Prefab;
import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityData;

/**
 * Holds a file path to a light probe asset.
 * 
 * @author codex
 */
public class ProbeInfo implements EntityComponent {
    
    private final Prefab prefab;

    public ProbeInfo(Prefab prefab) {
        this.prefab = prefab;
    }

    public Prefab getPrefab() {
        return prefab;
    }
    public String getName(EntityData ed) {
        return prefab.getName(ed);
    }
    
}
