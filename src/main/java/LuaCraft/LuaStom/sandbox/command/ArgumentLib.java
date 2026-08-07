package LuaCraft.LuaStom.sandbox.command;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import net.minestom.server.command.builder.arguments.ArgumentType;

public class ArgumentLib {
    public static LuaTable creator() {
        LuaTable tbl = new LuaTable();

        tbl.set("Player", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                return LuaValue.userdataOf(
                    ArgumentType.Entity(LuaErrorAssert.checkString(name, "Argument.Player", 1)).onlyPlayers(true)
                );
            }
        });

        tbl.set("String", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                return LuaValue.userdataOf(ArgumentType.String(LuaErrorAssert.checkString(name, "Argument.String", 1)));
            }
        });

        tbl.set("Integer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                return LuaValue.userdataOf(ArgumentType.Integer(LuaErrorAssert.checkString(name, "Argument.Integer", 1)));
            }
        });

        return tbl;
    }
}