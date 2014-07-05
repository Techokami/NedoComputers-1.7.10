package nedocomputers;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

public class ItemForthROM extends Item {

	public ItemForthROM() {
		setUnlocalizedName(Settings.MODID + "." + "ForthROM");
		setTextureName(Settings.MODID + ":" + "ForthROM");
		setCreativeTab(CreativeTabs.tabRedstone);
	}
}
