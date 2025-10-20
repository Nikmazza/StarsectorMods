package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;

import Jaydee8652.JaydeePiracy.JaydeePiracyPlugin
import com.fs.starfarer.api.Global
import com.fs.starfarer.api.impl.campaign.ids.MemFlags
import com.fs.starfarer.api.impl.campaign.ids.Stats
import com.fs.starfarer.api.plugins.OfficerLevelupPlugin
import com.fs.starfarer.api.ui.TooltipMakerAPI
import com.fs.starfarer.api.util.Misc
import org.apache.log4j.Logger
import org.magiclib.kotlin.getSalvageSeed
import org.magiclib.kotlin.isAutomated
import second_in_command.SCData
import second_in_command.SCUtils.addAndCheckTag
import second_in_command.specs.SCBaseSkillPlugin

class jdp_flowerfishPersonalDiscretion : SCBaseSkillPlugin() {
    val log: Logger = Global.getLogger(JaydeePiracyPlugin::class.java)

    val RATE_MALUS: Int = 75;


    override fun getAffectsString(): String {
        return "all officers"
    }

    override fun addTooltip(data: SCData, tooltip: TooltipMakerAPI) {
        tooltip.addPara("+1 to maximum level of officers under your command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("+1 to maximum number of elite skills for officers under your command", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addPara("*If this skill is unassigned officers over the level limit will have excess skills made inactive, prioritising elite skills", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "")
        //tooltip.addPara(" Elite skills will be removed first, inactive skills can be restored by re-assigning this officer", 0f, Misc.getGrayColor(), Misc.getHighlightColor(), "")
        tooltip.addSpacer(10f)

        tooltip.addPara("Affects: fleet", 0f, Misc.getGrayColor(), Misc.getBasePlayerColor(), "fleet")
        tooltip.addSpacer(10f)

        tooltip.addPara("Command point recovery rate is reduced by " + (100 - RATE_MALUS) + "%%.", 0f, Misc.getHighlightColor(), Misc.getHighlightColor())
        tooltip.addSpacer(10f)

        tooltip.addPara("\"I am confident in my abilities. I would not make such a mistake.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor())
        tooltip.addPara("  -Internal Trial Transcript, Maki Insubordination Case", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor())
        tooltip.addSpacer(10f)
    }

    override fun onActivation(data: SCData) {
        log.info("JDP_DEBUG: [ON] Fleet [" + data.fleet.name + "] just activated Personal Discretion!")

        data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).modifyFlat("jdp_flowerfishPersonalDiscretion", 1f)
        data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_ELITE_SKILLS_MOD).modifyFlat("jdp_flowerfishPersonalDiscretion", 1f)
        data.commander.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_COMMANDER).modifyMult("jdp_flowerfishPersonalDiscretion", (RATE_MALUS / 100).toFloat())

        if (!data.isNPC) {
            log.info("JDP_DEBUG: Fleet [" + data.fleet.name + "] is not an NPC")

            var officers = Global.getSector().playerFleet.fleetData.officersCopy.map { it.person }
            for (officer in officers) {
                var map = officer.memoryWithoutUpdate.get("\$jdp_flowerfishPersonalDiscretion_inactive") as HashMap<String, Float>?
                    ?: continue

                for ((skill, level) in map) {
                    officer.stats.setSkillLevel(skill, level)
                }

                officer.memoryWithoutUpdate.set("\$jdp_flowerfishPersonalDiscretion_inactive", null)

                officer.stats.setSkillLevel("jdp_inactive", 0f)
            }
        }

        //Increase officer levels and give skills
        if (data.isNPC && !data.fleet.addAndCheckTag("jdp_flowerfishPersonalDiscretion_update")) {
            var levels = 1

            var membersWithOfficers = data.fleet.fleetData.membersListCopy.filter { (it.captain != null && !it.captain.isDefault) && !it.isAutomated() }
            membersWithOfficers.filter { !Global.getSector().importantPeople.containsPerson(it.captain) }


            val plugin = Global.getSettings().getPlugin("officerLevelUp") as OfficerLevelupPlugin
            for (member in membersWithOfficers) {

                var captain = member.captain
                if (captain.hasTag("jdp_flowerfishPersonalDiscretion_increased_lv")) continue //Do not increase the level multiple times

                captain.addTag("jdp_flowerfishPersonalDiscretion_increased_lv")

                for (level in 0 until levels) {
                    captain.stats.level += 1

                    var skills = plugin.pickLevelupSkills(captain, java.util.Random(data.fleet.getSalvageSeed()))
                    if (skills.isNotEmpty()) {
                        var pick = skills.random()
                        captain.stats.increaseSkill(pick)
                    }
                }
            }
        }
    }

    override fun onDeactivation(data: SCData) {
        log.info("JDP_DEBUG: [OFF] Fleet [" + data.fleet.name + "] just deactivated Personal Discretion!")

        data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).unmodify("jdp_flowerfishPersonalDiscretion")
        data.commander.stats.dynamic.getMod(Stats.OFFICER_MAX_ELITE_SKILLS_MOD).unmodify("jdp_flowerfishPersonalDiscretion")
        data.commander.stats.dynamic.getMod(Stats.COMMAND_POINT_RATE_COMMANDER).unmodify("jdp_flowerfishPersonalDiscretion")


        if (!data.isNPC) {
            log.info("JDP_DEBUG: Fleet [" + data.fleet.name + "] is not an NPC")

            var maxLevel = Global.getSector().characterData.person.stats.dynamic.getMod(Stats.OFFICER_MAX_LEVEL_MOD).computeEffective(Global.getSettings().getFloat("officerMaxLevel"))

            var officers = Global.getSector().playerFleet.fleetData.officersCopy.map { it.person }
            for (officer in officers) {

                var anyRemoved = false

                if (officer.memoryWithoutUpdate.contains(MemFlags.OFFICER_MAX_LEVEL)) continue

                var map = HashMap<String, Float>()
                var stats = officer.stats

                var skills = officer.stats.skillsCopy
                var filtered = skills.filter { it.level > 0f && !it.skill.isAptitudeEffect }.toMutableList()

                filtered = filtered.filter { !it.skill.hasTag("npc_only") && !it.skill.hasTag("player_only") && !it.skill.hasTag("ai_core_only") }.toMutableList()
                var elite = filtered.filter { it.skill.isElite }.toMutableList()

                while (filtered.count() > maxLevel) {
                    filtered.sortBy { it.level }

                    if (filtered.isEmpty()) break
                    anyRemoved = true

                    var last = filtered.last()
                    filtered.remove(last)

                    map.put(last.skill.id, last.level)

                    last.level = 0f
                }

                officer.memoryWithoutUpdate.set("\$jdp_flowerfishPersonalDiscretion_inactive", map)

                if (anyRemoved) {
                    officer.stats.setSkillLevel("jdp_inactive", 1f)
                }
            }
        }
    }
}