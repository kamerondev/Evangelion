package com.potentiamc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.potentiamc.api.AsukaTab;
import com.potentiamc.api.Reflection;

public class Evangelion implements Listener {

	private JavaPlugin p;
	private boolean add;
	private static Evangelion instance;
	private Map<Integer, String> eight_map;
	private ArrayList<UUID> randomuuids;
	private ArrayList<String> blankStrings;
	
	// REFLECTION AND SOME HELP FROM SOURCES

	public static final Class<Object> gameProfileClass = Reflection
			.getUntypedClasses("net.minecraft.util.com.mojang.authlib.GameProfile", "com.mojang.authlib.GameProfile");
	public static final Reflection.ConstructorInvoker gameProfileConstructor = Reflection
			.getConstructor(gameProfileClass, UUID.class, String.class);
	public static final Reflection.FieldAccessor<UUID> gameProfileUUID = Reflection.getField(gameProfileClass,
			UUID.class, 0);
	public static final Reflection.FieldAccessor<String> gameProfileName = Reflection.getField(gameProfileClass,
			String.class, 0);
	public static final Reflection.FieldAccessor<Object> gameProfilePropertyMap = Reflection.getField(gameProfileClass,
			"properties", Object.class);
	public static final Class<?> tabPacketClass = Reflection.getClass("{nms}.PacketPlayOutPlayerInfo");
	public static final Reflection.ConstructorInvoker tabPacketConstructor = Reflection.getConstructor(tabPacketClass);
	public static final Reflection.FieldAccessor<String> tabPacketName = Reflection.getField(tabPacketClass,
			String.class, 0);
	public static final Reflection.FieldAccessor<Object> tabPacketGameProfile = Reflection.getField(tabPacketClass,
			"player", Object.class);
	public static final Reflection.FieldAccessor<Integer> tabPacketAction = Reflection.getField(tabPacketClass,
			int.class, 5);
	public static final Reflection.FieldAccessor<Integer> tabPacketGamemode = Reflection.getField(tabPacketClass,
			int.class, 6);

	public Evangelion(JavaPlugin plugin, boolean a) {

		if (Bukkit.getMaxPlayers() < 60) {
			throw new NumberFormatException("Player limit must be at least 60!");
		}

		add = a; // IF YOU WANT THIS TO ADD THE PLAYER
		instance = this;
		p = plugin;
		eight_map = new HashMap<>();
		randomuuids = new ArrayList<>();
		blankStrings = new ArrayList<>();

		generateBlankStrings();
		generateMap();
		generateRandoms();

		if (a)
			Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	public ArrayList<String> getBlankStrings() {
		return blankStrings;
	}

	public ArrayList<UUID> getRandomUUIDs() {
		return randomuuids;
	}

	private void generateMap() {
		for (int i = 0; i < 80; i++) {
			eight_map.put(i, "");
		}
	}
	
	private void generateBlankStrings() {
		int characterCounter = 0;

		while (characterCounter < 15) {
			StringBuilder sb = new StringBuilder();
			characterCounter++;
			for (int x = 0; x <= characterCounter; x++) {
				sb.append(" ");
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			StringBuilder sb = new StringBuilder();
			characterCounter++;
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26f7);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			StringBuilder sb = new StringBuilder();
			characterCounter++;
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26f8);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			characterCounter++;
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26c7);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 15) {
			characterCounter++;
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26c9);
			}
			this.blankStrings.add(sb.toString());
		}
		characterCounter = 0;

		while (characterCounter < 5) {
			characterCounter++;
			StringBuilder sb = new StringBuilder();
			for (int x = 0; x <= characterCounter; x++) {
				sb.append((char) 0x26cc);
			}
			this.blankStrings.add(sb.toString());
		}
	}

	private void generateRandoms() {
		for (int x = 0; x < 80; x++) {
			String uuid = UUID.randomUUID().toString();

			while (randomuuids.contains(UUID.fromString(uuid))) {
				uuid += x;
			}

			randomuuids.add(UUID.fromString(uuid));
		}
	}
	
	public Map<Integer, String> getPackets() {
		return eight_map;
	}

	public JavaPlugin getPlugin() {
		return p;
	}

	public static Evangelion getInstance() {
		return instance;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		new AsukaTab(player);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();

		AsukaTab tab = AsukaTab.getAsukaTab(player);
		if (tab != null) {
			AsukaTab.getAsukaTabs().remove(tab);
		}

	}
}
