/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.renderer.Camera;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class ViewPortRect implements EntityComponent {
    
    private final float left, right, bottom, top;
    
    public ViewPortRect() {
        this(0, 1, 0, 1);
    }
    public ViewPortRect(Camera cam) {
        this(cam.getViewPortLeft(), cam.getViewPortRight(), cam.getViewPortBottom(), cam.getViewPortTop());
    }
    public ViewPortRect(float left, float right, float bottom, float top) {
        this.left = left;
        this.right = right;
        this.bottom = bottom;
        this.top = top;
    }

    public float getLeft() {
        return left;
    }
    public float getRight() {
        return right;
    }
    public float getBottom() {
        return bottom;
    }
    public float getTop() {
        return top;
    }
    
    public void apply(Camera cam) {
        cam.setViewPort(left, right, bottom, top);
    }

    @Override
    public String toString() {
        return "ViewPortRect{" + "left=" + left + ", right=" + right + ", bottom=" + bottom + ", top=" + top + '}';
    }
    
}
