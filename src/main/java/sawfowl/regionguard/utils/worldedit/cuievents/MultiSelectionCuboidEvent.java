package sawfowl.regionguard.utils.worldedit.cuievents;

import java.util.UUID;

import sawfowl.regionguard.utils.worldedit.MultiSelectionType;

public class MultiSelectionCuboidEvent implements CUIEvent {

	protected final UUID uniqueId;
	private final String[] parameters;

	public MultiSelectionCuboidEvent(UUID uniqueId) {
		this.uniqueId = uniqueId;
		this.parameters = new String[] {
				String.valueOf(this.uniqueId)
			};
	}

	public String getTypeId() {
		return MultiSelectionType.SELECTION_CUBOID;
	}

	public String[] getParameters() {
		return this.parameters;
	}

}
