/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.control;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * Controls nodes.
 * 
 * @author gary
 */
public abstract class NodeControl extends AbstractControl {
	
	protected Node node;
	
	@Override
	public void setSpatial(Spatial spat) {
		super.setSpatial(spat);
		if (spatial == null) {
			node = null;
		}
		else if (spatial instanceof Node) {
			node = (Node)spatial;
		}
		else {
			throw new IllegalArgumentException("Control can only control nodes!");
		}
	}
	public Node getNode() {
		return node;
	}
	
}
