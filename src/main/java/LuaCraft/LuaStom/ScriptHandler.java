package LuaCraft.LuaStom;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.jse.JsePlatform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import LuaCraft.LuaStom.globals.GlobalFunctions;
import LuaCraft.LuaStom.sandbox.Enumerations;
import LuaCraft.LuaStom.sandbox.LuaLogger;
import LuaCraft.LuaStom.sandbox.component.LuaComponent;
import LuaCraft.LuaStom.sandbox.entities.EntityLib;
import LuaCraft.LuaStom.sandbox.entities.ItemLib;
import LuaCraft.LuaStom.sandbox.events.ServerEvent;
import LuaCraft.LuaStom.sandbox.instance.BiomeBuilderLib;
import LuaCraft.LuaStom.sandbox.instance.BlockLib;
import LuaCraft.LuaStom.sandbox.instance.FastSimplexBuilderLib;
import LuaCraft.LuaStom.sandbox.instance.InstanceManagerLib;
import LuaCraft.LuaStom.sandbox.instance.StructureLib;
import LuaCraft.LuaStom.sandbox.inventory.InventoryLib;
import LuaCraft.LuaStom.sandbox.inventory.ItemStackLib;
import LuaCraft.LuaStom.sandbox.os.LuaOs;
import LuaCraft.LuaStom.sandbox.position.PointLib;
import LuaCraft.LuaStom.sandbox.position.PositionLib;
import LuaCraft.LuaStom.sandbox.server.ServerLib;
import LuaCraft.LuaStom.sandbox.tag.TagLib;
import LuaCraft.LuaStom.sandbox.thread.NextTick;
import LuaCraft.LuaStom.sandbox.thread.RunAsync;
import LuaCraft.LuaStom.sandbox.thread.RunSync;
import LuaCraft.LuaStom.sandbox.thread.RunSyncWithLock;
import LuaCraft.LuaStom.sandbox.thread.Timer;

public class ScriptHandler {
    private static final Logger logger = LoggerFactory.getLogger("LuaCraft ScriptHandler");

    public record FileData(String fileName, String fileContents) {
    }

    private static File scriptsFolder;

    public static void setScriptsFolder(File folder) {
        scriptsFolder = folder;
    }

    public static File getScriptsFolder() {
        return scriptsFolder;
    }

    private static FileData readScriptFile(File file) throws IOException {
        String fileContents = null;
        Path filePath;
        String fileName;

        filePath = file.toPath();
        fileName = file.getName();

        fileContents = Files.readString(filePath);

        return new FileData(fileName, fileContents);
    }

    public static void setupScriptGlobals(Globals globals, String fileName) {
        // Restrict these from the user as they can be dangerous and cause unwanted side
        // effects
        globals.set("os", new LuaOs());
        globals.set("io", LuaValue.NIL);
        globals.set("debug", LuaValue.NIL);
        globals.set("_G", LuaValue.NIL);
        globals.set("luajava", LuaValue.NIL);
        // Destroy the loaded extras LuaJ might insert into LuaCraft to prevent
        // malicious code further
        LuaValue loaded = globals.get("package").get("loaded");
        loaded.set("os", new LuaOs());
        loaded.set("io", LuaValue.NIL);
        loaded.set("debug", LuaValue.NIL);
        loaded.set("luajava", LuaValue.NIL);

        // Add Global Functions
        globals.set("FindMetaTable", GlobalFunctions.findMetaTable());

        // Add Enums into the global scope
        globals.set("Items", Enumerations.ItemEnums());
        globals.set("Blocks", Enumerations.BlockEnums());
        globals.set("LogLevel", Enumerations.LogEnums());
        globals.set("Tags", Enumerations.TagEnums());
        globals.set("Entities", Enumerations.EntityEnums());
        globals.set("Guis", Enumerations.GuiEnums());

        // Add libraries into the default scope that do not need another object to be
        // accessed
        globals.set("Instance", new InstanceManagerLib());
        globals.set("ServerEvent", new ServerEvent());
        globals.set("Position", PositionLib.positionFactory());
        globals.set("ItemStack", ItemStackLib.creator());
        globals.set("ItemEntity", ItemLib.creator());
        globals.set("Entity", EntityLib.creator());
        globals.set("Logger", new LuaLogger());
        globals.set("Component", new LuaComponent());
        globals.set("FastSimplex", FastSimplexBuilderLib.creator());
        globals.set("Point", PointLib.creator());
        globals.set("Server", new ServerLib());
        globals.set("Timer", new Timer());
        globals.set("Structure", StructureLib.creator());
        globals.set("Biome", BiomeBuilderLib.creator());
        globals.set("Block", BlockLib.creator());
        globals.set("Gui", InventoryLib.creator());
        globals.set("Tag", TagLib.creator());

        // Add thread related functions
        globals.set("NextTick", new NextTick());
        globals.set("Sync", new RunSync());
        globals.set("SyncWithLock", new RunSyncWithLock());
        globals.set("Async", new RunAsync());

        // Force them to stick within the scripts folder for requiring for safety
        // reasons
        LuaValue pkg = globals.get("package");
        String scriptsPath = scriptsFolder.getAbsolutePath() + java.io.File.separator + "?.lua";
        pkg.set("path", LuaValue.valueOf(scriptsPath));
        pkg.set("cpath", LuaValue.NIL);
    }

    public static void loadAllScripts(ConcurrentHashMap<String, Globals> allGlobals, Boolean firstLoad) {
        List<File> allScriptFiles = collectScripts(scriptsFolder);

        if (allScriptFiles == null) {
            logger.error("Failed to collect all Lua script files, please contact author");
            return;
        }

        allScriptFiles.sort((a, b) -> Integer.compare(getFilePriority(b), getFilePriority(a)));

        Globals sharedGlobals = JsePlatform.standardGlobals();
        setupScriptGlobals(sharedGlobals, "shared");

        for (File file : allScriptFiles) {
            int priority = getFilePriority(file);

            Globals globals = JsePlatform.standardGlobals();
            setupScriptGlobals(globals, file.getName());

            LuaValue key = LuaValue.NIL;
            while (true) {
                Varargs entry = sharedGlobals.next(key);
                if (entry.arg1().isnil())
                    break;
                key = entry.arg1();

                if (key.tojstring().equals("ServerEvent"))
                    continue;

                globals.set(key, entry.arg(2));
            }

            FileData data = null;
            try {
                data = readScriptFile(file);
            } catch (IOException e) {}

            if (data == null || data.fileContents() == null) {
                logger.error("Failed to read contents of " + file.getName() + " please contact author");
                continue;
            }

            try {
                LuaValue loadedScript = globals.load(data.fileContents(), file.getName());

                if (loadedScript != null) {
                    loadedScript.call();
                    logger.info("Executed Lua file: " + file.getName() + " (Priority: " + priority + ")");

                    if (priority > -1) {
                        LuaValue envKey = LuaValue.NIL;
                        while (true) {
                            Varargs entry = globals.next(envKey);
                            if (entry.arg1().isnil())
                                break;
                            envKey = entry.arg1();

                            if (sharedGlobals.get(envKey).isnil()) {
                                sharedGlobals.set(envKey, entry.arg(2));
                            }
                        }
                    }
                }
            } catch (LuaError e) {
                logger.error(e.getMessage());
            }

            allGlobals.put(file.getName(), globals);
        }

        if (firstLoad) {
            for (Globals globals : allGlobals.values()) {
                LuaValue serverEvent = globals.get("ServerEvent");
                LuaValue function = serverEvent.get("OnServerStart");
                if (!function.isnil() && function.isfunction()) {
                    function.call();
                }
            }

            LuaCraft.LuaStom.sandbox.events.EventHandler eventHandler = new LuaCraft.LuaStom.sandbox.events.EventHandler();
            eventHandler.initNodes();
            eventHandler.initListeners(allGlobals);
        }
    }

    public static void loadSingleScript(Map<String, Globals> allGlobals, String fileName) {
        if (fileName.startsWith("-"))
            return;

        File file = new File(scriptsFolder, fileName);
        Path filePath = file.toPath();

        try {
            String fileContents = Files.readString(filePath);

            Globals globals = JsePlatform.standardGlobals();
            setupScriptGlobals(globals, fileName);

            LuaValue loadedScript = globals.load(fileContents, fileName);

            if (loadedScript != null) {
                try {
                    loadedScript.call();
                    logger.info("Executed Lua file named" + file.getName());
                } catch (LuaError e) {
                    throw new LuaError(e.getMessage());
                }
                allGlobals.put(file.getName(), globals);
            }
        } catch (IOException e) {
        }
    }

    private static List<File> collectScripts(File folder) {
        List<File> result = new ArrayList<>();
        File[] files = folder.listFiles();
        if (files == null)
            return result;

        for (File file : files) {
            if (file.isDirectory()) {
                result.addAll(collectScripts(file));
            } else if (!file.getName().startsWith("-") && file.getName().endsWith(".lua")) {
                result.add(file);
            }
        }
        return result;
    }

    private static int getFilePriority(File file) {
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (!lines.isEmpty() && lines.get(0).startsWith("-- @Priority")) {
                return Integer.parseInt(lines.get(0).replace("-- @Priority", "").trim());
            }
        } catch (Exception e) {
        }
        return 0; // default priority
    }
}
