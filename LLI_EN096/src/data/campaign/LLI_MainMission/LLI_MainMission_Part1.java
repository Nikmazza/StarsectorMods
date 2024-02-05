package data.campaign.LLI_MainMission;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.DebugFlags;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BaseBarEventWithPerson;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import data.SSPI18nUtil;

import java.awt.*;
import java.util.Map;

public class LLI_MainMission_Part1 extends BaseBarEventWithPerson {
    public Color LLI_FactionColor =new Color(255,140,140,255);
    public LLI_MainMission_Part1() {
        super();
    }
    //固定刷新位置，BracksAo如果存在则不刷新
    public boolean shouldShowAtMarket(MarketAPI market) {
        if (!super.shouldShowAtMarket(market)) return false;
        //if (Global.getSector().getPlayerStats().getLevel() < 5 && !DebugFlags.BAR_DEBUG) return false;
        if (market.getFactionId().equals("LLI") && market.getId().equals("MirageCity_market")) { return true; }
        return false;
    }
    @Override
    public boolean isAlwaysShow() {
        return true;
    }

    @Override
    protected void regen(MarketAPI market) {
        if (this.market == market) return;
        super.regen(market);
    }
    @Override
    public void addPromptAndOption(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.addPromptAndOption(dialog, memoryMap);
        regen(dialog.getInteractionTarget().getMarket());
        dialog.getTextPanel().addPara(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_0"),LLI_FactionColor);
        dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_1"),this,LLI_FactionColor,null);
    }

    @Override
    public void init(InteractionDialogAPI dialog, Map<String, MemoryAPI> memoryMap) {
        super.init(dialog, memoryMap);
        done = false;
        person.setPostId(Ranks.CITIZEN);//设置该人物的职位
        person.setRankId(Ranks.CITIZEN);//设置该人物的军衔
        person.setGender(FullName.Gender.ANY);//设置性别
        person.getName().setFirst("Bracks");//众所周知，西方人的名字是由两部分组成
        person.getName().setLast("Ao");
        person.setPortraitSprite("graphics/portraits/portrait47.png");//设置人物的大头照
        dialog.getVisualPanel().showPersonInfo(person, true);
        optionSelected(null,0);
    }

    @Override
    public void optionSelected(String optionText, Object optionData) {
        int option = (int) optionData;
        dialog.getOptionPanel().clearOptions();
        switch (option) {
            case 0:
                dialog.getTextPanel().addPara(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_2"));
                dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_continue"), 1);
                dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_leave"), 3);
                break;
            case 1:
                dialog.getTextPanel().addPara(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_3"));
                dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_continue"), 2);
                dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_leave"), 3);
                break;
            case 2:
                dialog.getTextPanel().addPara(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_4"));
                dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_leave"), 3);
                break;
            case 3:
                ImportantPeopleAPI IP = Global.getSector().getImportantPeople();
                MarketAPI Market_MirageCity = Global.getSector().getEconomy().getMarket("MirageCity_market");
                if(Market_MirageCity != null) {
                    PersonAPI NPC_0 = Market_MirageCity.getFaction().createRandomPerson();//创建一个该阵营的随机人物
                    NPC_0.setId("NPC_0");//人物id，游戏中可以唯一找到它的识别名
                    NPC_0.setPostId(Ranks.CITIZEN);//设置该人物的职位
                    NPC_0.setRankId(Ranks.CITIZEN);//设置该人物的军衔
                    NPC_0.setGender(FullName.Gender.ANY);//设置性别
                    NPC_0.getName().setFirst("Bracks");//众所周知，西方人的名字是由两部分组成
                    NPC_0.getName().setLast("Ao");
                    NPC_0.setPortraitSprite("graphics/portraits/portrait47.png");//设置人物的大头照
                    NPC_0.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);//设置人物的重要性，至于Voice是角色打招呼的语气，例如voice = faithful就会说“卢德保佑你”之类，可在rules中自定义
                    IP.addPerson(NPC_0);//只有加入ImportantPeople，该人物才能被rules和missionHub识别
                    IP.getData(NPC_0).getLocation().setMarket(Market_MirageCity);//将人物传送到指定market里
                    IP.checkOutPerson(NPC_0, "permanent_staff");//"这个的意思是把人物以'永久成员(permanent_staff)'的理由签发出去，如此一来就不会成为某些随机任务的目标。“————感谢议长订正
                    Market_MirageCity.getCommDirectory().addPerson(NPC_0, 0);//将其加入通讯录中
                    Market_MirageCity.addPerson(NPC_0);//将该person加入市场的人物列表，使某些按市场寻人的方法可以找到
                }
                dialog.getTextPanel().addPara(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_5"));
                dialog.getTextPanel().addPara(SSPI18nUtil.getStoryString("LLI_MainMission_Part1_6"), Misc.getHighlightColor());
                dialog.getOptionPanel().addOption(SSPI18nUtil.getStoryString("LLI_leave"), 4);
                break;
            case 4:
                noContinue = true;
                done = true;
                break;
        }
    }

    @Override
    public boolean shouldRemoveEvent() {
        return isDialogFinished();
    }
}
