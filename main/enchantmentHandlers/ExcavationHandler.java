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
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class ExcavationHandler {
	
	public static List<Material> forbiddenBlocks = new ArrayList<Material>();
	static Random r = new Random();
	
	public static NamespacedKey key = new NamespacedKey(XcavationMain.getInstance(), "excavation");
	
	public static BlockFace face;
	
	public static List<Material> fortuneOres = new ArrayList<Material>();
	
	static {
		forbiddenBlocks.add(Material.BEDROCK);
		forbiddenBlocks.add(Material.BARRIER);
		forbiddenBlocks.add(Material.COMMAND_BLOCK);
		forbiddenBlocks.add(Material.STRUCTURE_BLOCK);
		forbiddenBlocks.add(Material.AIR);
		forbiddenBlocks.add(Material.WATER);
		forbiddenBlocks.add(Material.LAVA);
		forbiddenBlocks.add(Material.END_PORTAL_FRAME);
		forbiddenBlocks.add(Material.END_PORTAL);
		forbiddenBlocks.add(Material.END_GATEWAY);
		
		fortuneOres.add(Material.COAL_ORE);
		fortuneOres.add(Material.LAPIS_ORE);
		fortuneOres.add(Material.DIAMOND_ORE);
		fortuneOres.add(Material.REDSTONE_ORE);
		fortuneOres.add(Material.EMERALD_ORE);
		fortuneOres.add(Material.NETHER_QUARTZ_ORE);
	}
	
	/**
	 * Excavates an area based on the level of the Excavation Enchantment
	 * @param player The Player that fired the Event
	 * @param block The block that was originally mined by <b>player</b>
	 * @param level The level of excavation
	 * @param world The world that <b>player</b> is in
	 * @param blockFace The BlockFace that <b>player</b> mined
	 */
	public static void excavate(Player player, Block block, int level, World world, BlockFace blockFace) {
		
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		
		ItemStack item = player.getInventory().getItemInMainHand();
		ItemMeta meta = item.getItemMeta();
		Damageable d = (Damageable) meta;
		float ogHardness = block.getType().getHardness();
		int damage = d.getDamage();
		
		int depth = level;
		
		Location[] upDownLocations = {new Location(world, x-1, y, z-1), new Location(world, x, y, z-1), new Location(world, x+1, y, z-1), new Location(world, x-1, y, z), new Location(world, x, y, z), new Location(world, x+1, y, z), new Location(world, x-1, y, z+1), new Location(world, x, y, z+1), new Location(world, x+1, y, z+1)};
		Location[] northSouthLocations = {new Location(world, x-1, y+1, z), new Location(world, x, y+1, z), new Location(world, x+1, y+1, z), new Location(world, x-1, y, z), new Location(world, x, y, z), new Location(world, x+1, y, z), new Location(world, x-1, y-1, z), new Location(world, x, y-1, z), new Location(world, x+1, y-1, z)};
		Location[] eastWestLocations = {new Location(world, x, y+1, z-1), new Location(world, x, y+1, z), new Location(world, x, y+1, z+1), new Location(world, x, y, z-1), new Location(world, x, y, z), new Location(world, x, y, z+1), new Location(world, x, y-1, z-1), new Location(world, x, y-1, z), new Location(world, x, y-1, z+1)};
		int blocksMined = 0;
		
		BlockFace[] faces = {BlockFace.NORTH, BlockFace.SOUTH, BlockFace.EAST, BlockFace.WEST, BlockFace.UP, BlockFace.DOWN};
		int[] plusMinus = {1, -1, -1, 1, -1, 1};
		Location[][] locations = {northSouthLocations, eastWestLocations, upDownLocations};
		
		for (int j = 0; j < faces.length; j++) {
			if (blockFace.equals(faces[j])) {
				for (int i = 0; i < depth; i++) {
					for (Location l : locations[(int) j/2]) {
						Block b = world.getBlockAt(l);
						boolean badBlock = false;
						for(Material mat : ExcavationHandler.forbiddenBlocks)
							if (b.getType().equals(mat) || b.getType().getHardness() > ogHardness) {
								badBlock = true;
								break;
							}
						if (!badBlock) {
							if (item.containsEnchantment(Enchantment.SILK_TOUCH)) {
								try {
									world.dropItemNaturally(l, new ItemStack(b.getType()));
								} catch (Exception e) {}
								b.setType(Material.AIR);
							} else if (item.containsEnchantment(Enchantment.LOOT_BONUS_BLOCKS) && r.nextBoolean()) {
								int amt = b.getDrops().iterator().next().getAmount();
								boolean isOre = false;
								for (Material ore : fortuneOres)
									if (b.getType().equals(ore)) {
										isOre = true;
										break;
									}
								if (isOre)
									amt += r.nextInt((item.getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS)*2)+1);
								try {
									world.dropItemNaturally(l, new ItemStack(b.getDrops().iterator().next().getType(), amt));
								} catch (Exception e) {}
								b.setType(Material.AIR);
							}
							if (item.containsEnchantment(Enchantment.DURABILITY)) {
								if (r.nextInt((item.getEnchantmentLevel(Enchantment.DURABILITY)+1))+1 == 1)
									blocksMined++;
							} else {
								blocksMined++;
							}
							b.breakNaturally(item);
						}
						if (faces[j].equals(BlockFace.NORTH) || faces[j].equals(BlockFace.SOUTH))
							l.setZ(l.getZ()+plusMinus[j]);
						else if (faces[j].equals(BlockFace.EAST) || faces[j].equals(BlockFace.WEST))
							l.setX(l.getX()+plusMinus[j]);
						else if (faces[j].equals(BlockFace.UP) || faces[j].equals(BlockFace.DOWN))
							l.setY(l.getY()+plusMinus[j]);
					}
				}
				break;
			}
		}
		
		d.setDamage(damage+blocksMined);

		item.setItemMeta(meta);
		
		if(d.getDamage() >= item.getType().getMaxDurability()) {
			player.getInventory().setItemInMainHand(null);
		}
		
	}
	
}
