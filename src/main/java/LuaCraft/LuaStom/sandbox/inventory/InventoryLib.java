package LuaCraft.LuaStom.sandbox.inventory;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.inventory.InventoryType;

public class InventoryLib extends AbstractInventoryLib {
    private Inventory inventory;

    public static LuaTable creator() {
        LuaTable tbl = new LuaTable();

        tbl.set("New", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue type, LuaValue title) {
                Inventory inv = new Inventory(InventoryType.valueOf(LuaErrorAssert.checkString(type, "Gui.New", 1)),
                        LuaErrorAssert.checkString(title, "Gui.New", 2));

                return new InventoryLib(inv);
            }
        });

        return tbl;
    }

    public InventoryLib(Inventory inventory) {
        this.inventory = inventory;

        super(inventory);
    }

    @Override
    public Inventory getInventory() {
        return inventory;
    }
}