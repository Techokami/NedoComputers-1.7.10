package nedocomputers.client;

import io.netty.buffer.ByteBuf;

import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

import nedocomputers.ServerPacketHandler;
import nedocomputers.Settings;
import nedocomputers.TileEntityCPU;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.network.FMLNetworkEvent.ClientCustomPacketEvent;

public class ClientPacketHandler extends ServerPacketHandler {

	@SubscribeEvent
	public void onClientPacket(ClientCustomPacketEvent event) throws IOException {
		String channelName = event.packet.channel();
		if (channelName.equals(Settings.NETWORK_CHANNEL_NAME)) {
			ByteBuf buf = event.packet.payload();
			byte packet_id = buf.readByte();
			switch (packet_id) {
				case Settings.PACKET_TYPE_DISPLAY:
					int x = buf.readInt(); int y = buf.readInt(); int z = buf.readInt();
					TileEntity te = Minecraft.getMinecraft().theWorld.getTileEntity(x, y, z);
					if (te instanceof TileEntityCPU) {
						int compressed_len = buf.readInt();
						ByteBuf compressed_video_ram = buf.readBytes(compressed_len);
						byte[] vram = ((TileEntityCPU)te).video_ram;
						Inflater inflater = new Inflater();
						inflater.setInput(compressed_video_ram.array());
						try {
							inflater.inflate(vram);
						} catch (DataFormatException e) {
							e.printStackTrace();
						}
						inflater.end();
					}
			}
		}
	}
}
