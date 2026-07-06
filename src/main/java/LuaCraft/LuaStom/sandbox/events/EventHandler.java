package LuaCraft.LuaStom.sandbox.events;

import java.util.concurrent.ConcurrentHashMap;

import org.luaj.vm2.Globals;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.entity.EntitySpawnEvent;
import net.minestom.server.event.instance.InstanceChunkLoadEvent;
import net.minestom.server.event.item.PickupItemEvent;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerBlockBreakEvent;
import net.minestom.server.event.player.PlayerBlockInteractEvent;
import net.minestom.server.event.player.PlayerBlockPlaceEvent;
import net.minestom.server.event.player.PlayerChatEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.trait.BlockEvent;
import net.minestom.server.event.trait.EntityEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.event.trait.InventoryEvent;
import net.minestom.server.event.trait.ItemEvent;
import net.minestom.server.event.trait.PlayerEvent;

public class EventHandler {

    private EventNode<PlayerEvent> playerNode = EventNode.type("luacraft_playerNode", EventFilter.PLAYER);
    private EventNode<EntityEvent> entityNode = EventNode.type("luacraft_entityNode", EventFilter.ENTITY);
    private EventNode<InventoryEvent> inventoryNode = EventNode.type("luacraft_inventoryNode", EventFilter.INVENTORY);
    private EventNode<ItemEvent> itemNode = EventNode.type("luacraft_itemNode", EventFilter.ITEM);
    private EventNode<BlockEvent> blockNode = EventNode.type("luacraft_blockNode", EventFilter.BLOCK);
    private EventNode<InstanceEvent> instanceNode = EventNode.type("luacraft_instanceNode", EventFilter.INSTANCE);

    public void initNodes() {
        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();

        handler.addChild(playerNode);
        handler.addChild(entityNode);
        handler.addChild(inventoryNode);
        handler.addChild(itemNode);
        handler.addChild(blockNode);
        handler.addChild(instanceNode);
    }

    public void initListeners(ConcurrentHashMap<String, Globals> allGlobals) {
        // Player Listeners
        playerNode.addListener(PlayerBlockBreakEvent.class, event -> {
            OnPlayerBlockBreak.handle(event, allGlobals);
        });
        playerNode.addListener(AsyncPlayerConfigurationEvent.class, event -> {
            OnAsyncPlayerConfiguration.handle(event, allGlobals);
        });
        playerNode.addListener(PlayerChatEvent.class, event -> {
            OnPlayerChat.handle(event, allGlobals);
        });
        playerNode.addListener(PlayerSpawnEvent.class, event -> {
            OnPlayerSpawn.handle(event, allGlobals);
        });
        playerNode.addListener(PlayerBlockInteractEvent.class, event -> {
            OnPlayerBlockInteract.handle(event, allGlobals);
        });
        playerNode.addListener(PlayerBlockPlaceEvent.class, event -> {
            OnPlayerBlockPlace.handle(event, allGlobals);
        });

        // Entity Listeners
        entityNode.addListener(PickupItemEvent.class, event -> {
            OnPickupItem.handle(event, allGlobals);
        });
        entityNode.addListener(EntitySpawnEvent.class, event -> {
            OnEntitySpawn.handle(event, allGlobals);
        });

        instanceNode.addListener(InstanceChunkLoadEvent.class, event -> {
            OnChunkLoad.handle(event, allGlobals);
        });
    }
}