package LuaCraft.LuaStom.sandbox.tag;

import org.luaj.vm2.LuaValue;

import LuaCraft.LuaStom.sandbox.inventory.InventoryLib;
import net.minestom.server.inventory.Inventory;

public class TagHandler {
    public Object TagResolver(LuaValue value) {
        if (value.isstring()) return value.tojstring();
        if (value.isnumber()) return value.todouble();
        if (value.isboolean()) return value.toboolean();

        return null;
    }

    // Yeah people always say blah blah don't use wildcards, fuck you. This is all it will let me do without yapping
    public Class<?> TagTypeResolver(LuaValue value) {
        if (value.isstring()) return String.class;
        if (value.isnumber()) return Double.class;
        if (value.isboolean()) return Boolean.class;
        if (value instanceof InventoryLib) return Inventory.class;

        return null;
    }
}