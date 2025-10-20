package Jaydee8652.JaydeePiracy.utils.codex;

import Jaydee8652.JaydeePiracy.utils.jdp_Conditions;
import Jaydee8652.JaydeePiracy.utils.jdp_Planets;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.ids.Items;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.impl.codex.CodexEntryPlugin;

import static com.fs.starfarer.api.impl.codex.CodexDataV2.*;

public class jdp_CodexData {
    public static void linkCodexEntries() {
        //Marines
        createReciprocalLink(getCommodityEntryId(Commodities.MARINES), getHullmodEntryId("jdp_boardingnets"));
        createReciprocalLink(getCommodityEntryId(Commodities.MARINES), getHullmodEntryId("jdp_supportfighter"));
        createReciprocalLink(getFighterEntryId("jdp_manjusaka_wing"), getHullmodEntryId("jdp_supportfighter"));

        //Skills
        createReciprocalLink(getSkillEntryId("jdp_omega_ecm"), getCommodityEntryId("jdp_omega_core"));
        createReciprocalLink(getSkillEntryId("jdp_omegakin"), getShipEntryId("jdp_penrose"));

        //Hull Modifications
        createReciprocalLink(getShipEntryId("jdp_phaeton2"), getShipEntryId("phaeton"));
        createReciprocalLink(getShipEntryId("jdp_epistle"), getShipEntryId("kite"));
        createReciprocalLink(getShipEntryId("jdp_grendel_lp"), getShipEntryId("grendel"));
        createReciprocalLink(getShipEntryId("jdp_buffalo_pmmm"), getShipEntryId("buffalo"));
        createReciprocalLink(getShipEntryId("jdp_cassius"), getShipEntryId("legion"));
        createReciprocalLink(getShipEntryId("jdp_falcon_vi"), getShipEntryId("falcon"));
        createReciprocalLink(getShipEntryId("jdp_lupara"), getShipEntryId("falcon"));
        createReciprocalLink(getShipEntryId("jdp_drover_lg"), getShipEntryId("drover"));
        createReciprocalLink(getShipEntryId("jdp_florencia"), getShipEntryId("jdp_florencia_hegemony"));
        createReciprocalLink(getShipEntryId("jdp_supremator"), getShipEntryId("conquest"));
        createReciprocalLink(getShipEntryId("jdp_pegasus_vi"), getShipEntryId("pegasus"));

        //Built Ins
        createReciprocalLink(getWeaponEntryId("jdp_tyrantseye"), getShipEntryId("jdp_chuuni"));
        createReciprocalLink(getWeaponEntryId("jdp_nariman"), getShipEntryId("jdp_thunderer"));
        createReciprocalLink(getWeaponEntryId("jdp_cargo"), getShipEntryId("jdp_atlas"));
        createReciprocalLink(getWeaponEntryId("jdp_beleagure_gun"), getShipEntryId("jdp_beleagure"));

        //PK SILO
        createReciprocalLink(getIndustryEntryId("jdp_pksilo"),  getItemEntryId("planetkiller"));
        createReciprocalLink(getIndustryEntryId("jdp_pksilo"), getConditionEntryId("jdp_pkinspire"));

        //Lobsters
        createReciprocalLink(getIndustryEntryId("aquaculture"), getItemEntryId("jdp_lobster_eggs"));
        createReciprocalLink(getItemEntryId("jdp_lobster_eggs"), getConditionEntryId("volturnian_lobster_pens"));

        //Systems
        createReciprocalLink(getShipEntryId("jdp_grendel_lp"), getShipSystemEntryId("orion_device"));

        //Conditions
        createReciprocalLink(getPlanetEntryId(jdp_Planets.JDP_GLASSED), getConditionEntryId(jdp_Conditions.JDP_GLASSEDWORLD));
        createReciprocalLink(getPlanetEntryId(jdp_Planets.JDP_INFESTED), getConditionEntryId(jdp_Conditions.JDP_ENCROACHINGBIOFACTORY));
        createReciprocalLink(getPlanetEntryId(jdp_Planets.JDP_EUTROPHICATED), getConditionEntryId(jdp_Conditions.JDP_EUTROPHICATEDBIOSPHERE));
        createReciprocalLink(getPlanetEntryId(jdp_Planets.JDP_BEGUILING), getConditionEntryId(jdp_Conditions.JDP_METASTATICSENSORIUM));
        createReciprocalLink(getPlanetEntryId(jdp_Planets.JDP_DEPLETED), getConditionEntryId(jdp_Conditions.JDP_QUARRYLAKES));
        createReciprocalLink(getPlanetEntryId(jdp_Planets.JDP_VOLATILE_GIANT), getConditionEntryId(jdp_Conditions.JDP_DOMAINMESHUGATRON));

        createReciprocalLink(getItemEntryId(Items.ORBITAL_FUSION_LAMP), getPlanetEntryId(jdp_Planets.JDP_GLASSED));
        createReciprocalLink(getItemEntryId(Items.BIOFACTORY_EMBRYO), getPlanetEntryId(jdp_Planets.JDP_INFESTED));
        createReciprocalLink(getItemEntryId(Items.SOIL_NANITES), getPlanetEntryId(jdp_Planets.JDP_EUTROPHICATED));
        createReciprocalLink(getItemEntryId(Items.DEALMAKER_HOLOSUITE), getPlanetEntryId(jdp_Planets.JDP_BEGUILING));
        createReciprocalLink(getItemEntryId(Items.MANTLE_BORE), getPlanetEntryId(jdp_Planets.JDP_DEPLETED));

        createReciprocalLink(getItemEntryId(Items.ORBITAL_FUSION_LAMP), getConditionEntryId(jdp_Conditions.JDP_GLASSEDWORLD));
        createReciprocalLink(getItemEntryId(Items.BIOFACTORY_EMBRYO), getConditionEntryId(jdp_Conditions.JDP_ENCROACHINGBIOFACTORY));
        createReciprocalLink(getItemEntryId(Items.SOIL_NANITES), getConditionEntryId(jdp_Conditions.JDP_EUTROPHICATEDBIOSPHERE));
        createReciprocalLink(getItemEntryId(Items.DEALMAKER_HOLOSUITE), getConditionEntryId(jdp_Conditions.JDP_METASTATICSENSORIUM));
        createReciprocalLink(getItemEntryId(Items.MANTLE_BORE), getConditionEntryId(jdp_Conditions.JDP_QUARRYLAKES));
        createReciprocalLink(getItemEntryId(Items.FULLERENE_SPOOL), getConditionEntryId(jdp_Conditions.JDP_LOGISTICSCOMPLEX));
        createReciprocalLink(getItemEntryId(Items.DRONE_REPLICATOR), getConditionEntryId(jdp_Conditions.JDP_REBELARSENAL));
        createReciprocalLink(getItemEntryId(Items.PLASMA_DYNAMO), getConditionEntryId(jdp_Conditions.JDP_DOMAINMESHUGATRON));

        createReciprocalLink(getItemEntryId(Items.CRYOARITHMETIC_ENGINE), getShipEntryId("jdp_coordination_centre"));


        createReciprocalLink(getIndustryEntryId(Industries.AQUACULTURE), getConditionEntryId(jdp_Conditions.JDP_SUBSHEETECOSYSTEM));
        makeUnrelated(getIndustryEntryId(Industries.FARMING), getConditionEntryId(jdp_Conditions.JDP_SUBSHEETECOSYSTEM));
    }

    private static void createReciprocalLink(String entryIdOne, String entryIdTwo) {
        CodexEntryPlugin entryOne =  getEntry(entryIdOne);
        CodexEntryPlugin entryTwo =  getEntry(entryIdTwo);

        entryOne.addRelatedEntry(entryTwo);
        entryTwo.addRelatedEntry(entryOne);
    }
}