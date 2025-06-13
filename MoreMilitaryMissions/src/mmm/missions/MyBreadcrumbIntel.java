package mmm.missions;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.Set;

// Modelfed after BreadcrumbIntel
public class MyBreadcrumbIntel extends FleetLogIntel {
    protected SectorEntityToken target;
    protected String title;
    protected String text;
    public MyBreadcrumbIntel(SectorEntityToken target, String title, String text) {
        this.target = target;
        this.title = title;
        this.text = text;
        setRemoveTrigger(target);
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        info.addPara(text, opad);

        float days = getDaysSincePlayerVisible();
        if (days >= 1) {
            addDays(info, "ago.", days, tc, opad);
        }

        addDeleteButton(info, width);
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_EXPLORATION);
        return tags;
    }

    @Override
    public String getSortString() { return "Location"; }

    @Override
    protected String getName() { return title; }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) { return target; }

    @Override
    public boolean shouldRemoveIntel() {
        if (target instanceof PlanetAPI) {
            PlanetAPI planet = (PlanetAPI) target;
            if (planet.getMarket() == null) return false;  // Sanity check
            return !Misc.hasUnexploredRuins(planet.getMarket());
        }
        return super.shouldRemoveIntel();
    }

    @Override
    public String getCommMessageSound() {
        if (sound != null) return sound;
        return getSoundMinorMessage();
    }

//    @Override
//    public boolean canMakeVisibleToPlayer(boolean playerInRelayRange) {
//        return true;
//    }
}
