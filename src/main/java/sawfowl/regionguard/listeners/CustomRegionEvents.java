package sawfowl.regionguard.listeners;

import java.util.Optional;

import org.spongepowered.api.entity.living.player.server.ServerPlayer;
import org.spongepowered.api.event.Cause;
import org.spongepowered.api.event.impl.AbstractEvent;
import org.spongepowered.math.vector.Vector3i;

import net.kyori.adventure.text.Component;

import sawfowl.regionguard.api.data.Region;
import sawfowl.regionguard.api.events.RegionCreateEvent;
import sawfowl.regionguard.api.events.RegionResizeEvent;

class CustomRegionEvents {

	class RegionCreate extends AbstractEvent implements RegionCreateEvent {

		RegionCreate(Cause cause, ServerPlayer serverPlayer, Region region) {
			this.cause = cause;
			this.serverPlayer = serverPlayer;
			this.region = region;
		}

		private Cause cause;
		private ServerPlayer serverPlayer;
		private Region region;
		private boolean cancel = false;
		private Component message;

		@Override
		public Cause cause() {
			return cause;
		}

		@Override
		public boolean isCancelled() {
		return cancel;
		}

		@Override
		public void setCancelled(boolean cancel) {
			this.cancel = cancel;
		}

		@Override
		public ServerPlayer getPlayer() {
			return serverPlayer;
		}

		@Override
		public Region getRegion() {
			return region;
		}

		@Override
		public Optional<Region> getParrent() {
			return region.getParrent();
		}

		@Override
		public void setMessage(Component component) {
			message = component;
		
		}

		@Override
		public Optional<Component> getMessage() {
			return Optional.ofNullable(message);
		}

		@Override
		public boolean isSubdivision() {
			return getParrent().isPresent();
		}
	}

	class RegionResizeEventClass extends AbstractEvent implements RegionResizeEvent {

		RegionResizeEventClass(Cause cause, ServerPlayer serverPlayer, Region region, Vector3i newCorner, Vector3i oppositeCorner, Component message) {
			this.cause = cause;
			this.serverPlayer = serverPlayer;
			this.region = region;
			this.newCorner = newCorner;
			this.oppositeCorner = oppositeCorner;
			this.message = message;
		}

		private Cause cause;
		private ServerPlayer serverPlayer;
		private Region region;
		private boolean cancel = false;
		private Component message;
		private Vector3i newCorner;
		private Vector3i oppositeCorner;

		@Override
		public Cause cause() {
			return cause;
		}

		@Override
		public boolean isCancelled() {
			return cancel;
		}

		@Override
		public void setCancelled(boolean cancel) {
			this.cancel = cancel;
		}

		@Override
		public ServerPlayer getPlayer() {
			return serverPlayer;
		}

		@Override
		public Region getRegion() {
			return region;
		}

		@Override
		public void setMessage(Component component) {
			message = component;
		
		}

		@Override
		public Optional<Component> getMessage() {
			return Optional.ofNullable(message);
		}

		@Override
		public Optional<Vector3i> getNewCorner() {
			return Optional.ofNullable(newCorner);
		}

		@Override
		public Vector3i getOppositeCorner() {
			return oppositeCorner;
		}

	}

}
