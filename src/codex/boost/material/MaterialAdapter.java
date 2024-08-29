/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.material;

import com.jme3.asset.AssetKey;
import com.jme3.asset.AssetManager;
import com.jme3.material.MatParam;
import com.jme3.material.Material;
import com.jme3.material.MaterialDef;
import com.jme3.material.TechniqueDef;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Adapts materials by merging registered tributary materials into them.
 * 
 * @author codex
 */
public class MaterialAdapter {
    
    private final HashMap<String, LinkedList<String>> adapters = new HashMap<>();
    
    /**
     * Registers the named material definition as needing merging with the
     * named tributary material definition.
     * 
     * @param targetName
     * @param tributaryName 
     */
    public void add(String targetName, String tributaryName) {
        LinkedList<String> tributaries = adapters.get(targetName);
        if (tributaries == null) {
            tributaries = new LinkedList<>();
            adapters.put(targetName, tributaries);
        }
        tributaries.add(tributaryName);
    }
    
    /**
     * Registers the named material definition as needing merging with the
     * named tributary material definitions.
     * 
     * @param targetName
     * @param tributaryNames 
     */
    public void add(String targetName, String... tributaryNames) {
        LinkedList<String> tributaries = adapters.get(targetName);
        if (tributaries == null) {
            tributaries = new LinkedList<>();
            adapters.put(targetName, tributaries);
        }
        tributaries.addAll(Arrays.asList(tributaryNames));
    }
    
    private boolean adapt(AssetManager assetManager, MaterialDef target, String key) {
        LinkedList<String> tributaries = adapters.remove(key);
        if (tributaries == null) return false;
        for (String t : tributaries) {
            MaterialDef tributary = assetManager.loadAsset(new AssetKey<>(t));
            merge(target, tributary);
        }
        return true;
    }
    
    /**
     * Adapts all registered material definitions.
     * 
     * @param assetManager 
     */
    public void adaptAll(AssetManager assetManager) {
        for (String m : adapters.keySet()) {
            MaterialDef target = assetManager.loadAsset(new AssetKey<>(m));
            adapt(assetManager, target, m);
        }
    }
    
    /**
     * Adapts the target material if registered.
     * 
     * @param assetManager
     * @param target
     * @return true if the target material was adapted
     */
    public boolean adaptMaterial(AssetManager assetManager, Material target) {
        MaterialDef matdef = target.getMaterialDef();
        return adapt(assetManager, matdef, matdef.getAssetName());
    }
    
    /**
     * Adapts the target material if registered and the material does not contain
     * the named required technique.
     * 
     * @param assetManager
     * @param material
     * @param requiredTechnique
     * @return true if the target material contains the required technique (after merging)
     */
    public boolean adaptMaterial(AssetManager assetManager, Material material, String requiredTechnique) {
        List<TechniqueDef> techs = material.getMaterialDef().getTechniqueDefs(requiredTechnique);
        if (techs != null && !techs.isEmpty()) {
            return true;
        }
        return adaptMaterial(assetManager, material);
    }
    
    /**
     * Merges the tributary material definition into the target material definition.
     * 
     * @param target
     * @param tributary
     * @return 
     */
    public static MaterialDef merge(MaterialDef target, MaterialDef tributary) {
        try {
            // add cloned technique defs to intermediate list to avoid concurrent modification
            LinkedList<TechniqueDef> clones = new LinkedList<>();
            for (String n : tributary.getTechniqueDefsNames()) {
                for (TechniqueDef t : tributary.getTechniqueDefs(n)) {
                    clones.add(t.clone());
                }
            }
            for (TechniqueDef t : clones) {
                target.addTechniqueDef(t);
            }
            clones.clear();
            // move material parameters
            for (MatParam p : tributary.getMaterialParams()) {
                if (target.getMaterialParam(p.getName()) == null) {
                    target.addMaterialParam(p.getVarType(), p.getName(), p.getValue());
                    MatParam clone = target.getMaterialParam(p.getName());
                    clone.setTypeCheckEnabled(p.isTypeCheckEnabled());
                }
            }
        } catch (Exception ex) {
            throw new RuntimeException("Material merging unsuccessful.", ex);
        }
        return target;
    }
    
    /**
     * Merges the tributary material into the target material.
     * <p>
     * Current material parameters are also merged, which could overwrite
     * existing parameters in the target material.
     * 
     * @param target
     * @param tributary
     * @return 
     */
    public static Material merge(Material target, Material tributary) {
        merge(target.getMaterialDef(), tributary.getMaterialDef());
        for (MatParam p : tributary.getParams()) {
            target.setParam(p.getName(), p.getVarType(), p.getValue());
        }
        return target;
    }
    
}
