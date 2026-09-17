package rodrigo.utilities;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.maps.MapId;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Map {
    private static final byte[][] emberIcons    = {
            {126, 124, 124, 124, 124, 34, 125, 125, 125, 124, 34, 125, 125, 125, 124, 34, 125, 125, 125, 124, 34, 33, 33, 33, 124},
            {-30, -31, -31, -31, -31, 126, 125, 125, 125, -31, 126, 125, 125, 125, -31, 126, 125, 125, 125, -31, 126, 126, 126, 126, -31},
            {-30, -35, -35, -35, -35, 126, -31, -31, -31, -35, 126, -31, -31, -31, -35, 126, -31, -31, -31, -35, 126, 124, 124, 124, -34},
            {-34, -36, -36, -36, -36, -30, -32, -32, -32, -36, -30, -32, -32, -32, -36, -30, -32, -32, -32, -36, -30, -31, -31, -31, -36}};
    private static final byte[][] treasureIcons = {
            {60, 61, 61, 61, 61, 56, 121, 121, 121, 62, 56, 121, 121, 121, 62, 32, 121, 121, 121, 62, 32, 57, 57, 57, 62},
            {62, 61, 61, 61, 61, 121, 62, 62, 62, 61, 121, 62, 62, 62, 61, 121, 62, 62, 62, 61, 121, 121, 121, 121, 62},
            {113, 114, 114, 114, 114, 62, 60, 60, 60, 114, 62, 60, 60, 60, 114, 62, 60, 60, 60, 114, 62, 62, 62, 62, 113},
            {112, -114, -114, -114, -114, 114, 113, 113, 113, -114, 114, 113, 113, 113, -114, 114, 113, 113, 113, -114, 114, 114, 114, 114, -115}};
    private static final byte[][] hazardIcons   = {
            {-114, -115, -115, -115, -115, 18, 113, 113, 113, -114, 18, 113, 113, 113, -114, 18, 113, 113, 113, -114, 18, 17, 17, 17, -114},
            {-114, -116, -116, -116, -116, 114, -115, -115, -115, -116, 114, -115, -115, -115, -116, 114, -115, -115, -115, -116, 114, 113, 113, 113, -116},
            {-38, -40, -40, -40, -40, -114, -116, -116, -116, -40, -114, -116, -116, -116, -40, -114, -116, -116, -116, -40, -114, -115, -115, -115, -40},
            {118, 116, 116, 116, 116, -38, 118, 118, 118, 116, -38, 118, 118, 118, 116, -38, 118, 118, 118, 116, -38, -40, -40, -40, 116}};
    private static final byte[][] clankIcons    = {
            {-127, -127, -127, -127, -127, 32, 21, 21, 21, -127, 32, 21, 21, 21, -127, 32, 21, 21, 21, -127, 32, 33, 33, 33, -127},
            {-126, -128, -128, -128, -128, -126, -126, -126, -126, -128, -126, -126, -126, -126, -128, -126, -126, -126, -126, -128, -126, -127, -127, -127, -128},
            {102, 101, 101, 101, 101, -126, -128, -128, -128, 101, -126, -128, -128, -128, 101, -126, -128, -128, -128, 101, -126, -126, -126, -126, 102},
            {102, 100, 100, 100, 100, -126, 101, 101, 101, 100, -126, 101, 101, 101, 100, -126, 101, 101, 101, 100, -126, -128, -128, -128, 101}};
    private static final byte[] cardIcon = {32, 33, 33, 32, 33, 33};
    private static final byte[] emberPos    = {30,34};
    private static final byte[] treasurePos = {30,57};
    private static final byte[] hazardPos   = {30,80};
    private static final byte[] clankPos    = {30,103};
    private static final byte[] recyclePos  = {20,116};
    private static final byte[] cardPos     = {62,115};

    private byte embers    = 0;
    private byte treasure  = 0;
    private byte hazardB   = 0;
    private byte hazard   = 0;
    private byte clankB    = 0;
    private byte clank    = 0;
    private byte recycles  = 0;
    private byte cards     = 0;

    public final int mapId;
    public final MapItemSavedData mapData;
    public byte fillColour = 84;

    public Map(int mapId, ServerLevel level) {
        this.mapId = mapId;
        this.mapData = level.getMapData(new MapId(mapId));
    }

    public int drawPixel(CommandContext<CommandSourceStack> context) {
        mapData.setColor(
                IntegerArgumentType.getInteger(context, "x"),
                IntegerArgumentType.getInteger(context, "y"),
         (byte) IntegerArgumentType.getInteger(context, "colour")
        );
        return 1;
    }

    public int drawRectangle(CommandContext<CommandSourceStack> context) {
        fill(
            IntegerArgumentType.getInteger(context, "x"),
            IntegerArgumentType.getInteger(context, "y"),
            IntegerArgumentType.getInteger(context, "width"),
            IntegerArgumentType.getInteger(context, "height"),
            (byte) IntegerArgumentType.getInteger(context, "colour")
        ); return 1;
    }

    public void printIcon(int x, int y,int width, int height, byte[] colours) {
        int ix = 0;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                mapData.setColor(i,j, colours[ix++]);
            }
        }
    }

    public void fill(int x, int y, int width, int height, byte colour) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                mapData.setColor(i,j, colour);
            }
        }
    }

    private void interpolate(int x, int y,int width, int height, byte[] colours) {
        int idx = -1;
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                idx++;
                if (j % 2 == i % 2) {
                    continue;
                }
                mapData.setColor(i,j, colours[idx]);
            }
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            int _idx = -1;
            for (int i = x; i < x + width; i++) {
                for (int j = y; j < y + height; j++) {
                    _idx++;
                    if (j % 2 != i % 2) {
                        continue;
                    }
                    mapData.setColor(i,j, colours[_idx]);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

    private void interpolate(int x, int y,int width, int height, byte colour) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (j % 2 == i % 2) {
                    continue;
                }
                mapData.setColor(i,j, colour);
            }
        }

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.schedule(() -> {
            for (int i = x; i < x + width; i++) {
                for (int j = y; j < y + height; j++) {
                    if (j % 2 != i % 2) {
                        continue;
                    }
                    mapData.setColor(i,j, colour);
                }
            }
        }, 500, TimeUnit.MILLISECONDS);
        scheduler.shutdown();
    }

    private void interpolate(int x, int y, int value, byte[][] icon, boolean decrement) {
        int level = value / 15;
        if (decrement) {
            level--;
        }

        if (level < 0) {
            interpolate(x + (value % 15) * 6, y, 5, 5, fillColour);
        } else {
            interpolate(x + (value % 15) * 6, y, 5, 5, icon[level]);
        }
    }

    public int getValue(CommandContext<CommandSourceStack> context) {
        int value = switch (StringArgumentType.getString(context, "type")) {
            case "embers" -> embers;
            case "treasure" -> treasure;
            case "hazard_block" -> hazardB;
            case "clank_block" -> clankB;
            case "recycles" -> recycles;
            case "cards" -> cards;
            case "clank" -> clank;
            case "hazard" -> hazard;

            default -> 0;
        };
        context.getSource().sendSuccess(() -> Component.literal("" + value), false);
        return value;
    }

    public int increment(CommandContext<CommandSourceStack> context) {
        switch (StringArgumentType.getString(context, "type")) {
            case "embers" -> {
                if (embers < 60) {
                    interpolate(emberPos[0], emberPos[1], embers++, emberIcons, false);
                } return embers;}
            case "treasure" -> {
                if (treasure < 60) {
                    interpolate(treasurePos[0], treasurePos[1], treasure++, treasureIcons, false);
                } return treasure;}
            case "hazard_block" -> {
                if (hazardB < 60) {
                    interpolate(hazardPos[0], hazardPos[1], hazardB++, hazardIcons, false);
                } return hazardB;}
            case "clank_block" -> {
                if (clankB < 60) {
                    interpolate(clankPos[0], clankPos[1], clankB++, clankIcons, false);
                } return clankB;}
            case "recycles" -> {
                if (recycles < 3) {
                    recycles++;
                    updateRecycle();
                } return recycles;}
            case "cards" -> {
                if (cards < 40) {
                    interpolate(cardPos[0] + (cards / 2) * 3, cardPos[1] + (cards % 2) * 5, 2, 3, cardIcon);
                    cards ++;
                } return cards;}
            case "clank" -> {if (clankB > 0) {decrement("clank_block");} else {clank++;} return clank;}
            case "hazard" -> {if (hazardB > 0) {decrement("hazard_block");} else {hazard++;} return hazard;}
        }
        return 0;
    }

    public int decrement(CommandContext<CommandSourceStack> context) {
        return decrement(StringArgumentType.getString(context, "type"));
    }

    private int decrement(String type) {
        switch (type) {
            case "embers" -> {
                if (embers > 0) {
                    interpolate(emberPos[0], emberPos[1], --embers, emberIcons, true);
                } return embers;}
            case "treasure" -> {
                if (treasure > 0) {
                    interpolate(treasurePos[0], treasurePos[1], --treasure, treasureIcons, true);
                } return treasure;}
            case "hazard_block" -> {
                if (hazardB > 0) {
                    interpolate(hazardPos[0], hazardPos[1], --hazardB, hazardIcons, true);
                } return hazardB;}
            case "clank_block" -> {
                if (clankB > 0) {
                    interpolate(clankPos[0], clankPos[1], --clankB, clankIcons, true);
                } return clankB;}
            case "recycles" -> {
                if (recycles > 0) {
                    recycles --;
                    updateRecycle();
                } return recycles;}
            case "cards" -> {
                if (cards > 0) {
                    cards --;
                    interpolate(cardPos[0] + (cards / 2) * 3, cardPos[1] + (cards % 2) * 5, 2, 3, fillColour);
                } return cards;}
            case "clank" -> {clank--; return clank;}
            case "hazard" -> {hazard--; return hazard;}
        }
        return 0;
    }

    public int set(CommandContext<CommandSourceStack> context) {
        byte value = (byte) IntegerArgumentType.getInteger(context, "value");
        switch (StringArgumentType.getString(context, "type")) {
            case "embers" -> { if (value <= 60) {
                embers = value; update(emberPos, emberIcons, embers); return 1;} return 0;}
            case "treasure" -> { if (value <= 60) {
                treasure = value; update(treasurePos, treasureIcons, treasure); return 1;} return 0;}
            case "hazard_block" -> { if (value <= 60) {
                hazardB = value; update(hazardPos, hazardIcons, hazardB); return 1;} return 0;}
            case "clank_block" -> { if (value <= 60) {
                clankB = value; update(clankPos, clankIcons, clankB); return 1;} return 0;}
            case "recycles" -> { if (value <= 3) {
                recycles = value; updateRecycle(); return 1;} return 0;}
            case "cards" -> { if (value <= 40) {
                cards = value; updateCards(); return 1;} return 0;}
            case "clank" -> {clank = value; return 1;}
            case "hazard" -> {hazard = value; return 1;}
            default -> {return 0;}
        }
    }

    public int clear() {
        byte[] pos = emberPos.clone();
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 15; j++) {
                fill(pos[0], pos[1], 5, 5, fillColour);
                pos[0] += 6;
            }
            pos[0] = emberPos[0];
            pos[1] += 23;
        }

        pos = cardPos.clone();
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < 20; j++) {
                fill(pos[0], pos[1], 2, 3, fillColour);
                pos[0] += 3;
            }
            pos[0] = cardPos[0];
            pos[1] += 5;
        }

        embers = 0;
        treasure = 0;
        hazardB = 0;
        clankB = 0;
        recycles = 0;
        cards = 0;

        updateRecycle();

        return 1;
    }

    private void update(byte[] _pos, byte[][] iconSet, byte value) {
        final byte[] pos = _pos.clone();
        final int level = (value)/15;
        byte[] icon = iconSet[Math.min(level, 3)];
        int i = 0;


        while (i < value % 15) {
            printIcon(pos[0], pos[1], 5, 5, icon);
            pos[0] += 6;
            i++;
        }

        if (level == 0) {
            while (i < 15) {
                fill(pos[0], pos[1], 5, 5, fillColour);
                pos[0] += 6;
                i++;
            }
        } else {
            icon = iconSet[level-1];
            while (i < 15) {
                printIcon(pos[0], pos[1], 5, 5, icon);
                pos[0] += 6;
                i++;
            }
        }
    }

    private void updateRecycle() {
        final byte[] pos = recyclePos.clone();
        int i = 0;

        while (i < recycles) {
            replaceIn(pos[0], pos[1], 3, 6, (byte) 113, (byte) 132);
            pos[0] += 5;
            i++;
        }
        while (i < 3) {
            replaceIn(pos[0], pos[1], 3, 6, (byte) 132, (byte) 113);
            pos[0] += 5;
            i++;
        }
    };

    private void updateCards() {
        final byte[] pos = cardPos.clone();
        int i = 0;

        while (i < cards) {
            printIcon(pos[0] + (i/2) * 3, pos[1] + (i%2) * 5, 2, 3, cardIcon);
            i++;
        }
        while (i < 40) {
            fill(pos[0] + (i/2) * 3, pos[1] + (i%2) * 5, 2, 3, fillColour);
            i++;
        }
    }

    public int colourMap(CommandContext<CommandSourceStack> context) {
        byte colour_new = (byte) IntegerArgumentType.getInteger(context,"colourID");

        for (int i = 0; i < mapData.colors.length; i++) {
            if (mapData.colors[i] == 0) {mapData.setColor(i % 128, i / 128, colour_new);}
        }

        return 1;
    }

    public int replaceIn(CommandContext<CommandSourceStack> context) {
        replaceIn(
          (byte)IntegerArgumentType.getInteger(context, "x"),
          (byte)IntegerArgumentType.getInteger(context, "y"),
                IntegerArgumentType.getInteger(context, "width"),
                IntegerArgumentType.getInteger(context, "height"),
          (byte)IntegerArgumentType.getInteger(context, "colour"),
          (byte)IntegerArgumentType.getInteger(context, "new_colour")
        );
        return 1;
    }

    public void replaceIn(byte x, byte y, int width, int height, byte colour, byte colour_new) {
        for (int i = x; i < x + width; i++) {
            for (int j = y; j < y + height; j++) {
                if (mapData.colors[i + j * 128] == colour) {mapData.setColor(i, j, colour_new);}
            }
        }
    }

    public int getColour(CommandContext<CommandSourceStack> context) {
        int x = IntegerArgumentType.getInteger(context, "x");
        int y = IntegerArgumentType.getInteger(context, "y");
        int colour = mapData.colors[x + y * 128];

        context.getSource().sendSuccess(() -> Component.literal("" + (colour < 0 ? 256 + colour : colour)), false);
        return colour;
    }

    public static int copy(CommandContext<CommandSourceStack> context) {
        ServerLevel level = context.getSource().getLevel();
        MapItemSavedData to = level.getMapData(new MapId(IntegerArgumentType.getInteger(context, "to")));
        MapItemSavedData from = level.getMapData(new MapId(IntegerArgumentType.getInteger(context, "ID")));

        try {
            for (int i = 0; i < 128; i++) {
                for (int j = 0; j < 128; j++) {
                    to.setColor(i,j, from.colors[i + j * 128]);
                }
            }

        } catch (NullPointerException e) {
            context.getSource().sendFailure(Component.literal("Map not found in the current dimension"));
            return 0;
        }

        return 1;
    }
}