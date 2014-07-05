package nedocomputers;

import io.netty.buffer.ByteBuf;

import java.io.IOException;

import nedocomputers.Settings;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.FMLNetworkEvent.ServerCustomPacketEvent;

public class ServerPacketHandler {

	@SubscribeEvent
	public void onServerPacket(ServerCustomPacketEvent event) throws IOException {
		String channelName = event.packet.channel();
		if (channelName.equals(Settings.NETWORK_CHANNEL_NAME)) {
			World world = ((NetHandlerPlayServer)event.handler).playerEntity.worldObj;
			ByteBuf buf = event.packet.payload();
			byte packet_id = buf.readByte();
			switch (packet_id) {
				case Settings.PACKET_TYPE_DISPLAY_KEYDOWN: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					byte chr = buf.readByte();
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						((TileEntityCPU)te).KeyTyped(chr);
					}
					break;
				}
				case Settings.PACKET_TYPE_COMPUTER_ON: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						((TileEntityCPU)te).turnOn();
					}
					break;
				}
				case Settings.PACKET_TYPE_COMPUTER_OFF: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						((TileEntityCPU)te).turnOff();
					}
					break;
				}
				case Settings.PACKET_TYPE_COMPUTER_RESET: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						((TileEntityCPU)te).reset();
					}
					break;
				}
				case Settings.PACKET_TYPE_COMPUTER_SAVE: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					String str = ByteBufUtils.readUTF8String(buf);
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						((TileEntityCPU)te).SaveToEEPROM(str);
					}
					break;
				}
				case Settings.PACKET_TYPE_COMPUTER_SET_ID: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					int id = buf.readInt();
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						((TileEntityCPU)te).cpu_id = id;
					}
					break;
				}
				case Settings.PACKET_TYPE_PERIPHERAL_SET_ID: {
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					int id = buf.readInt();
					TileEntity te = world.getTileEntity(x, y, z);
					if (te instanceof INedoPeripheral) {
						((INedoPeripheral)te).setBusId(id);
					}
					break;
				}
			}
		}
	}
}
