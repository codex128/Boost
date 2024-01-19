/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import codex.esboost.bullet.GeometricShape;
import codex.esboost.factories.Prefab;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class GeometricShapeInfo implements EntityComponent {
    
    private final Prefab prefab;
    private final String type;

    public GeometricShapeInfo(Prefab prefab, String type) {
        this.prefab = prefab;
        this.type = type;
    }
    public GeometricShapeInfo(Prefab prefab, GeometricShape shape) {
        this(prefab, shape.name());
    }
    
    public Prefab getPrefab() {
        return prefab;
    }
    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "GeometricShapeInfo{" + "type=" + type + '}';
    }
    
    
    
}
