package LuaCraft.LuaStom;

import java.util.concurrent.ConcurrentHashMap;

import org.luaj.vm2.Globals;

import net.minestom.server.MinecraftServer;

public class Main {
    static ConcurrentHashMap<String, Globals> allGlobals = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        new ScriptGeneration();
        ScriptHandler.loadAllScripts(allGlobals, true);

        MinecraftServer.getCommandManager().register(new LuaCommand(allGlobals, ScriptHandler.getScriptsFolder()));
    }

    public static ConcurrentHashMap<String, Globals> getAllGlobals() {
        return allGlobals;
    }
}
