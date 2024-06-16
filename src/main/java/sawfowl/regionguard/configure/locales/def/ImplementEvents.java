package sawfowl.regionguard.configure.locales.def;

import org.spongepowered.configurate.objectmapping.ConfigSerializable;
import org.spongepowered.configurate.objectmapping.meta.Setting;

import sawfowl.regionguard.configure.locales.abstractlocale.Events;
import sawfowl.regionguard.configure.locales.def.events.ImplementRegionEvents;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementBlock;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementEntity;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementFly;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementItem;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementKeep;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementMove;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementPiston;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementTeleport;
import sawfowl.regionguard.configure.locales.def.events.world.ImplementCommand;

@ConfigSerializable
public class ImplementEvents implements Events {

	public ImplementEvents() {}

	@Setting("Fly")
	private ImplementFly fly = new ImplementFly();
	@Setting("Teleport")
	private ImplementTeleport teleport = new ImplementTeleport();
	@Setting("Region")
	private ImplementRegionEvents regionEvents = new ImplementRegionEvents();
	@Setting("Block")
	private ImplementBlock block = new ImplementBlock();
	@Setting("Entity")
	private ImplementEntity entity = new ImplementEntity();
	@Setting("Keep")
	private ImplementKeep Keep = new ImplementKeep();
	@Setting("Piston")
	private ImplementPiston piston = new ImplementPiston();
	@Setting("Command")
	private ImplementCommand command = new ImplementCommand();
	@Setting("Item")
	private ImplementItem item = new ImplementItem();
	@Setting("Move")
	private ImplementMove move = new ImplementMove();

	@Override
	public Fly getFly() {
		return fly;
	}

	@Override
	public Teleport getTeleport() {
		return teleport;
	}

	@Override
	public RegionEvents getRegion() {
		return regionEvents;
	}

	@Override
	public Block getBlock() {
		return block;
	}

	@Override
	public Entity getEntity() {
		return entity;
	}

	@Override
	public Keep getKeep() {
		return Keep;
	}

	@Override
	public Piston getPiston() {
		return piston;
	}

	@Override
	public Command getCommand() {
		return command;
	}

	@Override
	public Item getItem() {
		return item;
	}

	@Override
	public Move getMove() {
		return move;
	}

}
