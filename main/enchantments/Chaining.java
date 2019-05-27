package main.enchantments;

import main.enchantmentHandlers.ExcavationHandler;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;

public class Chaining extends Enchantment {

	public Chaining(NamespacedKey key) {
		super(key);
	}

	@Override
	public boolean canEnchantItem(ItemStack item) {
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment ench) {
		if (ench.equals(Enchantment.getByKey(ExcavationHandler.key)))
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
		return 1;
	}

	@Override
	public String getName() {
		return "CHAINING";
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
