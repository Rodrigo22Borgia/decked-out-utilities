package rodrigo.utilities.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import rodrigo.utilities.DeckedOutUtilities;
import rodrigo.utilities.ItemCache;

@Mixin(ItemEntity.class)
public class ItemEntityMixin {
	@Inject(at = @At("HEAD"), method = "tick", cancellable = true)
	private void init(CallbackInfo ci) {
		ItemEntity item = (ItemEntity) (Object) this;
		if (!(ItemCache.isCaching && item.level() instanceof ServerLevel && item.getAge() % ItemCache.TICKS_PER_CACHE == 0)) {return;}

		if (item.level().players().stream().noneMatch(player -> player.position().distanceTo(item.position()) < ItemCache.CACHING_DISTANCE)) {
			ItemCache.store(item);
			ci.cancel();
		}
	}
}

