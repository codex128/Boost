/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.anim;

import java.util.Collection;
import java.util.LinkedList;

/**
 *
 * @author codex
 */
public abstract class AbstractAnimDriver implements AnimationDriver {

    private final LinkedList<AnimEventListener> listeners = new LinkedList<>();
    
    @Override
    public Collection<AnimEventListener> getListeners() {
        return listeners;
    }
    
}
