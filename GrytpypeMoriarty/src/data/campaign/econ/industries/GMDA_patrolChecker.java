package data.campaign.econ.industries;

import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import data.campaign.econ.GMDA_industries;

public class GMDA_patrolChecker extends MilitaryBase {
    //this is just a temporary solution to get Fabs to block HI
    //Replace ASAP
    
    @Override
    public boolean isAvailableToBuild() {
        boolean hasGMDA = false;
        boolean hasCOND = false;
		
        if (market.getPlanetEntity() != null && (market.hasIndustry(GMDA_industries.SEAGOON))) {
            hasGMDA = true;
        }
        
        if (hasGMDA) {
            return false;
        } else if (!hasGMDA) {
            return true;
        }

        if (market.getPlanetEntity() != null && (market.hasIndustry(GMDA_industries.CONDOT))) {
            hasCOND = true;
        }

        if (hasCOND) {
            return false;
        } else if (!hasCOND) {
            return true;
        }

        return false;

    }

    @Override
    public String getUnavailableReason() {
        return "Modular Fabricators already present";
    }

    @Override
    public boolean showWhenUnavailable() {
        return true;
    }
}
