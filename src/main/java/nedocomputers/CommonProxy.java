package nedocomputers;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class CommonProxy implements IGuiHandler {
	public void registerRenders() {
	}
	
	public void InitPacketHandler() {
		NedoComputers.channel.register(new ServerPacketHandler());
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		TileEntity te = world.getTileEntity(x, y, z);
		switch (ID) {
			case Settings.GUI_DISPLAY:
				if (te != null && te instanceof TileEntityCPU)
					return new ContainerDisplay((TileEntityCPU)te);
				else
					return null;
			case Settings.GUI_COMPUTER:
				if (te != null && te instanceof TileEntityCPU)
					return new ContainerCPU(player, (TileEntityCPU)te);
				else
					return null;
			case Settings.GUI_BUS_ID:
				if (te != null && te instanceof INedoPeripheral)
					return new ContainerBusId(te);
				else
					return null;
			default: return null;
		}
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world,
			int x, int y, int z) {
		return null;
	}
}
