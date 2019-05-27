package main.enchantmentHandlers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import main.XcavationMain;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class LumberingHandler {

	public static NamespacedKey key = new NamespacedKey(XcavationMain.getInstance(), "lumbering");
	public static List<Material> logs = new ArrayList<Material>();
	public static List<Location> treeBlockLocations = new ArrayList<Location>();
	
	static Random r = new Random();
	
	static int blocksMined = 0;
	public static final int MAX_COUNT = 48;
	
	static {
		logs.add(Material.ACACIA_LOG);
		logs.add(Material.BIRCH_LOG);
		logs.add(Material.DARK_OAK_LOG);
		logs.add(Material.JUNGLE_LOG);
		logs.add(Material.OAK_LOG);
		logs.add(Material.SPRUCE_LOG);
	}
	
	public static void chainBlocks(Block b, Player p) {
		
		ItemStack item = p.getInventory().getItemInMainHand();
		
		if (logs.contains(b.getType()) && !treeBlockLocations.contains(b.getLocation()) && !item.getType().equals(Material.AIR) && treeBlockLocations.size() < MAX_COUNT) {
			
			int x = b.getX();
			int y = b.getY();
			int z = b.getZ();
			
			World world = b.getWorld();
			Location centerBlockLoc = b.getLocation();
			
			if(!treeBlockLocations.contains(centerBlockLoc))
				treeBlockLocations.add(centerBlockLoc);
			
			Location[] relativeLocations = {
					new Location(world, x, y+1, z),
					new Location(world, x+1, y, z),
					new Location(world, x-1, y, z),
					new Location(world, x, y, z+1),
					new Location(world, x, y, z-1),
					};
			
			for (Location loc : relativeLocations) {
				Block block = world.getBlockAt(loc);
				if (block.getType().equals(b.getType())) {
					chainBlocks(block, p);
				}
			}
			
			if (item.containsEnchantment(Enchantment.DURABILITY)) {
				if (r.nextInt((item.getEnchantmentLevel(Enchantment.DURABILITY)+1))+1 == 1)
					blocksMined++;
			} else {
				blocksMined++;
			}
			
			b.breakNaturally(item);

			treeBlockLocations.remove(centerBlockLoc);
			
		}
		
	}
	
	public static void damageItem(Player p) {
		ItemStack item = p.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		Damageable d = (Damageable) meta;
		int damage = d.getDamage();
		
		d.setDamage(damage+blocksMined);

		item.setItemMeta(meta);
		
		if(d.getDamage() >= item.getType().getMaxDurability()) {
			p.getInventory().setItemInMainHand(null);
		}
		
		blocksMined = 0;
	}
	
}
