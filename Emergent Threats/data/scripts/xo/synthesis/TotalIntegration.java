package data.scripts.xo.synthesis;

import second_in_command.misc.SCSettings;
import second_in_command.skills.automated.SCBaseAutoPointsSkillPlugin;

public class TotalIntegration extends SCBaseAutoPointsSkillPlugin {
	
	@Override
	public int getProvidedPoints() {
		return Math.round((90 * SCSettings.getAutoPointsMult()));
	}
}