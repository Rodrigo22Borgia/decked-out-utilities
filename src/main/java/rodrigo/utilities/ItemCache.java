package rodrigo.utilities;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.lang3.tuple.Triple;
import rodrigo.utilities.mixin.ItemAgeAccessor;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ItemCache {
    private static final HashMap<ServerLevel, LinkedList<Triple<ItemStack, Vec3, Long>>> CACHE = new HashMap<>();
    private static ScheduledExecutorService scheduler;
    public static boolean isCaching = false;
    public static int CACHING_DISTANCE = 10;
    public static int TICKS_PER_CACHE = 20;


    public static void store(ItemEntity item) {
        final ServerLevel level = (ServerLevel) item.level();
        CACHE.putIfAbsent(level, new LinkedList<>());
        CACHE.get(level).add(Triple.of(item.getItem(), item.position(), level.getGameTime()));


        item.discard();
    }

    public static void restore(ServerLevel level) {
        final LinkedList<Triple<ItemStack, Vec3, Long>> items = CACHE.remove(level);
        if (items == null) return;
        Vec3 pos;
        ItemEntity entity;
        for (Triple<ItemStack, Vec3, Long> item : items) {
            pos = item.getMiddle();
            entity = new ItemEntity(level, pos.x,pos.y,pos.z, item.getLeft());
            ((ItemAgeAccessor) entity).$setAge((int)((level.getGameTime()-item.getRight()) % 1_000_000_000));
            level.addFreshEntity(entity);
        }
    }

    public static void clear(Level level) {
        CACHE.remove(level);
    }

    public static void clear() {
        CACHE.clear();
    }
    
    public static void pop() {
        CACHE.forEach((level, items) -> {
            final Iterator<Triple<ItemStack, Vec3, Long>> iterator = items.iterator();
            Triple<ItemStack, Vec3, Long> item;
            while (iterator.hasNext()) {
                item = iterator.next();
                for (Player player : level.players()) {
                    if (player.position().distanceTo(item.getMiddle()) < (CACHING_DISTANCE - 1)) {
                        Vec3 pos = item.getMiddle();
                        ItemEntity entity = new ItemEntity(level, pos.x, pos.y, pos.z, item.getLeft());
                        ((ItemAgeAccessor) entity).$setAge((int) ((level.getGameTime() - item.getRight()) % 1_000_000_000));
                        level.addFreshEntity(entity);
                        iterator.remove();
                        break;
                    }
                }
            }
        });
    }

    public static int start(CommandContext<CommandSourceStack> context)
    {
        if (isCaching) {return 0;}
        CACHING_DISTANCE = IntegerArgumentType.getInteger(context, "distance");
        TICKS_PER_CACHE  = IntegerArgumentType.getInteger(context, "ticks");

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleWithFixedDelay(ItemCache::pop, 0, 50L*TICKS_PER_CACHE, TimeUnit.MILLISECONDS);
        isCaching = true;
        return 1;

    }
    public static int stop() {
        if (!isCaching) {return 0;}
        scheduler.shutdown();
        isCaching = false;
        return 1;
    }
}
