package com.eu.habbo.habbohotel.roleplay.gambling;

import com.eu.habbo.Emulator;
import com.eu.habbo.habbohotel.items.Item;
import com.eu.habbo.habbohotel.rooms.FurnitureMovementError;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomTile;
import com.eu.habbo.habbohotel.users.HabboItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TexasHoldEmItem {
    private static final Logger LOGGER = LoggerFactory.getLogger(TexasHoldEmItem.class);

    public int roomId;
    public int itemId; // Base item ID
    public int x;
    public int y;
    public double z;
    public int rotation;

    public HabboItem furni;
    public boolean rolled;
    public int value;

    public TexasHoldEmItem(int roomId, int itemId, int x, int y, double z, int rotation) {
        this.roomId = roomId;
        this.itemId = itemId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotation = rotation;
        this.furni = null;
        this.rolled = false;
        this.value = 0;
    }

    public void spawnDice() {
        try {
            Room room = Emulator.getGameEnvironment().getRoomManager().getRoom(this.roomId);
            if (room == null) {
                LOGGER.error("Failed to generate room with ID: {}", this.roomId);
                return;
            }

            Item baseItem = Emulator.getGameEnvironment().getItemManager().getItem(this.itemId);
            if (baseItem == null) {
                LOGGER.error("Failed to get base item with ID: {}", this.itemId);
                return;
            }

            HabboItem item = Emulator.getGameEnvironment().getItemManager().createItem(0, baseItem, 0, 0, "0");
            if (item == null) {
                LOGGER.error("Failed to create HabboItem for base item ID: {}", this.itemId);
                return;
            }

            RoomTile tile = room.getLayout().getTile((short) this.x, (short) this.y);
            if (tile == null) {
                LOGGER.error("Failed to get tile at ({}, {}) in room: {}", this.x, this.y, this.roomId);
                Emulator.getGameEnvironment().getItemManager().deleteItem(item);
                return;
            }

            FurnitureMovementError error = room.placeFloorFurniAt(item, tile, this.rotation, null);
            if (error == FurnitureMovementError.NONE) {
                this.furni = item;
            } else {
                LOGGER.error("Failed to place item in room. Error: {}", error);
                Emulator.getGameEnvironment().getItemManager().deleteItem(item);
            }
        } catch (Exception e) {
            LOGGER.error("Error in spawnDice() for room: {}", this.roomId, e);
        }
    }
}
