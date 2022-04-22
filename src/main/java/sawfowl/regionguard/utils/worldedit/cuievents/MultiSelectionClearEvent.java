package sawfowl.regionguard.utils.worldedit.cuievents;

import java.util.UUID;


import sawfowl.regionguard.utils.worldedit.MultiSelectionType;

public class MultiSelectionClearEvent implements CUIEvent {

	private final String[] parameters;

	public MultiSelectionClearEvent() {
		this.parameters = new String[] {};
	}

	public MultiSelectionClearEvent(UUID uniqueId) {
		this.parameters = new String[] {
				uniqueId.toString()};
	}

	public String[] getParameters() {
		return this.parameters;
	}

	public String getTypeId() {
		return MultiSelectionType.SELECTION_CLEAR;
	}

}
