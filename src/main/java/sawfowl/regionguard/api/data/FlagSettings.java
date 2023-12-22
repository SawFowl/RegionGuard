package sawfowl.regionguard.api.data;

import java.util.stream.Stream;

public interface FlagSettings {

	boolean isAllowArgs();

	Stream<String> getSources();

	Stream<String> getTargets();

	default boolean isAllowSource(String source) {
		return isAllowArgs() && (source.equals("all") || (getSources() != null && getSources().filter(id -> id.equals(source)).findFirst().isPresent()));
	}

	default boolean isAllowTarget(String target) {
		return isAllowArgs() && (target.equals("all") || (getTargets() != null && getTargets().filter(id -> id.equals(target)).findFirst().isPresent()));
	}

}
