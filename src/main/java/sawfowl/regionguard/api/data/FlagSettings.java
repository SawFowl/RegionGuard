package sawfowl.regionguard.api.data;

import java.util.stream.Stream;

public interface FlagSettings {

	/**
	 * Determines whether the flag provides the event's cause and target arguments to `/rg flag` command.<br>
	 * If false, the arguments will not be available.
	 */
	boolean isAllowArgs();

	/**
	 * Valid event initiators.<br>
	 * Used in the `/rg flag' command.
	 */
	Stream<String> getSources();

	/**
	 * Allowable event targets.<br>
	 * Used in the `/rg flag' command.
	 */
	Stream<String> getTargets();

}
