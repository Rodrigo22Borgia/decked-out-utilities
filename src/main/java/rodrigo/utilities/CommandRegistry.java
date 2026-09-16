package rodrigo.utilities;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Vex;

import java.util.Optional;

public class CommandRegistry {
    private static boolean isAdmin(ServerPlayer player) {
        if (player == null) return false;
        return player.permissions().hasPermission(Permissions.COMMANDS_ADMIN);
    }

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
//CACHE
        dispatcher.register(
                Commands.literal("decked-out")
                        .requires(source -> isAdmin(source.getPlayer()) || source.getEntity() == null)
                        .then(Commands.literal("cache")
                                .then(Commands.literal("start")
                                        .then(Commands.argument("distance" ,IntegerArgumentType.integer(0))
                                            .then(Commands.argument("ticks" ,IntegerArgumentType.integer(0))
                                                .executes(ItemCache::start))))
                                .then(Commands.literal("stop")
                                        .executes(context1 -> ItemCache.stop()))
                                .then(Commands.literal("restore")
                                        .executes(context -> {
                                            ItemCache.restore(context.getSource().getLevel());
                                            context.getSource().sendSuccess(() -> Component.literal("Restoring current world level's items from cache."), false);
                                            return 1;
                                        }))
                                .then(Commands.literal("clear")
                                        .executes(context -> {
                                            ItemCache.clear(context.getSource().getLevel());
                                            context.getSource().sendSuccess(() -> Component.literal("Cleared current world level's cache."), false);
                                            return 1;
                                        })
                                        .then(Commands.literal("all")
                                                .executes(context -> {
                                                    ItemCache.clear();
                                                    context.getSource().sendSuccess(() -> Component.literal("Cleared entire server cache."), false);
                                                    return 1;
        })))));
//MAP
        dispatcher.register(
                Commands.literal("decked-out")
                        .then(Commands.literal("map")
                                .then(Commands.argument("ID", IntegerArgumentType.integer(0))
                                        .then(Commands.literal("clear")
                                                .executes(context -> getMap(context).clear()))
                                        .then(Commands.literal("colour")
                                                .then(Commands.argument("colourID", IntegerArgumentType.integer(0, 255))
                                                        .executes(context -> getMap(context).colourMap(context))))
                                        .then(Commands.literal("pixel")
                                                .then(Commands.argument("x", IntegerArgumentType.integer(0, 127))
                                                        .then(Commands.argument("y", IntegerArgumentType.integer(0, 127))
                                                                .then(Commands.argument("colour", IntegerArgumentType.integer(0, 255))
                                                                        .executes(context -> getMap(context).drawPixel(context))))))
                                        .then(Commands.literal("rectangle")
                                                .then(Commands.argument("x", IntegerArgumentType.integer(0, 127))
                                                            .then(Commands.argument("y", IntegerArgumentType.integer(0, 127))
                                                                .then(Commands.argument("to_x", IntegerArgumentType.integer(0, 127))
                                                                        .then(Commands.argument("to_y", IntegerArgumentType.integer(0, 127))
                                                                                .then(Commands.argument("colour", IntegerArgumentType.integer(0, 255))
                                                                        .executes(context -> getMap(context).drawRectangle(context))))))))
                                        .then(Commands.argument("type", StringArgumentType.word()).suggests((context, builder) -> builder
                                                        .suggest("embers")
                                                        .suggest("treasure")
                                                        .suggest("hazard")
                                                        .suggest("clank")
                                                        .suggest("recycles")
                                                        .suggest("cards")
                                                        .buildFuture())
                                                .then(Commands.literal("increment").executes(context -> getMap(context).increment(context)))
                                                .then(Commands.literal("decrement").executes(context -> getMap(context).decrement(context)))
                                                .then(Commands.literal("get").executes(context -> getMap(context).getValue(context)))
                                                .then(Commands.literal("set")
                                                        .then(Commands.argument("value", IntegerArgumentType.integer(0))
                                                                .executes(context -> getMap(context).set(context))))
                                        ))));
//VEX
        dispatcher.register(
                Commands.literal("decked-out")
                        .then(Commands.literal("vex")
                                .then(Commands.argument("vexes", EntityArgument.entities())
                                        .then(Commands.argument("target", EntityArgument.player())
                                                .executes(CommandRegistry::Vex))
                                        .then(Commands.literal("clear")
                                                .executes(CommandRegistry::VexClear)))));
    }

    private static Map getMap(CommandContext<CommandSourceStack> context) {
        final int ID = IntegerArgumentType.getInteger(context, "ID");
        final Optional<Map> mapOptional = DeckedOutUtilities.MAPS.stream().filter(map -> map.mapId == ID).findFirst();

        if (mapOptional.isEmpty()) {
            final Map map = new Map(ID, context.getSource().getLevel());
            DeckedOutUtilities.MAPS.add(map);
            return map;
        }
        return mapOptional.get();
    }

    private static int Vex(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return _Vex(context, EntityArgument.getPlayer(context, "target"));
    };

    private static int VexClear(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return _Vex(context, null);
    }

    private static int _Vex(CommandContext<CommandSourceStack> context, LivingEntity target) throws CommandSyntaxException {
        int vexes = 0;
        for (Entity vex : EntityArgument.getEntities(context, "vexes")) {
            if (vex instanceof Vex) {
                ((Vex) vex).setTarget(target);
                vexes++;
            }
        }
        return vexes;
    }
}
