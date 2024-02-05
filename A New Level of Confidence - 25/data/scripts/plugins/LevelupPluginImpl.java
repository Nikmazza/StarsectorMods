package data.scripts.plugins;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.plugins.LevelupPlugin;

public class LevelupPluginImpl implements LevelupPlugin {

	/**
	 * Only used if max level is increased beyond 15 via settings.json
	 */
	public static float EXPONENT_BEYOND_MAX_SPECIFIED_LEVEL = 1.1f;
	
	/**
	 * Max level XP times this is how much XP it takes to gain storyPointsPerLevel story points once at max level.
	 */
	public static float XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT = 1f;
	public static int LEVEL_FOR_BASE_XP_FOR_MAXED_STORY_POINT_GAIN = 25;
	//33480000
	public static long [] XP_PER_LEVEL = new long [] {
		0,		// level 1
		40000,									//40k
		60000,									//20k
		80000,									//20k
		100000,  // level 5, ramp up after		//20k
		200000,									//100k
		300000,									//100k
		400000,									//100k
		500000,									//100k
		600000, // level 10, ramp up after		//100k
		740000,									//140k
		880000,									//140k
		1020000,								//140k
		1160000,								//140k
		1300000, // level 15					//140k
		1500000,								//200k
		1700000,								//200k
		1900000,								//200k
		2100000,								//200k
		2300000, // level 20					//200k
		2640000,								//340k
		2980000,								//340k
		3320000,								//340k
		3660000,								//340k
		4000000, //level 25						//340k
		//3700000,
		//3900000,
		//4100000,
		//4300000,
		//4500000, //level 30
	};
	
	public static long [] TOTAL_XP_PER_LEVEL = new long [XP_PER_LEVEL.length];
	
	static {
		long total = 0;
		for (int i = 0; i < XP_PER_LEVEL.length; i++) {
			total += XP_PER_LEVEL[i];
			TOTAL_XP_PER_LEVEL[i] = total;
		}
	}
	                                               
	                                              
	
	
	
	public int getPointsAtLevel(int level) {
		return (int) Global.getSettings().getFloat("skillPointsPerLevel");
	}

	public int getMaxLevel() {
		return (int) Global.getSettings().getFloat("playerMaxLevel");
	}
	
	public int getStoryPointsPerLevel() {
		return (int) Global.getSettings().getFloat("storyPointsPerLevel");
	}
	
	public int getBonusXPUseMultAtMaxLevel() {
		return (int) Global.getSettings().getFloat("bonusXPUseMultAtMaxLevel");
	}
	
//	public long getXPForNextLevel(int level) {
//		if (level < XP_PER_LEVEL.length) {
//			return XP_PER_LEVEL[level];
//		}
//		
//		return (long) (XP_PER_LEVEL[LEVEL_FOR_BASE_XP_FOR_MAXED_STORY_POINT_GAIN - 1] * XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT);
//	}

	
	public long getXPForLevel(int level) {
		if (level <= 1) return 0;
		
		if (level - 1 < TOTAL_XP_PER_LEVEL.length) {
			return TOTAL_XP_PER_LEVEL[level - 1];
		}
		
		int max = getMaxLevel();
		int maxSpecified = TOTAL_XP_PER_LEVEL.length;
		long curr = TOTAL_XP_PER_LEVEL[maxSpecified - 1];
		long last = XP_PER_LEVEL[maxSpecified - 1];
		for (int i = maxSpecified; i < level && i < max; i++) {
			last *= EXPONENT_BEYOND_MAX_SPECIFIED_LEVEL;
			curr += last;
		}
		
		if (level >= max + 1) {
			//last *= XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT;
			last = (long) (XP_PER_LEVEL[LEVEL_FOR_BASE_XP_FOR_MAXED_STORY_POINT_GAIN - 1] * 
						   XP_REQUIRED_FOR_STORY_POINT_GAIN_AT_MAX_LEVEL_MULT);
			curr += last;
		}
		
		return curr;
	}

	
	public static void main(String[] args) {
		LevelupPluginImpl plugin = new LevelupPluginImpl();
		
//		System.out.println(plugin.getXPForLevel(16) - plugin.getXPForLevel(15));
		
		for (int i = 1; i < 16; i++) {
			//System.out.println(i + ": " + (plugin.getXPForLevel(i) - plugin.getXPForLevel(i - 1)));
			System.out.println(i + ": " + (plugin.getXPForLevel(i)));
		}
	}


}
