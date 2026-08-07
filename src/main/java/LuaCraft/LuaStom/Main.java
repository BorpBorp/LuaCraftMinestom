package LuaCraft.LuaStom;

import java.io.File;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.luaj.vm2.Globals;

import LuaCraft.LuaStom.Addons.AddonManager;
import LuaCraft.LuaStom.Addons.LuaStomAddon;
import LuaCraft.LuaStom.sandbox.command.CommandLib;
import net.minestom.server.MinecraftServer;

public class Main {
    static ConcurrentHashMap<String, Globals> allGlobals = new ConcurrentHashMap<>();
    static final ConcurrentHashMap<String, List<CommandLib>> scriptCommands = new ConcurrentHashMap<>();
    public static String version = "1.0.0-ALPHA";

    public static void main(String[] args) {
        new ScriptGeneration();

        addonHandle();

        ScriptHandler.loadAllScripts(allGlobals, true);

        MinecraftServer.getCommandManager().register(new LuaCommand(allGlobals, ScriptHandler.getScriptsFolder()));
    }

    public static ConcurrentHashMap<String, Globals> getAllGlobals() {
        return allGlobals;
    }

    public static ConcurrentHashMap<String, List<CommandLib>> getScriptCommands() {
        return scriptCommands;
    }

    private static void addonHandle() {
        try {
            AddonManager.loadAll(new File("addons"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (LuaStomAddon addon : AddonManager.getAddons()) {
                addon.onDisable();
            }
        }));
    }
}
