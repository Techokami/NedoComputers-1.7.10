package nedocomputers.client;

import cpw.mods.fml.client.registry.ClientRegistry;
import nedocomputers.CommonProxy;
import nedocomputers.INedoPeripheral;
import nedocomputers.NedoComputers;
import nedocomputers.Settings;
import nedocomputers.TileEntityCPU;
import nedocomputers.TileEntityCable;
import nedocomputers.TileEntityIOExpander;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;

public class ClientProxy extends CommonProxy {

	public void registerRenders() {
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityCable.class, new RendererCable());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityIOExpander.class, new RendererIOExpander());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(NedoComputers.IOExpanderBlock), new RendererItemIOExpander());
		MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(NedoComputers.cableBlock), new RendererItemCable());
	}
	
	public void InitPacketHandler() {
		NedoComputers.channel.register(new ClientPacketHandler());
	}
	
	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {
			case Settings.GUI_DISPLAY:
				if (te != null && te instanceof TileEntityCPU)
					return new GuiDisplay((TileEntityCPU)te);
				else
					return null;
			case Settings.GUI_COMPUTER:
				if (te != null && te instanceof TileEntityCPU)
					return new GuiComputer(player, (TileEntityCPU)te);
				else
					return null;
			case Settings.GUI_BUS_ID:
				if (te != null && te instanceof INedoPeripheral)
					return new GuiBusId(te);
				else
					return null;
			default: return null;
		}
	}
}
