package II_BG.data.script;

import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.codex.CodexDataV2;
import com.fs.starfarer.api.impl.codex.CodexEntryPlugin;

public class II_BG_CodexData {
    public static void linkCodexEntries() {
        //Hull Modifications
        createReciprocalLink(CodexDataV2.getShipEntryId("conquest_ii"), CodexDataV2.getShipEntryId("conquest"));
        createReciprocalLink(CodexDataV2.getShipEntryId("eagle_ii"), CodexDataV2.getShipEntryId("eagle"));
        createReciprocalLink(CodexDataV2.getShipEntryId("hammerhead_ii"), CodexDataV2.getShipEntryId("hammerhead"));
        createReciprocalLink(CodexDataV2.getShipEntryId("brawler_ii"), CodexDataV2.getShipEntryId("brawler"));
        createReciprocalLink(CodexDataV2.getShipEntryId("invictus_ii"), CodexDataV2.getShipEntryId("invictus"));
        createReciprocalLink(CodexDataV2.getShipEntryId("heron_ii"), CodexDataV2.getShipEntryId("heron"));
        createReciprocalLink(CodexDataV2.getShipEntryId("atlas_iia"), CodexDataV2.getShipEntryId("atlas"));
        createReciprocalLink(CodexDataV2.getShipEntryId("condor_iia"), CodexDataV2.getShipEntryId("condor"));
        createReciprocalLink(CodexDataV2.getShipEntryId("champion_ii"), CodexDataV2.getShipEntryId("champion"));
    }

    private static void createReciprocalLink(String entryIdOne, String entryIdTwo) {
        CodexEntryPlugin entryOne = CodexDataV2.getEntry(entryIdOne);
        CodexEntryPlugin entryTwo = CodexDataV2.getEntry(entryIdTwo);

        entryOne.addRelatedEntry(entryTwo);
        entryTwo.addRelatedEntry(entryOne);
    }
}