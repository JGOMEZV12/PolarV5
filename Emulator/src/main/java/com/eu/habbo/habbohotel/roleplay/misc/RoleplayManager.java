package com.eu.habbo.habbohotel.roleplay.misc;

import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.roleplay.food.FoodManager;
import com.eu.habbo.habbohotel.roleplay.houses.HouseManager;
import com.eu.habbo.habbohotel.roleplay.products.ProductsManager;
import com.eu.habbo.habbohotel.roleplay.rooms.RPRoomManager;
import com.eu.habbo.habbohotel.roleplay.vehicles.VehicleManager;
import com.eu.habbo.habbohotel.roleplay.vehicles.VehiclesOwnedManager;
import com.eu.habbo.habbohotel.roleplay.weapons.WSkinManager;
import com.eu.habbo.habbohotel.roleplay.weapons.WeaponManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RoleplayManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(RoleplayManager.class);

    private static RPRoomManager rpRoomManager;
    private static VehiclesOwnedManager vehiclesOwnedManager;
    private static HouseManager houseManager;

    public static void initialize() {
        LOGGER.info("RoleplayManager -> Starting initialization of all Roleplay systems...");

        rpRoomManager = new RPRoomManager();
        rpRoomManager.init();

        vehiclesOwnedManager = new VehiclesOwnedManager();
        vehiclesOwnedManager.init();

        houseManager = new HouseManager();
        houseManager.init();

        WeaponManager.initialize();
        WSkinManager.initialize();
        VehicleManager.initialize();
        FoodManager.initialize();
        ProductsManager.initialize();
        com.eu.habbo.habbohotel.roleplay.farming.FarmingManager.initialize();
        com.eu.habbo.habbohotel.roleplay.wizards.HechizosManager.initialize();
        com.eu.habbo.habbohotel.roleplay.apartments.ApartmentManager.initialize();
        com.eu.habbo.habbohotel.roleplay.vehicles.VehicleJobsManager.initialize();
        com.eu.habbo.habbohotel.roleplay.combat.CombatManager.initialize();
        com.eu.habbo.habbohotel.roleplay.groups.RoleplayGroupManager.initialize();
        com.eu.habbo.habbohotel.roleplay.bots.RoleplayBotManager.initialize();
        com.eu.habbo.habbohotel.roleplay.phones.PhonesManager.initialize();
        com.eu.habbo.habbohotel.roleplay.websocket.chats.WebSocketChatManager.initialize();
        com.eu.habbo.habbohotel.roleplay.misc.RoleplayData.initialize();
        com.eu.habbo.habbohotel.roleplay.misc.BlackListManager.initialize();
        com.eu.habbo.habbohotel.roleplay.misc.BountyManager.initialize();
        com.eu.habbo.habbohotel.roleplay.misc.LotteryManager.initialize();
        com.eu.habbo.habbohotel.roleplay.misc.ToDoManager.initialize();
        com.eu.habbo.habbohotel.roleplay.comodin.ComodinManager.initialize();
        com.eu.habbo.habbohotel.roleplay.internet.PlayInternetManager.initialize();
        com.eu.habbo.habbohotel.roleplay.gambling.TexasHoldEmManager.initialize();

        LOGGER.info("RoleplayManager -> All Roleplay systems successfully loaded!");
    }

    public static void shout(GameClient client, String speech, int bubble) {
        if (client != null && client.getHabbo() != null) {
            client.getHabbo().shout(speech);
        }
    }

    public static void shout(GameClient client, String speech) {
        shout(client, speech, 0);
    }

    public static void whisper(GameClient client, String speech) {
        if (client != null && client.getHabbo() != null) {
            client.getHabbo().whisper(speech);
        }
    }

    public static void giveMoneyToCompany(
            int companyId, GameClient client, String reason, boolean someBool, int amount) {
        LOGGER.info(
                "RoleplayManager: Give money to company {} for client {}, amount: {}",
                companyId,
                client.getHabbo().getHabboInfo().getUsername(),
                amount);
    }

    public static RPRoomManager getRpRoomManager() {
        return rpRoomManager;
    }

    public static VehiclesOwnedManager getVehiclesOwnedManager() {
        return vehiclesOwnedManager;
    }

    public static HouseManager getHouseManager() {
        return houseManager;
    }
}
