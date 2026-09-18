package rodrigo.utilities.mixin;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import rodrigo.utilities.DeckedOutUtilities;

@Mixin(ServerPlayer.class)
public class GamemodeMixin {

	@Inject(at = @At("RETURN"), method = "setGameMode")
	private void init(GameType gameType, CallbackInfoReturnable<Boolean> cir) {
		if (cir.getReturnValue()) {
			ServerPlayer player = (ServerPlayer) (Object) this;
			MinecraftServer server = player.level().getServer();

			server.getFunctions().execute(server.getFunctions().get(DeckedOutUtilities.ANTICHEAT).get(), player.createCommandSourceStack());
		}
	}
}

