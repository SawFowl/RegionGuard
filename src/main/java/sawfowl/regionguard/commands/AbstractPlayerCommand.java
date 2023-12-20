package sawfowl.regionguard.commands;

import sawfowl.commandpack.api.commands.raw.RawPlayerCommand;
import sawfowl.regionguard.RegionGuard;

public abstract class AbstractPlayerCommand extends AbstractCommand implements RawPlayerCommand {

	public AbstractPlayerCommand(RegionGuard regionGuard) {
		super(regionGuard);
	}

}
