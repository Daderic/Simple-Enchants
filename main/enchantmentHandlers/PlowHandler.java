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

public class PlowHandler {

	public static NamespacedKey key = new NamespacedKey(XcavationMain.getInstance(), "plow");	
	public static List<Material> tillableBlocks = new ArrayList<Material>();
	static Random r = new Random();
	
	static {
		tillableBlocks.add(Material.DIRT);
		tillableBlocks.add(Material.GRASS_BLOCK);
		tillableBlocks.add(Material.PODZOL);
		tillableBlocks.add(Material.MYCELIUM);
		tillableBlocks.add(Material.GRASS_PATH);
		tillableBlocks.add(Material.COARSE_DIRT);
	}
	
	
	/**
	 * Plows an area based on the level
	 * @param b The block that the player clicked
	 * @param p The player that clicked the block
	 * @param l The level of the plow enchant
	 * @param w The world this took place in
	 */
	public static void plow(Block b, Player p, int l, World w) {
		
		if (tillableBlocks.contains(b.getType())) {
			int x = b.getX();
			int y = b.getY();
			int z = b.getZ();
			
			ItemStack item = p.getInventory().getItemInMainHand();
			ItemMeta meta = item.getItemMeta();
			Damageable d = (Damageable) meta;
			int damage = d.getDamage();
			
			// area = 2*l+1; NxN or like 3x3 then 5x5 then 7x7
			
			int blocksTilled = 0;
			
			List<Location> locs = new ArrayList<Location>();
			
			for (int i = x-l; i <= x+l; i++) {
				for (int j = z-l; j <= z+l; j++) {
					locs.add(new Location(w,i,y,j));
				}
			}
			
			for (Location loc : locs) {
				Block block = w.getBlockAt(loc);
				if (tillableBlocks.contains(block.getType()) && w.getBlockAt(loc.getBlockX(), loc.getBlockY()+1, loc.getBlockZ()).getType().equals(Material.AIR)) {
					if (block.getType().equals(Material.COARSE_DIRT))
						block.setType(Material.DIRT);
					else
						block.setType(Material.FARMLAND);
					
					if (item.containsEnchantment(Enchantment.DURABILITY)) {
						if (r.nextInt((item.getEnchantmentLevel(Enchantment.DURABILITY)+1))+1 == 1)
							blocksTilled++;
					} else {
						blocksTilled++;
					}
				}
			}
			
			d.setDamage(damage+blocksTilled);
	
			item.setItemMeta(meta);
			
			if(d.getDamage() >= item.getType().getMaxDurability()) {
				p.getInventory().setItemInMainHand(null);
			}
		}
		
	}
	
}
