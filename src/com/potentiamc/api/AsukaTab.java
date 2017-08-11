package com.potentiamc.api;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

import com.potentiamc.Evangelion;

import net.minecraft.server.v1_7_R4.EntityPlayer;
import net.minecraft.server.v1_7_R4.MinecraftServer;
import net.minecraft.server.v1_7_R4.Packet;
import net.minecraft.server.v1_7_R4.PacketPlayOutPlayerInfo;
import net.minecraft.server.v1_7_R4.PlayerInteractManager;
import net.minecraft.util.com.mojang.authlib.GameProfile;

public class AsukaTab {

	private static Set<AsukaTab> tabs = new HashSet<>();
	private Player player;
	private Scoreboard board;
	private boolean eight;
	private UUID uuid;
	private String[][] entries;
	private Team[][] teams;
	private EntityPlayer[][] nms;
	private final ConcurrentHashMap<Integer, String> packets;
	
	public AsukaTab(Player player) {
		this(player, !player.getScoreboard().equals(Bukkit.getScoreboardManager().getMainScoreboard())
				? Bukkit.getScoreboardManager().getNewScoreboard() : player.getScoreboard());
	}

	public AsukaTab(Player player, Scoreboard board) {
		this.player = player;
		this.eight = (((CraftPlayer) player).getHandle().playerConnection.networkManager.getVersion() >= 47);
		this.board = board;
		this.uuid = UUID.randomUUID();
		this.entries = new String[3][20];
		this.nms = new EntityPlayer[3][20];
		this.teams = new Team[3][20];

		if (eight) {
			this.teams = new Team[81][1];
			this.packets = new ConcurrentHashMap<>(Evangelion.getInstance().getPackets());
			sendTab(eight);
		} else {
			this.packets = null;
			clear1_7();
		}
		
		tabs.add(this);
	}

	private void clear1_7() {
		for (int i = 0; i < 60; i++) {
			int x = i % 3;
			int y = i / 3;
			if (nms[x][y] != null) {
				PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(nms[x][y]);
				((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
			}
		}
		for (Player online : Bukkit.getServer().getOnlinePlayers()) {
			PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.removePlayer(((CraftPlayer) online).getHandle());
			((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);

		}

		entries = new String[3][20];
		nms = new EntityPlayer[3][20];
		assemble();
	}
	
	private void clear1_8() {
			for (UUID uuid : Evangelion.getInstance().getRandomUUIDs()) {
				try {
					Object gameProfile = Evangelion.gameProfileConstructor.invoke(uuid, "");

					Object packet = Evangelion.tabPacketClass.newInstance();
					Evangelion.tabPacketAction.set(packet, 4); // Remove Player
					Evangelion.tabPacketGameProfile.set(packet, gameProfile);

					((CraftPlayer) player).getHandle().playerConnection.sendPacket((Packet)packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

	}

	private void assemble() {

		for (int i = 0; i < 60; i++) {
			int x = i % 3;
			int y = i / 3;
			put(x, y, getNextBlank());
		}

	}

	public String getNextBlank() {
		outer: for (String string : getAllBlanks()) {
			for (int i = 0; i < 60; i++) {
				int x = i % 3;
				int y = i / 3;
				if (entries[x][y] != null && entries[x][y].startsWith(string)) {
					continue outer;
				}
			}
			return string;
		}
		return null;
	}
	
	public String getNextBlank1_8() {
		outer: for (String string : getAllBlanks()) {
			for (int i = 0; i < 60; i++) {
				int x = i % 3;
				int y = i / 3;
				if (entries[x][y] != null && entries[x][y].startsWith(string)) {
					continue outer;
				}
			}
			return string;
		}
		return null;
	}

	public void put(int x, int y, String text) {
		entries[x][y] = text;
		CraftPlayer craftplayer = (CraftPlayer) player;

		if (nms[x][y] != null) {
			if (teams[x][y] != null) {
				length(teams[x][y], text);
			}
		} else {

			if (eight) {
				put1_8(x * y, text);

			} else {
				nms[x][y] = new EntityPlayer(MinecraftServer.getServer(), ((CraftWorld) player.getWorld()).getHandle(),
						new GameProfile(UUID.randomUUID(), text),
						new PlayerInteractManager(((CraftWorld) player.getWorld()).getHandle()));

				PacketPlayOutPlayerInfo packet = PacketPlayOutPlayerInfo.updateDisplayName(nms[x][y]);
				craftplayer.getHandle().playerConnection.sendPacket(packet);

				teams[x][y] = board.registerNewTeam(UUID.randomUUID().toString().substring(0, 16));
				teams[x][y].addEntry(nms[x][y].getName());
			}

		}

	}

	public void put1_8(int id, String text) {
		String oldEntry = packets.get(id);
		packets.put(id, text);

		try {
			Object gameProfile = Evangelion.gameProfileConstructor
					.invoke(Evangelion.getInstance().getRandomUUIDs().get(id), oldEntry);

			Object packet = Evangelion.tabPacketClass.newInstance();
			Evangelion.tabPacketAction.set(packet, 3);
			Evangelion.tabPacketGameProfile.set(packet, gameProfile);
			
			length(teams[id][0], text);

			((CraftPlayer) this.player).getHandle().playerConnection.sendPacket((Packet) packet);
		} catch (Exception e) {
			e.printStackTrace();
		}

		/*try {
			Object gameProfile = Evangelion.gameProfileConstructor
					.invoke(Evangelion.getInstance().getRandomUUIDs().get(((x) * y) - 1), text);

			Object packet = Evangelion.tabPacketClass.newInstance();
			Evangelion.tabPacketAction.set(packet, 0);
			Evangelion.tabPacketGameProfile.set(packet, gameProfile);

			((CraftPlayer) this.player).getHandle().playerConnection.sendPacket((Packet) packet);
		} catch (Exception e) {
			e.printStackTrace();
		}*/

	}

	private void sendTab(boolean b) {

		if (b) {
			ArrayList<String> blanks = new ArrayList<>(Evangelion.getInstance().getBlankStrings());
			/*for (Integer positionID : this.packets.keySet()) {
				String entry = this.packets.get(positionID);
			}*/
			for (Integer positionID : this.packets.keySet()) {
				//int x = i % 3;
				//int y = i / 3;
				String entry = this.packets.get(positionID);
				System.out.println(String.valueOf(positionID));
				
				if (entry == null || entry.equals("")) {
					entry = blanks.get(0);
					blanks.remove(0);
				}

				Team team = teams[positionID][0];
				if (team == null) {
					teams[positionID][0] = board.registerNewTeam(UUID.randomUUID().toString().substring(0, 16));
				}

				teams[positionID][0].addEntry(entry);

				try {
					Object packet = Evangelion.tabPacketClass.newInstance();
					Evangelion.tabPacketAction.set(packet, 0);

					Object gameProfile = Evangelion.gameProfileConstructor
							.invoke(Evangelion.getInstance().getRandomUUIDs().get(positionID), entry);

					Evangelion.tabPacketGameProfile.set(packet, gameProfile);
					Evangelion.tabPacketGamemode.set(packet, 4);

					((CraftPlayer) this.player).getHandle().playerConnection.sendPacket((Packet) packet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} else {

		}
	}

	
	public void length(Team team, String text) {

		text = ChatColor.translateAlternateColorCodes('&', text);

		if (text.length() > 16) {
			team.setPrefix(text.substring(0, 16));
			String suffix = ChatColor.getLastColors(team.getPrefix()) + text.substring(16, text.length());
			if (suffix.length() > 16) {
				if (suffix.length() <= 16) {
					suffix = text.substring(16, text.length());
					team.setSuffix(suffix.substring(0, suffix.length()));
				} else {
					team.setSuffix(suffix.substring(0, 16));
				}
			} else {
				team.setSuffix(suffix);
			}
		} else {
			team.setPrefix(text);
			team.setSuffix("");
		}

	}

	public UUID getUniqueId() {
		return uuid;
	}

	public Player getPlayer() {
		return player;
	}

	public static AsukaTab getAsukaTab(Player player) {

		AsukaTab found = null;

		for (AsukaTab tab : tabs) {
			if (tab.getPlayer().getUniqueId() == player.getUniqueId()) {
				found = tab;
				break;
			}
		}

		return found;

	}
	private List<String> getAllBlanks1_8() {
		List<String> toReturn = new ArrayList<>();
		for (ChatColor chatColor : ChatColor.values()) {
			toReturn.add(chatColor + "" + ChatColor.RESET);
			for (ChatColor chatColor1 : ChatColor.values()) {

				if (toReturn.size() >= 81) {
					return toReturn;
				}

				toReturn.add(chatColor + "" + chatColor1 + ChatColor.RESET);
			}
		}

		return toReturn;
	}
	
	private List<String> getAllBlanks() {
		List<String> toReturn = new ArrayList<>();
		for (ChatColor chatColor : ChatColor.values()) {
			toReturn.add(chatColor + "" + ChatColor.RESET);
			for (ChatColor chatColor1 : ChatColor.values()) {

				if (toReturn.size() >= 60) {
					return toReturn;
				}

				toReturn.add(chatColor + "" + chatColor1 + ChatColor.RESET);
			}
		}

		return toReturn;
	}

	public static Set<AsukaTab> getAsukaTabs() {
		return tabs;
	}
}
