package nedocomputers;

import java.util.Iterator;
import java.util.logging.LogManager;

import org.apache.logging.log4j.Logger;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.ShapedOreRecipe;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.FMLEventChannel;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.common.registry.GameRegistry;

@Mod(modid = Settings.MODID, name = "NedoComputers", dependencies="after:RedLogic;after:ProjRed|Transmission")
public class NedoComputers {
	public static FMLEventChannel channel;
	
	public static BlockCPU CPU_Block;
	public static BlockDisplay displayBlock;
	public static BlockCable cableBlock;
	public static BlockIOExpander IOExpanderBlock;
	
	public static Item itemEEPROM;
	public static Item itemForthROM;
	
	public static boolean isRedLogicLoaded = false;
	public static boolean isProjectRedLoaded = false;
	
	@SidedProxy(clientSide = "nedocomputers.client.ClientProxy", serverSide = "nedocomputers.CommonProxy")
	public static CommonProxy proxy;
	
	@Instance(Settings.MODID)
	public static NedoComputers instance;
	
	public NedoComputers() {
		instance = this;
	}
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		if (isRedLogicLoaded = Loader.isModLoaded("RedLogic")) {
			System.out.println("Detect RedLogic");
		}
		if (isProjectRedLoaded = Loader.isModLoaded("ProjRed|Transmission")) {
			System.out.println("Detect ProjRed|Transmission");
		}
		
		Settings.getConfig(event);
		channel = NetworkRegistry.INSTANCE.newEventDrivenChannel(Settings.NETWORK_CHANNEL_NAME);
		proxy.InitPacketHandler();
		
		CPU_Block = new BlockCPU();
		GameRegistry.registerBlock(CPU_Block, "CPU");
		
		displayBlock = new BlockDisplay();
		GameRegistry.registerBlock(displayBlock, "display");
		
		cableBlock = new BlockCable();
		GameRegistry.registerBlock(cableBlock, "cable");
		
		IOExpanderBlock = new BlockIOExpander();
		GameRegistry.registerBlock(IOExpanderBlock, "IOExpander");
		
		itemEEPROM = new ItemEEPROM();
		GameRegistry.registerItem(itemEEPROM, "EEPROM");
		
		itemForthROM = new ItemForthROM();
		GameRegistry.registerItem(itemForthROM, "ForthROM");
		
		GameRegistry.addRecipe(new ShapedOreRecipe(NedoComputers.CPU_Block,
				"ppp", "rdr", "ptp",
				'p', "plankWood",
				'r', Blocks.redstone_block,
				'd', Items.diamond,
				't', NedoComputers.cableBlock));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(NedoComputers.displayBlock,
				"gpp", "glr", "gtp",
				'g', Blocks.glass,
				'p', "plankWood",
				'l', Blocks.redstone_lamp,
				'r', Blocks.redstone_block,
				't', NedoComputers.cableBlock));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(NedoComputers.itemForthROM,
				"rgr", "ldl", "rgr",
				'r', Items.redstone,
				'l', new ItemStack(Items.dye, 1, 4),
				'd', "record",
				'g', Items.glowstone_dust));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(NedoComputers.itemEEPROM,
				"rgr", "lil", "rgr",
				'r', Items.redstone,
				'l', new ItemStack(Items.dye, 1, 4),
				'i', Items.iron_ingot,
				'g', Items.glowstone_dust));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(NedoComputers.IOExpanderBlock,
				"pwp", "prp", "ptp",
				'p', "plankWood",
				'w', Items.redstone,
				'r', Blocks.redstone_block,
				't', NedoComputers.cableBlock));
		
		GameRegistry.addRecipe(new ShapedOreRecipe(NedoComputers.cableBlock,
				"ccc", "rrr", "ccc",
				'c', "ingotCopper",
				'r', Items.redstone));
	}
	
	@EventHandler
	public void load(FMLInitializationEvent evt) {
		proxy.registerRenders();
		NetworkRegistry.INSTANCE.registerGuiHandler(instance, proxy);
		GameRegistry.registerTileEntity(TileEntityCPU.class, Settings.MODID + ".CPU");
		GameRegistry.registerTileEntity(TileEntityCable.class, Settings.MODID + ".Cable");
		if (isRedLogicLoaded) {
			System.out.println("Register TileEntityIOExpanderRL");
			GameRegistry.registerTileEntity(TileEntityIOExpanderRL.class, Settings.MODID + ".IOExpander");
		} else if (isProjectRedLoaded) {
			System.out.println("Register TileEntityIOExpanderPR");
			GameRegistry.registerTileEntity(TileEntityIOExpanderPR.class, Settings.MODID + ".IOExpander");
		} else {
			System.out.println("Register TileEntityIOExpander");
			GameRegistry.registerTileEntity(TileEntityIOExpander.class, Settings.MODID + ".IOExpander");
		}
	}
}
