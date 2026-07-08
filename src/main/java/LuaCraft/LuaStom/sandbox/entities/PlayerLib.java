package LuaCraft.LuaStom.sandbox.entities;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import LuaCraft.LuaStom.sandbox.component.ComponentLib;
import LuaCraft.LuaStom.sandbox.component.ComponentUtil;
import LuaCraft.LuaStom.sandbox.inventory.InventoryLib;
import LuaCraft.LuaStom.sandbox.inventory.PlayerInventoryLib;
import LuaCraft.LuaStom.sandbox.position.PositionLib;
import net.kyori.adventure.text.Component;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.inventory.Inventory;
import net.minestom.server.tag.Tag;

public class PlayerLib extends LivingEntityLib {
    private Player player;

    public static final LuaTable PLAYER_METATABLE = new LuaTable();

    static {
        PLAYER_METATABLE.rawset("__index", PLAYER_METATABLE);

        PLAYER_METATABLE.rawset("SetSpawnPoint", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue pos) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    if (pos instanceof PositionLib positionLib) {
                        Pos position = positionLib.getPoint();

                        ply.setRespawnPoint(position);
                    }

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SetDisplayName", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue test) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.setDisplayName(Component.text(LuaErrorAssert.checkString(test, "SetDisplayName", 1)));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetInventory", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    return new PlayerInventoryLib(ply.getInventory(), ply);
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SendMessage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue message) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.sendMessage(ComponentUtil.luaValueToComponent(message));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetName", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    return new ComponentLib(ply.getName());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("GetUsername", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    return LuaValue.valueOf(ply.getUsername());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SetTag", new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue name, LuaValue value) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    String tagName = LuaErrorAssert.checkString(name, "Player:SetTag", 1);

                    if (value.isstring())
                        ply.setTag(Tag.String(tagName), value.tojstring());
                    if (value.isint())
                        ply.setTag(Tag.Integer(tagName), value.toint());
                    if (value.isboolean())
                        ply.setTag(Tag.Boolean(tagName), value.toboolean());
                    if (value instanceof ComponentLib)
                        ply.setTag(Tag.Component(tagName), ComponentUtil.luaValueToComponent(value));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("HasTag", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue name) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    String tagName = LuaErrorAssert.checkString(name, "Player.SetTag", 1);
                    boolean hasTag = ply.getTag(Tag.String(tagName)) != null ||
                            ply.getTag(Tag.Integer(tagName)) != null ||
                            ply.getTag(Tag.Boolean(tagName)) != null;

                    return LuaValue.valueOf(hasTag);
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetUUID", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    return LuaValue.valueOf(ply.getUuid().toString());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SetGamemode", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue gm) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.setGameMode(
                        GameMode.valueOf(LuaErrorAssert.checkString(gm, "Player:SetGamemode", 1).toUpperCase()));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetGamemode", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    return LuaValue.valueOf(ply.getGameMode().toString());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SetFlySpeed", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue speed) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.setFlyingSpeed(LuaErrorAssert.checkFloat(speed, "Player:SetFlySpeed", 1));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetFlySpeed", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    return LuaValue.valueOf(ply.getFlyingSpeed());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SetLevel", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue exp) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.setLevel(LuaErrorAssert.checkInt(exp, "Player:SetLevel", 1));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetLevel", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    var level = ply.getLevel();
                    return LuaValue.valueOf(level);
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SetEXP", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue EXP) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.setExp(LuaErrorAssert.checkFloat(EXP, "Player:SetEXP", 1));
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("GetEXP", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    var exp = ply.getExp();
                    return LuaValue.valueOf(exp);
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SwingMainHand", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue othersOnly) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.swingMainHand(LuaErrorAssert.checkBoolean(othersOnly, "Player:SwingMainHand", 1));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        PLAYER_METATABLE.rawset("SwingOffHand", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue othersOnly) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    ply.swingOffHand(LuaErrorAssert.checkBoolean(othersOnly, "Player:SwingOffHand", 1));

                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("GetLatency", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    return LuaValue.valueOf(ply.getLatency());
                } else {
                    return LuaValue.NIL;
                }
            } 
        });
        PLAYER_METATABLE.rawset("GetFood", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    return LuaValue.valueOf(ply.getFood());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("SetFood", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue food) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    ply.setFood(LuaErrorAssert.checkInt(food, "Player:SetFood", 2));
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("GetSaturation", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    return LuaValue.valueOf(ply.getFoodSaturation());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("SetSaturation", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue saturation) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    ply.setFoodSaturation(LuaErrorAssert.checkFloat(saturation, "Player:SetSaturation", 2));
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("GetIsEating", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    return LuaValue.valueOf(ply.isEating());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("GetIsUsingItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();
                    return LuaValue.valueOf(ply.isUsingItem());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        PLAYER_METATABLE.rawset("OpenInventory", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue inventory) {
                if (self instanceof PlayerLib playerLib) {
                    Player ply = playerLib.getEntity();

                    if (inventory instanceof InventoryLib inv) {
                        Inventory inven = inv.getInventory();

                        ply.openInventory(inven);
                    }
                }
                return LuaValue.NIL;
            }
        });
        PLAYER_METATABLE.setmetatable(LIVINGENTITY_METATABLE);
    }

    public PlayerLib(Player player) {
        super(player);

        this.player = player;
        this.setmetatable(PLAYER_METATABLE);
    }

    @Override
    public Player getEntity() {
        return player;
    }
}