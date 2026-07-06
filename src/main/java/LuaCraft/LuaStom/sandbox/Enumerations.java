package LuaCraft.LuaStom.sandbox;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;

import net.minestom.server.entity.EntityType;
import net.minestom.server.instance.block.Block;
import net.minestom.server.inventory.InventoryType;
import net.minestom.server.item.Material;
import net.minestom.server.registry.RegistryTag;

public class Enumerations {
    public static LuaTable ItemEnums() {
        LuaTable tbl = new LuaTable();

        for (Material mat : Material.values()) {
            String material = mat.toString().replace("minecraft:", "").toUpperCase();
            tbl.set(material, LuaValue.valueOf(mat.toString()));
        }

        return tbl;
    }

    public static LuaTable LogEnums() {
        LuaTable tbl = new LuaTable();

        tbl.set("INFO", LuaValue.valueOf("Info"));
        tbl.set("WARN", LuaValue.valueOf("Warn"));
        tbl.set("ERROR", LuaValue.valueOf("Error"));

        return tbl;
    }

    public static LuaTable BlockEnums() {
        LuaTable tbl = new LuaTable();

        for (Block block : Block.values()) {
            String blockName = block.name().toString().replace("minecraft:", "").toUpperCase();
            tbl.set(blockName, LuaValue.valueOf(block.name()));
        }

        return tbl;
    }

    public static LuaTable TagEnums() {
        LuaTable tbl = new LuaTable();

        for (RegistryTag<Block> tag : Block.staticRegistry().tags()) {
            String tagName = tag.key().key().asString();
            String key = tagName.replace("minecraft:", "").toUpperCase();
            tbl.set(key, LuaValue.valueOf(tagName));
        }

        for (RegistryTag<Material> tag : Material.staticRegistry().tags()) {
            String tagName = tag.key().key().asString();
            String key = tagName.replace("minecraft:", "").toUpperCase();
            tbl.set(key, LuaValue.valueOf(tagName));
        }

        return tbl;
    }

    public static LuaTable EntityEnums() {
        LuaTable tbl = new LuaTable();

        for (EntityType type : EntityType.values()) {
            String entityName = type.key().value().toUpperCase();

            tbl.set(entityName, LuaValue.valueOf(type.key().asString()));
        }

        return tbl;
    }

    public static LuaTable GuiEnums() {
        LuaTable tbl = new LuaTable();

        for (InventoryType type : InventoryType.values()) {
            tbl.set(type.toString(), LuaValue.valueOf(type.toString()));
        }

        return tbl;
    }
}