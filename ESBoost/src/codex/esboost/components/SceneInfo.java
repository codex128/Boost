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
public class SceneInfo implements EntityComponent {
    
    private final Prefab prefab;
    private final boolean update;
    
    public SceneInfo() {
        this(Prefab.getDefault(), true);
    }
    public SceneInfo(Prefab prefab) {
        this(prefab, true);
    }
    public SceneInfo(boolean update) {
        this(Prefab.getDefault(), update);
    }
    public SceneInfo(Prefab prefab, boolean update) {
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
