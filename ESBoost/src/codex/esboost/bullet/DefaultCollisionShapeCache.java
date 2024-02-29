/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.simsilica.bullet.CollisionShapes;
import com.simsilica.bullet.ShapeInfo;
import java.util.HashMap;
import java.util.function.Function;

/**
 *
 * @author codex
 */
public class DefaultCollisionShapeCache implements CollisionShapeCache {

    private final HashMap<Integer, CollisionShape> shapes = new HashMap<>();
    private final HashMap<Integer, CollisionShape> tempShapes = new HashMap<>();
    private Function<ShapeInfo, CollisionShape> factory;
    
    @Override
    public CollisionShape register(ShapeInfo info, CollisionShape shape) {
        shapes.put(info.getShapeId(), shape);
        return shape;
    }    
    @Override
    public CollisionShape registerTemporary(ShapeInfo info, CollisionShape shape) {
        tempShapes.put(info.getShapeId(), shape);
        return shape;
    }
    @Override
    public CollisionShape getShape(ShapeInfo info) {
        CollisionShape shape = tempShapes.remove(info.getShapeId());
        if (shape == null) {
            shape = shapes.get(info.getShapeId());
        }
        if (shape == null && factory != null) {
            shape = factory.apply(info);
        }
        if (shape == null) {
            throw new NullPointerException("Unable to load shape from "+info);
        }
        return shape;
    }
    
    public void setFactoryFunction(Function<ShapeInfo, CollisionShape> factory) {
        this.factory = factory;
    }
    public Function<ShapeInfo, CollisionShape> getFactoryFunction() {
        return factory;
    }
    
}
