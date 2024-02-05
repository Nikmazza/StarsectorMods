package data;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignPlugin;
import com.fs.starfarer.api.campaign.PersonImportance;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.ImportantPeopleAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.intel.bar.events.BarEventManager;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.thoughtworks.xstream.XStream;
import data.campaign.LLI_MainMission.LLI_MainMission_Creator;
import data.campaign.ssp_ItemEffectsRepo;
import data.world.SSPWorldGen;

public class SSPModPlugin extends BaseModPlugin {
    @Override
    public void onNewGame() {
            new SSPWorldGen().generate(Global.getSector());
    }
    @Override
    public void onApplicationLoad() { ssp_ItemEffectsRepo.add_item_effects(); }

    @Override
    public void onGameLoad(boolean newGame) {
        if(!BarEventManager.getInstance().hasEventCreator(LLI_MainMission_Creator.class)){
            BarEventManager.getInstance().addEventCreator(new LLI_MainMission_Creator());
        }

    }


//    @Override
//    public void onNewGameAfterEconomyLoad() {
//        ImportantPeopleAPI IP = Global.getSector().getImportantPeople();
//        MarketAPI Market_MirageCity = Global.getSector().getEconomy().getMarket("MirageCity_market");
//        if(Market_MirageCity != null){
//            PersonAPI NPC_0 = Market_MirageCity.getFaction().createRandomPerson();//创建一个该阵营的随机人物
//            NPC_0.setId("BracksAo");//人物id，游戏中可以唯一找到它的识别名
//            NPC_0.setPostId(Ranks.CITIZEN);//设置该人物的职位
//            NPC_0.setRankId(Ranks.CITIZEN);//设置该人物的军衔
//            NPC_0.setGender(FullName.Gender.ANY);//设置性别
//            NPC_0.getName().setFirst("Bracks");//众所周知，西方人的名字是由两部分组成
//            NPC_0.getName().setLast("Ao");
//            NPC_0.setPortraitSprite("graphics/portraits/portrait47.png");//设置人物的大头照
//            NPC_0.setImportanceAndVoice(PersonImportance.HIGH, StarSystemGenerator.random);//设置人物的重要性，至于Voice是角色打招呼的语气，例如voice = faithful就会说“卢德保佑你”之类，可在rules中自定义
//            IP.addPerson(NPC_0);//只有加入ImportantPeople，该人物才能被rules和missionHub识别
//            IP.getData(NPC_0).getLocation().setMarket(Market_MirageCity);//将人物传送到指定market里
//            IP.checkOutPerson(NPC_0, "permanent_staff");//"这个的意思是把人物以'永久成员(permanent_staff)'的理由签发出去，如此一来就不会成为某些随机任务的目标。“————感谢议长订正
//            Market_MirageCity.getCommDirectory().addPerson(NPC_0, 0);//将其加入通讯录中
//            Market_MirageCity.addPerson(NPC_0);//将该person加入市场的人物列表，使某些按市场寻人的方法可以找到
//
//        }
//    }

}



