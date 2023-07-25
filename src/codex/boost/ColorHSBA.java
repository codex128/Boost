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
	
	public float h, s, b, a;
	
	public ColorHSBA(float h, float s, float b, float a) {
		this.h = h;
		this.s = s;
		this.b = b;
		this.a = a;
	}
	
	public ColorRGBA toRGBA() {
		Color c = Color.getHSBColor(h, s, b);
		return ColorRGBA.fromRGBA255(c.getRed(), c.getGreen(), c.getBlue(), (int)(a*255));
	}
	
}
