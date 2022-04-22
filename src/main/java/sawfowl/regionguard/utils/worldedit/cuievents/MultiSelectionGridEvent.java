package sawfowl.regionguard.utils.worldedit.cuievents;

import sawfowl.regionguard.utils.worldedit.MultiSelectionType;

public class MultiSelectionGridEvent implements CUIEvent {

	final String[] parameters;

	public MultiSelectionGridEvent(double spacing) {
		this.parameters = new String[] { String.valueOf(spacing),
				MultiSelectionType.GRID_CULL};
	}

	public String[] getParameters() {
		return this.parameters;
	}

	public String getTypeId() {
		return MultiSelectionType.GRID;
	}

}
