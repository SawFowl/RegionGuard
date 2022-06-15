package sawfowl.regionguard.utils.worldedit;

import sawfowl.regionguard.api.data.Region;

public class MultiSelectionColors {

	public static final String RED = "#990000";
	public static final String GREEN = "#5AC25A";
	public static final String YELLOW = "#EAEA32";
	public static final String GRAY = "#D2D2D2";
	public static final String BLUE = "#2801ff";
	public static final String ORANGE = "#ff8900";
	public static final String PURPLE = "#A020F0";

	public static String getClaimColor(Region region) {
		if(region.isSubdivision()) return GRAY;
		if(region.isAdmin()) return RED;
		if(region.isArena()) return GREEN;
		return YELLOW;
	}

}
