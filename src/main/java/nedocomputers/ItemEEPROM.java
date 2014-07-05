package nedocomputers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemEEPROM extends Item {

	public ItemEEPROM() {
		setUnlocalizedName(Settings.MODID + "." + "EEPROM");
		setTextureName(Settings.MODID + ":" + "EEPROM");
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
