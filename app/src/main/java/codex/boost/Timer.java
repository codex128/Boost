/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost;

import java.util.LinkedList;

/**
 *
 *
 * @author gary
 */
public class Timer implements Listenable<TimerListener> {

    public enum CycleMode {
        /**
         * Cycle only once before automatically stopping.
         */
        ONCE,
        /**
         * Cycle a set number of times before automatically stopping.
         */
        DEFINED,
        /**
         * Cycle forever without automatically stopping.
         */
        INFINITE;
    }

    float time = 0;
    float duration;
    boolean start = false;
    LinkedList<TimerListener> listeners = new LinkedList<>();
    CycleMode cycle = CycleMode.INFINITE;
    int cycleNum = 0;
    int cycleMax = 5;

    public Timer(float duration) {
        this.duration = duration;
    }

    public void update() {
        update(1f);
    }

    public void update(float tpf) {
        if (start && (time += tpf) >= getDuration() && getDuration() >= 0f) {
            time = getDuration();
            cycleNum++;
            boolean max = cycleNum == cycleMax;
            if (cycle == CycleMode.ONCE || (cycle == CycleMode.DEFINED && max)) {
                start = false;
            }
            notifyListeners(l -> l.onTimerFinish(this));
            if (max) {
                cycleNum = 0;
            }
            if (cycle != CycleMode.ONCE) {
                time = 0;
            }
        }
    }

    public void start() {
        start = true;
        notifyListeners(l -> l.onTimerStart(this));
    }

    public void pause() {
        start = false;
        notifyListeners(l -> l.onTimerPause(this));
    }

    public void reset() {
        time = 0;
        start = false;
        cycleNum = 0;
        notifyListeners(l -> l.onTimerReset(this));
    }

    @Override
    public LinkedList<TimerListener> getListeners() {
        return listeners;
    }

    public void setDuration(float duration) {
        this.duration = duration;
    }

    public void setTime(float time) {
        this.time = Math.max(Math.min(time, duration), 0);
    }

    public void setCycleMode(CycleMode cycle) {
        this.cycle = cycle;
    }

    public void setCycleMode(CycleMode cycle, int max) {
        setCycleMode(cycle);
        setCycleMax(max);
    }

    public void setCycleMax(int max) {
        cycleMax = max;
    }

    @Override
    public String toString() {
        return "Timer[" + time + " / " + duration + "]";
    }

    public int timeToSeconds() {
        return (int)time;
    }

    public float getTime() {
        return time;
    }

    public float getDuration() {
        return duration;
    }

    public CycleMode getCycleMode() {
        return cycle;
    }

    public int getCycleNum() {
        return cycleNum;
    }

    public int getCycleMax() {
        return cycleMax;
    }

    public float getTimeRemaining() {
        return duration - time;
    }

    public float getPercentageComplete() {
        return time / duration;
    }

    public boolean isComplete() {
        return time >= duration;
    }

    public boolean isRunning() {
        return start;
    }

    public static int secondsToLimitedSeconds(int seconds) {
        return seconds % 60;
    }

    public static int secondsToMinutes(int seconds, boolean max) {
        if (max) {
            return (seconds % 3600) / 60;
        }
        else {
            return seconds / 60;
        }
    }

    public static int secondsToHours(int seconds, boolean max) {
        if (max) {
            return (seconds % (3600 * 24)) / 3600;
        }
        else {
            return seconds / 3600;
        }
    }

    public static int secondsToDays(int seconds) {
        return seconds / (3600 * 24);
    }

}
