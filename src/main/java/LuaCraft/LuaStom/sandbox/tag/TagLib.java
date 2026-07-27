package LuaCraft.LuaStom.sandbox.tag;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import net.minestom.server.tag.Tag;

public class TagLib extends LuaTable {
    public static LuaTable creator() {
        LuaTable tbl = new LuaTable();

        tbl.set("NewString", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                Tag<String> tag = Tag.String(LuaErrorAssert.checkString(name, "Tag.NewString", 1));

                return new StringTagLib(tag);
            }
        });

        tbl.set("NewNumber", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue name) {
                Tag<Double> tag = Tag.Double(LuaErrorAssert.checkString(name, "Tag.NewNumber", 1));

                return new DoubleTagLib(tag);
            }
        });

        return tbl;
    }

    static class StringTagLib extends LuaTable {
        public StringTagLib(Tag<String> tag) {
            rawset("SetDefault", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue def) {
                    String newDefault = LuaErrorAssert.checkString(def, "StringTag.SetDefault", 1);

                    Tag<String> newTag = tag.defaultValue(newDefault);

                    return new StringTagLib(newTag);
                }
            });
        }
    }

    static class DoubleTagLib extends LuaTable {
        public DoubleTagLib(Tag<Double> tag) {
            rawset("SetDefault", new OneArgFunction() {
                @Override
                public LuaValue call(LuaValue def) {
                    Double newDefault = LuaErrorAssert.checkDouble(def, "StringTag.SetDefault", 1);

                    Tag<Double> newTag = tag.defaultValue(newDefault);

                    return new DoubleTagLib(newTag);
                }
            });
        }
    }
}