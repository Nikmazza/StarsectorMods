package data.scripts.utils;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.combat.BattleObjectiveAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.MissileAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipHullSpecAPI;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

public class NES_Util {

    //for sparkle effect
    public static int clamp255(int x) {
        return Math.max(0, Math.min(255, x));
    }

    public static Color colorJitter(Color color, float amount) {
        return new Color(clamp255((int) (color.getRed() + (int) (((float) Math.random() - 0.5f) * amount))),
                clamp255((int) (color.getGreen() + (int) (((float) Math.random() - 0.5f) * amount))),
                clamp255((int) (color.getBlue() + (int) (((float) Math.random() - 0.5f) * amount))),
                color.getAlpha());
    }

    //for translation purposes
    private static final String nes = "nes";
    public static String txt(String id) {
        return Global.getSettings().getString(nes, id);
    }
}
