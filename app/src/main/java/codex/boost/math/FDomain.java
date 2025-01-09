/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.math;

import com.jme3.math.FastMath;

/**
 * For limiting values within a domain defined by two floats.
 *
 * @author gary
 */
public class FDomain {

    public static final FDomain RADIANS = new FDomain(0f, FastMath.TWO_PI);

    public Float min, max;

    public FDomain() {
    }

    public FDomain(Float min, Float max) {
        set(min, max);
    }

    public FDomain(FDomain range) {
        set(range);
    }

    /**
     * Set the minimum value. May be null, in which case there is no minimum.
     *
     * @param min
     * @return
     */
    public FDomain setMin(Float min) {
        this.min = min;
        confirmValues();
        return this;
    }

    /**
     * Set the maximum value. May be null, in which case there is no minimum.
     *
     * @param max
     * @return
     */
    public FDomain setMax(Float max) {
        this.max = max;
        confirmValues();
        return this;
    }

    public FDomain set(Float min, Float max) {
        this.min = min;
        return setMax(max);
    }

    public FDomain set(FDomain range) {
        return set(range.min, range.max);
    }

    /**
     * Constrains the value within the minimum and maximum values.
     *
     * @param value
     * @return the constrained value
     */
    public float applyConstrain(float value) {
        if (min != null && value < min) {
            return min;
        }
        else if (max != null && value > max) {
            return max;
        }
        else {
            return value;
        }
    }

    /**
     * Repels the value to outside the minimum and maximum values (whichever is closest). Only repels if both
     * min and max are not null.
     *
     * @param value
     * @return the repelled value
     */
    public float applyRepel(float value) {
        if (min != null && max != null && value > min && value < max) {
            if (value < min + getRangeDistance() / 2) {
                return min;
            }
            else {
                return max;
            }
        }
        else {
            return value;
        }
    }

    /**
     * Wraps the value within the minimum and maximum values. If min or max is null, will use
     * <code>-Float.MAX_VALUE</code> or <code>Float.MAX_VALUE</code> instead, respectively.
     * <br>
     * <strong>Known Issue!</strong><br>
     * Will not work correctly if the value is less than the minimum minus the range or more than the maximum
     * plus the range.
     *
     * @param value
     * @return the wrapped value
     */
    public float applyWrap(float value) {
        if (min != null && value < min) {
            if (max == null) {
                return Float.MAX_VALUE;
            }
            else {
                return max - (min - value);
            }
        }
        else if (max != null && value > max) {
            if (min == null) {
                return -Float.MAX_VALUE;
            }
            else {
                return min + (value - max);
            }
        }
        else {
            return value;
        }
    }

    /**
     * Checks if the asserted value is inside this domain.
     *
     * @param value
     * @return
     */
    public boolean isInside(float value) {
        return (min == null || value >= min)
                && (max == null || value <= max);
    }

    /**
     * Returns the distance between the minimum and maximum values. Returns -1f if min or max is null.
     *
     * @return
     */
    public float getRangeDistance() {
        if (max == null || min == null) {
            return -1f;
        }
        else {
            return max - min;
        }
    }

    public Float getMin() {
        return min;
    }

    public Float getMax() {
        return max;
    }

    private void confirmValues() {
        if (max != null && min != null && max < min) {
            throw new UnsupportedOperationException("Max value cannot be less than min value!");
        }
    }

}
