package me.herobrinedobem.htickets;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Commands implements CommandExecutor {

	@Override
	public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args) {
		final Player p;
		if (sender instanceof Player) {
			p = (Player) sender;
			if (cmd.getName().equalsIgnoreCase("ticket")) {
				if (args.length == 0) {
					for (final String s : HTickets.geHTickets().getConfig().getStringList("Mensagens.Default")) {
						p.sendMessage(s.replace("&", "§"));
					}
				} else if (args.length == 1 && args[0].equalsIgnoreCase("lista")) {
					if (args[0].equalsIgnoreCase("lista")) {
						if (p.hasPermission("ticket.admin")) {
							HTickets.geHTickets().getMysql().getAllTicketsOpen(p);
						} else {
							p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_NoPerm")
									.replace("&", "§"));
						}
					} else {
						p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Usage").replace("&",
								"§"));
					}
				} else if (args.length == 2 && args[0].equalsIgnoreCase("tp") || args[0].equalsIgnoreCase("fechar")
						|| args[0].equalsIgnoreCase("abrir") || args[0].equalsIgnoreCase("ler")) {
					if (args[0].equalsIgnoreCase("tp")) {
						if (p.hasPermission("ticket.admin")) {
							try {
								final int id = Integer.valueOf(args[1]);
								if (HTickets.geHTickets().getMysql().hasTicketID(id)) {
									p.teleport(this.getLocation(
											HTickets.geHTickets().getMysql().getTicketValue(id, "localizacao")));
									p.sendMessage(
											HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Teleportado")
													.replace("&", "§").replace("$id$", id + ""));
									return false;
								} else {
									p.sendMessage(HTickets.geHTickets().getConfig()
											.getString("Mensagens.Ticket_Nao_Encontrado").replace("&", "§"));
									return false;
								}
							} catch (final NumberFormatException e) {
								p.sendMessage(HTickets.geHTickets().getConfig()
										.getString("Mensagens.Ticket_Numero_String").replace("&", "§"));
								return false;
							}
						} else {
							p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_NoPerm")
									.replace("&", "§"));
						}
					} else if (args[0].equalsIgnoreCase("fechar")) {
						if (p.hasPermission("ticket.admin")) {
							try {
								final int id = Integer.parseInt(args[1]);
								if (HTickets.geHTickets().getMysql().hasTicketID(id)) {
									if (HTickets.geHTickets().getMysql().getTicketValue(id, "aberto")
											.equalsIgnoreCase("0")) {
										HTickets.geHTickets().getMysql().changeTicketStatus(id, 1);
										p.sendMessage(HTickets.geHTickets().getConfig()
												.getString("Mensagens.Ticket_Fechado_Admin").replace("&", "§"));
										if (HTickets.geHTickets().getServer().getPlayer(HTickets.geHTickets().getMysql()
												.getTicketValue(id, "player")) != null) {
											HTickets.geHTickets().getServer()
													.getPlayer(HTickets.geHTickets().getMysql().getTicketValue(id,
															"player"))
													.sendMessage(HTickets.geHTickets().getConfig()
															.getString("Mensagens.Ticket_Fechado_Player")
															.replace("&", "§").replace("$id$", id + "")
															.replace("$staffer$", p.getName()));
										}
										return false;
									} else {
										p.sendMessage(HTickets.geHTickets().getConfig()
												.getString("Mensagens.Ticket_Ja_Fechado").replace("&", "§"));
										return false;
									}
								} else {
									p.sendMessage(HTickets.geHTickets().getConfig()
											.getString("Mensagens.Ticket_Nao_Encontrado").replace("&", "§"));
									return false;
								}
							} catch (final NumberFormatException e) {
								p.sendMessage(HTickets.geHTickets().getConfig()
										.getString("Mensagens.Ticket_Numero_String").replace("&", "§"));
								return false;
							}
						} else {
							p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_NoPerm")
									.replace("&", "§"));
						}
					} else if (args[0].equalsIgnoreCase("abrir")) {
						if (p.hasPermission("ticket.admin")) {
							try {
								final int id = Integer.parseInt(args[1]);
								if (HTickets.geHTickets().getMysql().hasTicketID(id)) {
									if (HTickets.geHTickets().getMysql().getTicketValue(id, "aberto")
											.equalsIgnoreCase("1")) {
										HTickets.geHTickets().getMysql().changeTicketStatus(id, 0);
										p.sendMessage(HTickets.geHTickets().getConfig()
												.getString("Mensagens.Ticket_Aberto_Admin").replace("&", "§"));
										if (HTickets.geHTickets().getServer().getPlayer(HTickets.geHTickets().getMysql()
												.getTicketValue(id, "player")) != null) {
											HTickets.geHTickets().getServer()
													.getPlayer(HTickets.geHTickets().getMysql().getTicketValue(id,
															"player"))
													.sendMessage(HTickets.geHTickets().getConfig()
															.getString("Mensagens.Ticket_Aberto_Player")
															.replace("&", "§").replace("$id$", id + "")
															.replace("$staffer$", p.getName()));
										}
										return false;
									} else {
										p.sendMessage(HTickets.geHTickets().getConfig()
												.getString("Mensagens.Ticket_Ja_Aberto").replace("&", "§"));
										return false;
									}
								} else {
									p.sendMessage(HTickets.geHTickets().getConfig()
											.getString("Mensagens.Ticket_Nao_Encontrado").replace("&", "§"));
									return false;
								}
							} catch (final NumberFormatException e) {
								p.sendMessage(HTickets.geHTickets().getConfig()
										.getString("Mensagens.Ticket_Numero_String").replace("&", "§"));
								return false;
							}
						} else {
							p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_NoPerm")
									.replace("&", "§"));
						}
					} else if (args[0].equalsIgnoreCase("ler")) {
						try {
							final int id = Integer.parseInt(args[1]);
							if (HTickets.geHTickets().getMysql().hasTicketID(id)) {
								if (HTickets.geHTickets().getMysql().getTicketValue(id, "player")
										.equalsIgnoreCase(p.getName()) || p.hasPermission("ticket.admin")) {
									final int i = Integer
											.parseInt(HTickets.geHTickets().getMysql().getTicketValue(id, "aberto"));
									String status = null;
									if (i == 0) {
										status = "aberto";
									} else {
										status = "fechado";
									}
									for (final String s : HTickets.geHTickets().getConfig()
											.getStringList("Mensagens.Ticket_Ler")) {
										p.sendMessage(
												s.replace("&", "§").replace("$id$", id + "").replace("$status$", status)
														.replace("$player$",
																HTickets.geHTickets().getMysql().getTicketValue(id,
																		"player"))
														.replace("$ticket$", HTickets.geHTickets().getMysql()
																.getTicketValue(id, "ticket")));
									}
									HTickets.geHTickets().getMysql().getAllRespForTicketID(p, id);
									return false;
								} else {
									p.sendMessage(HTickets.geHTickets().getConfig()
											.getString("Mensagens.Ticket_Nao_E_Seu").replace("&", "§"));
									return false;
								}
							} else {
								p.sendMessage(HTickets.geHTickets().getConfig()
										.getString("Mensagens.Ticket_Nao_Encontrado").replace("&", "§"));
								return false;
							}
						} catch (final NumberFormatException e) {
							p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Numero_String")
									.replace("&", "§"));
							return false;
						}
					} else {
						p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Usage").replace("&",
								"§"));
					}
				} else if (args.length > 0 & !(args[0].equalsIgnoreCase("responder"))) {
					if (!HTickets.geHTickets().delay.containsKey(p)) {
						final StringBuilder build = new StringBuilder();
						for (final String s : args) {
							build.append(s + " ");
						}
						HTickets.geHTickets().getMysql().addTicket(p.getName(), build.toString(), p.getLocation());
						p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Enviado")
								.replace("&", "§").replace("$id$",
										HTickets.geHTickets().getMysql().getTicketIDFromTheLast(p.getName()) + ""));
						for (final Player pa : HTickets.geHTickets().getServer().getOnlinePlayers()) {
							if (pa.hasPermission("ticket.admin")) {
								pa.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Novo")
										.replace("&", "§").replace("$player$", p.getName()));
							}
						}
						if (!p.hasPermission("ticket.admin")) {
							HTickets.geHTickets().delay.put(p,
									HTickets.geHTickets().getMysql().getTicketIDFromTheLast(p.getName()));
							new BukkitRunnable() {
								@Override
								public void run() {
									HTickets.geHTickets().delay.remove(p);
								}
							}.runTaskLater(HTickets.geHTickets(), 30 * 20);
						}
						return false;
					} else {
						p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Aguarde")
								.replace("&", "§"));
					}
				} else if (args.length >= 3 & args[0].equalsIgnoreCase("responder")) {
					if (args[0].equalsIgnoreCase("responder")) {
						try {
							final int id = Integer.parseInt(args[1]);
							if (HTickets.geHTickets().getMysql().hasTicketID(id)) {
								if (HTickets.geHTickets().getMysql().getTicketValue(id, "aberto")
										.equalsIgnoreCase("0")) {
									final StringBuilder build = new StringBuilder();
									int loc = 0;
									for (final String s : args) {
										if (loc <= 1) {
											loc++;
										} else {
											build.append(s + " ");
										}
									}
									if (p.hasPermission("ticket.admin")) {
										HTickets.geHTickets().getMysql().addResposta(id, p.getName(), build.toString());
										p.sendMessage(HTickets.geHTickets().getConfig()
												.getString("Mensagens.Ticket_Respondido_Admin").replace("&", "§"));
										if (HTickets.geHTickets().getServer().getPlayer(HTickets.geHTickets().getMysql()
												.getTicketValue(id, "player")) != null) {
											HTickets.geHTickets().getServer()
													.getPlayer(HTickets.geHTickets().getMysql().getTicketValue(id,
															"player"))
													.sendMessage(HTickets.geHTickets().getConfig()
															.getString("Mensagens.Ticket_Respondido_Player")
															.replace("&", "§").replace("$id$", id + "")
															.replace("$staffer$", p.getName()));
										}
									} else {
										if (HTickets.geHTickets().getMysql().getTicketValue(id, "player")
												.equalsIgnoreCase(p.getName())) {
											HTickets.geHTickets().getMysql().addResposta(id, p.getName(),
													build.toString());
											p.sendMessage(HTickets.geHTickets().getConfig()
													.getString("Mensagens.Ticket_Respondido_Admin").replace("&", "§"));
											for (final Player pa : HTickets.geHTickets().getServer()
													.getOnlinePlayers()) {
												if (pa.hasPermission("ticket.admin")) {
													pa.sendMessage(HTickets.geHTickets().getConfig()
															.getString("Mensagens.Ticket_Respondido").replace("&", "§")
															.replace("$id$", id + "").replace("$player$", p.getName()));
												}
											}
										} else {
											p.sendMessage(HTickets.geHTickets().getConfig()
													.getString("Mensagens.Ticket_Nao_E_Seu").replace("&", "§"));
											return false;
										}
									}
									return false;
								} else {
									p.sendMessage(HTickets.geHTickets().getConfig()
											.getString("Mensagens.Ticket_Fechado_Sem_Responder").replace("&", "§"));
									return false;
								}
							} else {
								p.sendMessage(HTickets.geHTickets().getConfig()
										.getString("Mensagens.Ticket_Nao_Encontrado").replace("&", "§"));
								return false;
							}
						} catch (final NumberFormatException e) {
							p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Numero_String")
									.replace("&", "§"));
							return false;
						}
					}
				}
			}

		}
		return false;
	}

	private Location getLocation(final String loc) {
		final World world = HTickets.geHTickets().getServer().getWorld(loc.split(";")[0]);
		final int x = Integer.parseInt(loc.split(";")[1]);
		final int y = Integer.parseInt(loc.split(";")[2]);
		final int z = Integer.parseInt(loc.split(";")[3]);
		return new Location(world, x, y, z);
	}

}
