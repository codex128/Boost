/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.boost.gui;

import com.simsilica.lemur.Container;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.ListBox;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.ValueRenderer;
import com.simsilica.lemur.component.IconComponent;
import com.simsilica.lemur.style.ElementId;
import java.io.File;
import java.io.FileFilter;
import java.util.function.Function;

/**
 *
 * @author codex
 */
public class FileBrowser extends Container implements ValueRenderer<File> {
    
    private FileFilter filter;
    private ListBox<File> list = new ListBox<>();
    private Function<File, IconComponent> iconFactory;
    
    public FileBrowser(File root) {
        assert root.isDirectory() : "Root file must be a directory.";
        list.setCellRenderer(this);
    }

    @Override
    public void configureStyle(ElementId elementId, String style) {}
    @Override
    public Panel getView(File value, boolean selected, Panel existing) {
        Label label;
        if (existing == null) {
            label = new Label("");
        } else {
            label = (Label)existing;
        }
        label.setText(value.getName());
        if (iconFactory != null) {
            IconComponent icon = iconFactory.apply(value);
            if (icon != null) {
                label.setIcon(icon);
            }
        }
        return existing;
    }
    
    private static class FilePanel extends Container {
        
    }
    
}
