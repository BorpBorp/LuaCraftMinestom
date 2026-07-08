package LuaCraft.LuaStom.sandbox.inventory;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import net.minestom.server.inventory.AbstractInventory;
import net.minestom.server.item.ItemStack;

public class AbstractInventoryLib extends LuaTable {
    private AbstractInventory inventory;

    private static final LuaTable INVENTORY_METATABLE = new LuaTable();

    static {
        INVENTORY_METATABLE.set("__index", INVENTORY_METATABLE);

        INVENTORY_METATABLE.set("AddItem", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue item) {
                if (self instanceof AbstractInventoryLib inv) {
                    ItemStack itemstack = ((ItemStackLib) item).getItemStack();
                    AbstractInventory inventory = inv.getInventory();

                    inventory.addItemStack(itemstack);

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        INVENTORY_METATABLE.set("RemoveItemAmount", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue item, LuaValue amount) {
                if (!(self instanceof AbstractInventoryLib inv)) return LuaValue.NIL;
                if (!(item instanceof ItemStackLib itemstack)) return LuaValue.NIL;

                ItemStack itemStack = itemstack.getItemStack();
                AbstractInventory inventory = inv.getInventory();
                int amountToRemove = LuaErrorAssert.checkInt(amount, "Inventory:RemoveItemAmount", 2);

                for (int i = 0; i < inventory.getSize(); i++) {
                    ItemStack otherItem = inventory.getItemStack(i);

                    if (otherItem.material() == itemStack.material()) {
                        int currentAmount = otherItem.amount();

                        if (currentAmount <= amountToRemove) {
                            inventory.setItemStack(i, ItemStack.AIR);
                            amountToRemove -= currentAmount;
                        } else {
                            inventory.setItemStack(i, otherItem.withAmount(currentAmount - amountToRemove));
                            amountToRemove = 0;
                            break;
                        }
                    }
                }

                return self;
            }
        });

        INVENTORY_METATABLE.set("SetSlot", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue slot, LuaValue item) {
                if (!(self instanceof AbstractInventoryLib inv)) return LuaValue.NIL;
                if (!(item instanceof ItemStackLib itemstack)) return LuaValue.NIL;

                ItemStack itemStack = itemstack.getItemStack();
                AbstractInventory inventory = inv.getInventory();

                inventory.setItemStack(LuaErrorAssert.checkInt(slot, "Inventory:SetSlot", 1), itemStack);

                return self;
            }
        });
    }

    public AbstractInventoryLib(AbstractInventory inventory) {
        this.inventory = inventory;
        this.setmetatable(INVENTORY_METATABLE);
    }

    public AbstractInventory getInventory() {
        return inventory;
    }
}