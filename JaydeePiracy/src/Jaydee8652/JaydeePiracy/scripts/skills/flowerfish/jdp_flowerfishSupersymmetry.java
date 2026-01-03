package Jaydee8652.JaydeePiracy.scripts.skills.flowerfish;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.WeaponAPI.WeaponType;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import second_in_command.SCData;
import second_in_command.specs.SCBaseSkillPlugin;

import java.util.*;

import static Jaydee8652.JaydeePiracy.scripts.jdp_StolenUtils.*;
import static Jaydee8652.JaydeePiracy.scripts.skills.flowerfish.jdp_AptitudeFlowerfish.isHumanOfficer;
import static com.fs.starfarer.api.combat.WeaponAPI.WeaponSize.SMALL;
import static com.fs.starfarer.api.combat.WeaponAPI.WeaponType.BALLISTIC;
import static com.fs.starfarer.api.combat.WeaponAPI.WeaponType.ENERGY;

public class jdp_flowerfishSupersymmetry extends SCBaseSkillPlugin {
    public static float duration = 3f;
    public static float bonus = 1f;
    public static int cap = 50;
    public static float beamRefire = 1f;

    public static Object STATUS_KEY1 = new Object();
    public static Object STATUS_KEY2 = new Object();

    @Override
    public String getAffectsString() {
        return "all ships with human officers";
    }

    @Override
    public void addTooltip(SCData scData, TooltipMakerAPI tooltipMakerAPI) {
        tooltipMakerAPI.addPara("Firing ballistic/energy weapons reduces the flux cost of the other type by " + Math.round(bonus) + "%% for " + Math.round(duration) + " seconds, capped at " + cap + "%%", 0f, Misc.getHighlightColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addPara("*Only affects weapons of the same size or smaller, beams are considered to be firing once a second", 0f, Misc.getGrayColor(), Misc.getGrayColor());
        tooltipMakerAPI.addSpacer(10f);

        tooltipMakerAPI.addPara("\"There is irony in, through destructive interference, making destruction serve yet further destruction.\"", 0f, Misc.getTextColor(), Misc.getHighlightColor());
        tooltipMakerAPI.addPara("  -Proving Grounds Transcript, Undisclosed Location", 0f, jdp_AptitudeFlowerfish.JDP_FLOWERFISH_COLOUR, Misc.getHighlightColor());
        tooltipMakerAPI.addSpacer(10f);
    }

    public static class SupersymmetryAdvanceListener implements AdvanceableListener {
        ShipAPI ship;

        ArrayList<WeaponStack> stacks = new ArrayList<>();
        List<WeaponAPI> firingWeapons = new ArrayList<WeaponAPI>();

        IntervalUtil beamTracker = new IntervalUtil(beamRefire, beamRefire);

        public SupersymmetryAdvanceListener(ShipAPI ship) {
            this.ship = ship;
        }

        @Override
        public void advance(float amount) {
            if (!ship.isAlive() || !isHumanOfficer(ship)) ship.removeListener(this);

            beamTracker.advance(amount);
            for (Iterator<WeaponStack> iterator = new ArrayList<>(stacks).iterator(); iterator.hasNext(); ) {
                WeaponStack stack = iterator.next();
                stack.duration -= 1 * amount;

                if (stack.duration < 0f) {
                    stacks.remove(stack);
                }
            }

            float countLargeBallistic = stacks.stream().filter(s -> s.size.equals(WeaponAPI.WeaponSize.LARGE) && s.type.equals(WeaponType.ENERGY)).toList().size();
            float countMediumBallistic = countLargeBallistic + stacks.stream().filter(s -> s.size.equals(WeaponAPI.WeaponSize.MEDIUM) && s.type.equals(WeaponType.ENERGY)).toList().size();
            float countSmallBallistic = countMediumBallistic + stacks.stream().filter(s -> s.size.equals(SMALL) && s.type.equals(WeaponType.ENERGY)).toList().size();

            //Technically unnecessary, comes from when each stack was worth more than 1%.
            // Will be useful if it is ever dropped to less than 1%
            //countLargeBallistic *= bonus;
            //countMediumBallistic *= bonus;
            //countSmallBallistic *= bonus;

            if (countLargeBallistic >= cap) countLargeBallistic = cap;
            if (countMediumBallistic >= cap) countMediumBallistic = cap;
            if (countSmallBallistic >= cap) countSmallBallistic = cap;

            float countLargeEnergy = stacks.stream().filter(s -> s.size.equals(WeaponAPI.WeaponSize.LARGE) && s.type.equals(BALLISTIC)).toList().size();
            float countMediumEnergy = countLargeEnergy + stacks.stream().filter(s -> s.size.equals(WeaponAPI.WeaponSize.MEDIUM) && s.type.equals(BALLISTIC)).toList().size();
            float countSmallEnergy = countMediumEnergy + stacks.stream().filter(s -> s.size.equals(SMALL) && s.type.equals(BALLISTIC)).toList().size();

            //countLargeEnergy *= bonus;
            //countMediumEnergy *= bonus;
            //countSmallEnergy *= bonus;

            if (countLargeEnergy >= cap) countLargeEnergy = cap;
            if (countMediumEnergy >= cap) countMediumEnergy = cap;
            if (countSmallEnergy >= cap) countSmallEnergy = cap;

            for (WeaponAPI weapon : ship.getAllWeapons()) {
                //Allows beams to "fire" again every beamRefire duration
                if (weapon.isFiring() && weapon.isBeam() && beamTracker.intervalElapsed()) firingWeapons.remove(weapon);

                if (weapon.isFiring() && (weapon.getType().equals(BALLISTIC) || weapon.getType().equals(ENERGY)) && !firingWeapons.contains(weapon)) {
                    //"Firing" is an animation state, not a discrete moment, so this allows us to only consider each firing event once
                    firingWeapons.add(weapon);

                    WeaponAPI.WeaponSize size = weapon.getSize();
                    WeaponType type = weapon.getType();

                    switch (type) {
                        case BALLISTIC:
                            switch (size) {
                                case SMALL ->
                                        ship.getFluxTracker().decreaseFlux(weapon.getFluxCostToFire() * 0.01f * countSmallEnergy);
                                case MEDIUM ->
                                        ship.getFluxTracker().decreaseFlux(weapon.getFluxCostToFire() * 0.01f * countMediumEnergy);
                                case LARGE ->
                                        ship.getFluxTracker().decreaseFlux(weapon.getFluxCostToFire() * 0.01f * countLargeEnergy);
                            }
                        case ENERGY:
                            switch (size) {
                                case SMALL ->
                                        ship.getFluxTracker().decreaseFlux(weapon.getFluxCostToFire() * 0.01f * countSmallBallistic);
                                case MEDIUM ->
                                        ship.getFluxTracker().decreaseFlux(weapon.getFluxCostToFire() * 0.01f * countMediumBallistic);
                                case LARGE ->
                                        ship.getFluxTracker().decreaseFlux(weapon.getFluxCostToFire() * 0.01f * countLargeBallistic);
                            }
                    }

                    stacks.add(new WeaponStack(duration, type, size));
                    break;
                } else if (!weapon.isFiring()) {
                    firingWeapons.remove(weapon);
                }
            }

            if (playerShip(ship)) {
                Global.getCombatEngine().maintainStatusForPlayerShip(STATUS_KEY1, Global.getSettings().getSpriteName("ui", "jdp_icon_ballistic"), "SUPERSYMMETRY - BALLISTIC", Math.round(countSmallBallistic) + "/" + Math.round(countMediumBallistic) + "/" + Math.round(countLargeBallistic) + "% flux discount.", false);
                Global.getCombatEngine().maintainStatusForPlayerShip(STATUS_KEY2, Global.getSettings().getSpriteName("ui", "jdp_icon_energy"), "SUPERSYMMETRY - ENERGY", Math.round(countSmallEnergy) + "/" + Math.round(countMediumEnergy) + "/" + Math.round(countLargeEnergy) + "% flux discount.", false);
            }
        }
    }


    @Override
    public void advanceInCombat(SCData data, ShipAPI ship, Float amount) {
    }

    @Override
    public void applyEffectsAfterShipCreation(SCData data, ShipAPI ship, ShipVariantAPI variant, String id) {
        if (isHumanOfficer(ship) && ship.getListeners(SupersymmetryAdvanceListener.class).isEmpty()) ship.addListener(new SupersymmetryAdvanceListener(ship));
    }

    @Override
    public void onActivation(SCData data) {}

    @Override
    public void onDeactivation(SCData data) {}

    public static class WeaponStack {
        private float duration;
        private final WeaponType type;
        private final WeaponAPI.WeaponSize size;

        public WeaponStack(float duration, WeaponType type, WeaponAPI.WeaponSize size) {
            this.duration = duration;
            this.type = type;
            this.size = size;
        }
    }
}
