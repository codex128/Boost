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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 *
 * @author codex
 */
public class ConstructedObject implements ProxySavable {
    
    private static final String OBJECT = "object";
    private static final String ARGUMENTS = "arguments";
    
    private Object object;
    private Savable[] arguments;
    
    public ConstructedObject() {
        this(null, new Savable[0]);
    }
    public ConstructedObject(Object object, Savable... arguments) {
        this.object = object;
        this.arguments = arguments;
    }
    
    @Override
    public void setObject(Object object) {
        this.object = object;
    }
    @Override
    public Object getObject() {
        return object;
    }
    @Override
    public void write(JmeExporter ex) throws IOException {
        if (object == null) {
            throw new NullPointerException("Object to write cannot be null.");
        }
        OutputCapsule out = ex.getCapsule(this);
        Class clazz;
        if (object instanceof Class) {
            clazz = (Class)object;
        } else {
            clazz = object.getClass();
        }
        SavableObject.writeClass(out, clazz, OBJECT, null);
        out.write(arguments, ARGUMENTS, new Savable[0]);
    }
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        Class clazz = SavableObject.readClass(in, OBJECT, null);
        arguments = in.readSavableArray(ARGUMENTS, new Savable[0]);
        Class[] argTypes = new Class[arguments.length];
        for (int i = 0; i < arguments.length; i++) {
            Object a = arguments[i];
            while (a instanceof ProxySavable) {
                a = ((ProxySavable)a).getObject();
            }
            if (a == null) {
                argTypes[i] = Object.class;
            } else {
                argTypes[i] = a.getClass();
            }
        }
        try {
            Constructor c = clazz.getDeclaredConstructor(argTypes);
            c.setAccessible(true);
            object = c.newInstance((Object[])arguments);
        } catch (NoSuchMethodException | SecurityException | InstantiationException
                | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new RuntimeException("Error instantiating constructed object.", ex);
        }
    }
    
    public void setArguments(Savable[] arguments) {
        this.arguments = arguments;
    }
    
    public Savable[] getArguments() {
        return arguments;
    }
    
}
