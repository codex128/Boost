/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.export;

import com.jme3.asset.AssetManager;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import java.io.IOException;

/**
 *
 * @author codex
 */
public class AssetManagerPointer implements ProxySavable<AssetManager> {
    
    private AssetManager assetManager;
    
    @Override
    public void write(JmeExporter ex) throws IOException {
        ex.getCapsule(this);
    }
    @Override
    public void read(JmeImporter im) throws IOException {
        this.assetManager = im.getAssetManager();
        im.getCapsule(this);
    }
    @Override
    public void setObject(AssetManager object) {
        this.assetManager = object;
    }
    @Override
    public AssetManager getObject() {
        return assetManager;
    }
    
}
