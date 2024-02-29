/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.bullet;

import codex.esboost.factories.Prefab;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.simsilica.bullet.CollisionShapes;
import com.simsilica.bullet.ShapeInfo;

/**
 *
 * @author codex
 */
public interface CollisionShapeCache extends CollisionShapes {
    
    /**
     * Registers the shape with a temporary cache.
     * <p>
     * When the shape is next loaded, it will be removed from the cache.
     * 
     * @param info
     * @param shape
     * @return 
     */
    public CollisionShape registerTemporary(ShapeInfo info, CollisionShape shape);
    
    /**
     * Registers the shape with a temporary cache under a unique id.
     * 
     * @param shape
     * @return shape info containing a unique id
     * @see #registerTemporary(com.simsilica.bullet.ShapeInfo, com.jme3.bullet.collision.shapes.CollisionShape)
     */
    public default ShapeInfo registerTemporary(CollisionShape shape) {
        ShapeInfo info = new ShapeInfo(Prefab.generateUniqueId());
        registerTemporary(info, shape);
        return info;
    }
    
}
