package nedocomputers.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import cpw.mods.fml.common.network.internal.FMLProxyPacket;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import nedocomputers.ContainerDisplay;
import nedocomputers.NedoComputers;
import nedocomputers.Settings;
import nedocomputers.TileEntityCPU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;

@SideOnly(Side.CLIENT)
public class GuiDisplay extends GuiContainer {
	private TileEntityCPU tile;
	
	private static int monitor_screen_width = 800;
	private static int monitor_screen_height = 600;

	final private ResourceLocation charset_loc = new ResourceLocation("nedocomputers", "textures/gui/charset.png");

	public GuiDisplay(TileEntityCPU te) {
		super(new ContainerDisplay(te));
		tile = te;
	}
	
	@Override
	public void initGui() {
		//Keyboard.enableRepeatEvents(true);
	}

	@Override
	public void onGuiClosed() {
		//Keyboard.enableRepeatEvents(false);
	}
	
	@Override
    public void drawScreen(int par1, int par2, float par3) {
    	byte[] video_ram = tile.video_ram;
    	int first_line = video_ram[0x2600];
    	int cursor_x = video_ram[0x2602];
    	int cursor_y = video_ram[0x2604];
    	
    	drawDefaultBackground();
    	ScaledResolution scaledresolution = new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
    	int scaleFactor = scaledresolution.getScaleFactor();
    	GL11.glScaled(1.0D/(double)scaleFactor, 1.0D/(double)scaleFactor, 0.0D);
    	
    	drawRect((mc.displayWidth / 2) - (monitor_screen_width / 2) - 20,
    			(mc.displayHeight / 2) - (monitor_screen_height / 2) - 20,
    			(mc.displayWidth / 2) + (monitor_screen_width / 2) + 20,
    			(mc.displayHeight / 2) + (monitor_screen_height / 2) + 20,
    			0xFF888888);

    	GL11.glColor3f(1.0F, 1.0F, 1.0F);
    	mc.renderEngine.bindTexture(charset_loc);
    	int line = first_line;
    	for (int j = 0; j < 75; j++) {
    		for (int i = 0; i < 100; i++) {
    			int x = i * 8 + (mc.displayWidth / 2) - (monitor_screen_width / 2);
    			int y = j * 8 + (mc.displayHeight / 2) - (monitor_screen_height / 2);
    			int chr = ((int)video_ram[line * 128 + i]) & 0xFF;
    			drawTexturedModalRect(x, y, (chr & 0x0F) * 8, (chr / 16) * 8, 8, 8);
    		}
    		line = (line + 1) % 75;
    	}

    	if ((Minecraft.getSystemTime() % 700) < 350 /* && tile.isTurnOn */) {
			int x = cursor_x * 8 + (mc.displayWidth / 2) - (monitor_screen_width / 2);
			int y = cursor_y * 8 + (mc.displayHeight / 2) - (monitor_screen_height / 2);
			int chr = ((((int)video_ram[((cursor_y + first_line) % 75) * 128 + cursor_x]) & 0xFF) ^ 128);
    		drawTexturedModalRect(x, y, (chr & 0x0F) * 8, (chr / 16) * 8, 8, 8);
    	}
    }
	
	@Override
	protected void keyTyped(char chr, int scancode) {
		if (scancode == 1) mc.thePlayer.closeScreen();
		byte ascii = to_ascii(chr, scancode);
		if (ascii != -1) {
			ByteBuf buf = Unpooled.buffer();
			buf.writeByte(Settings.PACKET_TYPE_DISPLAY_KEYDOWN);
			buf.writeInt(tile.xCoord);
			buf.writeInt(tile.yCoord);
			buf.writeInt(tile.zCoord);
			buf.writeByte(ascii);
			FMLProxyPacket packet = new FMLProxyPacket(buf, Settings.NETWORK_CHANNEL_NAME);
			NedoComputers.channel.sendToServer(packet);
		}		
	}
	
	@Override
    public boolean doesGuiPauseGame() {
        return false;
    }
	
	protected byte to_ascii(char chr, int scancode) {
		switch (scancode) {
			case Keyboard.KEY_SPACE: return 0x20;
			case Keyboard.KEY_BACK: return 0x08; // Backspace.
			case Keyboard.KEY_RETURN: return 0x0D;
		}
		if (Character.isUpperCase(chr)) {
			switch (scancode) {
				case Keyboard.KEY_A: return 'A';
				case Keyboard.KEY_B: return 'B';
				case Keyboard.KEY_C: return 'C';
				case Keyboard.KEY_D: return 'D';
				case Keyboard.KEY_E: return 'E';
				case Keyboard.KEY_F: return 'F';
				case Keyboard.KEY_G: return 'G';
				case Keyboard.KEY_H: return 'H';
				case Keyboard.KEY_I: return 'I';
				case Keyboard.KEY_J: return 'J';
				case Keyboard.KEY_K: return 'K';
				case Keyboard.KEY_L: return 'L';
				case Keyboard.KEY_M: return 'M';
				case Keyboard.KEY_N: return 'N';
				case Keyboard.KEY_O: return 'O';
				case Keyboard.KEY_P: return 'P';
				case Keyboard.KEY_Q: return 'Q';
				case Keyboard.KEY_R: return 'R';
				case Keyboard.KEY_S: return 'S';
				case Keyboard.KEY_T: return 'T';
				case Keyboard.KEY_U: return 'U';
				case Keyboard.KEY_V: return 'V';
				case Keyboard.KEY_W: return 'W';
				case Keyboard.KEY_X: return 'X';
				case Keyboard.KEY_Y: return 'Y';
				case Keyboard.KEY_Z: return 'Z';
			}
		} else {
			switch (scancode) {
			case Keyboard.KEY_A: return 'a';
			case Keyboard.KEY_B: return 'b';
			case Keyboard.KEY_C: return 'c';
			case Keyboard.KEY_D: return 'd';
			case Keyboard.KEY_E: return 'e';
			case Keyboard.KEY_F: return 'f';
			case Keyboard.KEY_G: return 'g';
			case Keyboard.KEY_H: return 'h';
			case Keyboard.KEY_I: return 'i';
			case Keyboard.KEY_J: return 'j';
			case Keyboard.KEY_K: return 'k';
			case Keyboard.KEY_L: return 'l';
			case Keyboard.KEY_M: return 'm';
			case Keyboard.KEY_N: return 'n';
			case Keyboard.KEY_O: return 'o';
			case Keyboard.KEY_P: return 'p';
			case Keyboard.KEY_Q: return 'q';
			case Keyboard.KEY_R: return 'r';
			case Keyboard.KEY_S: return 's';
			case Keyboard.KEY_T: return 't';
			case Keyboard.KEY_U: return 'u';
			case Keyboard.KEY_V: return 'v';
			case Keyboard.KEY_W: return 'w';
			case Keyboard.KEY_X: return 'x';
			case Keyboard.KEY_Y: return 'y';
			case Keyboard.KEY_Z: return 'z';
			}
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			switch (scancode) {
				case Keyboard.KEY_0: return ')';
				case Keyboard.KEY_1: return '!';
				case Keyboard.KEY_2: return '@';
				case Keyboard.KEY_3: return '#';
				case Keyboard.KEY_4: return '$';
				case Keyboard.KEY_5: return '%';
				case Keyboard.KEY_6: return '^';
				case Keyboard.KEY_7: return '&';
				case Keyboard.KEY_8: return '*';
				case Keyboard.KEY_9: return '(';
				case Keyboard.KEY_MINUS: return '_';
				case Keyboard.KEY_EQUALS: return '+';
				case Keyboard.KEY_GRAVE: return '~';
				case Keyboard.KEY_LBRACKET: return '{';
				case Keyboard.KEY_RBRACKET: return '}';
				case Keyboard.KEY_SEMICOLON: return ':';
				case Keyboard.KEY_APOSTROPHE: return '"';
				case Keyboard.KEY_BACKSLASH: return '|';
				case Keyboard.KEY_COMMA: return '<';
				case Keyboard.KEY_PERIOD: return '>';
				case Keyboard.KEY_SLASH: return '?';
			}
		} else {
			switch (scancode) {
				case Keyboard.KEY_0: return '0';
				case Keyboard.KEY_1: return '1';
				case Keyboard.KEY_2: return '2';
				case Keyboard.KEY_3: return '3';
				case Keyboard.KEY_4: return '4';
				case Keyboard.KEY_5: return '5';
				case Keyboard.KEY_6: return '6';
				case Keyboard.KEY_7: return '7';
				case Keyboard.KEY_8: return '8';
				case Keyboard.KEY_9: return '9';
				case Keyboard.KEY_MINUS: return '-';
				case Keyboard.KEY_EQUALS: return '=';
				case Keyboard.KEY_GRAVE: return '`';
				case Keyboard.KEY_LBRACKET: return '[';
				case Keyboard.KEY_RBRACKET: return ']';
				case Keyboard.KEY_SEMICOLON: return ';';
				case Keyboard.KEY_APOSTROPHE: return '\'';
				case Keyboard.KEY_BACKSLASH: return '\\';
				case Keyboard.KEY_COMMA: return ',';
				case Keyboard.KEY_PERIOD: return '.';
				case Keyboard.KEY_SLASH: return '/';
			}
		}
		return -1;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2,
			int var3) {
		// TODO Auto-generated method stub
		
	}
}
