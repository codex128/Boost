/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class RenderBucket implements EntityComponent {
    
    private final Bucket bucket;

    public RenderBucket(Bucket bucket) {
        this.bucket = bucket;
    }

    public Bucket getBucket() {
        return bucket;
    }
    
}
