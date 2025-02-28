/*
 * Copyright (c) 2024 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package codex.boost.export;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.util.IntMap;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Saves the internal object, either as a legitimate savable or by reflection.
 * 
 * @author codex
 */
public class SavableObject implements ProxySavable<Object> {

    public static final SavableObject NULL = new SavableObject();
    private static final String NAME = "name";
    private static final String OBJECT = "object";
    private static final String TYPE = "type";
    private static final String NULL_TYPE = "Null";
    private static final String UNKNOWN_TYPE = "Unknown";
    private static final Logger LOG = Logger.getLogger(SavableObject.class.getName());
    
    private String name;
    private Object object;
    
    public SavableObject() {
        this(null, null);
    }
    public SavableObject(String name) {
        this(name, null);
    }
    public SavableObject(Object object) {
        this(null, object);
    }
    public SavableObject(String name, Object object) {
        this.name = name;
        this.object = object;
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
        OutputCapsule out = ex.getCapsule(this);
        out.write(name, NAME, null);
        if (object == null) {
            return;
        }
        if (object instanceof Savable) {
            out.write((Savable)object, OBJECT, null);
            out.write("Savable", TYPE, NULL_TYPE);
        } else if (object instanceof Savable[]) {
            out.write((Savable[])object, OBJECT, new Savable[0]);
            out.write("Savable[]", TYPE, NULL_TYPE);
        } else if (object instanceof Savable[][]) {
            out.write((Savable[][])object, OBJECT, new Savable[0][0]);
            out.write("Savable[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Integer) {
            out.write((int)object, OBJECT, 0);
            out.write("Integer", TYPE, NULL_TYPE);
        } else if (object instanceof Integer[]) {
            out.write((int[])object, OBJECT, new int[0]);
            out.write("Integer[]", TYPE, NULL_TYPE);
        } else if (object instanceof Integer[][]) {
            out.write((int[][])object, OBJECT, new int[0][0]);
            out.write("Integer[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Float) {
            out.write((float)object, OBJECT, 0);
            out.write("Float", TYPE, NULL_TYPE);
        } else if (object instanceof Float[]) {
            out.write((float[])object, OBJECT, new float[0]);
            out.write("Float[]", TYPE, NULL_TYPE);
        } else if (object instanceof Float[][]) {
            out.write((float[][])object, OBJECT, new float[0][0]);
            out.write("Float[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Double) {
            out.write((double)object, OBJECT, 0);
            out.write("Double", TYPE, NULL_TYPE);
        } else if (object instanceof Double[]) {
            out.write((double[])object, OBJECT, new double[0]);
            out.write("Double[]", TYPE, NULL_TYPE);
        } else if (object instanceof Double[][]) {
            out.write((double[][])object, OBJECT, new double[0][0]);
            out.write("Double[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Boolean) {
            out.write((boolean)object, OBJECT, false);
            out.write("Boolean", TYPE, NULL_TYPE);
        } else if (object instanceof Boolean[]) {
            out.write((boolean[])object, OBJECT, new boolean[0]);
            out.write("Boolean[]", TYPE, NULL_TYPE);
        } else if (object instanceof Boolean[][]) {
            out.write((boolean[][])object, OBJECT, new boolean[0][0]);
            out.write("Boolean[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Byte) {
            out.write((byte)object, OBJECT, (byte)0);
            out.write("Byte", TYPE, NULL_TYPE);
        } else if (object instanceof Byte[]) {
            out.write((byte[])object, OBJECT, new byte[0]);
            out.write("Byte[]", TYPE, NULL_TYPE);
        } else if (object instanceof Byte[][]) {
            out.write((byte[][])object, OBJECT, new byte[0][0]);
            out.write("Byte[][]", TYPE, NULL_TYPE);
        } else if (object instanceof String) {
            out.write((String)object, OBJECT, null);
            out.write("String", TYPE, NULL_TYPE);
        } else if (object instanceof String[]) {
            out.write((String[])object, OBJECT, new String[0]);
            out.write("String[]", TYPE, NULL_TYPE);
        } else if (object instanceof String[][]) {
            out.write((String[][])object, OBJECT, new String[0][0]);
            out.write("String[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Long) {
            out.write((Long)object, OBJECT, 0);
            out.write("Long", TYPE, NULL_TYPE);
        } else if (object instanceof Long[]) {
            out.write((long[])object, OBJECT, new long[0]);
            out.write("Long[]", TYPE, NULL_TYPE);
        } else if (object instanceof Long[][]) {
            out.write((long[][])object, OBJECT, new long[0][0]);
            out.write("Long[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Short) {
            out.write((short)object, OBJECT, (short)0);
            out.write("Short", TYPE, NULL_TYPE);
        } else if (object instanceof Short[]) {
            out.write((short[])object, OBJECT, new short[0]);
            out.write("Short[]", TYPE, NULL_TYPE);
        } else if (object instanceof Short[][]) {
            out.write((short[][])object, OBJECT, new short[0][0]);
            out.write("Short[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Class) {
            writeClass(out, (Class)object, OBJECT, Integer.class);
            out.write("Class", TYPE, NULL_TYPE);
        } else if (object instanceof Class[]) {
            writeClassArray(out, (Class[])object, OBJECT);
            out.write("Class[]", TYPE, NULL_TYPE);
        } else if (object instanceof Class[][]) {
            writeClassArray2D(out, (Class[][])object, OBJECT);
            out.write("Class[][]", TYPE, NULL_TYPE);
        } else if (object instanceof BitSet) {
            out.write((BitSet)object, OBJECT, null);
            out.write("BitSet", TYPE, NULL_TYPE);
        } else if (object instanceof FloatBuffer) {
            out.write((FloatBuffer)object, OBJECT, null);
            out.write("FloatBuffer", TYPE, NULL_TYPE);
        } else if (object instanceof IntBuffer) {
            out.write((IntBuffer)object, OBJECT, null);
            out.write("IntBuffer", TYPE, NULL_TYPE);
        } else if (object instanceof ByteBuffer) {
            out.write((ByteBuffer)object, OBJECT, null);
            out.write("ByteBuffer", TYPE, NULL_TYPE);
        } else if (object instanceof ShortBuffer) {
            out.write((ShortBuffer)object, OBJECT, null);
            out.write("ShortBuffer", TYPE, NULL_TYPE);
        } else if (object instanceof ArrayList) {
            out.writeSavableArrayList((ArrayList)object, OBJECT, null);
            out.write("ArrayList", TYPE, NULL_TYPE);
        } else if (object instanceof ArrayList[]) {
            out.writeSavableArrayListArray((ArrayList[])object, OBJECT, new ArrayList[0]);
            out.write("ArrayList[]", TYPE, NULL_TYPE);
        } else if (object instanceof ArrayList[][]) {
            out.writeSavableArrayListArray2D((ArrayList[][])object, OBJECT, new ArrayList[0][0]);
            out.write("ArrayList[][]", TYPE, NULL_TYPE);
        } else if (object instanceof Map) {
            out.writeStringSavableMap((Map<String, ? extends Savable>)object, OBJECT, null);
            out.write("StringSavableMap", TYPE, NULL_TYPE);
        } else if (object instanceof IntMap) {
            out.writeIntSavableMap((IntMap)object, OBJECT, null);
            out.write("IntMap", TYPE, NULL_TYPE);
        } else if (object instanceof Object) {
            writeClass(out, object.getClass(), OBJECT, null);
            out.write(UNKNOWN_TYPE, TYPE, NULL_TYPE);
        }
    }
    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule in = im.getCapsule(this);
        name = in.readString(NAME, null);
        String type = in.readString(TYPE, NULL_TYPE);
        switch (type) {
            case "Savable":
                object = in.readSavable(OBJECT, null);
                break;
            case "Savable[]":
                object = in.readSavableArray(OBJECT, new Savable[0]);
                break;
            case "Savable[][]":
                object = in.readSavableArray2D(OBJECT, new Savable[0][0]);
                break;
            case "Integer":
                object = in.readInt(OBJECT, 0);
                break;
            case "Integer[]":
                object = in.readIntArray(OBJECT, new int[0]);
                break;
            case "Integer[][]":
                object = in.readIntArray2D(OBJECT, new int[0][0]);
                break;
            case "Float":
                object = in.readFloat(OBJECT, 0);
                break;
            case "Float[]":
                object = in.readFloatArray(OBJECT, new float[0]);
                break;
            case "Float[][]":
                object = in.readFloatArray2D(OBJECT, new float[0][0]);
                break;
            case "Double":
                object = in.readDouble(OBJECT, 0);
                break;
            case "Double[]":
                object = in.readDoubleArray(OBJECT, new double[0]);
                break;
            case "Double[][]":
                object = in.readDoubleArray2D(OBJECT, new double[0][0]);
                break;
            case "Boolean":
                object = in.readBoolean(OBJECT, false);
                break;
            case "Boolean[]":
                object = in.readBooleanArray(OBJECT, new boolean[0]);
                break;
            case "Boolean[][]":
                object = in.readBooleanArray2D(OBJECT, new boolean[0][0]);
                break;
            case "Byte":
                object = in.readByte(OBJECT, (byte)0);
                break;
            case "Byte[]":
                object = in.readByteArray(OBJECT, new byte[0]);
                break;
            case "Byte[][]":
                object = in.readByteArray2D(OBJECT, new byte[0][0]);
                break;
            case "String":
                object = in.readString(OBJECT, null);
                break;
            case "String[]":
                object = in.readStringArray(OBJECT, new String[0]);
                break;
            case "String[][]":
                object = in.readStringArray2D(OBJECT, new String[0][0]);
                break;
            case "Long":
                object = in.readLong(OBJECT, 0);
                break;
            case "Long[]":
                object = in.readLongArray(OBJECT, new long[0]);
                break;
            case "Long[][]":
                object = in.readLongArray2D(OBJECT, new long[0][0]);
                break;
            case "Short":
                object = in.readShort(OBJECT, (short)0);
                break;
            case "Short[]":
                object = in.readShortArray(OBJECT, new short[0]);
                break;
            case "Short[][]":
                object = in.readShortArray2D(OBJECT, new short[0][0]);
                break;
            case "Class":
                object = readClass(in, OBJECT, Integer.class);
                break;
            case "Class[]":
                object = readClassArray(in, OBJECT);
                break;
            case "Class[][]":
                object = readClassArray2D(in, OBJECT);
                break;
            case "BitSet":
                object = in.readBitSet(OBJECT, null);
                break;
            case "FloatBuffer":
                object = in.readFloatBuffer(OBJECT, null);
                break;
            case "IntBuffer":
                object = in.readIntBuffer(OBJECT, null);
                break;
            case "ByteBuffer":
                object = in.readByteBuffer(OBJECT, null);
                break;
            case "ShortBuffer":
                object = in.readShortBuffer(OBJECT, null);
                break;
            case "ArrayList":
                object = in.readSavableArrayList(OBJECT, null);
                break;
            case "ArrayList[]":
                object = in.readSavableArrayListArray(OBJECT, new ArrayList[0]);
                break;
            case "ArrayList[][]":
                object = in.readSavableArrayListArray2D(OBJECT, new ArrayList[0][0]);
                break;
            case "StringSavableMap":
                object = in.readStringSavableMap(OBJECT, null);
                break;
            case "IntMap":
                object = in.readIntSavableMap(OBJECT, null);
                break;
            case UNKNOWN_TYPE:
                Class clazz = Objects.requireNonNull(readClass(in, OBJECT, null));
                try {
                    Constructor c = clazz.getDeclaredConstructor();
                    c.setAccessible(true);
                    object = c.newInstance();
                } catch (NoSuchMethodException | SecurityException | InstantiationException
                        | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                    throw new IOException("Error instantiating object of unknown type: " + clazz.getName(), ex);
                }
                break;
            case NULL_TYPE:
                object = null;
                break;
            default:
                LOG.log(Level.WARNING, "Attempted to load unsupported type {0}", type);
                break;
        }
        while (object instanceof ProxySavable) {
            object = ((ProxySavable)object).getObject();
        }
    }
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 61 * hash + Objects.hashCode(this.name);
        hash = 61 * hash + Objects.hashCode(this.object);
        return hash;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SavableObject other = (SavableObject) obj;
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        return Objects.equals(this.object, other.object);
    }
    
    /**
     * Sets the name of this SavableObject.
     * <p>
     * Is written and read.
     * 
     * @param name 
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the name.
     * 
     * @return 
     */
    public String getName() {
        return name;
    }
    
    /**
     * Writes the class to the OutputCapsule as a string.
     * 
     * @param out
     * @param clazz
     * @param name
     * @param defVal
     * @throws IOException 
     */
    public static void writeClass(OutputCapsule out, Class clazz, String name, Class defVal) throws IOException {
        out.write(clazz.getName(), name, defVal.getName());
    }
    
    /**
     * Writes the array of classes to the OutputCapsule as an array of strings.
     * 
     * @param out
     * @param array
     * @param name
     * @throws IOException 
     */
    public static void writeClassArray(OutputCapsule out, Class[] array, String name) throws IOException {
        String[] names = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            names[i] = array[i].getName();
        }
        out.write(names, name, new String[0]);
    }
    
    /**
     * Writes the 2D array of classes to the OutputCapsule as a 2D array of strings.
     * 
     * @param out
     * @param array
     * @param name
     * @throws IOException 
     */
    public static void writeClassArray2D(OutputCapsule out, Class[][] array, String name) throws IOException {
        String[][] names = new String[array.length][];
        for (int i = 0; i < array.length; i++) {
            names[i] = new String[array[i].length];
            for (int j = 0; j < array[i].length; j++) {
                names[i][j] = array[i][j].getName();
            }
        }
        out.write(names, name, new String[0][0]);
    }
    
    /**
     * Reads a Savable of the given type from the InputCapsule.
     * 
     * @param <T>
     * @param in
     * @param name
     * @param type
     * @param defVal
     * @return
     * @throws IOException 
     */
    public static <T extends Savable> T readSavable(InputCapsule in, String name, Class<T> type, T defVal) throws IOException {
        Savable s = in.readSavable(name, defVal);
        if (s != defVal && type.isAssignableFrom(s.getClass())) {
            return (T)s;
        } else {
            return defVal;
        }
    }
    
    /**
     * Reads the SavableObject from the InputCapsule.
     * 
     * @param in
     * @param name
     * @param defVal
     * @return
     * @throws IOException 
     */
    public static SavableObject read(InputCapsule in, String name, SavableObject defVal) throws IOException {
        return readSavable(in, name, SavableObject.class, defVal);
    }
    
    /**
     * Reads the SavableObject from the InputCapsule.
     * 
     * @param in
     * @param name
     * @return
     * @throws IOException 
     */
    public static SavableObject read(InputCapsule in, String name) throws IOException {
        return readSavable(in, name, SavableObject.class, SavableObject.NULL);
    }
    
    /**
     * Reads the SavableObject from the InputCapsule.
     * 
     * @param <T>
     * @param in
     * @param name
     * @param defVal
     * @param type
     * @return internal object of the read SavableObject
     * @throws IOException 
     */
    public static <T> T read(InputCapsule in, String name, SavableObject defVal, Class<T> type) throws IOException {
        return readSavable(in, name, SavableObject.class, defVal).getObject(type);
    }
    
    /**
     * Reads the SavableObject from the InputCapsule.
     * 
     * @param <T>
     * @param in
     * @param name
     * @param type
     * @return internal object of the read SavableObject
     * @throws IOException 
     */
    public static <T> T read(InputCapsule in, String name, Class<T> type) throws IOException {
        return readSavable(in, name, SavableObject.class, SavableObject.NULL).getObject(type);
    }
    
    /**
     * Reads the class from the InputCapsule.
     * 
     * @param in
     * @param name
     * @param defVal
     * @return
     * @throws IOException 
     */
    public static Class readClass(InputCapsule in, String name, Class defVal) throws IOException {
        String className = in.readString(name, null);
        if (className != null) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ex) {
                throw new RuntimeException("Failed to find class: " + className, ex);
            }
        } else {
            return defVal;
        }
    }
    
    /**
     * Reads the array of classes from the InputCapsule.
     * 
     * @param in
     * @param name
     * @return
     * @throws IOException 
     */
    public static Class[] readClassArray(InputCapsule in, String name) throws IOException {
        String[] names = in.readStringArray(name, new String[0]);
        Class[] array = new Class[names.length];
        try {
            for (int i = 0; i < names.length; i++) {
                array[i] = Class.forName(names[i]);
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Failed to locate saved class by name.", ex);
        }
        return array;
    }
    
    /**
     * Reads the 2D array of classes from the InputCapsule.
     * 
     * @param in
     * @param name
     * @return
     * @throws IOException 
     */
    public static Class[][] readClassArray2D(InputCapsule in, String name) throws IOException {
        String[][] names = in.readStringArray2D(name, new String[0][0]);
        Class[][] array = new Class[names.length][];
        try {
            for (int i = 0; i < names.length; i++) {
                array[i] = new Class[names[i].length];
                for (int j = 0; j < names[i].length; i++) {
                    array[i][j] = Class.forName(names[i][j]);
                }
            }
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException("Failed to locate saved class by name.", ex);
        }
        return array;
    }
    
    /**
     * Reads the named array list to the target collection.
     * 
     * @param in
     * @param name
     * @param target
     * @return
     * @throws IOException 
     */
    public static Collection readToCollection(InputCapsule in, String name, Collection target) throws IOException {
        ArrayList<SavableObject> list = in.readSavableArrayList(name, new ArrayList<>());
        for (SavableObject obj : list) {
            target.add(obj.getObject());
        }
        return target;
    }
    
    /**
     * Writes the collection to the OutputCapsule.
     * <p>
     * All elements are encapsulated in a SavableObject for export.
     * 
     * @param out
     * @param collection
     * @param name
     * @throws IOException 
     */
    public static void writeFromCollection(OutputCapsule out, Collection collection, String name) throws IOException {
        ArrayList<SavableObject> list = new ArrayList<>(collection.size());
        for (Object obj : collection) {
            list.add(new SavableObject(obj));
        }
        out.writeSavableArrayList(list, name, new ArrayList());
    }
    
}
