package main;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import main.enchantmentHandlers.ChainingHandler;
import main.enchantmentHandlers.EnchantmentRegister;
import main.enchantmentHandlers.ExcavationHandler;
import main.enchantmentHandlers.LumberingHandler;
import main.enchantmentHandlers.PlowHandler;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.StringUtil;

public class XcavationMain extends JavaPlugin implements Listener, Plugin, TabCompleter {

	private static Plugin instance;
	
	//Enchants
	public Enchantment excavationEnchantment;
	public Enchantment chainingEnchantment;
	public Enchantment lumberingEnchantment;
	public Enchantment plowEnchantment;
	
	public XcavationMain() {
		instance = this;
		//Register Enchants
		EnchantmentRegister.register();
		//Enchants
		excavationEnchantment = Enchantment.getByKey(ExcavationHandler.key);
		chainingEnchantment = Enchantment.getByKey(ChainingHandler.key);
		lumberingEnchantment = Enchantment.getByKey(LumberingHandler.key);
		plowEnchantment = Enchantment.getByKey(PlowHandler.key);
	}
	
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
	}
	
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onPlayerLeave(PlayerQuitEvent e) {

	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
	}
	
	public static Plugin getInstance() {
		return instance;
	}
	
	@EventHandler
	public void onPlayerMove(PlayerMoveEvent e) {
		if (e.getPlayer().getInventory().getItemInMainHand().getType() != Material.AIR) {
			ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
			ItemMeta meta = item.getItemMeta();
			List<String> lore =(meta.getLore() == null ? new ArrayList<String>() : meta.getLore());
			List<String> colorlessLore = new ArrayList<String>();
			for(int i = 0; i < lore.size(); i++) {
				colorlessLore.add(ChatColor.stripColor(lore.get(i)));
			}
			// Enchantments
			if (meta.hasEnchants())
				for (NamespacedKey key : EnchantmentRegister.keys)
					if (meta.hasEnchant(Enchantment.getByKey(key))) {
						for (Enchantment ench : EnchantmentRegister.levelableEnchants.keySet())
							if (!colorlessLore.contains(EnchantmentRegister.levelableEnchants.get(ench) + " " + toRomanNumeral(meta.getEnchantLevel(ench))) && ench.getKey().equals(key)) {
								for(String line : colorlessLore)
									if(line.contains(EnchantmentRegister.levelableEnchants.get(ench)))
										lore.remove(colorlessLore.indexOf(line));
								lore.add(ChatColor.GRAY + EnchantmentRegister.levelableEnchants.get(ench) + " " + toRomanNumeral(meta.getEnchantLevel(ench)));
							}
						for (Enchantment ench : EnchantmentRegister.nonLevelableEnchants.keySet())
							if (!colorlessLore.contains(EnchantmentRegister.nonLevelableEnchants.get(ench)) && ench.getKey().equals(key)) {
								lore.add(ChatColor.GRAY + EnchantmentRegister.nonLevelableEnchants.get(ench));
							}
					}
		
			// Enchantments
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		Player player;
		if (sender instanceof Player) {
			player = (Player) sender;
		
			if (cmd.getName().equalsIgnoreCase("addEnchant") && args.length >= 2) {
				if (!player.getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
					for(NamespacedKey key : EnchantmentRegister.keys)
						if (args[0].equalsIgnoreCase(key.getKey())) {
							ItemStack item = player.getInventory().getItemInMainHand();
							ItemMeta meta = item.getItemMeta();
							if (!meta.hasEnchant(Enchantment.getByKey(key))) {
								meta.addEnchant(Enchantment.getByKey(key), Integer.parseInt(args[1]), true);	
							}
							item.setItemMeta(meta);
						}
					return true;
				}
			}
		
		}
		return false;
	}
	
	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		List<String> suggestions = new ArrayList<String>();
		if (cmd.getName().equalsIgnoreCase("addEnchant")) {
			if (args.length == 1) {
				Iterator<NamespacedKey> iterator = EnchantmentRegister.keys.iterator();
				while(iterator.hasNext()) {
					suggestions.add(iterator.next().getKey());
				}
			} else if (args.length == 2) {
				//suggestions.add("[<level>]");
			}
			Collections.sort(suggestions);
		}
		return StringUtil.copyPartialMatches(args[0], suggestions, new ArrayList<String>());
	}
	
	/**
	 * Turns an Integer into the Roman Numeral representation of itself
	 * @param number The number to be changed into a Roman Numeral
	 * @return The Roman Numeral representation of <b>number</b>
	 */
	public static String toRomanNumeral(int number) {
		String text = "";
		int[] valueI = {1,4,5,9,10,40,50,90,100,400,500,900,1000};
		String[] valueS = {"I","IV","V","IX","X","XL","L","XC","C","CD","D","CM","M"};
		for(int i = 12; i >= 0; i--) {
			while (number >= valueI[i]) {
				text += valueS[i];
				number -= valueI[i];
			}
			if (number == 0) break;
		}
		return text;
	}
	
	/**
	 * Turns the Roman Numeral representation of a number into the number it represents
	 * @param numeral The Roman Numeral to be converted into a number
	 * @return The number represented by <b>numeral</b>
	 */
	public static int toNumber(String numeral) {
		int number = 0;
		int[] valueI = {4,9,40,90,400,900,1,5,10,50,100,500,1000};
		String[] valueS = {"IV","IX","XL","XC","CD","CM","I","V","X","L","C","D","M"};
		for(int i = 0; i <= 12; i++) {
			while (numeral.contains(valueS[i])) {
				number += valueI[i];
				numeral = numeral.replaceFirst(valueS[i], "");
			}
			if (numeral.equals("")) break;
		}
		return number;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		if (e.getItem() != null)
			if (e.getItem().hasItemMeta()) {
				if (e.getAction().equals(Action.LEFT_CLICK_BLOCK) && e.getItem().getItemMeta().hasEnchant(excavationEnchantment)) {
					ExcavationHandler.face = e.getBlockFace();
				}
				if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getItem().getItemMeta().hasEnchant(plowEnchantment)) {
					PlowHandler.plow(e.getClickedBlock(), e.getPlayer(), e.getItem().getItemMeta().getEnchantLevel(plowEnchantment), e.getClickedBlock().getWorld());
				}
			}
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		if (!e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.AIR)) {
			
			if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(excavationEnchantment))
				ExcavationHandler.excavate(e.getPlayer(), e.getBlock(), e.getPlayer().getInventory().getItemInMainHand().getItemMeta().getEnchantLevel(excavationEnchantment), e.getPlayer().getWorld(), ExcavationHandler.face);
			else if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(chainingEnchantment)) {
				ChainingHandler.chainOres(e.getBlock(), e.getPlayer());
				ChainingHandler.damageItem(e.getPlayer());
			} else if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(lumberingEnchantment)) {
				LumberingHandler.chainBlocks(e.getBlock(), e.getPlayer());
				LumberingHandler.damageItem(e.getPlayer());
			}
			
		}
	}
	
	@EventHandler
	public void onPrepareAnvil(PrepareAnvilEvent e) {
		if (e.getInventory().getItem(0) != null) {
			for (NamespacedKey key : EnchantmentRegister.keys) {
				if (e.getInventory().getItem(1) != null) {
					if (e.getInventory().getItem(1).getItemMeta().hasEnchant(Enchantment.getByKey(key)) && !e.getResult().getType().equals(Material.AIR)) {
						ItemStack item = e.getResult();
						ItemMeta meta = item.getItemMeta();
						
						int level = e.getInventory().getItem(0).getItemMeta().getEnchantLevel(Enchantment.getByKey(key));
						boolean allClear = true;
						
						for (Enchantment ench : e.getInventory().getItem(0).getItemMeta().getEnchants().keySet())
							if (ench.conflictsWith(Enchantment.getByKey(key))) {
								allClear = false;
								//e.getViewers().get(0).sendMessage("Conflicting with: " + ench.getKey().getKey());
							}
						
						if (allClear) {
							meta.addEnchant(Enchantment.getByKey(key), level, true);
							item.setItemMeta(meta);
							e.setResult(item);
						}
					}
				}
				if (e.getInventory().getItem(0).getItemMeta().hasEnchant(Enchantment.getByKey(key)) && !e.getResult().getType().equals(Material.AIR)) {
					ItemStack item = e.getResult();
					ItemMeta meta = item.getItemMeta();
					
					int level = e.getInventory().getItem(0).getItemMeta().getEnchantLevel(Enchantment.getByKey(key));
					
					if (e.getInventory().getItem(1) != null && Enchantment.getByKey(key).getMaxLevel() > level) {
						if (e.getInventory().getItem(1).getItemMeta().hasEnchant(Enchantment.getByKey(key))) 
							if (e.getInventory().getItem(1).getItemMeta().getEnchantLevel(Enchantment.getByKey(key)) == level) {
								level += 1;
							}
					}
					
					meta.addEnchant(Enchantment.getByKey(key), level, true);
					
					if (level > e.getInventory().getItem(0).getItemMeta().getEnchantLevel(Enchantment.getByKey(key))) {
						List<String> lore =(meta.getLore() == null ? new ArrayList<String>() : meta.getLore());
						List<String> colorlessLore = new ArrayList<String>();
						for(int i = 0; i < lore.size(); i++) {
							colorlessLore.add(ChatColor.stripColor(lore.get(i)));
						}
						for (Enchantment ench : EnchantmentRegister.levelableEnchants.keySet())
							if (!colorlessLore.contains(EnchantmentRegister.levelableEnchants.get(ench) + " " + toRomanNumeral(meta.getEnchantLevel(ench))) && ench.getKey().equals(key)) {
								for(String line : colorlessLore)
									if(line.contains(EnchantmentRegister.levelableEnchants.get(ench)))
										lore.remove(colorlessLore.indexOf(line));
								lore.add(ChatColor.GRAY + EnchantmentRegister.levelableEnchants.get(ench) + " " + toRomanNumeral(meta.getEnchantLevel(ench)));
							}
						meta.setLore(lore);
					}
					
					item.setItemMeta(meta);
					e.setResult(item);
				}
			}
		}
	}
	
}
