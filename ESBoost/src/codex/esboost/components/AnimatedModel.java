/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package codex.esboost.components;

import com.simsilica.es.EntityComponent;

/**
 *
 * @author codex
 */
public class AnimatedModel implements EntityComponent {
    
    private final boolean searchChildren;

    public AnimatedModel(boolean searchChildren) {
        this.searchChildren = searchChildren;
    }

    public boolean isSearchChildren() {
        return searchChildren;
    }

    @Override
    public String toString() {
        return "AnimatedModel{" + "searchChildren=" + searchChildren + '}';
    }
    
}
