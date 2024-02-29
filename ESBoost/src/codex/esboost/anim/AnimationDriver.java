/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package codex.esboost.anim;

import codex.boost.Listenable;
import com.jme3.anim.AnimComposer;
import com.jme3.anim.SkinningControl;
import com.simsilica.sim.SimTime;

/**
 *
 * @author codex
 */
public interface AnimationDriver extends Listenable<AnimEventListener> {
    
    public void setupAnimations(AnimComposer anim, SkinningControl skin);
    
    public void updateAnimations(SimTime time, AnimComposer anim, SkinningControl skin);
    
}
