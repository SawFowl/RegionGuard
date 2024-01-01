package sawfowl.regionguard.implementsapi.worldedit.cui.events;

import sawfowl.regionguard.implementsapi.worldedit.MultiSelectionColors;
import sawfowl.regionguard.implementsapi.worldedit.MultiSelectionType;

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
