/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.scene.Spatial;
import vhacd.VHACDParameters;
import vhacd4.Vhacd4Parameters;

/**
 *
 * @author codex
 */
public enum GeometricShape {
    
    Box("Box"),
    DynamicMesh("DynamicMesh"),
    GImpact("GImpact"),
    MergedBox("MergedBox"),
    MergedHull("MergedHull"),
    MergedMesh("MergedMesh"),
    Mesh("Mesh"),
    Vhacd("Vhacd"),
    Vhacd4("Vhacd4");
    
    private final String name;
    private GeometricShape(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    /**
     * Creates a collision shape based on the spatial shape.
     * 
     * @param shape shape generation method
     * @param spatial spatial to calculate from
     * @param vhacd parameters for vhacd collision shape (may be null if shape != Vhacd)
     * @param vhacd4 parameters for vhacd4 collision shape (may be null if shape != Vhacd4)
     * @return physical collision shape
     */
    public static CollisionShape createCollisionShape(GeometricShape shape, Spatial spatial, VHACDParameters vhacd, Vhacd4Parameters vhacd4) {
        switch (shape) {
            case Box: return CollisionShapeFactory.createBoxShape(spatial);
            case DynamicMesh: return CollisionShapeFactory.createDynamicMeshShape(spatial);
            case GImpact: return CollisionShapeFactory.createGImpactShape(spatial);
            case MergedBox: return CollisionShapeFactory.createMergedBoxShape(spatial);
            case MergedHull: return CollisionShapeFactory.createMergedHullShape(spatial);
            case MergedMesh: return CollisionShapeFactory.createMergedMeshShape(spatial);
            case Mesh: return CollisionShapeFactory.createMeshShape(spatial);
            case Vhacd:
                if (vhacd == null) {
                    throw new NullPointerException("Vhacd parameters must be specified for "+GeometricShape.Vhacd+".");
                }
                return CollisionShapeFactory.createVhacdShape(spatial, vhacd, new CompoundCollisionShape());
            case Vhacd4:
                if (vhacd4 == null) {
                    throw new NullPointerException("Vhacd4 parameters must be specified for "+GeometricShape.Vhacd4+".");
                }
                return CollisionShapeFactory.createVhacdShape(spatial, vhacd4, new CompoundCollisionShape());
            default: return null;
        }
    }
    /**
     * 
     * @param shape
     * @param spatial
     * @return 
     * @see #createCollisionShape(codex.esboost.bullet.GeometricShape, com.jme3.scene.Spatial, vhacd.VHACDParameters, vhacd4.Vhacd4Parameters)
     */
    public static CollisionShape createCollisionShape(GeometricShape shape, Spatial spatial) {
        return createCollisionShape(shape, spatial, null, null);
    }
    /**
     * 
     * @param shape
     * @param spatial
     * @param vhacd
     * @return 
     * @see #createCollisionShape(codex.esboost.bullet.GeometricShape, com.jme3.scene.Spatial, vhacd.VHACDParameters, vhacd4.Vhacd4Parameters)
     */
    public static CollisionShape createCollisionShape(GeometricShape shape, Spatial spatial, VHACDParameters vhacd) {
        return createCollisionShape(shape, spatial, vhacd, null);
    }
    /**
     * 
     * @param shape
     * @param spatial
     * @param vhacd4
     * @return 
     * @see #createCollisionShape(codex.esboost.bullet.GeometricShape, com.jme3.scene.Spatial, vhacd.VHACDParameters, vhacd4.Vhacd4Parameters)
     */
    public static CollisionShape createCollisionShape(GeometricShape shape, Spatial spatial, Vhacd4Parameters vhacd4) {
        return createCollisionShape(shape, spatial, null, vhacd4);
    }
    
}
