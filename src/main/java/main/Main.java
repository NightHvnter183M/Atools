package main;

import arc.util.CommandHandler;
import arc.util.Log;
import mindustry.game.EventType;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import arc.Events;

import java.util.Objects;


public class Main extends Plugin {

    @Override
    public void init() {
        BanDB.init();
        new ClientMenuManager().Init();
        Events.on(EventType.PlayerConnect.class, event -> {
            String uuid = event.player.uuid();
            String ip = event.player.con.address; // Исправлено .ip() на .con.address
            String reason = BanDB.getBanReason(uuid, ip);
            if (reason != null) {
                event.player.kick("You have been banned!\nReason: " + reason);
            }
        });
    }

    @Override
    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("aban", "<reason> <period_minutes>", "Bans player by opening GUI menu", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("[red]Not enough permissions!");
                return;
            }

            String reason = args[0];
            int period;
            try {
                period = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("[red]Invalid period! Insert integer.");
                return;
            }

            ClientMenuManager.showBanMenu(player, reason, period);
        });

        handler.<Player>register("aipban", "<reason> <period_minutes>", "IP Bans player by opening GUI menu", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("[red]Not enough permissions!");
                return;
            }

            String reason = args[0];
            int period;
            try {
                period = Integer.parseInt(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("[red]Invalid period!");
                return;
            }

            ClientMenuManager.showIpBanMenu(player, reason, period);
        });

        handler.<Player>register("aunban", "Removes player from the ban list (Name, UUID or IP)", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("[red]Not enough permissions!");
                return;
            }

            ClientMenuManager.ShowUnBanMenu(player);
        });
    }

    @Override
    public void registerServerCommands(CommandHandler handler){

        handler.register("aban", "<uuid> <reason> <period>", "Bans player by uuid", (args) -> {
            String uuid = args[0];
            String reason = args[1];
            final Player[] banned = new Player[1];
            int period;
            try {
                period = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                Log.info("[red]Invalid period!");
                return;
            }
            Groups.player.each(other -> {
                if (Objects.equals(other.uuid(), uuid)) {
                    banned[0] = other;
                }
            });
            BanDB.ban(uuid, null, reason, period, banned[0].name);
            Log.info("[red]Successfully banned player " + banned[0].name);
        } );

        handler .register("aipban", "<ip> <reason> <period>", "IP Bans player by ip", (args) -> {
            String ip = args[0];
            String reason = args[1];
            final Player[] banned = new Player[1];
            int period;
            try {
                period = Integer.parseInt(args[2]);
            }  catch (NumberFormatException e) {
                Log.info("[red]Invalid period!");
                return;
            }
            Groups.player.each(other -> {
                if (Objects.equals(other.con.address, ip)) {
                    banned[0] = other;
                }
            });
            BanDB.ban(ip, null, reason, period, banned[0].name);
            Log.info("[red]Successfully banned player " + banned[0].name + "by ip: " + ip);
        });



        handler.register("aunban", "<target>", "Removes player from the banlist by UUID", args -> {
            boolean success = BanDB.unBan(args[0], null);
            if (success) {
                Log.info("Target successfully unbanned.");
            } else {
                Log.info("Target not found in ban database.");
            }
        });

    }
}