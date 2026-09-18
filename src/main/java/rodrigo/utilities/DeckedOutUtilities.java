package rodrigo.utilities;

import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Random;

public class DeckedOutUtilities implements ModInitializer {
	public static final String MOD_ID = "decked-out-utilities";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final Identifier ANTICHEAT = Identifier.fromNamespaceAndPath("dom","adv_logic/debug/on_gamemode_switch");
	public static final ArrayList<Map> MAPS = new ArrayList<>();
	public static final Random RNG = new Random();

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
