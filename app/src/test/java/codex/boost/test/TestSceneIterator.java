/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.test;

import codex.boost.scene.SceneGraphIterator;
import com.jme3.app.SimpleApplication;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 *
 * @author codex
 */
public class TestSceneIterator extends SimpleApplication {

    public static void main(String[] args) {
        new TestSceneIterator().start();
    }
    
    @Override
    public void simpleInitApp() {
        
        // setup scene graph
        Node n1 = new Node("n1");
        rootNode.attachChild(n1);
        n1.attachChild(new Node("n1-1"));
        n1.attachChild(new Node("n1-2"));
        Node n2 = new Node("n1-3");
        n1.attachChild(n2);
        n2.attachChild(new Node("n1-3-1"));
        n2.attachChild(new Node("n1-3-2"));
        Node n3 = new Node("n2");
        rootNode.attachChild(n3);
        
        // iterate
        SceneGraphIterator iterator = new SceneGraphIterator(rootNode);
        for (Spatial spatial : iterator) {
            System.out.println(constructTabs(iterator.getDepth())+spatial.getName());
        }
        
    }
    
    private String constructTabs(int n) {
        StringBuilder render = new StringBuilder();
        for (; n > 0; n--) {
            render.append("  ");
        }
        return render.toString();
    }
    
}
