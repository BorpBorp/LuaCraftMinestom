package LuaCraft.LuaStom;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.luaj.vm2.Globals;

import LuaCraft.LuaStom.Addons.AddonManager;
import LuaCraft.LuaStom.Addons.LuaStomAddon;
import LuaCraft.LuaStom.sandbox.component.ComponentUtil;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.ArgumentType;
import net.minestom.server.command.builder.suggestion.SuggestionEntry;
import net.minestom.server.timer.ExecutionType;

public class LuaCommand extends Command {
    public LuaCommand(ConcurrentHashMap<String, Globals> allGlobals, File scriptsFolder) {
        super("lua", "luacraft");

        setDefaultExecutor((sender, context) -> {
            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] Use &6/lua help &rfor more information"));
        });

        addSyntax((sender, context) -> {
            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] Welcome to LuaStom"));
            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] &6/lua reload [filename] &rto reload a script file"));
            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] &6/lua reloadall &rto reload all script files"));
            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] &6/lua info &rto see information about this version of LuaStom"));
        }, ArgumentType.Literal("help"));

        var scriptName = ArgumentType.String("filename");

        scriptName.setSuggestionCallback((sender, context, suggestion) -> {
            List<String> completions = new ArrayList<>();
            collectCompletions(scriptsFolder, scriptsFolder, completions);
            completions.forEach(name -> {
                suggestion.addEntry(new SuggestionEntry(name));
            });
        });

        addSyntax((sender, context) -> {
            String fileName = context.get(scriptName);
            MinecraftServer.getSchedulerManager().buildTask(() -> {
                ScriptHandler.loadSingleScript(allGlobals, fileName);
            }).executionType(ExecutionType.TICK_START).schedule();
        }, ArgumentType.Literal("reload"), scriptName);

        addSyntax((sender, context) -> {
            ScriptHandler.loadAllScripts(allGlobals, false);
        }, ArgumentType.Literal("reloadall"));

        addSyntax((sender, context) -> {
            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] You are running LuaStom &6v" + Main.version));
        }, ArgumentType.Literal("info"));

        addSyntax((sender, context) -> {
            String addons = AddonManager.getAddons().stream()
                .map(LuaStomAddon::getName)
                .collect(Collectors.joining(", "));

            sender.sendMessage(ComponentUtil.parseLegacy("[&7LuaStom&r] Installed Addons: "));
            sender.sendMessage(ComponentUtil.parseLegacy("&6" + addons));
        }, ArgumentType.Literal("addons"));
    }

    private void collectCompletions(File root, File folder, List<String> result) {
        File[] files = folder.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                collectCompletions(root, file, result);
            } else if (!file.getName().startsWith("-") && file.getName().endsWith(".lua")) {
                String relative = root.toURI().relativize(file.toURI()).getPath();
                result.add(relative);
            }
        }
    }
}