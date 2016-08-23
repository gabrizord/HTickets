package me.herobrinedobem.htickets;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class HTickets extends JavaPlugin {

	private MySQL mysql;
	Map<Player, Integer> delay = new HashMap<Player, Integer>();
	
	@Override
	public void onEnable() {
		if (!new File(this.getDataFolder(), "config.yml").exists()) {
			this.saveDefaultConfig();
		}
		this.getCommand("ticket").setExecutor(new Commands());
		this.mysql = new MySQL(this.getConfig().getString("MySQL.Username"), this.getConfig().getString("MySQL.Password"), this.getConfig().getString("MySQL.Database"), this.getConfig().getString("MySQL.Host"));
		System.out.println("[HTickets] Plugin Habilitado - Versao(" + this.getDescription().getVersion() + ")");
	}

	@Override
	public void onDisable() {
		System.out.println("[HTickets] Plugin Desabilitado - Versao(" + this.getDescription().getVersion() + ")");
		delay.clear();
	}

	public void scheduler() {
		final BukkitScheduler scheduler = HTickets.geHTickets().getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, new Runnable() {
			@Override
			public void run() {
				for (final Player p : HTickets.this.getServer().getOnlinePlayers()) {
					if (p.hasPermission("ticket.admin")) {
						p.sendMessage(HTickets.this.getConfig().getString("Mensagens.Tickets_Abertos_Scheduler").replace("&", "§").replace("tickets", HTickets.this.mysql.getTicketsOpenNumber() + ""));
					}
				}
			}
		}, 60 * 20, 0);
	}

	public static HTickets geHTickets() {
		return (HTickets) Bukkit.getServer().getPluginManager().getPlugin("HTickets");
	}

	public MySQL getMysql() {
		return this.mysql;
	}

}
