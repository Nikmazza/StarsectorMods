package data.scripts.world.systems;

import java.awt.Color;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import static com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator.addOrbitingEntities;

public class ork_Dakka_Den {
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Rionnagmor");

        system.setBackgroundTextureFilename("graphics/backgrounds/background4.jpg");

        system.getLocation().set(-8000, -29000); // Roughly centered and below the core worlds

        PlanetAPI dakka_star = system.initStar("Dakka Den",
                "star_red_giant",
                1000f,
                1500,
                5f,
                0.5f,
                2f);

        system.setLightColor(new Color(255, 210, 200)); // system light color

        PlanetAPI dakkaDen1 = system.addPlanet("dregruk", dakka_star, "Mèinnearach", "barren", 140, 110, 3300, 180);
        dakkaDen1.setCustomDescriptionId("bt_meinnearach");

        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint("dakka_den_jump_1", "Rionnagmor Inner Jump-point");
        jumpPoint1.setCircularOrbit(dakka_star, 200, 3300, 180); // Angle at 200, L5 of Dreguk
        jumpPoint1.setRelatedPlanet(dakkaDen1);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();
        system.addEntity(jumpPoint1);

        system.addAsteroidBelt(dakka_star, 100, 4500, 300, 160, 220); // Ring system located between inner and outer planets
        system.addRingBand(dakka_star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4460, 200f, null, null);
        system.addRingBand(dakka_star, "misc", "rings_dust0", 256f, 1, Color.white, 256f, 4540, 200f, null, null);

        PlanetAPI dakkaDen2 = system.addPlanet("orguk", dakka_star, "Dhachaigh", "terran", 250, 170, 6000, 280);
        dakkaDen2.setCustomDescriptionId("bt_dhachnaigh");
        PlanetAPI dakkaDen2a = system.addPlanet("gathrog", dakkaDen2, "Miotail", "barren2", 40, 50, 600, 29);
        dakkaDen2a.setCustomDescriptionId("bt_miotail");

        PlanetAPI dakkaDen3 = system.addPlanet("nastruime", dakka_star, "Nastruime", "gas_giant", 250, 350, 8500, 300);

            SectorEntityToken dakkaDen3a = system.addCustomEntity("bt_siphon_station", "Bultach Siphon Station", "station_side02", "orks");
            dakkaDen3a.setCircularOrbitPointingDown(dakkaDen3, 30, 500,55);
            dakkaDen3a.setCustomDescriptionId("bt_siphon");
            dakkaDen3a.setInteractionImage("illustrations", "orbital");


        SectorEntityToken orkRelay = system.addCustomEntity(null, "Bultach Comm Relay", "comm_relay_makeshift", "orks"); // Makeshift comm relay at L4 of Orguk
        orkRelay.setCircularOrbit(dakka_star, 190, 6000, 280);

        SectorEntityToken orkBuoy = system.addCustomEntity(null, "Bultach Nav Buoy", "nav_buoy_makeshift", "orks"); // Makeshift nav buoy at L5 of Orguk
        orkBuoy.setCircularOrbit(dakka_star, 310, 6000, 280);

        addOrbitingEntities(system, dakka_star, StarAge.AVERAGE, 2, 3, 7500, 2, false, true);

        system.autogenerateHyperspaceJumpPoints(true, true);

        cleanup(system);
    }

    void cleanup(StarSystemAPI system) {
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
    }
}