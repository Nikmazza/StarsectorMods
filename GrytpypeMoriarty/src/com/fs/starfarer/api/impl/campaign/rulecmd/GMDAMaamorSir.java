package com.fs.starfarer.api.impl.campaign.rulecmd;

import java.util.List;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemKeys;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName.Gender;
import com.fs.starfarer.api.util.Misc.Token;


public class GMDAMaamorSir extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;
        String yadda = (Global.getSector().getPlayerPerson().getGender() == Gender.MALE) ? "sir" : ((Global.getSector().getPlayerPerson().getGender() == Gender.FEMALE) ? "ma'am" : "sir");
        String yaddaCap = (Global.getSector().getPlayerPerson().getGender() == Gender.MALE) ? "Sir" : ((Global.getSector().getPlayerPerson().getGender() == Gender.FEMALE) ? "Ma'am" : "sir");

        String classy = (Global.getSector().getPlayerPerson().getGender() == Gender.MALE) ? "guy" : ((Global.getSector().getPlayerPerson().getGender() == Gender.FEMALE) ? "gal" : "guy");

        memoryMap.get(MemKeys.LOCAL).set("$sirOrMaam", yadda, 0);
		memoryMap.get(MemKeys.LOCAL).set("$sirOrMaamCapital", yaddaCap, 0);
        memoryMap.get(MemKeys.LOCAL).set("$guyOrGal", classy, 0);
        return true;
    }

}
