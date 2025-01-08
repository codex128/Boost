/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.export;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;

/**
 *
 * @author codex
 */
public class DirectSavable implements ProxySavable<Savable> {
    
    private static final String OBJECT = "object";
    
    private Savable object;
    
    @Override
    public void setObject(Savable object) {
        this.object = object;
    }
    @Override
    public Savable getObject() {
        return object;
    }
    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule out = ex.getCapsule(this);
        out.write(object, OBJECT, null);
    }
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        object = in.readSavable(OBJECT, null);
    }
    
}
