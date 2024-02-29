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
public class Scene implements EntityComponent {
    
    private final Prefab prefab;
    private final boolean update;
    
    public Scene() {
        this(Prefab.getDefault(), true);
    }
    public Scene(Prefab prefab) {
        this(prefab, true);
    }
    public Scene(boolean update) {
        this(Prefab.getDefault(), update);
    }
    public Scene(Prefab prefab, boolean update) {
        this.prefab = prefab;
        this.update = update;
    }

    public Prefab getPrefab() {
        return prefab;
    }
    public boolean isUpdate() {
        return update;
    }
    
}
