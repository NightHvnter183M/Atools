package main;
import arc.util.CommandHandler;
import mindustry.game.EventType;
import mindustry.gen.Player;
import mindustry.mod.Plugin;
import mindustry.game.EventType.*;
import arc.Events;

public class Main extends Plugin {
    @Override

    public void init() {
        Events.on(EventType.PlayerJoin.class, event -> {

        });

    }

    public void registerClientCommands(CommandHandler handler){
        handler.<Player>register("aban", "Bans player by uuid, (reason), (period in hours)", (args, player) -> {
            String reason = args[0];
            int period = 0;
            try {
                period = Integer.valueOf(args[1]);
            } catch (NumberFormatException e) {
                player.sendMessage("Invalid period");
                return;
            }
            if (!player.admin) {
                player.sendMessage("Not enough permissions");
            }
            else {
                main.ClientMenuManager.showBanMenu(player, reason, period);
            }
        });

        handler.<Player>register("aunban", "Removes player from the ban list", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("Not enough permissions");
                return;
            }
            else{

            }
        });

        handler.<Player>register("aipban", "Bans player by ip", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("Not enough permissions");
                return;
            }
            else{
                main.ClientMenuManager.showIpBanMenu(player);
            }
        });

        handler.<Player>register("aipunban", "Removes player from the ipban list", (args, player) -> {
            if (!player.admin) {
                player.sendMessage("Not enough permissions");
                return;
            }
            else{

            }
        });

        handler.<Player>register("showbanid", "Shows uuod's and names of players from the ban list",  (args, player) -> {
            if (!player.admin) {
                player.sendMessage("Not enough permissions");
                return;
            }
            else{

            }
        });

        handler.<Player>register("showipbanid", "Shows uuod's and names of players from the ipban list",  (args, player) -> {
            if (!player.admin) {
                player.sendMessage("Not enough permissions");
                return;
            }
            else{

            }
        });
    }

    public void registerServerCommands(CommandHandler handler){
        handler.register("aban", "Bans player by uuid", (args, player) -> {
            String uuid = args[0];
        });

        handler.register("aunban", "Removes player from the banlist", (args, player) -> {
            String uuid = args[0];
        });

        handler.register("aipban", "Bans player by ip", (args, player) -> {
           String ip = args[0];
        });

        handler.register("aipunban", "Removes player from the ipban list", (args, player) -> {
            String ip = args[0];
        });
    }
}
