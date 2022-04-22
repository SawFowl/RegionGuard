package sawfowl.regionguard.utils.worldedit.cuievents;

import sawfowl.regionguard.utils.worldedit.MultiSelectionType;

public class MultiSelectionColorEvent implements CUIEvent {

	private final String edgeColor;
	private final String gridColor;
	private final String p1Color;
	private final String p2Color;
	private final String[] parameters;

	public MultiSelectionColorEvent(String edgeColor, String gridColor, String p1Color, String p2Color) {
		this.edgeColor = edgeColor;
		this.gridColor = gridColor;
		this.p1Color = p1Color;
		this.p2Color = p2Color;
		this.parameters = new String[] {
				this.edgeColor,
				this.gridColor,
				this.p1Color,
				this.p2Color
				};
	}

	public String getTypeId() {
		return MultiSelectionType.COLOR;
	}

	public String[] getParameters() {
		return this.parameters;
	}

}
