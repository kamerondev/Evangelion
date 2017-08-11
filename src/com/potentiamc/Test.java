package com.potentiamc;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.potentiamc.api.AsukaTab;

public class Test extends JavaPlugin implements Listener {

	public void onEnable() {
		Evangelion evan = new Evangelion(this, true);
		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@EventHandler
	public void onMove(PlayerMoveEvent event) {
		if (event.getTo().getBlockX() != event.getFrom().getBlockX()
				|| event.getTo().getBlockY() != event.getFrom().getBlockY()) {

			AsukaTab tab = AsukaTab.getAsukaTab(event.getPlayer());
			if(tab != null) {
				//tab.put(0, 0, String.valueOf(event.getTo().getBlockX()) + " X");
				//tab.put(1, 1, String.valueOf(event.getTo().getBlockY()) + " Y");
				tab.put1_8(0, "hl");
				tab.put1_8(1, "ha");
				tab.put1_8(2, "hoa");
			}
			
			
		}
	}

}
