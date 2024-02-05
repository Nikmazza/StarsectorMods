package mmm.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.contacts.ContactIntel;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.magiclib.util.MagicSettings;

import java.text.MessageFormat;
import java.util.*;

public abstract class OrbitalMissionBase extends HubMissionWithBarEvent {
    public static final String MOD_ID = "MoreMilitaryMissions";
    private static final Logger log = Global.getLogger(OrbitalMissionBase.class);
    static {
        if (MagicSettings.getBoolean(MOD_ID, "MmmDebug")) {
            log.setLevel(Level.ALL);
        }
    }

    // Compare 2 person by relationships with player, whether they're in the comm directory or not, and whether they
    // are contacts.
    public static class ComparePersonByRel implements Comparator<PersonAPI> {
        public CommDirectoryAPI dir;
        public ComparePersonByRel(MarketAPI market) {
            dir = market.getCommDirectory();
        }

        @Override
        public int compare(PersonAPI o0, PersonAPI o1) {
            int priority0 = Math.round(1000 * (o0.getRelToPlayer().getRel()));
            int priority1 = Math.round(1000 * (o1.getRelToPlayer().getRel()));

            CommDirectoryEntryAPI entry0 = dir.getEntryForPerson(o0);
            CommDirectoryEntryAPI entry1 = dir.getEntryForPerson(o1);
            if (entry0 != null && !entry0.isHidden()) priority0 += 10000;
            if (entry1 != null && !entry1.isHidden()) priority1 += 10000;

            if (ContactIntel.playerHasIntelItemForContact(o0)) priority0 += 20000;
            if (ContactIntel.playerHasIntelItemForContact(o1)) priority1 += 20000;

            return priority0 - priority1;
        }
    }

    public static PersonAPI findStationCommander(MarketAPI market) {
        // Try to get the existing station commander instead of generating duplicates. In fact nothing prevents the
        // game can from generating multiple station commanders in the background, so we prefer the non-hidden ones in
        // the comm directory, preferring contacts. In there are ties, break them by relationship with player.
        HashSet<PersonAPI> candidates = new HashSet<>(market.getPeopleCopy());
        // For some reason the people the comm directory of in Pirate/LP outposts isn't actually in the market. It
        // probably doesn't matter in the base game since you can't do anything with them, but that needs to be fixed
        // in createBarGiver before we can use them as quest giver.
        for (CommDirectoryEntryAPI entry : market.getCommDirectory().getEntriesCopy()) {
            if (entry.getType() == CommDirectoryEntryAPI.EntryType.PERSON &&
                    entry.getEntryData() instanceof PersonAPI) {
                candidates.add((PersonAPI) entry.getEntryData());
            }
        }

        // Remove candidates that don't have the right attributes.
        for (Iterator<PersonAPI> iterator = candidates.iterator(); iterator.hasNext();) {
            PersonAPI person = iterator.next();
            String market_str = person.getMarket() == null ? "null" : person.getMarket().getName();
            if (!person.getPostId().equals(Ranks.POST_STATION_COMMANDER) ||
                    person.getFaction() != market.getFaction() ||
                    (person.getMarket() != null && person.getMarket() != market)) {
                iterator.remove();
            }
        }

        return candidates.isEmpty() ? null : Collections.max(candidates, new ComparePersonByRel(market));
    }

    public void createBarGiver(MarketAPI createdAt) {
        createBarGiver(this, createdAt);
    }
    public static void createBarGiver(HubMissionWithBarEvent event, MarketAPI createdAt) {
        PersonAPI commander = findStationCommander(createdAt);
        if (commander != null) {
            log.debug("Existing station commander " + commander.getNameString() + " found with tags=" +
                    commander.getTags());
            // Give them military/underworld tag so they work as a military/underworld contact
            if (Factions.PIRATES.equals(commander.getFaction().getId())) {
                commander.addTag(Tags.CONTACT_UNDERWORLD);
            } else {
                commander.addTag(Tags.CONTACT_MILITARY);
            }

            if (commander.getMarket() == null) {
                createdAt.addPerson(commander);
            }
            event.makePersonRequired(commander);  // Prevent another mission from removing him.
            event.setPersonOverride(commander);
            return;
        }

        // Adapted from MilitaryCustomBounty. To avoid duplicate station commanders, use it only if the above code
        // fails to find someone.
        event.setGiverPost(Ranks.POST_STATION_COMMANDER);
        event.setGiverTags(Tags.CONTACT_MILITARY);
        event.setGiverRank(Ranks.SPACE_CAPTAIN);
        event.setGiverImportance(event.pickHighImportance());
        event.findOrCreateGiver(createdAt, false, false);
        event.setPersonOverride(event.getPerson());
        log.info("Created new station commander " + event.getPerson().getNameString());
    }

    // Add quest giver as contact; the probability is 100% for player faction.
    // Code adapted from ContactIntel.addPotentialContact
    public void addPotentialContact(float contact_prob) {
        addPotentialContact(this, contact_prob);
    }
    public static void addPotentialContact(HubMissionWithBarEvent event, float contact_prob) {
        PersonAPI contact = event.getPerson();
        MarketAPI market= contact.getMarket();

        if (market == null) return;  // Sanity check
        if (ContactIntel.playerHasIntelItemForContact(contact)) return;
        if (market.getMemoryWithoutUpdate().getBoolean(ContactIntel.NO_CONTACTS_ON_MARKET)) return;
        if (contact.getFaction().getCustomBoolean(Factions.CUSTOM_NO_CONTACTS)) return;

        if (!contact.getFaction().isPlayerFaction()) {
            // If the player already has some existing relationship with the person, use it to modify the probability
            // they'll be available as a potential contact
            float probability = contact_prob + contact.getRelToPlayer().getRel() * 2.4f;
            if (event.getGenRandom().nextFloat() >= probability) {
                log.info(MessageFormat.format(
                        "Failed to roll {0} for potential contact {1}", probability, contact.getNameString()));
                return;
            }
        }
        contact.removeTag(REMOVE_ON_MISSION_OVER);  // Not sure if this is needed
        // Note that ContactIntel will ensure that the person is added to the market and comm directory.
        Global.getSector().getIntelManager().addIntel(new ContactIntel(contact, market), false);
    }
}
