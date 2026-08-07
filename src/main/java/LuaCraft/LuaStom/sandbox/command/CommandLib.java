package LuaCraft.LuaStom.sandbox.command;

import java.util.ArrayList;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import LuaCraft.LuaStom.Main;
import LuaCraft.LuaStom.sandbox.entities.PlayerLib;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.builder.Command;
import net.minestom.server.command.builder.arguments.Argument;
import net.minestom.server.entity.Player;

public class CommandLib extends LuaTable {
    private Command command;
    private String scriptName;

    public static LuaValue creator(String scriptName) {
        LuaTable tbl = new LuaTable();

        tbl.set("New", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                return new CommandLib(new Command(LuaErrorAssert.checkString(name, "Command.New", 1)), scriptName);
            }
        });

        return tbl;
    }

    public CommandLib(Command command, String scriptName) {
        this.command = command;
        this.scriptName = scriptName;

        rawset("SetDefaultExecutor", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue callback) {
                command.setDefaultExecutor((sender, context) -> {
                    if (sender instanceof Player player) {
                        LuaErrorAssert.checkFunction(callback, "Command:SetDefaultExecutor", 1).call(new PlayerLib(player), new CommandContextLib(context, sender));
                    }
                });

                return CommandLib.this;
            }
        });

        rawset("AddSyntax", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue callback, LuaValue args) {
                Argument<?>[] parsed = new Argument<?>[args.length()];
                for (int i = 1; i <= args.length(); i++) {
                    parsed[i - 1] = (Argument<?>) args.get(i).checkuserdata(Argument.class);
                }

                command.addSyntax((sender, context) -> {
                    if (sender instanceof Player p) {
                        LuaErrorAssert.checkFunction(callback, "Command:AddSyntax", 1)
                            .call(new PlayerLib(p), new CommandContextLib(context, sender));
                    }
                }, parsed);
                return CommandLib.this;
            }
        });

        rawset("Register", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                MinecraftServer.getCommandManager().register(command);
                Main.getScriptCommands().computeIfAbsent(scriptName, k -> new ArrayList<>()).add(CommandLib.this);
                return CommandLib.this;
            }
        });

        rawset("Unregister", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                MinecraftServer.getCommandManager().unregister(command);
                return CommandLib.this;
            }
        });
    }

    public Command getCommand() {
        return command;
    }

    public String getCommandScriptName() {
        return scriptName;
    }
}
