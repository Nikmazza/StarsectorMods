package II_BG.data.script.utils;

import II_BG.data.script.II_BG_CodexData;
import com.fs.starfarer.api.BaseModPlugin;



public class II_BGPlugin extends BaseModPlugin {
  
    @Override
    public void onAboutToLinkCodexEntries() {
        super.onAboutToLinkCodexEntries();

        II_BG_CodexData.linkCodexEntries();
    }
}
