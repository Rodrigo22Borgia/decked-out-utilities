package rodrigo.utilities;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.resources.Identifier;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.monster.Vex;
import net.minecraft.world.entity.monster.illager.Evoker;
import net.minecraft.world.level.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class DeckedOutUtilities implements ModInitializer {
	public static final String MOD_ID = "decked-out-utilities";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final ArrayList<Map> MAPS = new ArrayList<>();

	@Override
	public void onInitialize() {
		CommandRegistrationCallback.EVENT.register(((commandDispatcher, commandBuildContext, commandSelection) -> {
			CommandRegistry.register(commandDispatcher);
		}));
		LOGGER.info("Initialized");
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
