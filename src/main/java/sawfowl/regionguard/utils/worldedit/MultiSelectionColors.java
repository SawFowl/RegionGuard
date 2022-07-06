package sawfowl.regionguard.utils.worldedit;

import java.util.Map;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.configure.CuiConfigPaths;

public class MultiSelectionColors {

	public static final String RED = "#990000";
	public static final String GREEN = "#5AC25A";
	public static final String YELLOW = "#EAEA32";
	public static final String GRAY = "#D2D2D2";
	public static final String BLUE = "#2801ff";
	public static final String ORANGE = "#ff8900";
	public static final String BURLYWOOD = "#deB887";
	public static final String PURPLE = "#A020F0";
	private static final String[] IF_UNSET = {RED, RED, RED, RED};

	public static String getClaimColor(Region region) {
		if(region.isSubdivision()) return GRAY;
		if(region.isAdmin()) return RED;
		if(region.isArena()) return GREEN;
		return YELLOW;
	}

	public static String[] getColors(Map<String, String[]> cuiColors, String type) {
		if(cuiColors.isEmpty()) return IF_UNSET;
		if(!cuiColors.containsKey(type)) type = CuiConfigPaths.DEFAULT;
		if(!cuiColors.containsKey(type)) return IF_UNSET;
		return cuiColors.get(type);
	}

}
