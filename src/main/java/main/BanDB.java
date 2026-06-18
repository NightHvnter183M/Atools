package main;

import arc.Core;
import arc.util.Log;
import arc.struct.Seq;
import org.sqlite.SQLiteDataSource;
import java.io.File;
import java.sql.*;

public class BanDB {
    private static Connection conn;
    public static class BanEntry {
        public String uuid;
        public String ip;
        public String name;
        public String reason;
        public BanEntry(String uuid, String ip, String name, String reason) {
            this.uuid = uuid;
            this.ip = ip;
            this.name = name;
            this.reason = reason;
        }
    }

    public static void init(){
        try {
            File dir = Core.settings.getDataDirectory().child("mods/ATools").file();
            if (!dir.exists()) dir.mkdirs();

            String url = "jdbc:sqlite:" + new File(dir, "bans.db").getAbsolutePath();
            SQLiteDataSource ds = new SQLiteDataSource();
            ds.setUrl(url);
            conn = ds.getConnection();
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("""
                    CREATE TABLE IF NOT EXISTS bans (
                        uuid TEXT,
                        ip TEXT,
                        name TEXT,
                        reason TEXT,
                        expiry INTEGER
                    );
                """);
            }
        } catch (SQLException e) {
            Log.err("Error initializing ban database", e);
        }
    }
    public static void ban(String uuid, String ip, String reason, int period, String name){
        String sql = "INSERT INTO bans(uuid, ip, name, reason, expiry) VALUES(?, ?, ?, ?, ?)";
        long expiryTime = (period == -1) ? -1 : System.currentTimeMillis() + ((long) period * 60 * 1000);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, ip);
            pstmt.setString(3, name);
            pstmt.setString(4, reason);
            pstmt.setLong(5, expiryTime);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Seq<BanEntry> getBannedEntries() {
        Seq<BanEntry> entries = new Seq<>();
        String sql = "SELECT uuid, ip, name, reason, expiry FROM bans";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                long expiry = rs.getLong("expiry");
                if (expiry != -1 && expiry <= System.currentTimeMillis()) {
                    continue;
                }
                entries.add(new BanEntry(
                        rs.getString("uuid"),
                        rs.getString("ip"),
                        rs.getString("name"),
                        rs.getString("reason")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return entries;
    }
    public static boolean unBan(String uuid, String ip){
        String sql = "DELETE FROM bans WHERE (uuid = ? AND uuid IS NOT NULL) OR (ip = ? AND ip IS NOT NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, ip);
            int rowsAffected = pstmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void deleteExpired(String uuid) {
        String sql = "DELETE FROM bans WHERE uuid = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public static String getBanReason(String uuid, String ip){
        String sql = "SELECT reason, expiry FROM bans WHERE (uuid = ? AND uuid IS NOT NULL) OR (ip = ? AND ip IS NOT NULL)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uuid);
            pstmt.setString(2, ip);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    long expiry = rs.getLong("expiry");
                    if (expiry == -1 || expiry > System.currentTimeMillis()) {
                        return rs.getString("reason");
                    } else {
                        deleteExpired(uuid);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}