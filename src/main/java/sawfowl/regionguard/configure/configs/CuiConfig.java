package sawfowl.regionguard.configure.configs;

import java.util.HashMap;
import java.util.Map;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.CuiConfigPaths;
import sawfowl.regionguard.implementsapi.worldedit.MultiSelectionColors;

@ConfigSerializable
public class CuiConfig {

	@Setting("Colors")
	private Map<String, Map<String, String>> colors = defaultCuiColors();

	@Setting("Spaces")
	private Map<String, Integer> spaces = defaultCuiSpaces();

	public Map<String, Map<String, String>> getColors() {
		return colors;
	}

	public Map<String, Integer> getSpaces() {
		return spaces;
	}

	private Map<String, Map<String, String>> defaultCuiColors() {
		
		Map<String, Map<String, String>> cuiColors = new HashMap<String, Map<String, String>>();
		
		Map<String, String> defaultColors = new HashMap<String, String>();
		Map<String, String> basicClaimColors = new HashMap<String, String>();
		Map<String, String> arenaColors = new HashMap<String, String>();
		Map<String, String> adminColors = new HashMap<String, String>();
		Map<String, String> subdivisionColors = new HashMap<String, String>();
		Map<String, String> tempRegionColors = new HashMap<String, String>();
		Map<String, String> dragColors = new HashMap<String, String>();
		
		defaultColors.put(CuiConfigPaths.FIRST_POSITION, MultiSelectionColors.BLUE);
		defaultColors.put(CuiConfigPaths.SECOND_POSITION, MultiSelectionColors.ORANGE);
		defaultColors.put(CuiConfigPaths.EDGE, MultiSelectionColors.RED);
		basicClaimColors.putAll(defaultColors);
		arenaColors.putAll(defaultColors);
		adminColors.putAll(defaultColors);
		subdivisionColors.putAll(defaultColors);
		tempRegionColors.putAll(defaultColors);
		dragColors.putAll(defaultColors);
		defaultColors.put(CuiConfigPaths.GRID, MultiSelectionColors.RED);
		basicClaimColors.put(CuiConfigPaths.GRID, MultiSelectionColors.YELLOW);
		arenaColors.put(CuiConfigPaths.GRID, MultiSelectionColors.GREEN);
		adminColors.put(CuiConfigPaths.GRID, MultiSelectionColors.RED);
		subdivisionColors.put(CuiConfigPaths.GRID, MultiSelectionColors.GRAY);
		tempRegionColors.put(CuiConfigPaths.GRID, MultiSelectionColors.PURPLE);
		dragColors.put(CuiConfigPaths.GRID, MultiSelectionColors.BURLYWOOD);
		
		cuiColors.put(CuiConfigPaths.DEFAULT, defaultColors);
		cuiColors.put(CuiConfigPaths.CLAIM, basicClaimColors);
		cuiColors.put(CuiConfigPaths.ARENA, arenaColors);
		cuiColors.put(CuiConfigPaths.ADMIN, adminColors);
		cuiColors.put(CuiConfigPaths.SUBDIVISION, subdivisionColors);
		cuiColors.put(CuiConfigPaths.TEMP, tempRegionColors);
		cuiColors.put(CuiConfigPaths.DRAG, dragColors);
		
		return cuiColors;
	}

	private Map<String, Integer> defaultCuiSpaces() {
		Map<String, Integer> cuiSpaces = new HashMap<String, Integer>();
		cuiSpaces.put(CuiConfigPaths.DEFAULT, 5);
		cuiSpaces.put(CuiConfigPaths.CLAIM, 3);
		cuiSpaces.put(CuiConfigPaths.ARENA, 3);
		cuiSpaces.put(CuiConfigPaths.ADMIN, 3);
		cuiSpaces.put(CuiConfigPaths.SUBDIVISION, 3);
		cuiSpaces.put(CuiConfigPaths.TEMP, 3);
		cuiSpaces.put(CuiConfigPaths.DRAG, 3);
		return cuiSpaces;
	}

}
