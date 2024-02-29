/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.factories;

import com.simsilica.es.EntityData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author codex
 */
public class Prefab {
    
    private static final Logger Log = LoggerFactory.getLogger(Prefab.class);
    private static int nextId = -1;
    private static Prefab defPrefab = null;
    
    private final int id;
    private final String name;
    
    public Prefab(int id) {
        this(id, null);
    }
    public Prefab(String name) {
        this(0, name);
    }
    private Prefab(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getName(EntityData ed) {
        return name != null ? name : ed.getStrings().getString(id);
    }
    @Override
    public String toString() {
        return "Prefab{" + "id=" + id + " name=" + name + '}';
    }
    
    public static Prefab create(String name, EntityData ed) {
        return new Prefab(ed.getStrings().getStringId(name, true));
    }
    public static Prefab generateUnique() {
        return new Prefab(generateUniqueId());
    }
    public static int generateUniqueId() {
        int id = nextId;
        if (id == -Integer.MAX_VALUE) {
            nextId = -1;
            Log.warn("Unique id was reset because it exceeded the negative integer boundary.");
        } else {
            nextId--;
        }
        return id;
    }
    public static void setDefault(String name, EntityData ed) {
        defPrefab = Prefab.create("default", ed);
    }
    public static Prefab getDefault() {
        return defPrefab;
    }
    
}
