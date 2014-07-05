package nedocomputers;

import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.common.config.Configuration;

public class Settings {
	public static final String MODID = "nedocomputers";
	
	public static final int GUI_DISPLAY = 0;
	public static final int GUI_COMPUTER = 1;
	public static final int GUI_BUS_ID = 2;
	
	public static final String NETWORK_CHANNEL_NAME = MODID;
	
	public static final byte PACKET_TYPE_DISPLAY_KEYDOWN = 0;
	public static final byte PACKET_TYPE_DISPLAY = 1;
	public static final byte PACKET_TYPE_COMPUTER_ON = 2;
	public static final byte PACKET_TYPE_COMPUTER_OFF = 3;
	public static final byte PACKET_TYPE_COMPUTER_RESET = 4;
	public static final byte PACKET_TYPE_COMPUTER_SAVE = 5;
	public static final byte PACKET_TYPE_COMPUTER_SET_ID = 6;
	public static final byte PACKET_TYPE_PERIPHERAL_SET_ID = 7;
	
	public static int INSTRUCTIONS_PER_TICK = 5000;
	
	public static void getConfig(FMLPreInitializationEvent event) {
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		INSTRUCTIONS_PER_TICK = config.get(Configuration.CATEGORY_GENERAL,
				"INSTRUCTIONS_PER_TICK", INSTRUCTIONS_PER_TICK).getInt();
		config.save();
	}
}
