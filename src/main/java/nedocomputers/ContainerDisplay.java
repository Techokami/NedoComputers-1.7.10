package nedocomputers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.Deflater;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.item.ItemStack;

public class ContainerDisplay extends Container {
	private TileEntityCPU Computer_te;

	public ContainerDisplay(TileEntityCPU te) {
		super();
		Computer_te = te;
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting)super.crafters.get(i);
			ByteBuf buf = Unpooled.buffer();
			buf.writeByte(Settings.PACKET_TYPE_DISPLAY);
			buf.writeInt(Computer_te.xCoord);
			buf.writeInt(Computer_te.yCoord);
			buf.writeInt(Computer_te.zCoord);
		    Deflater deflater = new Deflater();
		    deflater.setLevel(Deflater.BEST_COMPRESSION);
		    deflater.setInput(Computer_te.video_ram);
		    deflater.finish();
		    ByteArrayOutputStream bos = new ByteArrayOutputStream();
		    byte[] def_buf = new byte[1024];
		    while (!deflater.finished()) {
		        int count = deflater.deflate(def_buf);
		        bos.write(def_buf, 0, count);
		    }
		    try {bos.close();} catch (IOException e) {}
		    byte[] compressed_video_ram = bos.toByteArray();
		    buf.writeInt(compressed_video_ram.length);
		    buf.writeBytes(compressed_video_ram);
			FMLProxyPacket packet = new FMLProxyPacket(buf, Settings.NETWORK_CHANNEL_NAME);
			NedoComputers.channel.sendTo(packet, (EntityPlayerMP)ic);
		}
	}
	
	@Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slotID){
        return null;
    }
	
	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
}
