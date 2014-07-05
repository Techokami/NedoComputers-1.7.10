package nedocomputers.client;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import nedocomputers.ContainerCPU;
import nedocomputers.ItemEEPROM;
import nedocomputers.ItemForthROM;
import nedocomputers.NedoComputers;
import nedocomputers.Settings;
import nedocomputers.TileEntityCPU;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class GuiComputer extends GuiContainer {
	private TileEntityCPU tile;
	private GuiTextField TextField;
	
	final private ResourceLocation texture = new ResourceLocation("nedocomputers", "textures/gui/computer.png");
	
	public GuiComputer(EntityPlayer p, TileEntityCPU te) {
		super(new ContainerCPU(p, te));
		tile = te;
		xSize = 176;
		ySize = 206;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		Keyboard.enableRepeatEvents(true);
		buttonList.add(new GuiButton(0, guiLeft + 10, guiTop + 98, 50, 20, ""));
		buttonList.add(new GuiButton(1, guiLeft + 65, guiTop + 98, 50, 20, "Reset"));
		buttonList.add(new GuiButton(2, guiLeft + 120, guiTop + 98, 45, 20, "Save"));
		TextField = new GuiTextField(fontRendererObj, guiLeft + 10, guiTop + 75, 120, 16);
		TextField.setMaxStringLength(25);
	}
	
	@Override
	public void drawScreen(int par1, int par2, float par3) {
		super.drawScreen(par1, par2, par3);
        GL11.glDisable(GL11.GL_LIGHTING);
        GL11.glDisable(GL11.GL_BLEND);
		TextField.drawTextBox();
	}
	
	@Override
	protected void keyTyped(char chr, int scancode) {
		if (!TextField.textboxKeyTyped(chr, scancode))
			super.keyTyped(chr, scancode);
	}
	
	@Override
	protected void mouseClicked(int i, int j, int par3) {
		super.mouseClicked(i, j, par3);
        TextField.mouseClicked(i, j, par3);
        int x = i - (width - xSize) / 2;
        int y = j - (height - ySize) / 2;
        int n = 7 - (x - 22) / 12;
        if (y > 27 && y < 46)
        	if (n >= 0 && n < 8 && ((x - 22) % 12) < 10) {
        		ByteBuf buf = Unpooled.buffer();
        		buf.writeByte(Settings.PACKET_TYPE_COMPUTER_SET_ID);
    			buf.writeInt(tile.xCoord);
    			buf.writeInt(tile.yCoord);
    			buf.writeInt(tile.zCoord);
    			buf.writeInt(tile.cpu_id ^ (1 << n));
    			FMLProxyPacket packet = new FMLProxyPacket(buf, Settings.NETWORK_CHANNEL_NAME);
    			NedoComputers.channel.sendToServer(packet);
        	}
	}
	
	@Override
	public void updateScreen() {
		if (tile.isTurnedOn) {
			((GuiButton)buttonList.get(0)).displayString = "Turn off";
			((GuiButton)buttonList.get(1)).enabled = true;
		} else {
			((GuiButton)buttonList.get(0)).displayString = "Turn on";
			if (inventorySlots.getSlot(0).getStack() != null)
				if (inventorySlots.getSlot(0).getStack().getItem() instanceof ItemEEPROM ||
						inventorySlots.getSlot(0).getStack().getItem() instanceof ItemForthROM)
					((GuiButton)buttonList.get(0)).enabled = true;
				else
					((GuiButton)buttonList.get(0)).enabled = false;
			else
				((GuiButton)buttonList.get(0)).enabled = false;
			((GuiButton)buttonList.get(1)).enabled = false;
		}
		
		if (inventorySlots.getSlot(0).getStack() != null)
			if (inventorySlots.getSlot(0).getStack().getItem() instanceof ItemEEPROM)
				((GuiButton)buttonList.get(2)).enabled = true;
			else
				((GuiButton)buttonList.get(2)).enabled = false;
		else
			((GuiButton)buttonList.get(2)).enabled = false;
	}
	
	@Override
	protected void actionPerformed(GuiButton button) {
		ByteBuf buf = Unpooled.buffer();
		if (button.id == 0) {
			if (tile.isTurnedOn)
				buf.writeByte(Settings.PACKET_TYPE_COMPUTER_OFF);
			else
				buf.writeByte(Settings.PACKET_TYPE_COMPUTER_ON);
			buf.writeInt(tile.xCoord);
			buf.writeInt(tile.yCoord);
			buf.writeInt(tile.zCoord);
		} else if (button.id == 1) {
			buf.writeByte(Settings.PACKET_TYPE_COMPUTER_RESET);
			buf.writeInt(tile.xCoord);
			buf.writeInt(tile.yCoord);
			buf.writeInt(tile.zCoord);
		} else if (button.id == 2) {
			buf.writeByte(Settings.PACKET_TYPE_COMPUTER_SAVE);
			buf.writeInt(tile.xCoord);
			buf.writeInt(tile.yCoord);
			buf.writeInt(tile.zCoord);
			ByteBufUtils.writeUTF8String(buf, TextField.getText().trim());
		}
		FMLProxyPacket packet = new FMLProxyPacket(buf, Settings.NETWORK_CHANNEL_NAME);
		NedoComputers.channel.sendToServer(packet);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int x, int y) {
		mc.getTextureManager().bindTexture(texture);
		int cx = (width - xSize) / 2;
		int cy = (height - ySize) / 2;
		drawTexturedModalRect(cx, cy, 0, 0, xSize, ySize);
		for (int i = 0; i < 8; i++)
			if ((tile.cpu_id & (1 << i)) != 0)
				drawTexturedModalRect(cx + 106 - i * 12, cy + 28, 0, 206, 10, 18);
	}
	
	@Override
	 protected void drawGuiContainerForegroundLayer(int p_146979_1_, int p_146979_2_) {
		//fontRendererObj.drawString("Set Bus ID", 45, 10, 0x404040, false);
		fontRendererObj.drawString(String.format("ID: %d", tile.cpu_id), 133, 37, 0x404040, false);
		//for (int i = 0; i < 8; i++) fontRendererObj.drawString(String.format("%d", i), 109 - i * 12, 50, 0x404040, false);
	 }
	
	@Override
	public void onGuiClosed() {
        super.onGuiClosed();
        Keyboard.enableRepeatEvents(false);
	}
}
