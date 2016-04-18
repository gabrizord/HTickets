package me.herobrinedobem.htickets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class MySQL {

	private String user, password, database, host;
	private Connection connection;
	private Statement stmt;

	public MySQL(final String user, final String password, final String database, final String host) {
		try {
			this.user = user;
			this.password = password;
			this.database = database;
			this.host = host;
			this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", user, password);
			this.stmt = this.connection.createStatement();
			this.stmt.execute("CREATE TABLE IF NOT EXISTS tickets (id INTEGER NOT NULL UNIQUE AUTO_INCREMENT, player VARCHAR(255), ticket VARCHAR(255), localizacao VARCHAR(255), aberto INTEGER)");
			this.stmt.execute("CREATE TABLE IF NOT EXISTS respostas (id INTEGER, staffer VARCHAR(255), resposta VARCHAR(255))");
		} catch (final SQLException e) {
			e.printStackTrace();
		}
	}

	public void getAllRespForTicketID(final Player p, final int id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "SELECT * FROM respostas WHERE id='" + id + "'";
			final ResultSet rs = this.stmt.executeQuery(sql);
			while (rs.next()) {
				p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_Respostas_Template").replace("&", "§").replace("$resp$", rs.getString("staffer")).replace("$mensagem$", rs.getString("resposta")));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public int getAllTicketsOpen(final Player p) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "SELECT * FROM tickets WHERE aberto='" + 0 + "' ORDER BY id";
			final ResultSet rs = this.stmt.executeQuery(sql);
			p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_List").replace("&", "§"));
			if (rs.next()) {
				while (rs.next()) {
					p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_List_Template").replace("&", "§").replace("$id$", rs.getInt("id") + "").replace("$player$", rs.getString("player")));
				}
			} else {
				p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Nenhum_Ticket_Aberto").replace("&", "§"));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getTicketsOpenNumber() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "SELECT * FROM tickets WHERE aberto='" + 0 + "' ORDER BY id";
			final ResultSet rs = this.stmt.executeQuery(sql);
			int i = 0;
			if (rs.next()) {
				i++;
			}
			return i;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getAllTicketsOpenPlayer(final Player p) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "SELECT * FROM tickets WHERE aberto='" + 0 + "' ORDER BY id";
			final ResultSet rs = this.stmt.executeQuery(sql);
			p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_List").replace("&", "§"));
			if (rs.next()) {
				while (rs.next()) {
					if (rs.getString("player").equalsIgnoreCase(p.getName())) {
						p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Ticket_List_Template").replace("&", "§").replace("$id$", rs.getInt("id") + "").replace("$player$", rs.getString("player")));
					}
				}
			} else {
				p.sendMessage(HTickets.geHTickets().getConfig().getString("Mensagens.Nenhum_Ticket_Aberto").replace("&", "§"));
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public int getTicketIDFromTheLast(final String player) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "SELECT * FROM tickets WHERE player='" + player + "' ORDER BY id DESC LIMIT 1";
			final ResultSet rs = this.stmt.executeQuery(sql);
			while (rs.next()) {
				return rs.getInt("id");
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	public void addResposta(final int id, final String player, final String mensagem) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "INSERT INTO respostas (id, Staffer, resposta) VALUES ('" + id + "', '" + player + "', '" + mensagem + "');";
			this.stmt.executeUpdate(sql);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void addTicket(final String player, final String mensagem, final Location loc) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "INSERT INTO tickets (player, ticket, localizacao, aberto) VALUES ('" + player + "', '" + mensagem + "', '" + this.getLocationSerialize(loc) + "', '" + 0 + "');";
			this.stmt.executeUpdate(sql);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public void changeTicketStatus(final int id, final int status) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "UPDATE tickets SET aberto='" + status + "' WHERE id='" + id + "';";
			this.stmt.executeUpdate(sql);
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	public boolean hasTicketID(final int id) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			final String sql = "SELECT * FROM tickets WHERE id='" + String.valueOf(id) + "'";
			final ResultSet rs = this.stmt.executeQuery(sql);
			while (rs.next()) {
				if (rs.getInt("id") == id) {
					return true;
				} else {
					return false;
				}
			}
			return false;
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public String getTicketValue(final int id, final String value) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			if (value.equals("ticket")) {
				final String sql = "SELECT ticket FROM tickets WHERE id='" + id + "';";
				final ResultSet rs = this.stmt.executeQuery(sql);
				while (rs.next()) {
					return rs.getString("ticket");
				}
			} else if (value.equals("localizacao")) {
				final String sql = "SELECT localizacao FROM tickets WHERE id='" + id + "';";
				final ResultSet rs = this.stmt.executeQuery(sql);
				while (rs.next()) {
					return rs.getString("localizacao");
				}
			} else if (value.equals("player")) {
				final String sql = "SELECT player FROM tickets WHERE id='" + id + "';";
				final ResultSet rs = this.stmt.executeQuery(sql);
				while (rs.next()) {
					return rs.getString("player");
				}
			} else if (value.equals("aberto")) {
				final String sql = "SELECT aberto FROM tickets WHERE id='" + id + "';";
				final ResultSet rs = this.stmt.executeQuery(sql);
				while (rs.next()) {
					return String.valueOf(rs.getInt("aberto"));
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getLocationSerialize(final Location loc) {
		final String world = loc.getWorld().getName();
		final String x = String.valueOf(loc.getX());
		final String y = String.valueOf(loc.getY());
		final String z = String.valueOf(loc.getZ());
		return world + ";" + x + ";" + y + ";" + z;
	}

	public String getUser() {
		return this.user;
	}

	public void setUser(final String user) {
		this.user = user;
	}

	public String getPassword() {
		return this.password;
	}

	public void setPassword(final String password) {
		this.password = password;
	}

	public String getDatabase() {
		return this.database;
	}

	public void setDatabase(final String database) {
		this.database = database;
	}

	public String getHost() {
		return this.host;
	}

	public void setHost(final String host) {
		this.host = host;
	}

	public Connection getConnection() {
		return this.connection;
	}

	public void setConnection(final Connection connection) {
		this.connection = connection;
	}

	public Statement getStmt() {
		return this.stmt;
	}

	public void setStmt(final Statement stmt) {
		this.stmt = stmt;
	}

}
