package sawfowl.regionguard.implementsapi.worldedit.cui.events;

import org.spongepowered.math.vector.Vector3i;

import sawfowl.regionguard.implementsapi.worldedit.MultiSelectionType;

public class MultiSelectionPointEvent implements CUIEvent {

	private final String TILDE = "~";
	private final String[] parameters;

	// Used to force WECUI to follow player EYE
	public MultiSelectionPointEvent(int id) {
		this.parameters = new String[] { 
				String.valueOf(id),
				TILDE,
				TILDE,
				TILDE,
				String.valueOf(0)};
	}

	public MultiSelectionPointEvent(int id, Vector3i pos) {
		this.parameters = new String[] {
				String.valueOf(id),
				String.valueOf(pos.x()),
				String.valueOf(pos.y()),
				String.valueOf(pos.z()),
				String.valueOf(0)};
	}

	public MultiSelectionPointEvent(int id, Vector3i pos, long area) {
		this.parameters = new String[] {
				String.valueOf(id),
				String.valueOf(pos.x()),
				String.valueOf(pos.y()),
				String.valueOf(pos.z()),
				String.valueOf(area)};
	}

	public String getTypeId() {
		return MultiSelectionType.POINT;
	}

	public String[] getParameters() {
		return parameters;
	}

}
