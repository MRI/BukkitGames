package utilities;

import java.io.File;
import java.util.logging.Logger;

import main.BGMain;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class BGFiles {
	
	private static BGMain plugin;
	
	Logger log = BGMain.getPluginLogger();
	
	public static FileConfiguration abconf;
	public static FileConfiguration bookconf;
	public static FileConfiguration config;
	public static FileConfiguration dsign;
	public static FileConfiguration kitconf;
	public static FileConfiguration messageconf;
	public static FileConfiguration cornconf;
	public static FileConfiguration feastconf;
	public static FileConfiguration worldconf;

	public BGFiles(BGMain main) {
		
		plugin = main;
		
		try{
			loadFiles();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadFiles() throws Exception {
		
		//Create files if not exist
		File configFile = new File(plugin.getDataFolder(), "config.yml");
		File kitFile = new File(plugin.getDataFolder(), "kit.yml");
		File leaderboardFile = new File(plugin.getDataFolder(), "leaderboard.yml");
		File deathSignFile = new File(plugin.getDataFolder(), "deathsign.yml");
		File abilitiesFile = new File(plugin.getDataFolder(), "abilities.yml");
		File bookFile = new File(plugin.getDataFolder(), "book.yml");
		File messagesFile = new File(plugin.getDataFolder(), "messages.yml");
		File cornFile = new File(plugin.getDataFolder(), "cornucopia.yml");
		File feastFile = new File(plugin.getDataFolder(), "feast.yml");
		File worldFile = new File(plugin.getDataFolder(), "world.yml");

		Integer creation = 0;
		
		if (!configFile.exists()) {
			configFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("config.yml"), configFile);
			creation++;
		}
		if (!kitFile.exists()) {
			kitFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("kit.yml"), kitFile);
			creation++;
		}
		if (!leaderboardFile.exists()) {
			leaderboardFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("leaderboard.yml"), leaderboardFile);
			creation++;
		}
		if (!deathSignFile.exists()) {
			deathSignFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("deathsign.yml"), deathSignFile);
			creation++;
		}
		if (!abilitiesFile.exists()) {
			abilitiesFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("abilities.yml"), abilitiesFile);
			creation++;
		}
		if (!bookFile.exists()) {
			bookFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("book.yml"), bookFile);
			creation++;
		}
		if (!messagesFile.exists()) {
			messagesFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("messages.yml"), messagesFile);
			creation++;
		}
		if(!cornFile.exists()) {
			cornFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("cornucopia.yml"), cornFile);
			creation++;
		}
		if(!feastFile.exists()) {
			feastFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("feast.yml"), feastFile);
			creation++;
		}
		if(!worldFile.exists()) {
			worldFile.getParentFile().mkdirs();
			plugin.copy(plugin.getResource("world.yml"), worldFile);
			creation++;
		}
		
		if(creation > 0)
			log.info("Created " + creation + " files.");
		
		
		//Save files in variables
		abconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "abilities.yml"));
		bookconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "book.yml"));
		config = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "config.yml"));
		dsign = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "deathsign.yml"));
		kitconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "kit.yml"));
		messageconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "messages.yml"));
		cornconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "cornucopia.yml"));
		feastconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "feast.yml"));
		worldconf = YamlConfiguration.loadConfiguration(
				new File(plugin.getDataFolder(), "world.yml"));
	}
}
