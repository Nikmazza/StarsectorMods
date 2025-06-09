package data.scripts;  
  
import com.fs.starfarer.api.BaseModPlugin;  
import com.fs.starfarer.api.Global;
import data.scripts.world.YNAGen;
import exerelin.campaign.SectorManager;
  
public class YNAPlugin extends BaseModPlugin {  
  
    public static boolean isExerelin = false;
    
    private static void initYNA() { 
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getCorvusMode()){
            new YNAGen().generate(Global.getSector());
        }
    }  
	
    @Override
    public void onNewGame()
    {
        initYNA();
    }

    @Override
    public void onApplicationLoad()
    {
        
    }
}  