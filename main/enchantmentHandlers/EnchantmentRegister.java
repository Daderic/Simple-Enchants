package main.enchantmentHandlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import main.enchantments.Chaining;
import main.enchantments.Excavation;
import main.enchantments.Lumbering;
import main.enchantments.Plow;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;

public class EnchantmentRegister {

	public static List<Enchantment> customEnchants = new ArrayList<Enchantment>();
	public static List<NamespacedKey> keys = new ArrayList<NamespacedKey>();
	public static Map<Enchantment, String> levelableEnchants = new HashMap<Enchantment, String>();
	public static Map<Enchantment, String> nonLevelableEnchants = new HashMap<Enchantment, String>();
	
	static {
		customEnchants.add(new Excavation(ExcavationHandler.key));
		customEnchants.add(new Chaining(ChainingHandler.key));
		customEnchants.add(new Lumbering(LumberingHandler.key));
		customEnchants.add(new Plow(PlowHandler.key));
		
		//Visible Names for Enchants
		levelableEnchants.put(customEnchants.get(0), "Excavation");
		levelableEnchants.put(customEnchants.get(3), "Plow");
		
		nonLevelableEnchants.put(customEnchants.get(1), "Chaining");
		nonLevelableEnchants.put(customEnchants.get(2), "Lumbering");
		//Visible Names for Enchants
		
		keys.add(ExcavationHandler.key);
		keys.add(ChainingHandler.key);
		keys.add(LumberingHandler.key);
		keys.add(PlowHandler.key);
	}
	
	public static void register() {
		
		for (Enchantment ench : customEnchants) {
			try {
				Field acceptingNew = Enchantment.class.getDeclaredField("acceptingNew");
				acceptingNew.setAccessible(true);
				acceptingNew.set(null, true);
					Enchantment.registerEnchantment(ench);
			} catch (Exception e) {
				//e.printStackTrace();
			}
		}
		
	}
	
}
