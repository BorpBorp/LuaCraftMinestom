package LuaCraft.LuaStom.sandbox.entities;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import LuaCraft.LuaStom.LuaErrorAssert;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;

public class LivingEntityLib extends EntityLib {
    private LivingEntity entity;

    public static final LuaTable LIVINGENTITY_METATABLE = new LuaTable();

    static {
        LIVINGENTITY_METATABLE.rawset("__index", LIVINGENTITY_METATABLE);

        LIVINGENTITY_METATABLE.rawset("damage", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue damage) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();

                    lEntity.damage(DamageType.GENERIC, LuaErrorAssert.checkInt(damage, "entity:damage", 1));
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        
        LIVINGENTITY_METATABLE.rawset("GetHealth", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();

                    return LuaValue.valueOf(lEntity.getHealth());
                } else {
                    return LuaValue.NIL;
                }
            }
        });

        LIVINGENTITY_METATABLE.rawset("IsPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();

                    return LuaValue.valueOf(lEntity instanceof Player);
                } else {
                    return LuaValue.FALSE;
                }
            }
        });
        LIVINGENTITY_METATABLE.rawset("GetFireTicks", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    return LuaValue.valueOf((int)lEntity.getFireTicks());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        LIVINGENTITY_METATABLE.rawset("SetFireTicks", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue ticks) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    lEntity.setFireTicks(LuaErrorAssert.checkInt(ticks, "LivingEntity:SetFireTicks", 2));
                    return self;
                }
                return LuaValue.NIL;
            }
        });
        LIVINGENTITY_METATABLE.rawset("Kill", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    lEntity.kill();
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        LIVINGENTITY_METATABLE.rawset("AsPlayer", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();

                    if (lEntity instanceof Player ply) return new PlayerLib(ply);

                    return LuaValue.NIL;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        LIVINGENTITY_METATABLE.rawset("GetIsDead", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    return LuaValue.valueOf(lEntity.isDead());
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        LIVINGENTITY_METATABLE.rawset("getCanPickupItem", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    return LuaValue.valueOf(lEntity.canPickupItem());
                } else {
                    return LuaValue.NIL;
                }
                
            }
        });
        LIVINGENTITY_METATABLE.rawset("SetCanPickUpItem", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue pickup) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    lEntity.setCanPickupItem(LuaErrorAssert.checkBoolean(pickup, "LivingEntity:SetCanPickUpItem", 2));
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        LIVINGENTITY_METATABLE.rawset("SetHealth", new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue value) {
                if (self instanceof LivingEntityLib lEntityLib) {
                    LivingEntity lEntity = lEntityLib.getEntity();
                    lEntity.setHealth(LuaErrorAssert.checkFloat(value, "LivingEntity:SetHealth", 1));
                    return self;
                } else {
                    return LuaValue.NIL;
                }
            }
        });
        LIVINGENTITY_METATABLE.setmetatable(ENTITY_METATABLE);
    }

    public LivingEntityLib(LivingEntity entity) {
        super(entity);

        this.entity = entity;
        this.setmetatable(ENTITY_METATABLE);
    }

    @Override
    public LivingEntity getEntity() {
        return entity;
    }
}