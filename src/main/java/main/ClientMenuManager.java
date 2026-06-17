package main;

import arc.struct.Seq;
import mindustry.gen.Call;
import mindustry.gen.Groups;
import mindustry.gen.Player;
import mindustry.ui.Menus;
import static main.Cache.BanMenuId;
import static main.Cache.IpBanMenuId;

public class ClientMenuManager {
    public void Init(){
        BanMenuId = Menus.registerMenu((player, selection, String reason, Int period) -> {
            if (selection == -1) return;
            Seq<Player> players = getOthers(player);
            Player banned = players.get(selection);
            player.sendMessage("Banned " + banned.name());
        });

        IpBanMenuId = Menus.registerMenu((player, selection) -> {

        });
    }






    public static void showBanMenu(Player p, String reason,  int period){
        Seq<Player> players = getOthers(p);
        String[][] buttons = new String[players.size][1];
        Call.menu(p.con, BanMenuId, "Ban a player", "Choose a player", buttons, reason, period);
    }

    public static void showIpBanMenu(Player p){
        Seq<Player> players = getOthers(p);
        String[][] buttons = new String[players.size][1];
    }

    private static Seq<Player> getOthers(Player p){
        Seq<Player> players = new Seq<>();
        Groups.player.each(other -> {
           if (other == p){
               return;
           }
           players.add(other);
        });
        return players;
    }
}
