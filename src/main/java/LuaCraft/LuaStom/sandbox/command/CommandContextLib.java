package LuaCraft.LuaStom.sandbox.command;

import java.util.List;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import LuaCraft.LuaStom.sandbox.entities.PlayerLib;
import net.minestom.server.command.CommandSender;
import net.minestom.server.command.builder.CommandContext;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.utils.entity.EntityFinder;

public class CommandContextLib extends LuaTable {
    private CommandContext context;
    private CommandSender sender;

    public CommandContextLib(CommandContext context, CommandSender sender) {
        this.context = context;
        this.sender = sender;

        rawset("GetArg", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue name) {
                Object value = context.get(LuaErrorAssert.checkString(name, "CommandContext:GetArg", 1));

                if (value instanceof Player p) return new PlayerLib(p);
                if (value instanceof String s) return LuaValue.valueOf(s);
                if (value instanceof Integer i) return LuaValue.valueOf(i);
                if (value instanceof EntityFinder finder) {
                    List<Entity> found = finder.find(sender);
                    if (!found.isEmpty() && found.get(0) instanceof Player p) {
                        return new PlayerLib(p);
                    }
                    return LuaValue.NIL;
                }

                return LuaValue.NIL;
            }
        });
    }

    public CommandContext getContext() {
        return context;
    }

    public CommandSender getSender() {
        return sender;
    }
}
