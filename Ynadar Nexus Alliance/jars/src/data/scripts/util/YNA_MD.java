package data.scripts.util;

import com.fs.starfarer.api.Global;

public class YNA_MD {
    public static String base(String key) { return Global.getSettings().getString("yna_hmod", key); }
    public static String hull(String key) { return Global.getSettings().getString("yna_mod_hull", key); }
    public static String mis_load(String key) { return Global.getSettings().getString("yna_mod_mis_load", key); }
    public static String slam_load(String key) { return Global.getSettings().getString("yna_mod_slam_load", key); }
    public static String urgrand(String key) { return Global.getSettings().getString("yna_mod_urgrand", key); }
    public static String peril(String key) { return Global.getSettings().getString("yna_mod_peril", key); }
}
