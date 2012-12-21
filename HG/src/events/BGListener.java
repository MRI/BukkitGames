package events;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.CropState;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.entity.Vehicle;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityTargetEvent.TargetReason;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.vehicle.VehicleMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.bukkit.inventory.meta.BookMeta;

import pgDev.bukkit.DisguiseCraft.disguise.DisguiseType;

import utilities.BGChat;
import utilities.BGCornucopia;
import utilities.BGDisguise;
import utilities.BGFBattle;
import utilities.BGFeast;
import utilities.BGFiles;
import utilities.BGKit;
import utilities.BGTeam;
import utilities.BGVanish;
import utilities.CraftBook;
import utilities.Updater;

public class BGListener implements Listener {
	Logger log = BGMain.getPluginLogger();
	private BGMain plugin;
	private String last_quit;
	private String last_headshot;
	
	public ArrayList<Player> viperList = new ArrayList<Player>();
	public ArrayList<Player> monkList = new ArrayList<Player>();
	public ArrayList<Player> thiefList = new ArrayList<Player>();
	public ArrayList<Player> ghostList = new ArrayList<Player>();
	public ArrayList<Player> thorList = new ArrayList<Player>();
	public ArrayList<Player> timeList = new ArrayList<Player>();
	public ArrayList<Player> freezeList = new ArrayList<Player>();

	
	public BGListener(BGMain instance) {
		this.plugin = instance;
		this.plugin.getServer().getPluginManager()
				.registerEvents(this, this.plugin);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		Player p = (Player) event.getWhoClicked();
		if(plugin.isSpectator(p))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void onVehicleMove(VehicleMoveEvent event) {
		if (this.plugin.DENY_CHECK_WORLDBORDER.booleanValue()) {
			return;
		}
		if (this.plugin.inBorder(event.getTo()))
			return;
		Vehicle s = event.getVehicle();
		if (s.isEmpty())
			return;
		Entity Passenger = s.getPassenger();
		if (!(Passenger instanceof Player))
			return;

		BGChat.printPlayerChat((Player) Passenger, this.plugin.WORLD_BORDER_MSG);
		s.teleport(event.getFrom());
		this.plugin.getClass();
		Bukkit.getServer().getWorld("world")
				.playEffect(s.getLocation(), Effect.ENDER_SIGNAL, 5);
		this.plugin.getClass();
		Bukkit.getServer().getWorld("world")
				.playEffect(s.getLocation(), Effect.SMOKE, 5);
	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		Player p = event.getPlayer();
		Action a = event.getAction();
		if(plugin.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}
		
		if (this.plugin.DENY_BLOCKBREAK.booleanValue()
				& (!p.hasPermission("bg.admin.editblocks") || !p.hasPermission("bg.admin.*"))) {
			event.setCancelled(true);
			return;
		}

		if (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK) {
			if ((BGKit.hasAbility(p, Integer.valueOf(5)) & p.getItemInHand()
					.getType() == Material.COOKIE)) {
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.INCREASE_DAMAGE, BGFiles.abconf.getInt("AB.5.Duration") * 20, 0));
				p.getInventory().removeItem(
						new ItemStack[] { new ItemStack(Material.COOKIE, 1) });
			}
		}
		
		if (a == Action.LEFT_CLICK_BLOCK || a == Action.LEFT_CLICK_AIR) {
			if (BGKit.hasAbility(p, Integer.valueOf(4)) && p.getItemInHand() != null && 
					p.getItemInHand().getType().equals(Material.FIREBALL)) {
				Player player = p;
				Fireball fire;
				Vector lookat = player.getLocation().getDirection().multiply(10);
				fire = player.getWorld().spawn(player.getLocation().add(lookat), Fireball.class);
				fire.setShooter(player);

				p.getInventory()
						.removeItem(
								new ItemStack[] { new ItemStack(
										Material.FIREBALL, 1) });
			}
		}
		
		try{
			if (BGKit.hasAbility(p, 11) && a == Action.RIGHT_CLICK_BLOCK && p.getItemInHand()
					.getType() == Material.STONE_AXE) {
				if(!thorList.contains(p)) {
					thorList.add(p);
					plugin.cooldown.thorCooldown(p);
					Block block = event.getClickedBlock();
					Location loc = block.getLocation();
					World world = plugin.getServer().getWorld("world");
					world.strikeLightning(loc);
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.11.Expired"));
				}
			}
			
			if (BGKit.hasAbility(p, 16) && p.getItemInHand()
					.getType() == Material.APPLE && (a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				if(!ghostList.contains(p)) {
					ghostList.add(p);
					plugin.cooldown.ghostCooldown(p);
					
					Player[] players = plugin.getServer().getOnlinePlayers();
					
					plugin.cooldown.showPlayerCooldown(p, players);
					p.getInventory().removeItem(new ItemStack[] { new ItemStack(Material.APPLE, 1)});
					for(Player player : players) {
						if(player.getName().equals(p.getName())) {
							continue;
						}
						if(BGKit.hasAbility(player, 21)) {
							continue;
						}
						player.hidePlayer(p);
					}
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.16.invisible"));
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.16.Expired"));
				}
			}
			if(BGKit.hasAbility(p, 22) && !plugin.DENY_DAMAGE_PLAYER &&
					p.getItemInHand().getType() == Material.WATCH &&
					(a == Action.RIGHT_CLICK_AIR || a == Action.RIGHT_CLICK_BLOCK)) {
				
				if(!timeList.contains(p)) {
					timeList.add(p);
					plugin.cooldown.timeCooldown(p);
					
					p.getInventory().removeItem(new ItemStack[] {new ItemStack(Material.WATCH,1)});
					
					int radius = BGFiles.abconf.getInt("AB.22.radius");
					
					List<Entity> entities = p.getNearbyEntities(radius+30, radius+30, radius+30);
					for(Entity e : entities) {
						
						if(!e.getType().equals(EntityType.PLAYER) || plugin.isSpectator((Player)e) || plugin.isGameMaker((Player) e))
							continue;
						Player target = (Player) e;
						if(plugin.TEAM) {
							
							if(BGTeam.isInTeam(p, target.getName()))
								continue;
						}
						if(p.getLocation().distance(target.getLocation()) < radius) {
							
							freezeList.add(target);
							String text = BGFiles.abconf.getString("AB.22.target");
							text = text.replace("<player>", p.getName());
							BGChat.printPlayerChat(target, text);
							plugin.cooldown.freezeCooldown(target);
						}	
					}
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.success"));
				}else {
					BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.Expired"));
				}
			}
		}catch(NullPointerException e) {
			
		}catch(Exception e) {
			e.printStackTrace();
		}

		if ((p.getItemInHand().getType() == Material.COMPASS & this.plugin.COMPASS
				.booleanValue())) {
			Boolean found = Boolean.valueOf(false);
			for (int i = 0; i < 300; i++) {
				List<Entity> entities = p.getNearbyEntities(i, 64.0D, i);
				for (Entity e : entities) {
					if ((!e.getType().equals(EntityType.PLAYER))|| plugin.isSpectator((Player) e) || plugin.isGameMaker((Player) e))
						continue;
					if(plugin.TEAM) {
						
						if(BGTeam.isInTeam(p, ((Player) e).getName()))
								continue;
					}
					p.setCompassTarget(e.getLocation());
					Double distance = p.getLocation().distance(
							e.getLocation());
					DecimalFormat df = new DecimalFormat("##.#");
					BGChat.printPlayerChat(p, "Tracking player \""
							+ ((Player) e).getName() + "\" | Distance: "
							+ df.format(distance));
					found = Boolean.valueOf(true);
					break;
				}

				if (found.booleanValue())
					break;
			}
			if (!found.booleanValue()) {
				BGChat.printPlayerChat(p,
						"No players in range. Compass points to spawn.");
				p.setCompassTarget(this.plugin.spawn);
			}
		}		
	}

	@EventHandler
	public void onServerPing(ServerListPingEvent event) {
		if (this.plugin.DENY_LOGIN.booleanValue())
			event.setMotd(this.plugin.MOTD_PROGRESS_MSG);
		else
			event.setMotd(this.plugin.MOTD_COUNTDOWN_MSG.replace("<time>",
					this.plugin.TIME(BGMain.COUNTDOWN)));
	}

	@EventHandler
	public void onBucketFill(PlayerBucketFillEvent event) {
		if(plugin.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (this.plugin.DENY_BLOCKBREAK.booleanValue())
			event.setCancelled(true);
	}

	@EventHandler
	public void onBucketEmpty(PlayerBucketEmptyEvent event) {
		if(plugin.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (this.plugin.DENY_BLOCKPLACE.booleanValue())
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityShootArrow(EntityShootBowEvent event) {
		if(event.getEntity() instanceof Player)
			if(plugin.isSpectator((Player) event.getEntity())) {
				event.setCancelled(true);
				return;
			}
		if (((event.getEntity() instanceof Player)) && (this.plugin.DENY_SHOOT_BOW.booleanValue())) {
			event.getBow().setDurability((short) 0);
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onProjectileHit(ProjectileHitEvent event) {
		Entity entity = event.getEntity();

		if ((entity instanceof Arrow)) {
			Arrow arrow = (Arrow) entity;
			Entity shooter = arrow.getShooter();
			if ((shooter instanceof Player)) {
				Player player = (Player) shooter;
				if(plugin.isSpectator(player)) {
					return;
				}
				if (BGKit.hasAbility(player, Integer.valueOf(1))) {
					try{
						if((arrow.getLocation().distance(BGCornucopia.getMainBlock().getLocation()) <= 16) || 
							(arrow.getLocation().distance(BGFeast.getMainBlock().getLocation()) <= 16 ) ||
							(arrow.getLocation().distance(BGFBattle.getMainBlock().getLocation()) <= 16 )) {
							BGChat.printPlayerChat(player, "�cYou can't destroy this block!");
							arrow.remove();
							return;
						} else {
							Bukkit.getServer().getWorld("world").createExplosion(arrow.getLocation(), 2.0F);
							arrow.remove();
						}
					} catch (NullPointerException e) {}
				} else {
					return;
				}
			} else {
				return;
			}
		}

		if ((entity instanceof Snowball)) {
			Snowball ball = (Snowball) entity;
			Entity shooter = ball.getShooter();
			if ((shooter instanceof Player)) {
				Player player = (Player) shooter;
				if(plugin.isSpectator(player)) {
					return;
				}
				if (BGKit.hasAbility(player, Integer.valueOf(3)).booleanValue()) {
					Bukkit.getServer().getWorld("world")
							.createExplosion(ball.getLocation(), 0.0F);
					for (Entity e : ball.getNearbyEntities(3.0D, 3.0D, 3.0D))
						if ((e instanceof Player)) {
							Player pl = (Player) e;
							if (pl.getName() != player.getName()) {
								pl.addPotionEffect(new PotionEffect(
										PotionEffectType.BLINDNESS, 100, 1));
								pl.addPotionEffect(new PotionEffect(
										PotionEffectType.CONFUSION, 160, 1));
							}
						}
				}
			} else {
				return;
			}
		}
	}

	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent event) {
		if(plugin.isSpectator(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (this.plugin.DENY_ITEMDROP.booleanValue())
			event.setCancelled(true);
	}

	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event) {
		if (this.plugin.DENY_DAMAGE_ENTITY)
			event.setCancelled(true);
		
		try{
			if((BGCornucopia.isCornucopiaBlock(event.getEntity().getLocation().getBlock()) || 
				BGFeast.isFeastBlock(event.getEntity().getLocation().getBlock())) || 
				BGFBattle.isBattleBlock(event.getEntity().getLocation().getBlock())) {
				event.setCancelled(true);
				return;
			}
		} catch (NullPointerException e) {}
		
		try {
			if((event.getEntity().getLocation().distance(BGCornucopia.getMainBlock().getLocation()) <= 16) || 
				(event.getEntity().getLocation().distance(BGFeast.getMainBlock().getLocation()) <= 16 ) ||
				(event.getEntity().getLocation().distance(BGFBattle.getMainBlock().getLocation()) <= 16 )) {
				event.setCancelled(true);
				return;
			}
		} catch (NullPointerException e) {}
		
	}

	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if(plugin.isSpectator(event.getPlayer()) || plugin.isGameMaker(event.getPlayer())) {
			event.setCancelled(true);
			return;
		}
		if (this.plugin.DENY_ITEMPICKUP.booleanValue())
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {
		Player p = event.getPlayer();
		
		if(plugin.isGameMaker(p))
			plugin.remGameMaker(p);
		if(plugin.isSpectator(p))
			plugin.remSpectator(p);
		
		if (this.plugin.DENY_LOGIN.booleanValue() || plugin.ADV_CHAT_SYSTEM)
			event.setLeaveMessage(null);
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerLogin(PlayerLoginEvent event) {
		Player p = event.getPlayer();

		if (plugin.DENY_LOGIN &&
				!(plugin.SPECTATOR_SYSTEM && p.hasPermission("bg.spectator")) &&
				!(p.hasPermission("bg.admin.logingame") || p.hasPermission("bg.admin.*"))) {
			event.setKickMessage(ChatColor.RED + plugin.GAME_IN_PROGRESS_MSG);
			event.disallow(PlayerLoginEvent.Result.KICK_OTHER, event.getKickMessage());
		} else if (event.getResult() == Result.KICK_FULL) {
			if (p.hasPermission("bg.vip.full") || p.hasPermission("bg.admin.full") 
					|| Bukkit.getServer().getMaxPlayers() > plugin.getGamers().length) {
				event.allow();
			} else {
				event.setKickMessage(ChatColor.RED + plugin.SERVER_FULL_MSG.replace("<players>", Integer.toString(Bukkit.getOnlinePlayers().length)));
			}
		}

		BGVanish.updateVanished();
	}

	@EventHandler
	public void onPlayerOutBorder(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		if (this.plugin.DENY_CHECK_WORLDBORDER.booleanValue()) {
			return;
		}
		if (!this.plugin.inBorder(event.getTo())) {
			if (this.plugin.inBorder(event.getFrom())) {
				BGChat.printPlayerChat(p, this.plugin.WORLD_BORDER_MSG);
				event.setTo(event.getFrom());
				p.teleport(event.getFrom());
				this.plugin.getClass();
				Bukkit.getServer().getWorld("world")
						.playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 5);
				this.plugin.getClass();
				Bukkit.getServer().getWorld("world")
						.playEffect(p.getLocation(), Effect.SMOKE, 5);
				return;
			}
			p.teleport(p.getWorld().getSpawnLocation().add(0.0D, 20.0D, 0.0D));
			this.plugin.getClass();
			Bukkit.getServer().getWorld("world")
					.playEffect(p.getLocation(), Effect.ENDER_SIGNAL, 5);
			this.plugin.getClass();
			Bukkit.getServer().getWorld("world")
					.playEffect(p.getLocation(), Effect.SMOKE, 5);
		}
	}

	@EventHandler
	public void onPlayerMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if (!this.plugin.COMPASS.booleanValue() || !this.plugin.AUTO_COMPASS.booleanValue())
			return;
		Boolean found = Boolean.valueOf(false);
		for (int i = 0; i < 300; i++) {
			List<Entity> entities = p.getNearbyEntities(i, 64.0D, i);
			for (Entity e : entities) {
				if ((e.getType().equals(EntityType.PLAYER)) && !plugin.isSpectator((Player) e)) {
					p.setCompassTarget(e.getLocation());
					found = Boolean.valueOf(true);
					break;
				}
			}
			if (found.booleanValue())
				break;
		}
		if (!found.booleanValue()) {
			p.setCompassTarget(this.plugin.spawn);
		}
	}

	@EventHandler
	public void onKill(EntityDeathEvent e) {
		Player p = e.getEntity().getKiller();
		if (BGKit.hasAbility(p, Integer.valueOf(7)).booleanValue()) {
			if (e.getEntityType() == EntityType.PIG) {
				e.getDrops().clear();
				e.getDrops().add(new ItemStack(Material.PORK, BGFiles.abconf.getInt("AB.7.Amount")));
			}
		}
	}

	@EventHandler
	public void onFall(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if(plugin.isSpectator(p))
				return;
			if (BGKit.hasAbility(p, Integer.valueOf(8))) {
				if (e.getCause() == DamageCause.FALL) {
					if (e.getDamage() > 4) {
						e.setCancelled(true);
						p.damage(4);
					}
					List<Entity> nearbyEntities = e.getEntity()
							.getNearbyEntities(5, 5, 5);
					for (Entity target : nearbyEntities) {
						if (target instanceof Player) {
							if(plugin.isSpectator((Player) target) || plugin.isGameMaker((Player) target))
								continue;
							Player t = (Player) target;
							if(plugin.TEAM) {
								
								if(BGTeam.isInTeam(p, t.getName()))
									continue;
							}
							if (t.isSneaking())
								t.damage(e.getDamage() / 2, e.getEntity());
							else
								t.damage(e.getDamage(), e.getEntity());
						}
					}
				}
			}
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		BGVanish.updateVanished();
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!plugin.DENY_LOGIN & plugin.ADV_CHAT_SYSTEM) {
			BGChat.printDeathChat("�e" + event.getJoinMessage());
		}

		if (plugin.DENY_LOGIN || plugin.ADV_CHAT_SYSTEM) {
			event.setJoinMessage(null);
		}

		Player p = event.getPlayer();
		
		p.getInventory().clear();
		p.getInventory().setHelmet(null);
		p.getInventory().setChestplate(null);
		p.getInventory().setLeggings(null);
		p.getInventory().setBoots(null);
		p.setGameMode(GameMode.SURVIVAL);
		p.setHealth(20);
		p.setFoodLevel(20);
		p.setExp(0);
		
		if (plugin.DENY_LOGIN) {
			if (p.hasPermission("bg.admin.gamemaker") || p.hasPermission("bg.admin.*")) {
				plugin.addGameMaker(p);
			}else if(plugin.SPECTATOR_SYSTEM && p.hasPermission("bg.spectator")) {
				plugin.addSpectator(p);
			}
		} else {
			if (!plugin.ADV_CHAT_SYSTEM && !plugin.ITEM_MENU)
				BGChat.printKitChat(p);
		}

		if(plugin.isSpectator(p)) {
			p.setPlayerListName(ChatColor.GRAY + getShortStr(p.getName()) + ChatColor.RESET);
			p.setDisplayName(ChatColor.GRAY + p.getName() + ChatColor.RESET);
		} else if (plugin.winner(p)) {
			p.setPlayerListName(ChatColor.GOLD + getShortStr(p.getName())
					+ ChatColor.RESET);
			p.setDisplayName(ChatColor.GOLD + p.getName() + ChatColor.RESET);
		} else if (p.hasPermission("bg.admin.color")
				|| p.hasPermission("bg.admin.*")) {
			p.setPlayerListName(ChatColor.RED + getShortStr(p.getName())
					+ ChatColor.RESET);
			p.setDisplayName(ChatColor.RED + p.getName() + ChatColor.RESET);
		} else if (p.hasPermission("bg.vip.color")
				|| p.hasPermission("bg.vip.*")) {
			p.setPlayerListName(ChatColor.BLUE + getShortStr(p.getName())
					+ ChatColor.RESET);
			p.setDisplayName(ChatColor.BLUE + p.getName() + ChatColor.RESET);
		} else {
			p.setPlayerListName(p.getName());
			p.setDisplayName(p.getName());
		}
		
		if(!plugin.DENY_LOGIN) {
		//Creating a written book.
		List<String> pages = BGFiles.bookconf.getStringList("content");
		List<String> content = new ArrayList<String>();
		List<String> page = new ArrayList<String>();
		for(String line : pages)  {
			line = line.replace("<server_title>", plugin.SERVER_TITLE);
			line = line.replace("<space>", "�r\n");
			line = line.replace("&", "�");
			if(!line.contains("<newpage>")) {
				page.add(line + "\n");
			} else {
				String pagestr = "";
				for(String l : page)
					pagestr = pagestr + l;
				content.add(pagestr);
				page.clear();
			}
		}
		String pagestr = "";
		for(String l : page)
			pagestr = pagestr + l;
		content.add(pagestr);	
		page.clear();
		
		BookMeta bi;
		bi.setPages(content.toArray(new String[0]));
		bi.setAuthor(BGFiles.bookconf.getString("author"));
		bi.setTitle(BGFiles.bookconf.getString("title"));
		ItemStack writtenbook = new ItemStack(387,1);
		writtenbook.setItemMeta(bm);
		p.getInventory().addItem(writtenbook);
		}
		
		String playerName = p.getName();
		
		if (plugin.SQL_USE) {
			Integer PL_ID = plugin.getPlayerID(playerName);
			if (PL_ID == null) {
				plugin.SQLquery("INSERT INTO `PLAYERS` (`NAME`) VALUES ('"
						+ playerName + "') ;");
			}
		}
		
		if (plugin.REW) {
			Integer PL_ID = plugin.getCoins(plugin.getPlayerID(playerName));
			if (PL_ID == null) {
				plugin.reward.createUser(playerName);
			}
		}
		
		if(p.hasPermission("bg.admin.check")) {
			if(!plugin.getDescription().getVersion().contains("-DEV")) {
				Updater updater = new Updater(plugin, "bukkitgames", plugin.getPFile(), Updater.UpdateType.NO_DOWNLOAD, false);
				boolean update = updater.getResult() == Updater.UpdateResult.UPDATE_AVAILABLE;
			
				if (update) {
					String newversion = updater.getLatestVersionString();
					BGChat.printPlayerChat(p, "�bUpdate available: " + newversion + " �r/bgdownload");
				}
			}
		}
	}

	private String getShortStr(String s) {
		if (s.length() == 16) {
			String shorts = s.substring(0, s.length() - 4);
			return shorts;
		}
		if (s.length() == 15) {
			String shorts = s.substring(0, s.length() - 3);
			return shorts;
		}
		if (s.length() == 14) {
			String shorts = s.substring(0, s.length() - 2);
			return shorts;
		}
		if (s.length() == 13) {
			String shorts = s.substring(0, s.length() - 1);
			return shorts;
		}
		return s;
	}

	@EventHandler
	public void onBlockBreak(BlockBreakEvent event) {
		Player p = event.getPlayer();
		if(plugin.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}
		
		if ((this.plugin.DENY_BLOCKBREAK.booleanValue() & (!p.hasPermission("bg.admin.editblocks") 
				|| !p.hasPermission("bg.admin.*")))) {
			event.setCancelled(true);
			return;
		}

		if((plugin.CORNUCOPIA_PROTECTED && BGCornucopia.isCornucopiaBlock(event.getBlock())) || (plugin.FEAST_PROTECTED && BGFeast.isFeastBlock(event.getBlock())) || BGFBattle.isBattleBlock(event.getBlock())) {
			BGChat.printPlayerChat(p, "�cYou can't destroy this block!");
			event.setCancelled(true);
			return;
		}
		
		Block b = event.getBlock();
		
		if(plugin.DEATH_SG_PROTECTED && plugin.sign.signs.contains(b.getLocation())) {
				event.setCancelled(true);
				return;
		}
		
		if (BGKit.hasAbility(p, 2) && b.getType() == Material.LOG) {
			World w = Bukkit.getServer().getWorld(plugin.WORLD_TEMPOARY_NAME);
			Double y = b.getLocation().getY() + 1;
			Location l = new Location(w, b.getLocation().getX(), y, b
					.getLocation().getZ());
			while (l.getBlock().getType() == Material.LOG) {
				l.getBlock().breakNaturally();
				y++;
				l.setY(y);
			}
		}		
	}

	@EventHandler
	public void onBlockBurn(BlockBurnEvent event) {
		if((plugin.CORNUCOPIA_PROTECTED && BGCornucopia.isCornucopiaBlock(event.getBlock())) || 
			(plugin.FEAST_PROTECTED && BGFeast.isFeastBlock(event.getBlock())) || 
			BGFBattle.isBattleBlock(event.getBlock())) {
			
			event.setCancelled(true);
			return;
		}
	}
		
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		if(plugin.isSpectator(p)) {
			event.setCancelled(true);
			return;
		}
		if ((this.plugin.DENY_BLOCKPLACE.booleanValue() & (!p.hasPermission("bg.admin.editblocks") 
				|| !p.hasPermission("bg.admin.*")))) {
			event.setCancelled(true);
			return;
		}
		
		if((plugin.CORNUCOPIA_PROTECTED && BGCornucopia.isCornucopiaBlock(event.getBlock())) || (plugin.FEAST_PROTECTED && BGFeast.isFeastBlock(event.getBlock())) || BGFBattle.isBattleBlock(event.getBlock())) {
			BGChat.printPlayerChat(p, "�cYou can't place this block!");
			event.setCancelled(true);
			return;
		}
		
		Block block = event.getBlockPlaced();
		if (BGKit.hasAbility(p, 10) && block.getType() == Material.CROPS) {
			block.setData(CropState.RIPE.getData());
		}
		if (BGKit.hasAbility(p, 10) && block.getType() == Material.SAPLING) {
			TreeType t = getTree(block.getData());
			Bukkit.getServer().getWorld("world").generateTree(block.getLocation(), t);
		}
	}
	
    public TreeType getTree(int data) {
        TreeType tretyp = TreeType.TREE;
        switch(data) {
        case 0:
            tretyp = TreeType.TREE;
            break;
        case 1:
            tretyp = TreeType.REDWOOD;
            break;
        case 2:
            tretyp = TreeType.BIRCH;
            break;
        case 3:
            tretyp = TreeType.JUNGLE;
            break;
        default:
            tretyp = TreeType.TREE;
        }
        return tretyp;
    }
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if(plugin.isSpectator(event.getPlayer())) {
			if(plugin.ADV_CHAT_SYSTEM) {
				BGChat.printPlayerChat(event.getPlayer(), "�cSpectators can't chat.");
				event.setCancelled(true);			
				return;
			} else {
				event.getRecipients().clear();
				event.getRecipients().addAll(plugin.getSpectators());
				event.getRecipients().addAll(getOnlineOps());
				event.setFormat("�o[SPECTATORS] �r" + event.getFormat());
			}
		}
			
		if (plugin.ADV_CHAT_SYSTEM) {
			String m = String.format(event.getFormat(), event.getPlayer().getDisplayName(), event.getMessage());
			String s = ChatColor.stripColor(m);
			if(s.length() <= 53) {
				BGChat.playerChatMsg(m);
				log.info("[CHAT] " + m);
			} else {
				BGChat.printPlayerChat(event.getPlayer(), "�cYour message is too long!");
			}
			event.setCancelled(true);
		}
	}

	private ArrayList<Player> getOnlineOps() {
		ArrayList<Player> ops = new ArrayList<Player>();
		for(Player p : Bukkit.getServer().getOnlinePlayers()) {
			if(p.isOp())
				ops.add(p);
		}
				
		return ops;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		
		if(plugin.isGameMaker(p)) {
			event.setQuitMessage(null);
			plugin.remGameMaker(p);
			return;
		}
		
		if (plugin.isSpectator(p)) {
			event.setQuitMessage(null);
			plugin.remSpectator(p);
			return;
		}
		
		if (!plugin.DENY_LOGIN.booleanValue() & plugin.ADV_CHAT_SYSTEM) {
			BGChat.printDeathChat("�e" + event.getQuitMessage());
		}

		if (this.plugin.DENY_LOGIN.booleanValue() || plugin.ADV_CHAT_SYSTEM) {
			event.setQuitMessage(null);
		}

		if (this.plugin.QUIT_MSG.booleanValue() & !p.isDead()) {
			BGChat.printDeathChat(p.getName() + " left the game.");
			if (!plugin.ADV_CHAT_SYSTEM) {
				BGChat.printDeathChat(this.plugin.getGamers().length - 1
						+ " players remaining.");
				BGChat.printDeathChat("");
			}
			Location light = p.getLocation();
			last_quit = p.getName();
			p.setHealth(0);
			Bukkit.getServer().getWorld("world")
					.strikeLightningEffect(light.add(0.0D, 100.0D, 0.0D));
		}

		if (this.plugin.NEW_WINNER != p.getName()
				& this.plugin.DENY_LOGIN.booleanValue()) {
			Bukkit.getServer().getScheduler()
					.scheduleSyncDelayedTask(this.plugin, new Runnable() {
						public void run() {
							plugin.checkwinner();

							if (plugin.SQL_USE) {
								Integer PL_ID = plugin.getPlayerID(last_quit);
								if (last_quit == plugin.NEW_WINNER) {
									
								} else {
									plugin.SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `DEATH_REASON` = 'QUIT' WHERE `REF_PLAYER` = "
											+ PL_ID
											+ " AND `REF_GAME` = "
											+ plugin.SQL_GAMEID + " ;");
								}
							}
						}
					}, 60L);
		}
	}

	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		
		Entity damager = event.getDamager();
		Entity defender = event.getEntity();
		
		if(damager instanceof Player)
			if(plugin.isSpectator((Player) damager)) {
				event.setCancelled(true);
				return;
			}
		
		if (this.plugin.DENY_DAMAGE_ENTITY & !(event.getEntity() instanceof Player)) {
			return;
		}
		if (this.plugin.DENY_DAMAGE_PLAYER & event.getEntity() instanceof Player) {
			return;
		}
		if (event.getEntity().isDead()) {
			return;
		}

		if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				Player p = (Player) arrow.getShooter();
				if (!BGKit.hasAbility(p, 9)) {
					return;
				}
				if (p.getLocation().distance(event.getEntity().getLocation()) >= BGFiles.abconf.getInt("AB.9.Distance")) {
					if (event.getEntity() instanceof LivingEntity) {
						LivingEntity victom = (LivingEntity) event.getEntity();
						if (victom instanceof Player) {
							Player v = (Player) victom;
							ItemStack helmet = v.getInventory().getHelmet();
							if (helmet == null) {
								BGChat.printDeathChat(v.getName()
										+ " was headshotted by " + p.getName()
										+ ".");
								if (!plugin.ADV_CHAT_SYSTEM) {
									BGChat.printDeathChat((this.plugin
											.getGamers().length - 1)
											+ " players remaining.");
									BGChat.printDeathChat("");
								}
								Location light = v.getLocation();
								Bukkit.getServer()
										.getWorld("world")
										.strikeLightningEffect(
												light.add(0.0D, 100.0D, 0.0D));
								last_headshot = v.getName();
								v.setHealth(0);
								v.kickPlayer(ChatColor.RED + v.getName()
										+ " was headshotted by " + p.getName()
										+ ".");

								if(plugin.REW && plugin.COINS_FOR_KILL != 0){
									plugin.reward.giveCoins(p.getName(), plugin.COINS_FOR_KILL);
									if(plugin.COINS_FOR_KILL == 1)
										BGChat.printPlayerChat(p, "You got 1 Coin for killing "+v.getName());
									else
										BGChat.printPlayerChat(p, "You got "+plugin.COINS_FOR_KILL+" Coins for killing "+v.getName());
								}
								if (plugin.SQL_USE) {
									Integer PL_ID = plugin.getPlayerID(v
											.getName());
									Integer KL_ID = plugin.getPlayerID(p
											.getName());
									plugin.SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `REF_KILLER` = "
											+ KL_ID
											+ ", `DEATH_REASON` = 'HEADSHOT' WHERE `REF_PLAYER` = "
											+ PL_ID
											+ " AND `REF_GAME` = "
											+ plugin.SQL_GAMEID + " ;");
								}
							} else {
								helmet.setDurability((short) (helmet
										.getDurability() + 20));
								v.getInventory().setHelmet(helmet);
							}
						} else {
							BGChat.printPlayerChat(p, "Headshot!");
							victom.setHealth(0);
						}
					}
				}
			}
		}
		
		if (damager instanceof Player) {
			
			Player dam = (Player)damager;
			if(BGKit.hasAbility(dam, 12)) {
				
				if (dam.getHealth() == 20) {
					return;
				}
				
				dam.setHealth(dam.getHealth()+1);
			}
			
			if (defender.getType() == EntityType.PLAYER) {
				
				Player def = (Player)defender;
				
				if(BGKit.hasAbility(dam, 13) && dam.getItemInHand().getType() == Material.BLAZE_ROD && def.getItemInHand() != null) {
				
					if (!monkList.contains(dam)) {
						
						int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.13.Chance")-1)+1);
						if (random == 1) {
							monkList.add(dam);
							plugin.cooldown.monkCooldown(dam);
							def.getInventory().clear(def.getInventory().getHeldItemSlot());
							BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.13.Success"));
							BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.13.Success"));
						}
					}else {
						
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.13.Expired"));
					}
				}
				
				if(BGKit.hasAbility(dam, 15) && dam.getItemInHand().getType() == Material.STICK && def.getItemInHand() != null) {
					
					if(!thiefList.contains(dam)) {
						int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.15.Chance")-1)+1);
						if(random == 1) {
							thiefList.add(dam);
							plugin.cooldown.thiefCooldown(dam);
							dam.getInventory().clear(dam.getInventory().getHeldItemSlot());
							dam.getInventory().addItem(def.getItemInHand());
							def.getInventory().clear(def.getInventory().getHeldItemSlot());
							BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Success"));
							BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.15.Success"));
						}
					}else {
						
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.15.Expired"));
					}
				}
				
				if (BGKit.hasAbility(dam, 19)) {
					
					int random = (int) (Math.random()* (BGFiles.abconf.getInt("AB.19.Chance")-1)+1);
					if(random == 1 && !viperList.contains(def)) {
						
						def.addPotionEffect(new PotionEffect(PotionEffectType.POISON, BGFiles.abconf.getInt("AB.19.Duration")*20, 1));
						viperList.add(def);
						BGChat.printPlayerChat(dam, BGFiles.abconf.getString("AB.19.Damager"));
						BGChat.printPlayerChat(def, BGFiles.abconf.getString("AB.19.Defender"));
						plugin.cooldown.viperCooldown(def);
					}
				}
			}
			
			EntityType mob = defender.getType();
			
			if (BGKit.hasAbility(dam, 17) && BGDisguise.getDisguiseType(mob) != null && plugin.ADV_ABI) {
				
				DisguiseType mt = BGDisguise.getDisguiseType(mob);
				plugin.dis.disguise(dam, mt);
			}
			
			if (BGKit.hasAbility(dam, 18) && dam.getItemInHand().getType() == Material.AIR) {
				
				event.setDamage(event.getDamage()+ 4);
			}
		}
	}

	@EventHandler
	public void onEntityDamage(EntityDamageEvent event) {
		if(event.getEntity() instanceof Player) {
			if(plugin.isSpectator((Player) event.getEntity())) {
				event.setCancelled(true);
				return;
			}
		}
		
		if ((this.plugin.DENY_DAMAGE_PLAYER.booleanValue() & event.getEntity() instanceof Player)) {
			event.setCancelled(true);
			return;
		}

		if ((this.plugin.DENY_DAMAGE_ENTITY.booleanValue() & !(event
				.getEntity() instanceof Player))) {
			event.setCancelled(true);
			return;
		}
		
		if ((event.getCause() == EntityDamageEvent.DamageCause.FIRE_TICK & event
				.getEntity() instanceof Player)) {
			Player p = (Player) event.getEntity();
			if ((BGKit.hasAbility(p, Integer.valueOf(6)).booleanValue() & !p
					.hasPotionEffect(PotionEffectType.FIRE_RESISTANCE))) {
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.INCREASE_DAMAGE, 200, 1));
				p.addPotionEffect(new PotionEffect(
						PotionEffectType.FIRE_RESISTANCE, 260, 1));
			}
		}
		
		Entity en = event.getEntity();
		if (en.getType() == EntityType.PLAYER) {
			Player player = (Player)en;
			if(BGKit.hasAbility(player, 17) && plugin.ADV_ABI) {
				plugin.dis.unDisguise(player);
			}
			if(BGKit.hasAbility(player, 18)) {
				event.setDamage(event.getDamage() - 1);
			}
		}
	}

	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		Player dp = event.getEntity();
		
		if (plugin.isSpectator(dp) || plugin.isGameMaker(dp)) {
			event.setDeathMessage(null);
			return;
		}
		
		if (plugin.DEATH_SIGNS) {
			Location loc = dp.getLocation();
			String fl = BGFiles.dsign.getString("FIRST_LINE");
			String sl = BGFiles.dsign.getString("SECOND_LINE");
			String tl = BGFiles.dsign.getString("THIRD_LINE");
			String fol = BGFiles.dsign.getString("FOURTH_LINE");
			
			if(fl != null)	
				fl = fl.replace("[name]", dp.getName());
			if(sl != null)
				sl = sl.replace("[name]", dp.getName());
			if(tl != null)
				tl = tl.replace("[name]", dp.getName());
			
			if(fol != null)
				fol = fol.replace("[name]", dp.getName());
			
			plugin.sign.createSign(loc, fl, sl, tl, fol);
		}
		
		
		if(dp.getKiller() != null && dp.getKiller() instanceof Player) {
			Player killer = dp.getKiller();
			if(BGKit.hasAbility(killer, 14)) {
				if(killer.getFoodLevel() <= 14) {
					killer.setFoodLevel(killer.getFoodLevel()+ 6);
				}else {
					killer.setFoodLevel(20);
				}
			}
			if(plugin.REW && last_headshot != dp.getName() && plugin.COINS_FOR_KILL != 0){
				plugin.reward.giveCoins(killer.getName(), plugin.COINS_FOR_KILL);
				if(plugin.COINS_FOR_KILL == 1)
					BGChat.printPlayerChat(killer, "You got 1 Coin for killing "+dp.getName());
				else
					BGChat.printPlayerChat(killer, "You got "+plugin.COINS_FOR_KILL+" Coins for killing "+dp.getName());
			}
		}

		if (last_quit == event.getEntity().getName() || last_headshot == event.getEntity().getName()) {
			event.setDeathMessage(null);
			return;
		}

		if (plugin.DEATH_MSG) {
			Player p = event.getEntity();

			if (plugin.SQL_USE) {
				Integer PL_ID = plugin.getPlayerID(p.getName());

				Integer KL_ID = null;
				if (p.getKiller() != null) {
					KL_ID = plugin.getPlayerID(p.getKiller().getName());
				} else {
					KL_ID = null;
				}

				plugin.SQLquery("UPDATE `PLAYS` SET deathtime = NOW(), `REF_KILLER` = "
						+ KL_ID
						+ ", `DEATH_REASON` = '"
						+ p.getLastDamageCause().getCause().name().toString()
						+ "' WHERE `REF_PLAYER` = "
						+ PL_ID
						+ " AND `REF_GAME` = " + plugin.SQL_GAMEID + " ;");
			}
			
			Location light = p.getLocation();
			p.kickPlayer(ChatColor.RED + event.getDeathMessage() + ".");
			
			BGChat.printDeathChat(event.getDeathMessage() + ".");
			if (!plugin.ADV_CHAT_SYSTEM) {
				BGChat.printDeathChat(this.plugin.getGamers().length + " players remaining.");
				BGChat.printDeathChat("");
			}
			Bukkit.getServer().getWorld("world").strikeLightningEffect(light.add(0, 100, 0));
		}

		event.setDeathMessage(null);
	}
	
	@EventHandler
	public void onEntityTarget(EntityTargetEvent event) {
		Entity entity = event.getTarget();
		if (entity != null) {
			if (entity instanceof Player) {
				Player player = (Player)entity;
				if(BGKit.hasAbility(player, 20) && event.getReason() == TargetReason.CLOSEST_PLAYER) {
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
		if(event.getMessage().contains("/me"))
			event.setCancelled(true);
	}
	
	@EventHandler
	public void freezePlayers(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		
		if(freezeList.contains(p)) {
			event.setCancelled(true);
			BGChat.printPlayerChat(p, BGFiles.abconf.getString("AB.22.move"));
		}
	}
}
