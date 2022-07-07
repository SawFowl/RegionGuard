package sawfowl.regionguard.utils.worldedit.cuievents;

import sawfowl.regionguard.utils.worldedit.MultiSelectionColors;
import sawfowl.regionguard.utils.worldedit.MultiSelectionType;

public class MultiSelectionColorEvent implements CUIEvent {

	private final String[] parameters;

	public MultiSelectionColorEvent(String[] colors) {
		if(colors.length == 4) {
			this.parameters = colors;
		} else this.parameters = new String[]{MultiSelectionColors.RED, MultiSelectionColors.RED, MultiSelectionColors.RED, MultiSelectionColors.RED};
	}

	public String getTypeId() {
		return MultiSelectionType.COLOR;
	}

	public String[] getParameters() {
		return this.parameters;
	}

}
