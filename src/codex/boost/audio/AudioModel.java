/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.audio;

import codex.j3map.J3map;
import com.jme3.asset.AssetManager;
import com.jme3.audio.AudioData;
import com.jme3.audio.AudioKey;
import com.jme3.audio.AudioNode;

/**
 *
 * @author gary
 */
public class AudioModel {

    public static final String BUFFER = "buffer",
            STREAM = "stream";

    public static final class Defaults {

        public static float volume = 1f,
                pitch = 1f,
                timeOffset = 0f,
                refDistance = 10f,
                maxDistance = 20f;
        public static boolean looping = false,
                positional = true,
                directional = false,
                reverb = false;
        public static String dataType = BUFFER;

        public static void set(J3map def) {
            volume = def.getFloat("volume", volume);
            pitch = def.getFloat("pitch", pitch);
            timeOffset = def.getFloat("time_offset", timeOffset);
            refDistance = def.getFloat("ref_distance", refDistance);
            maxDistance = def.getFloat("max_distance", maxDistance);
            looping = def.getBoolean("looping", def.getBoolean("loop", looping));
            positional = def.getBoolean("positional", positional);
            directional = def.getBoolean("directional", directional);
            reverb = def.getBoolean("reverb", reverb);
            dataType = def.getString("data_type", dataType);
        }
    }

    private String sourceFile;
    private float volume;
    private float pitch;
    private boolean looping;
    private boolean positional;
    private boolean directional;
    private boolean reverb;
    private float timeOffset;
    private float refDistance;
    private float maxDistance;
    private AudioData.DataType dataType;

    public AudioModel(AudioNode reference) {
        volume = reference.getVolume();
        pitch = reference.getPitch();
        looping = reference.isLooping();
        positional = reference.isPositional();
        directional = reference.isDirectional();
        reverb = reference.isReverbEnabled();
        timeOffset = reference.getTimeOffset();
        refDistance = reference.getRefDistance();
        maxDistance = reference.getMaxDistance();
        dataType = reference.getType();
    }

    public AudioModel(J3map source) {
        sourceFile = source.getString("source");
        if (sourceFile == null) {
            throw new NullPointerException("Could not locate audio source property!");
        }
        volume = source.getFloat("volume", Defaults.volume);
        pitch = source.getFloat("pitch", Defaults.pitch);
        looping = source.getBoolean("looping", source.getBoolean("loop", Defaults.looping));
        positional = source.getBoolean("positional", Defaults.positional);
        directional = source.getBoolean("directional", Defaults.positional);
        reverb = source.getBoolean("reverb", Defaults.reverb);
        timeOffset = source.getFloat("time_offset", Defaults.timeOffset);
        refDistance = source.getFloat("ref_distance", Defaults.refDistance);
        maxDistance = source.getFloat("max_distance", Defaults.maxDistance);
        String type = source.getString("data_type", Defaults.dataType);
        if (type.equals(BUFFER)) {
            dataType = AudioData.DataType.Buffer;
        }
        else if (type.equals(STREAM)) {
            dataType = AudioData.DataType.Stream;
        }
        else {
            throw new IllegalArgumentException("Audio data type \"" + type + "\" is not supported!");
        }
    }

    public AudioModel(AudioModel model) {
        sourceFile = model.sourceFile;
        volume = model.volume;
        pitch = model.pitch;
        looping = model.looping;
        positional = model.positional;
        directional = model.directional;
        reverb = model.reverb;
        timeOffset = model.timeOffset;
        refDistance = model.refDistance;
        maxDistance = model.maxDistance;
        dataType = model.dataType;
    }

    public void setSourceFile(String sourceFile) {
        this.sourceFile = sourceFile;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public void setLooping(boolean looping) {
        this.looping = looping;
    }

    public void setPositional(boolean positional) {
        this.positional = positional;
    }

    public void setDirectional(boolean directional) {
        this.directional = directional;
    }

    public void setReverb(boolean reverb) {
        this.reverb = reverb;
    }

    public void setTimeOffset(float timeOffset) {
        this.timeOffset = timeOffset;
    }

    public void setRefDistance(float refDistance) {
        this.refDistance = refDistance;
    }

    public void setMaxDistance(float maxDistance) {
        this.maxDistance = maxDistance;
    }

    public void setDataType(AudioData.DataType dataType) {
        this.dataType = dataType;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public float getVolume() {
        return volume;
    }

    public float getPitch() {
        return pitch;
    }

    public boolean isLooping() {
        return looping;
    }

    public boolean isPositional() {
        return positional;
    }

    public boolean isDirectional() {
        return directional;
    }

    public boolean isReverb() {
        return reverb;
    }

    public float getTimeOffset() {
        return timeOffset;
    }

    public float getRefDistance() {
        return refDistance;
    }

    public float getMaxDistance() {
        return maxDistance;
    }

    public AudioData.DataType getDataType() {
        return dataType;
    }

    @Override
    public String toString() {
        return "AudioModel[...]";
    }

    public boolean isStreaming() {
        return dataType == AudioData.DataType.Stream;
    }

    public SFXSpeaker toSFXSpeaker(AssetManager assets) {
        return new SFXSpeaker(assets, this);
    }

    public SFXSpeaker toSFXSpeaker(AssetManager assets, String source) {
        return new SFXSpeaker(assets, source, this);
    }

    public AudioNode toAudioNode(AssetManager assets) {
        return toAudioNode(assets, sourceFile);
    }

    public AudioNode toAudioNode(AssetManager assets, String source) {
        AudioNode audio = new AudioNode(assets, source, dataType);
        audio.setVolume(volume);
        audio.setPitch(pitch);
        audio.setLooping(looping);
        audio.setPositional(positional);
        audio.setDirectional(directional);
        audio.setReverbEnabled(reverb);
        audio.setTimeOffset(timeOffset);
        audio.setRefDistance(refDistance);
        audio.setMaxDistance(maxDistance);
        return audio;
    }

    public AudioData toAudioData(AssetManager assets) {
        AudioKey key = new AudioKey(sourceFile, isStreaming(), true);
        return assets.loadAudio(key);
    }

    public J3map exportProperties() {
        J3map map = new J3map();
        exportPropertiesTo(map);
        return map;
    }

    public void exportPropertiesTo(J3map map) {
        map.store("source", sourceFile);
        map.store("volume", volume);
        map.store("pitch", pitch);
        map.store("looping", looping);
        map.store("positional", positional);
        map.store("directional", directional);
        map.store("reverb", reverb);
        map.store("time_offset", timeOffset);
        map.store("ref_distance", refDistance);
        map.store("max_distance", maxDistance);
        map.store("data_type", dataType);
    }

}
