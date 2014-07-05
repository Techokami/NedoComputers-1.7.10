package nedocomputers.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import nedocomputers.ContainerBusId;
import nedocomputers.INedoPeripheral;
import nedocomputers.NedoComputers;
import nedocomputers.Settings;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiBusId extends GuiContainer {
	private INedoPeripheral tilePeripheral;
	private TileEntity tile;
	final private ResourceLocation texture =
			new ResourceLocation("nedocomputers", "textures/gui/busId.png");
	
	public GuiBusId(TileEntity te) {
		super(new ContainerBusId(te));
		tilePeripheral = (INedoPeripheral)te;
		tile = te;
		xSize = 138;
		ySize = 89;
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		mc.getTextureManager().bindTexture(texture);
		int cx = (width - xSize) / 2;
		int cy = (height - ySize) / 2;
		drawTexturedModalRect(cx, cy, 0, 0, xSize, ySize);
		int id = tilePeripheral.getBusId();
		for (int i = 0; i < 8; i++)
			if ((id & (1 << i)) != 0)
				drawTexturedModalRect(cx + 106 - i * 12, cy + 28, 0, 89, 10, 18);
	}
	
	@Override
	protected void mouseClicked(int i, int j, int par3) {
		super.mouseClicked(i, j, par3);
        int x = i - (width - xSize) / 2;
        int y = j - (height - ySize) / 2;
        int n = 7 - (x - 22) / 12;
        if (y > 27 && y < 46)
        	if (n >= 0 && n < 8 && ((x - 22) % 12) < 10) {
        		ByteBuf buf = Unpooled.buffer();
        		buf.writeByte(Settings.PACKET_TYPE_PERIPHERAL_SET_ID);
    			buf.writeInt(tile.xCoord);
    			buf.writeInt(tile.yCoord);
    			buf.writeInt(tile.zCoord);
    			buf.writeInt(tilePeripheral.getBusId() ^ (1 << n));
    			FMLProxyPacket packet = new FMLProxyPacket(buf, Settings.NETWORK_CHANNEL_NAME);
    			NedoComputers.channel.sendToServer(packet);
        	}
	}
	
	@Override
	 protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		fontRendererObj.drawString(String.format("ID: %d", tilePeripheral.getBusId()), 55, 70, 0x404040, false);
	 }
}
