package main;

import arc.struct.ObjectMap;
import arc.struct.Seq;
import mindustry.gen.Player;

public class Cache {
    public static int BanMenuId, IpBanMenuId, UnBanMenuId;
    public static class BanContext {
        public String reason;
        public int period;
        public BanContext(String reason, int period) {
            this.reason = reason;
            this.period = period;
        }
    }
    public static class IpBanContext {
        public String reason;
        public int period;
        public IpBanContext(String reason, int period) {
            this.reason = reason;
            this.period = period;
        }
    }
    public static ObjectMap<Player, BanContext> banSessions = new ObjectMap<>();
    public static ObjectMap<Player, IpBanContext> ipBanSessions = new ObjectMap<>();
    public static ObjectMap<Player, Seq<BanDB.BanEntry>> unbanSessions = new ObjectMap<>();
}
