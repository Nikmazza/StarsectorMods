package Jaydee8652.JaydeePiracy.utils.codex;

import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.impl.codex.CodexEntryPlugin;

public class jdp_CodexData {
    public static void linkCodexEntries() {
        //Skills
        createReciprocalLink(CodexDataV2.getSkillEntryId("jdp_omega_ecm"), CodexDataV2.getCommodityEntryId("jdp_omega_core"));
        createReciprocalLink(CodexDataV2.getSkillEntryId("jdp_omegakin"), CodexDataV2.getShipEntryId("jdp_penrose"));

        //Hull Modifications
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_epistle"), CodexDataV2.getShipEntryId("kite"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_grendel_lp"), CodexDataV2.getShipEntryId("grendel"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_buffalo_pmmm"), CodexDataV2.getShipEntryId("buffalo"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_cassius"), CodexDataV2.getShipEntryId("legion"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_falcon_vi"), CodexDataV2.getShipEntryId("falcon"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_lupara"), CodexDataV2.getShipEntryId("falcon"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_drover_lg"), CodexDataV2.getShipEntryId("drover"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_florencia"), CodexDataV2.getShipEntryId("jdp_florencia_hegemony"));
        createReciprocalLink(CodexDataV2.getShipEntryId("jdp_supremator"), CodexDataV2.getShipEntryId("conquest"));

        //Built Ins
        createReciprocalLink(CodexDataV2.getWeaponEntryId("jdp_tyrantseye"), CodexDataV2.getShipEntryId("jdp_chuuni"));
        createReciprocalLink(CodexDataV2.getWeaponEntryId("jdp_nariman"), CodexDataV2.getShipEntryId("jdp_thunderer"));
        createReciprocalLink(CodexDataV2.getWeaponEntryId("jdp_cargo"), CodexDataV2.getShipEntryId("jdp_atlas"));

        //PK SILO
        createReciprocalLink(CodexDataV2.getIndustryEntryId("jdp_pksilo"), CodexDataV2.getItemEntryId("planetkiller"));
        createReciprocalLink(CodexDataV2.getIndustryEntryId("jdp_pksilo"), CodexDataV2.getConditionEntryId("jdp_pkinspire"));

        //Lobsters
        createReciprocalLink(CodexDataV2.getIndustryEntryId("aquaculture"), CodexDataV2.getItemEntryId("jdp_lobster_eggs"));
        createReciprocalLink(CodexDataV2.getItemEntryId("jdp_lobster_eggs"), CodexDataV2.getConditionEntryId("volturnian_lobster_pens"));
    }

    private static void createReciprocalLink(String entryIdOne, String entryIdTwo) {
        CodexEntryPlugin entryOne = CodexDataV2.getEntry(entryIdOne);
        CodexEntryPlugin entryTwo = CodexDataV2.getEntry(entryIdTwo);

        entryOne.addRelatedEntry(entryTwo);
        entryTwo.addRelatedEntry(entryOne);
    }
}