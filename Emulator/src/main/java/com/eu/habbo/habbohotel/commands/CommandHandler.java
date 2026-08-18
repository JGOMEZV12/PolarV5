package com.eu.habbo.habbohotel.commands;

import com.eu.habbo.habbohotel.commands.roleplay.*;

import com.eu.habbo.Emulator;
import com.eu.habbo.core.CommandLog;
import com.eu.habbo.habbohotel.gameclients.GameClient;
import com.eu.habbo.habbohotel.permissions.Permission;
import com.eu.habbo.habbohotel.permissions.PermissionSetting;
import com.eu.habbo.habbohotel.pets.Pet;
import com.eu.habbo.habbohotel.pets.PetCommand;
import com.eu.habbo.habbohotel.pets.PetVocalsType;
import com.eu.habbo.habbohotel.pets.RideablePet;
import com.eu.habbo.habbohotel.rooms.Room;
import com.eu.habbo.habbohotel.rooms.RoomRightLevels;
import com.eu.habbo.messages.outgoing.rooms.users.RoomUserTypingComposer;
import com.eu.habbo.plugin.events.users.UserCommandEvent;
import com.eu.habbo.plugin.events.users.UserExecuteCommandEvent;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(CommandHandler.class);

    private static final Map<String, Command> commands = new HashMap<>(5);
    private static final Comparator<Command> ALPHABETICAL_ORDER = new Comparator<Command>() {
        public int compare(Command c1, Command c2) {
            int res = String.CASE_INSENSITIVE_ORDER.compare(c1.permission, c2.permission);
            return (res != 0) ? res : c1.permission.compareTo(c2.permission);
        }
    };

    public CommandHandler() {
        long millis = System.currentTimeMillis();
        this.reloadCommands();
        LOGGER.info("Command Handler -> Loaded! ({} MS)", System.currentTimeMillis() - millis);
    }

    public static void addCommand(Command command) {
        if (command == null) return;

        commands.put(command.getClass().getName(), command);
    }

    public static void addCommand(Class<? extends Command> command) {
        try {
            // command.getConstructor().setAccessible(true);
            addCommand(command.getDeclaredConstructor().newInstance());
            LOGGER.debug("Added command: {}", command.getName());
        } catch (Exception e) {
            LOGGER.error("Caught exception", e);
        }
    }

    public static boolean handleCommand(GameClient gameClient, String commandLine) {
        if (gameClient != null && commandLine != null) {
            if (commandLine.startsWith(":")) {
                commandLine = commandLine.replaceFirst(":", "");

                String[] parts = commandLine.split(" ");

                if (parts.length >= 1) {
                    for (Command command : commands.values()) {
                        for (String s : command.keys) {
                            if (s.equalsIgnoreCase(parts[0])) {
                                boolean succes = false;
                                if (command.permission == null
                                        || gameClient
                                                .getHabbo()
                                                .hasPermission(
                                                        command.permission,
                                                        gameClient
                                                                                        .getHabbo()
                                                                                        .getHabboInfo()
                                                                                        .getCurrentRoom()
                                                                                != null
                                                                        && (gameClient
                                                                                .getHabbo()
                                                                                .getHabboInfo()
                                                                                .getCurrentRoom()
                                                                                .hasRights(gameClient.getHabbo()))
                                                                || gameClient
                                                                        .getHabbo()
                                                                        .hasPermission(Permission.ACC_PLACEFURNI)
                                                                || (gameClient
                                                                                        .getHabbo()
                                                                                        .getHabboInfo()
                                                                                        .getCurrentRoom()
                                                                                != null
                                                                        && gameClient
                                                                                        .getHabbo()
                                                                                        .getHabboInfo()
                                                                                        .getCurrentRoom()
                                                                                        .getGuildId()
                                                                                > 0
                                                                        && gameClient
                                                                                .getHabbo()
                                                                                .getHabboInfo()
                                                                                .getCurrentRoom()
                                                                                .getGuildRightLevel(
                                                                                        gameClient.getHabbo())
                                                                                .isEqualOrGreaterThan(
                                                                                        RoomRightLevels
                                                                                                .GUILD_RIGHTS)))) {
                                    try {
                                        UserExecuteCommandEvent userExecuteCommandEvent =
                                                new UserExecuteCommandEvent(gameClient.getHabbo(), command, parts);
                                        Emulator.getPluginManager().fireEvent(userExecuteCommandEvent);

                                        if (userExecuteCommandEvent.isCancelled()) {
                                            return userExecuteCommandEvent.isSuccess();
                                        }

                                        if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null)
                                            gameClient
                                                    .getHabbo()
                                                    .getHabboInfo()
                                                    .getCurrentRoom()
                                                    .sendComposer(new RoomUserTypingComposer(
                                                                    gameClient
                                                                            .getHabbo()
                                                                            .getRoomUnit(),
                                                                    false)
                                                            .compose());

                                        UserCommandEvent event = new UserCommandEvent(
                                                gameClient.getHabbo(), parts, command.handle(gameClient, parts));
                                        Emulator.getPluginManager().fireEvent(event);

                                        succes = event.succes;
                                    } catch (Exception e) {
                                        LOGGER.error("Caught exception", e);
                                    }

                                    if (gameClient
                                            .getHabbo()
                                            .getHabboInfo()
                                            .getRank()
                                            .isLogCommands()) {
                                        Emulator.getDatabaseLogger()
                                                .store(new CommandLog(
                                                        gameClient
                                                                .getHabbo()
                                                                .getHabboInfo()
                                                                .getId(),
                                                        command,
                                                        commandLine,
                                                        succes));
                                    }
                                }

                                return succes;
                            }
                        }
                    }
                }
            } else {
                String[] args = commandLine.split(" ");

                if (args.length <= 1) return false;

                if (gameClient.getHabbo().getHabboInfo().getCurrentRoom() != null) {
                    Room room = gameClient.getHabbo().getHabboInfo().getCurrentRoom();

                    if (room.getCurrentPets().isEmpty()) return false;

                    for (Pet pet : room.getCurrentPets().values()) {
                        if (pet != null) {
                            if (pet.getName().equalsIgnoreCase(args[0])) {
                                StringBuilder s = new StringBuilder();

                                for (int i = 1; i < args.length; i++) {
                                    s.append(args[i]).append(" ");
                                }

                                s = new StringBuilder(s.substring(0, s.length() - 1));

                                for (PetCommand command : pet.getPetData().getPetCommands()) {
                                    if (command.key.equalsIgnoreCase(s.toString())) {
                                        if (pet instanceof RideablePet && ((RideablePet) pet).getRider() != null) {
                                            if (((RideablePet) pet)
                                                            .getRider()
                                                            .getHabboInfo()
                                                            .getId()
                                                    == gameClient
                                                            .getHabbo()
                                                            .getHabboInfo()
                                                            .getId()) {
                                                ((RideablePet) pet)
                                                        .getRider()
                                                        .getHabboInfo()
                                                        .dismountPet();
                                            }
                                            break;
                                        }

                                        if (command.level <= pet.getLevel())
                                            pet.handleCommand(command, gameClient.getHabbo(), args);
                                        else pet.say(pet.getPetData().randomVocal(PetVocalsType.UNKNOWN_COMMAND));

                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;
    }

    public static Command getCommand(String key) {
        for (Command command : commands.values()) {
            for (String k : command.keys) {
                if (key.equalsIgnoreCase(k)) {
                    return command;
                }
            }
        }

        return null;
    }

    public void reloadCommands() {
        addCommand(new AboutCommand());
        addCommand(new AlertCommand());
        addCommand(new AllowTradingCommand());
        addCommand(new ArcturusCommand());
        addCommand(new BadgeCommand());
        addCommand(new BanCommand());
        addCommand(new BlockAlertCommand());
        addCommand(new BotsCommand());
        addCommand(new CalendarCommand());
        addCommand(new ChatTypeCommand());
        addCommand(new CommandsCommand());
        addCommand(new ControlCommand());
        addCommand(new CoordsCommand());
        addCommand(new CreditsCommand());
        addCommand(new DanceCommand());
        addCommand(new DiagonalCommand());
        addCommand(new DisableMassMentionsCommand());
        addCommand(new DisableMentionsCommand());
        addCommand(new DisconnectCommand());
        addCommand(new EjectAllCommand());
        addCommand(new EmptyInventoryCommand());
        addCommand(new EmptyBotsInventoryCommand());
        addCommand(new EmptyPetsInventoryCommand());
        addCommand(new EmuStatsCommand());
        addCommand(new EnableCommand());
        addCommand(new EventCommand());
        addCommand(new FacelessCommand());
        addCommand(new FastwalkCommand());
        addCommand(new FilterWordCommand());
        addCommand(new FreezeBotsCommand());
        addCommand(new FreezeCommand());
        addCommand(new FurniDataCommand());
        addCommand(new GiftCommand());
        addCommand(new GiveRankCommand());
        addCommand(new HabnamCommand());
        addCommand(new HandItemCommand());
        addCommand(new HappyHourCommand());
        addCommand(new HideWiredCommand());
        addCommand(new HotelAlertCommand());
        addCommand(new HotelAlertLinkCommand());
        addCommand(new InvisibleCommand());
        addCommand(new IPBanCommand());
        addCommand(new LayCommand());
        addCommand(new MachineBanCommand());
        addCommand(new MassBadgeCommand());
        addCommand(new RoomBadgeCommand());
        addCommand(new MassCreditsCommand());
        addCommand(new MassGiftCommand());
        addCommand(new MassPixelsCommand());
        addCommand(new MassPointsCommand());
        addCommand(new MimicCommand());
        addCommand(new MoonwalkCommand());
        addCommand(new MultiCommand());
        addCommand(new MuteBotsCommand());
        addCommand(new MuteCommand());
        addCommand(new MutePetsCommand());
        addCommand(new PetInfoCommand());
        addCommand(new PickallCommand());
        addCommand(new PingCommand());
        addCommand(new PixelCommand());
        addCommand(new PluginsCommand());
        addCommand(new PointsCommand());
        addCommand(new PromoteTargetOfferCommand());
        addCommand(new PullCommand());
        addCommand(new PushCommand());
        addCommand(new RedeemCommand());
        addCommand(new ReloadRoomCommand());
        addCommand(new RoomAlertCommand());
        addCommand(new RoomBundleCommand());
        addCommand(new RoomCreditsCommand());
        addCommand(new RoomDanceCommand());
        addCommand(new RoomEffectCommand());
        addCommand(new RoomItemCommand());
        addCommand(new RoomKickCommand());
        addCommand(new RoomMuteCommand());
        addCommand(new RoomPixelsCommand());
        addCommand(new RoomPointsCommand());
        addCommand(new SayAllCommand());
        addCommand(new SayCommand());
        addCommand(new SetMaxCommand());
        addCommand(new SetPollCommand());
        addCommand(new SetRoomTemplateCommand());
        addCommand(new SetSpeedCommand());
        addCommand(new ShoutAllCommand());
        addCommand(new ShoutCommand());
        addCommand(new ShutdownCommand());
        addCommand(new SitCommand());
        addCommand(new SnowWarSaveCommand());
        addCommand(new StandCommand());
        addCommand(new SitDownCommand());
        addCommand(new StaffAlertCommand());
        addCommand(new StaffOnlineCommand());
        addCommand(new StalkCommand());
        addCommand(new SummonCommand());
        addCommand(new SummonRankCommand());
        addCommand(new SuperbanCommand());
        addCommand(new SuperPullCommand());
        addCommand(new TakeBadgeCommand());
        addCommand(new TeleportCommand());
        addCommand(new TransformCommand());
        addCommand(new TrashCommand());
        addCommand(new UnbanCommand());
        addCommand(new UnloadRoomCommand());
        addCommand(new UnmuteCommand());
        addCommand(new UpdateAllCommand());
        addCommand(new UpdateAchievements());
        addCommand(new UpdateBotsCommand());
        addCommand(new UpdateCalendarCommand());
        addCommand(new UpdateCatalogCommand());
        addCommand(new UpdateConfigCommand());
        addCommand(new UpdateGuildPartsCommand());
        addCommand(new UpdateHotelViewCommand());
        addCommand(new UpdateItemsCommand());
        addCommand(new UpdateNavigatorCommand());
        addCommand(new UpdatePermissionsCommand());
        addCommand(new UpdatePetDataCommand());
        addCommand(new UpdatePluginsCommand());
        addCommand(new UpdatePollsCommand());
        addCommand(new UpdateTextsCommand());
        addCommand(new UpdateWordFilterCommand());
        addCommand(new UserInfoCommand());
        addCommand(new WordQuizCommand());
        addCommand(new UpdateYoutubePlaylistsCommand());
        addCommand(new AddYoutubePlaylistCommand());
        addCommand(new SoftKickCommand());
        addCommand(new SubscriptionCommand());
        addCommand(new UpdateChatBubblesCommand());
        addCommand(new GivePrefixCommand());
        addCommand(new ListPrefixesCommand());
        addCommand(new RemovePrefixCommand());
        addCommand(new WiredCommand());
        addCommand(new TestCommand());
        // Roleplay Commands Registration
        addCommand(new AbandonarCommand());
        addCommand(new AboutCommand());
        addCommand(new AcariciarCommand());
        addCommand(new AcceptCommand());
        addCommand(new AcceptDeathCommand());
        addCommand(new AcceptWeaponCommand());
        addCommand(new AccountCheckCommand());
        addCommand(new ActiveBotsCommand());
        addCommand(new AddBountyCommand());
        addCommand(new AdminJailCommand());
        addCommand(new AdminReleaseCommand());
        addCommand(new AdminTaxiCommand());
        addCommand(new AgarrarCommand());
        addCommand(new AgarratetaCommand());
        addCommand(new AlertCommand());
        addCommand(new AllAroundMeCommand());
        addCommand(new AllEyesOnMeCommand());
        addCommand(new AmbassadorAlertCommand());
        addCommand(new AmbassadorHelpCommand());
        addCommand(new AmbassadorOffDutyCommand());
        addCommand(new AmbassadorOnDutyCommand());
        addCommand(new AnalCommand());
        addCommand(new ArrestCommand());
        addCommand(new ArrowCommand());
        addCommand(new AtransferirCommand());
        addCommand(new AyudarCommand());
        addCommand(new BackupCommand());
        addCommand(new BailCommand());
        addCommand(new BalanceCommand());
        addCommand(new BanChatterCommand());
        addCommand(new BanCommand());
        addCommand(new BanVIPCommand());
        addCommand(new BaulCommand());
        addCommand(new BlackListCommand());
        addCommand(new BotCommand());
        addCommand(new BotRPCommand());
        addCommand(new BountyListCommand());
        addCommand(new BubbleCommand());
        addCommand(new BusCommand());
        addCommand(new BusinfoCommand());
        addCommand(new BuyApartmentCommand());
        addCommand(new BuyBulletsCommand());
        addCommand(new BuyCarCommand());
        addCommand(new BuyCommand());
        addCommand(new BuyCreditCommand());
        addCommand(new BuyFuelCommand());
        addCommand(new BuyFuelFillCommand());
        addCommand(new BuyTicketCommand());
        addCommand(new CallDeliveryCommand());
        addCommand(new CallPoliceCommand());
        addCommand(new CamionCommand());
        addCommand(new CaramelosCommand());
        addCommand(new CargarCamCommand());
        addCommand(new CasinoinfoCommand());
        addCommand(new CedulaCommand());
        addCommand(new ChalecopoliciaCommand());
        addCommand(new ChangeClassCommand());
        addCommand(new ChangeLogCommand());
        addCommand(new ChangeUClassCommand());
        addCommand(new CheckBalanceCommand());
        addCommand(new CheckCarInfoCommand());
        addCommand(new CheckMinutesCommand());
        addCommand(new CheckPollCommand());
        addCommand(new ChooserCommand());
        addCommand(new ClearWantedCommand());
        addCommand(new CloseCommand());
        addCommand(new ColourChangeCommand());
        addCommand(new CombatModeCommand());
        addCommand(new ComprarChalecoCommand());
        addCommand(new CooldownsCommand());
        addCommand(new CoordsCommand());
        addCommand(new CoquetearCommand());
        addCommand(new CorpInfoCommand());
        addCommand(new CorpListCommand());
        addCommand(new CreateCommand());
        addCommand(new CtransferirCommand());
        addCommand(new CuffCommand());
        addCommand(new CurarCommand());
        addCommand(new DanceCommand());
        addCommand(new DeclineCommand());
        addCommand(new DeleteChatCommand());
        addCommand(new DeleteGangCommand());
        addCommand(new DeleteRoomCommand());
        addCommand(new DemoteCommand());
        addCommand(new DepositCamCommand());
        addCommand(new DepositCommand());
        addCommand(new DisableDiagonalCommand());
        addCommand(new DisableMimicCommand());
        addCommand(new DisableWhispersCommand());
        addCommand(new DischargeCommand());
        addCommand(new DiscoCommand());
        addCommand(new DisconnectCommand());
        addCommand(new DiscountCommand());
        addCommand(new DisposeCommand());
        addCommand(new DivorceCommand());
        addCommand(new DownCommand());
        addCommand(new DrinkCommand());
        addCommand(new DriveCommand());
        addCommand(new EatCommand());
        addCommand(new EmbarazarCommand());
        addCommand(new EmergenciaCommand());
        addCommand(new EmptyItemsCommand());
        addCommand(new EnableCommand());
        addCommand(new EnterCommand());
        addCommand(new EquipCommand());
        addCommand(new EscortCommand());
        addCommand(new EscupirCommand());
        addCommand(new EstadoCommand());
        addCommand(new EventAlertCommand());
        addCommand(new ExitCommand());
        addCommand(new ExplosivosCommand());
        addCommand(new EyacularCommand());
        addCommand(new FarmingStatsCommand());
        addCommand(new FastwalkCommand());
        addCommand(new FireCommand());
        addCommand(new FixWeaponsCommand());
        addCommand(new FlagMeCommand());
        addCommand(new FlagOtherCommand());
        addCommand(new FlashBangCommand());
        addCommand(new FollowCommand());
        addCommand(new ForceLayCommand());
        addCommand(new ForceSitCommand());
        addCommand(new FreezeCommand());
        addCommand(new FreezeRoomCommand());
        addCommand(new FugaCommand());
        addCommand(new GamblingCommand());
        addCommand(new GangBackupCommand());
        addCommand(new GangCaptureCommand());
        addCommand(new GangCreateCommand());
        addCommand(new GangHealCommand());
        addCommand(new GangInfoCommand());
        addCommand(new GangInviteCommand());
        addCommand(new GangKickCommand());
        addCommand(new GangLeaveCommand());
        addCommand(new GangListCommand());
        addCommand(new GangMessageCommand());
        addCommand(new GangRankCommand());
        addCommand(new GangTransferCommand());
        addCommand(new GangTurfsCommand());
        addCommand(new GiveBadgeCommand());
        addCommand(new GiveCoinsCommand());
        addCommand(new GiveCommand());
        addCommand(new GiveDiamondsCommand());
        addCommand(new GiveDucketsCommand());
        addCommand(new GiveEventPointsCommand());
        addCommand(new GiveRankCommand());
        addCommand(new GiveRightsCommand());
        addCommand(new GiveSpecialReward());
        addCommand(new GiveVIPCommand());
        addCommand(new GlobalGiveCommand());
        addCommand(new HALCommand());
        addCommand(new HandItemCommand());
        addCommand(new HealCommand());
        addCommand(new HechizosCommand());
        addCommand(new HelpCommand());
        addCommand(new HideWiredCommand());
        addCommand(new HidratacionCommand());
        addCommand(new HijoCommand());
        addCommand(new HireCommand());
        addCommand(new HitCommand());
        addCommand(new HotRoomsCommand());
        addCommand(new HotelAlertCommand());
        addCommand(new HtmlPageCommand());
        addCommand(new HtmlRPageCommand());
        addCommand(new HtmlUIPageCommand());
        addCommand(new HtmlUPageCommand());
        addCommand(new HugCommand());
        addCommand(new IPBanCommand());
        addCommand(new InmunidadCommand());
        addCommand(new InvisibleCommand());
        addCommand(new IrCommand());
        addCommand(new JugarCommand());
        addCommand(new KevlarCommand());
        addCommand(new KickBotsCommand());
        addCommand(new KickCommand());
        addCommand(new KickPetsCommand());
        addCommand(new KillCommand());
        addCommand(new KissCommand());
        addCommand(new LawCommand());
        addCommand(new LawsCommand());
        addCommand(new LayCommand());
        addCommand(new LearningCommand());
        addCommand(new LeaveCamCommand());
        addCommand(new LlorarCommand());
        addCommand(new LoadsCamCommand());
        addCommand(new LocalizarCommand());
        addCommand(new LogOutCommand());
        addCommand(new MIPCommand());
        addCommand(new MPUCommand());
        addCommand(new MaintenanceCommand());
        addCommand(new MakeBotActionCommand());
        addCommand(new MakePetCommand());
        addCommand(new MakeSayCommand());
        addCommand(new ManosCommand());
        addCommand(new MapCommand());
        addCommand(new MarryCommand());
        addCommand(new MasajeCommand());
        addCommand(new MassActionCommand());
        addCommand(new MassBadgeCommand());
        addCommand(new MassDanceCommand());
        addCommand(new MassEnableCommand());
        addCommand(new MasturbarseCommand());
        addCommand(new MeCommand());
        addCommand(new MearCommand());
        addCommand(new MedicinaCommand());
        addCommand(new MimicCommand());
        addCommand(new MoonwalkCommand());
        addCommand(new MuteCommand());
        addCommand(new MyCarsCommand());
        addCommand(new MyNumberCommand());
        addCommand(new NalgadaCommand());
        addCommand(new NameCheckCommand());
        addCommand(new NoleerCommand());
        addCommand(new NoticeHotelAlertCommand());
        addCommand(new OffDutyCommand());
        addCommand(new OfferCommand());
        addCommand(new OffersCommand());
        addCommand(new OnDutyCommand());
        addCommand(new OnlineCommand());
        addCommand(new OpenAccountCommand());
        addCommand(new OpenCommand());
        addCommand(new OpenDimmerCommand());
        addCommand(new OralCommand());
        addCommand(new OverrideCommand());
        addCommand(new PackWelcomeCommand());
        addCommand(new PasajeroCommand());
        addCommand(new PatearCommand());
        addCommand(new PermisoCommand());
        addCommand(new PermisoWeedCommand());
        addCommand(new PetTransformCommand());
        addCommand(new PickAllCommand());
        addCommand(new PincharCommand());
        addCommand(new PingCommand());
        addCommand(new PlaceCommand());
        addCommand(new PoliceTrialCommand());
        addCommand(new PollCommand());
        addCommand(new PonerCommand());
        addCommand(new PonerchalecoCommand());
        addCommand(new PoofCommand());
        addCommand(new PrefixCommand());
        addCommand(new PromoteCommand());
        addCommand(new PullCommand());
        addCommand(new PurchaseEventCommand());
        addCommand(new PurgeCommand());
        addCommand(new PushCommand());
        addCommand(new RPFarmingStatsCommand());
        addCommand(new RPStatsCommand());
        addCommand(new RPWeaponsCommand());
        addCommand(new RadioAlertCommand());
        addCommand(new RapeCommand());
        addCommand(new ReCommand());
        addCommand(new RegenMapsCommand());
        addCommand(new ReirCommand());
        addCommand(new ReleaseAllCommand());
        addCommand(new ReleaseCommand());
        addCommand(new ReloadGunCommand());
        addCommand(new RemoveBountyCommand());
        addCommand(new RenunciarCommand());
        addCommand(new RestoreAllCommand());
        addCommand(new RestoreCommand());
        addCommand(new ReturnBasuCommand());
        addCommand(new ReturnCamCommand());
        addCommand(new ReviewMecCommand());
        addCommand(new ReviewPatientCommand());
        addCommand(new RideCommand());
        addCommand(new RobATMCommand());
        addCommand(new RobBankCommand());
        addCommand(new RobCommand());
        addCommand(new RobartiendaCommand());
        addCommand(new RoomAlertCommand());
        addCommand(new RoomBadgeCommand());
        addCommand(new RoomCommand());
        addCommand(new RoomHealCommand());
        addCommand(new RoomIDCommand());
        addCommand(new RoomInfoCommand());
        addCommand(new RoomKickCommand());
        addCommand(new RoomMakePetCommand());
        addCommand(new RoomMuteCommand());
        addCommand(new RoomReleaseCommand());
        addCommand(new RoomRestoreCommand());
        addCommand(new RoomUnmuteCommand());
        addCommand(new SayAllCommand());
        addCommand(new SearchCommand());
        addCommand(new SecuestrarCommand());
        addCommand(new SellCommand());
        addCommand(new SendRoomCommand());
        addCommand(new SendUserCommand());
        addCommand(new SendhomeCommand());
        addCommand(new ServeCommand());
        addCommand(new ServiceCommand());
        addCommand(new SetPriceommand());
        addCommand(new SetSHCommand());
        addCommand(new SetSpeedCommand());
        addCommand(new SetStatCommand());
        addCommand(new SetWhisperTileCommand());
        addCommand(new SexCommand());
        addCommand(new ShootCommand());
        addCommand(new SitCommand());
        addCommand(new SlapCommand());
        addCommand(new SmokeCommand());
        addCommand(new SmsCommand());
        addCommand(new StaffAlertCommand());
        addCommand(new StandCommand());
        addCommand(new StartQuestionCommand());
        addCommand(new StartWorkCommand());
        addCommand(new StatsCommand());
        addCommand(new StopEventCommand());
        addCommand(new StopSHCommand());
        addCommand(new StopTaxiCommand());
        addCommand(new StopTranslateCommand());
        addCommand(new StopWorkCommand());
        addCommand(new StunCommand());
        addCommand(new SubirCommand());
        addCommand(new SuicidarCommand());
        addCommand(new SummonAllCommand());
        addCommand(new SummonCommand());
        addCommand(new SummonPetsCommand());
        addCommand(new SummonStaffCommand());
        addCommand(new SuperFastwalkCommand());
        addCommand(new SuperHireCommand());
        addCommand(new SuperPullCommand());
        addCommand(new SuperPushCommand());
        addCommand(new SurrenderCommand());
        addCommand(new TLockCommand());
        addCommand(new TakeCoinsCommand());
        addCommand(new TakeDiamondsCommand());
        addCommand(new TakeDucketsCommand());
        addCommand(new TakeVIPCommand());
        addCommand(new TanqueCommand());
        addCommand(new TaxiCommand());
        addCommand(new TeamoCommand());
        addCommand(new TeleportCommand());
        addCommand(new TequieroCommand());
        addCommand(new TextCommand());
        addCommand(new TimeLeftCommand());
        addCommand(new TirarBasuraCommand());
        addCommand(new ToDoCommand());
        addCommand(new ToggleRadioAlertCommand());
        addCommand(new ToggleTextsCommand());
        addCommand(new ToggleVIPAlertCommand());
        addCommand(new ToggleWhispersCommand());
        addCommand(new TransformAllCommand());
        addCommand(new TranslateCommand());
        addCommand(new TrialCommand());
        addCommand(new TutorialCommand());
        addCommand(new TutorialbrCommand());
        addCommand(new UnBanChatterCommand());
        addCommand(new UnBanCommand());
        addCommand(new UnBanVIPCommand());
        addCommand(new UnBlackListCommand());
        addCommand(new UnCuffCommand());
        addCommand(new UnEquipCommand());
        addCommand(new UnFreezeCommand());
        addCommand(new UnFreezeRoomCommand());
        addCommand(new UnIdleCommand());
        addCommand(new UnLawCommand());
        addCommand(new UnStunCommand());
        addCommand(new UnloadCommand());
        addCommand(new UnmuteCommand());
        addCommand(new UpCommand());
        addCommand(new UpdateCommand());
        addCommand(new UseBidonCommand());
        addCommand(new UseBotiquCommand());
        addCommand(new UserInfoCommand());
        addCommand(new VIPAlertCommand());
        addCommand(new VacunaCommand());
        addCommand(new VaultCommand());
        addCommand(new VenderCommand());
        addCommand(new ViewBotiquCommand());
        addCommand(new VisibleCommand());
        addCommand(new VoteCommand());
        addCommand(new WOnlineCommand());
        addCommand(new WantedListCommand());
        addCommand(new WarpAllToMeCommand());
        addCommand(new WarpMeToCommand());
        addCommand(new WarpToMeCommand());
        addCommand(new WeaponsCommand());
        addCommand(new WhatsCommand());
        addCommand(new WhisperHotelAlertCommand());
        addCommand(new WiredCommand());
        addCommand(new WithdrawCommand());
        addCommand(new WorkoutCommand());

    }

    public List<Command> getCommandsForRank(int rankId) {
        List<Command> allowedCommands = new ArrayList<>();
        if (Emulator.getGameEnvironment().getPermissionsManager().rankExists(rankId)) {
            Map<String, Permission> permissions = Emulator.getGameEnvironment()
                    .getPermissionsManager()
                    .getRank(rankId)
                    .getPermissions();

            for (Command command : commands.values()) {
                if (allowedCommands.contains(command)) continue;

                if (permissions.containsKey(command.permission)
                        && permissions.get(command.permission).setting != PermissionSetting.DISALLOWED) {
                    allowedCommands.add(command);
                }
            }
        }

        allowedCommands.sort(CommandHandler.ALPHABETICAL_ORDER);

        return allowedCommands;
    }

    public void dispose() {
        commands.clear();
        LOGGER.info("Command Handler -> Disposed!");
    }
}
