package main;

import arc.struct.Seq;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import static main.Cache.*;

public class ClientMenuManager {

    public void Init(){
        BanMenuId = Menus.registerMenu((player, selection) -> {
            Cache.BanContext context = banSessions.remove(player);
            if (selection == -1 || context == null) return;
            Seq<Player> players = getOthers(player);
            if(selection >= players.size) return;
            Player banned = players.get(selection);
            player.sendMessage("[green]Successful banned " + banned.name() + " by: " + context.reason);
            banned.kick("You have been banned. Reason: " + context.reason + " for " + context.period + " min.");
            BanDB.ban(banned.uuid(), null, context.reason, context.period, banned.name());
        });

        IpBanMenuId = Menus.registerMenu((player, selection) -> {
            Cache.IpBanContext context = ipBanSessions.remove(player);
            if (selection == -1 || context == null) return;
            Seq<Player> players = getOthers(player);
            if(selection >= players.size) return;
            Player banned = players.get(selection);
            player.sendMessage("[green]Successful banned by IP " + banned.name());
            banned.kick("You have been banned by IP. Reason: " + context.reason);
            BanDB.ban(banned.uuid(), banned.con.address, context.reason, context.period, banned.name());
        });

        UnBanMenuId = Menus.registerMenu((player, selection) -> {
            Seq<BanDB.BanEntry> entries = unbanSessions.remove(player);
            if (selection == -1 || entries == null || selection >= entries.size) return;
            BanDB.BanEntry target = entries.get(selection);
            boolean success = BanDB.unBan(target.uuid, target.ip);
            if (success) {
                String label = target.name != null ? target.name : (target.ip != null ? target.ip : "Unknown");
                player.sendMessage("[green]Successful unbanned: " + label);
            } else {
                player.sendMessage("[red]Error unbanned: ).");
            }
        });
    }

    public static void showBanMenu(Player p, String reason, int period){
        Seq<Player> players = getOthers(p);
        String[][] buttons = new String[players.size][1];
        for(int i = 0; i < players.size; i++) {
            buttons[i][0] = players.get(i).name();
        }
        banSessions.put(p, new BanContext(reason, period));
        Call.menu(p.con, BanMenuId, "Ban a player", "Choose a player to ban", buttons);
    }

    public static void showIpBanMenu(Player p, String reason, int period){
        Seq<Player> players = getOthers(p);
        String[][] buttons = new String[players.size][1];
        for(int i = 0; i < players.size; i++) {
            buttons[i][0] = players.get(i).name();
        }
        ipBanSessions.put(p, new IpBanContext(reason, period));
        Call.menu(p.con, IpBanMenuId, "IP Ban a player", "Choose a player to IP ban", buttons);
    }

    public static void ShowUnBanMenu(Player p){
        Seq<BanDB.BanEntry> entries = BanDB.getBannedEntries();
        if (entries.isEmpty()) {
            p.sendMessage("[yellow]List is empty.");
            return;
        }
        String[][] buttons = new String[entries.size][1];
        for (int i = 0; i < entries.size; i++) {
            BanDB.BanEntry entry = entries.get(i);
            String typePrefix = (entry.uuid == null && entry.ip != null) ? "[orange][IP-Ban] " : "[red][Ban] ";
            String targetName = entry.name != null ? entry.name : (entry.ip != null ? entry.ip : "Unknown");
            String reason = entry.reason != null ? " (" + entry.reason + ")" : "";

            buttons[i][0] = typePrefix + targetName + reason;
        }
        unbanSessions.put(p, entries);
        Call.menu(p.con, UnBanMenuId, "[red]Manage bans", "Shoose a player:", buttons);
    }
    private static Seq<Player> getOthers(Player p){
        Seq<Player> players = new Seq<>();
        Groups.player.each(other -> {
            if (other != p){
                players.add(other);
            }
        });
        return players;
    }
}