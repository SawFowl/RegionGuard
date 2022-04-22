package sawfowl.regionguard.utils;

import java.util.stream.Stream;

import org.spongepowered.api.command.parameter.Parameter;
import org.spongepowered.api.command.parameter.managed.Flag;
import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import sawfowl.regionguard.RegionGuard;
import sawfowl.regionguard.api.Flags;
import sawfowl.regionguard.api.TrustTypes;

public class CommandParameters {

	public static final Parameter.Value<ServerPlayer> PLAYER = Parameter.player().optional().key("Player").build();

	public static final Parameter.Value<String> TRUST_TYPE = Parameter.choices(Stream.of(TrustTypes.values()).filter(type -> (type != TrustTypes.WITHOUT_TRUST)).map(TrustTypes::toString).toArray(String[]::new)).optional().key("TrustType").build();

	public static final Parameter.Value<String> FLAG = Parameter.choices(Stream.of(Flags.values()).map(Flags::toString).toArray(String[]::new)).optional().key("FlagValue").build();

	public static final Parameter.Value<Boolean> FLAG_VALUE = Parameter.bool().optional().key("FlagValue").build();

	public static final Parameter.Value<String> FLAG_SOURCE = Parameter.string().optional().key("Source").build();

	public static final Parameter.Value<String> FLAG_TARGET = Parameter.string().optional().key("Target").build();

	public static final Parameter.Value<String> MESSAGE = Parameter.string().optional().key("Message").build();

	public static final Parameter.Value<String> NAME = Parameter.string().optional().key("Name").build();

	public static final Parameter.Value<String> SELECTION_TYPE = Parameter.choices("Cuboid", "Flat").optional().key("Type").build();

	public static final Parameter.Value<String> CREATING_CLAIM_TYPE = Parameter.choices("Claim", "Arena", "Admin").optional().key("Type").build();

	public static final Parameter.Value<String> LOCALE = Parameter.choices(RegionGuard.getInstance().getLocales().getLocalesTags()).optional().key("Locale").build();

	public static final Flag CLEAR_FLAG = Flag.of("clear", "c");

	public static final Flag JOIN_FLAG = Flag.of("join", "j");

	public static final Flag EXIT_FLAG = Flag.of("exit", "e");

}
