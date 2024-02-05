package portrait_changer

import com.fs.starfarer.api.BaseModPlugin
import com.fs.starfarer.api.Global

class PCModPlugin : BaseModPlugin() {


    override fun onGameLoad(newGame: Boolean) {
        super.onGameLoad(newGame)

       /* for (i in 0..10)
        {
            var testPerson = Global.getFactory().createPerson()
            testPerson.name.first = "Test"
            Global.getSector().characterData.addAdmin(testPerson)
        }*/


        if (!Global.getSector().intelManager.hasIntelOfClass(PortraitChangerIntel::class.java))
        {
            Global.getSector().intelManager.addIntel(PortraitChangerIntel())
        }

    }
}