/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.bullet;

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
    
}
