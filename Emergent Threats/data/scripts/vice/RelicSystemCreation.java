package data.scripts.vice;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomCampaignEntityAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.JumpPointAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.StarTypes;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.ids.Terrain;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.util.Misc;

import data.scripts.util.MagicCampaign;

public class RelicSystemCreation {

	public static void generate(SectorAPI sector) {

		//make the system
		StarSystemAPI system = sector.createStarSystem("Circumfix");
		
		system.setBackgroundTextureFilename("graphics/backgrounds/background_galatia.jpg");
		
		//create the star
		PlanetAPI star = system.initStar("asm_circumfix", // unique id for this star
				StarTypes.WHITE_DWARF,  // id in planets.json
				450f, // radius (in pixels at default zoom)
				300); // corona radius, from star edge
		//star.setCustomDescriptionId("asm_circumfix_star");
		
		system.setLightColor(new Color(200, 240, 255)); // light color in entire system, affects all entities

		//get rid of the hyperspace around the star
		HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
		NebulaEditor editor = new NebulaEditor(plugin);

		float minRadius = plugin.getTileSize() * 4f;
		float radius = system.getMaxRadiusInHyperspace() * 1.4f;
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0, 360f);
		editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0, 360f, 0.25f);
		
		//local spacestation
		SectorEntityToken starbase = system.addCustomEntity("asm_circumfix_station", "Abandoned Military Outpost", "asm_relic_station", "asm_relic");
		
		starbase.setCircularOrbitPointingDown(star, 350, 3350, 250);
		starbase.setCustomDescriptionId("asm_relic_station");
		starbase.setInteractionImage("illustrations", "asm_circumfix_station");
		
		//add inner jump point
		JumpPointAPI point = Global.getFactory().createJumpPoint("circumfix_jump_inner", "Circumfix Jump-point");
		point.setCircularOrbit(star, 347, 3250, 250);
		point.setStandardWormholeToHyperspaceVisual();
		system.addEntity((SectorEntityToken) point);
		
		//autogenerate jump points
		system.autogenerateHyperspaceJumpPoints(true, false);
	}
}
