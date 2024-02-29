/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost;

/**
 *
 * @author codex
 */
public class Duration {
    
    private double duration, time;

    public Duration(float duration) {
        assert duration >= 0 : "Duration must be greater than or equal to zero";
        this.duration = duration;
        this.time = 0;
    }
    
    public boolean update(double tpf) {
        if (!isLocked()) {
            time += tpf;
        }
        return isComplete();
    }
    
    public void reset() {
        time = 0;
    }
    public double lock(boolean lock) {
        return (time = lock ? -1 : 0);
    }
    
    public void setDuration(float duration) {
        this.duration = duration;
    }
    public void setTime(float time) {
        this.time = time;
    }
    public double setPercent(double p, boolean clamp) {
        if (clamp) {
            p = clamp(p, 0, 1);
        }
        return (time = duration*p);
    }
    
    public double getTime() {
        return time;
    }
    public double getDuration() {
        return duration;
    }
    public double getTimeRemaining() {
        return Math.max(duration-time, 0);
    }
    public double getTimeOver() {
        return Math.max(time-duration, 0);
    }
    public double getPercent() {
        return clamp(time/duration, 0, 1);
    }
    
    public boolean isComplete() {
        return time >= duration;
    }
    public boolean isLocked() {
        return time < 0;
    }
    public boolean isProgressing() {
        return time >= 0 && time < duration;
    }
    
    private static double clamp(double value, double min, double max) {
        if (value > max) return max;
        else if (value < min) return min;
        else return value;
    }
    
}
