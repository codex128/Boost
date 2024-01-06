/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost;

import com.jme3.math.ColorRGBA;
import java.awt.Color;

/**
 *
 * @author gary
 */
public class ColorHSBA {
    
    public static final ColorHSBA
            Red = new ColorHSBA(0f, 1f, 1f, 1f),
            Green = new ColorHSBA(.33f, 1f, 1f, 1f),
            Blue = new ColorHSBA(.66f, 1f, 1f, 1f);
    
    public float h, s, b, a;

    public ColorHSBA() {
        h = s = b = a = 1;
    }
    public ColorHSBA(float h, float s, float b, float a) {
        set(h, s, b, a);
    }
    public ColorHSBA(ColorHSBA hsba) {
        set(hsba);
    }
    public ColorHSBA(ColorRGBA rgba) {
        set(rgba);
    }
    
    public final ColorHSBA set(float h, float s, float b, float a) {
        this.h = h;
        this.s = s;
        this.b = b;
        this.a = a;
        return this;
    }
    public final ColorHSBA set(ColorHSBA hsba) {
        h = hsba.h;
        s = hsba.s;
        b = hsba.b;
        a = hsba.a;
        return this;
    }
    public final ColorHSBA set(ColorRGBA rgba) {
        byte[] bytes = rgba.asBytesRGBA();
        float[] hsb = Color.RGBtoHSB(bytes[0], bytes[1], bytes[2], null);
        h = hsb[0];
        s = hsb[1];
        b = hsb[2];
        a = rgba.a;
        return this;
    }

    public void wrapHue() {
        if (h > 1) {
            h = 0;
        }
        else if (h < 0) {
            h = 1;
        }
    }
    
    public void setHue(float h) {
        this.h = h;
    }
    public void setSaturation(float s) {
        this.s = s;
    }
    public void setBrightness(float b) {
        this.b = b;
    }
    public void setAlpha(float a) {
        this.a = a;
    }
    
    public float getHue() {
        return h;
    }
    public float getSaturation() {
        return s;
    }
    public float getBrightness() {
        return b;
    }
    public float getAlpha() {
        return a;
    }
    
    public ColorRGBA toRGBA() {
        Color c = Color.getHSBColor(h, s, b);
        return ColorRGBA.fromRGBA255(c.getRed(), c.getGreen(), c.getBlue(), (int)(a * 255));
    }

}
