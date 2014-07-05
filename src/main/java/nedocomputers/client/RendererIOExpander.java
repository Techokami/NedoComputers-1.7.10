package nedocomputers.client;

import org.lwjgl.opengl.GL11;

import nedocomputers.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RendererIOExpander extends TileEntitySpecialRenderer {
	ModelIOExpander model;
	
	public RendererIOExpander() {
		model = new ModelIOExpander();
	}
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y, double z, float var8) {
		int blockMeta = te.getBlockMetadata();
		ResourceLocation textures = new ResourceLocation("nedocomputers", "textures/blocks/expander.png");
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5F, y + 0.5F, z + 0.5F);
		GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
		GL11.glRotatef(180F, 0.0F, 0.0F, 1.0F); 
		Minecraft.getMinecraft().renderEngine.bindTexture(textures);
		switch (blockMeta) {
			case 0:
				GL11.glRotatef(-90F, 1F, 0F, 0F);
				break;
			case 1:
				GL11.glRotatef(90F, 1F, 0F, 0F);
				break;
			case 2:
				GL11.glRotatef(180F, 0F, 1F, 0F);
				break;
			case 4:
				GL11.glRotatef(90F, 0F, 1F, 0F);
				break;
			case 5:
				GL11.glRotatef(-90F, 0F, 1F, 0F);
				break;
		}
		model.renderAll();
		GL11.glPopMatrix();
	}
}
