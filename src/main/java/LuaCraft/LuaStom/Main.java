package LuaCraft.LuaStom;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import org.luaj.vm2.Globals;

import LuaCraft.LuaStom.Addons.AddonManager;
import LuaCraft.LuaStom.Addons.LuaStomAddon;
import net.minestom.server.MinecraftServer;

public class Main {
    static ConcurrentHashMap<String, Globals> allGlobals = new ConcurrentHashMap<>();
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
