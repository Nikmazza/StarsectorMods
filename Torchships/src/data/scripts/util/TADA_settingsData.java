/*
By Tartiflette
 */
package data.scripts.util;

import java.util.ArrayList;
import java.util.List;
import org.magiclib.util.MagicSettings;

public class TADA_settingsData {        
    
    //////////////////////////////
    //                          //
    //      SETTINGS DATA       //
    //                          //
    //////////////////////////////            
    
    public static List<String> LighPlating_noncompatible = new ArrayList<>();
    public static List<String> ReactiveArmor_noncompatible = new ArrayList<>();
    public static List<String> MinPrep_noncompatible = new ArrayList<>();
    public static List<String> Missile_full = new ArrayList<>();
    
    public static void loadHullmodData(){
        LighPlating_noncompatible = MagicSettings.getList("TADA", "LighPlating_noncompatible");
        ReactiveArmor_noncompatible = MagicSettings.getList("TADA", "ReactiveArmor_noncompatible");
        MinPrep_noncompatible = MagicSettings.getList("TADA", "MinPrep_noncompatible");
        Missile_full = MagicSettings.getList("TADA", "Missile_full");
    }
}