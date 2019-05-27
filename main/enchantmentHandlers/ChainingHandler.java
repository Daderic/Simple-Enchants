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
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ChainingHandler {

	public static NamespacedKey key = new NamespacedKey(XcavationMain.getInstance(), "chaining");
	public static List<Material> ores = new ArrayList<Material>();
	public static List<Location> veinBlockLocations = new ArrayList<Location>();
	static Random r = new Random();
	static int blocksMined = 0;
	public static final int MAX_CHAIN = 16;
	
	static {
		ores.add(Material.IRON_ORE);
		ores.add(Material.GOLD_ORE);
		ores.add(Material.COAL_ORE);
		ores.add(Material.LAPIS_ORE);
		ores.add(Material.DIAMOND_ORE);
		ores.add(Material.REDSTONE_ORE);
		ores.add(Material.EMERALD_ORE);
		ores.add(Material.NETHER_QUARTZ_ORE);
	}
	
	public static void chainOres(Block block, Player player) {
		
		ItemStack item = player.getInventory().getItemInMainHand();
		
		if (ores.contains(block.getType()) && !veinBlockLocations.contains(block.getLocation()) && !item.getType().equals(Material.AIR) && veinBlockLocations.size() < MAX_CHAIN) {
			
			int x = block.getX();
			int y = block.getY();
			int z = block.getZ();
			
			//int blocksMined = 0;
			
			
			
			
			World world = block.getWorld();
			Location centerBlockLoc = block.getLocation();
			
			if(!veinBlockLocations.contains(centerBlockLoc))
				veinBlockLocations.add(centerBlockLoc);
			
			Location[] relativeLocations = {
					new Location(world, x, y+1, z),
					new Location(world, x, y-1, z),
					new Location(world, x+1, y, z),
					new Location(world, x-1, y, z),
					new Location(world, x, y, z+1),
					new Location(world, x, y, z-1),
					};
			
			for (Location loc : relativeLocations) {
				Block b = world.getBlockAt(loc);
				if (b.getType().equals(block.getType())) {
					chainOres(b, player);
					//player.sendMessage("Chained Successfully");
				}
			}
			
			if (item.containsEnchantment(Enchantment.SILK_TOUCH)) {
				try {
					world.dropItemNaturally(centerBlockLoc, new ItemStack(block.getType()));
				} catch (Exception e) {}
				block.setType(Material.AIR);
			} else if (item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && r.nextBoolean()) {
				int amt = block.getDrops().iterator().next().getAmount();
				boolean isOre = false;
				for (Material ore : ExcavationHandler.fortuneOres)
					if (block.getType().equals(ore)) {
						isOre = true;
						break;
					}
				if (isOre)
					amt += r.nextInt((item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)*2)+1);
				try {
					world.dropItemNaturally(centerBlockLoc, new ItemStack(block.getDrops().iterator().next().getType(), amt));
					ExperienceOrb orb = world.spawn(centerBlockLoc, ExperienceOrb.class);
					orb.setExperience(100);
				} catch (Exception e) {}
				block.setType(Material.AIR);
			}
			if (item.containsEnchantment(Enchantment.DURABILITY)) {
				if (r.nextInt((item.getEnchantmentLevel(Enchantment.DURABILITY)+1))+1 == 1)
					blocksMined++;
			} else {
				blocksMined++;
			}
			
			block.breakNaturally(item);

			veinBlockLocations.remove(centerBlockLoc);
			//System.out.println(veinBlockLocations.size());
			
		}
		
	}

	public static void damageItem(Player player) {
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		Damageable d = (Damageable) meta;
		int damage = d.getDamage();
		
		d.setDamage(damage+blocksMined);

		item.setItemMeta(meta);
		
		if(d.getDamage() >= item.getType().getMaxDurability()) {
			player.getInventory().setItemInMainHand(null);
		}
		
		blocksMined = 0;
	}
	
}
