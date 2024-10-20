/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;
import com.simsilica.es.EntityId;

/**
 *
 * @author codex
 */
public class View implements EntityComponent {
    
    public static final int PRE = 0, MAIN = 1, POST = 2;
    
    private final EntityId camera;
    private final String name;
    private final int order;

    public View(EntityId camera, String name, int order) {
        this.camera = camera;
        this.name = name;
        this.order = order;
    }

    public EntityId getCamera() {
        return camera;
    }
    public String getName() {
        return name;
    }
    public int getOrder() {
        return order;
    }

    @Override
    public String toString() {
        return "View{" + "camera=" + camera + ", name=" + name + ", order=" + order + '}';
    }
    
}
