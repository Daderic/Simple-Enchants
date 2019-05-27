package main.enchantments;

import main.enchantmentHandlers.ChainingHandler;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Excavation extends Enchantment {
	
	public static Excavation ench;
	
	public Excavation(NamespacedKey key) {
		super(key);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment ench) {
		if (ench.equals(Enchantment.getByKey(ChainingHandler.key)))
			return true;
		else
			return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		return EnchantmentTarget.TOOL;
	}

	@Override
	public int getMaxLevel() {
		return 6;
	}

	@Override
	public String getName() {
		return "EXCAVATION";
	}

	@Override
	public int getStartLevel() {
		return 1;
	}

	@Override
	public boolean isCursed() {
		return false;
	}

	@Override
	public boolean isTreasure() {
		return false;
	}

}
