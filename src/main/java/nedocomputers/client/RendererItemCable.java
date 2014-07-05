package nedocomputers.client;

import org.lwjgl.opengl.GL11;

import nedocomputers.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.IItemRenderer.ItemRenderType;
import net.minecraftforge.client.IItemRenderer.ItemRendererHelper;

public class RendererItemCable implements IItemRenderer {

	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
	    switch (type) {
	      case ENTITY:
	      case EQUIPPED:
	      case EQUIPPED_FIRST_PERSON:
	      case INVENTORY:
	        return true;
	      default:
	        return false;
	    }
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		double r = 0.125;
		int conMask = 1 | 2;
		
		double minY = -0.5;  // DOWN
		double maxY = 0.5;    // UP
		double minZ = -r;  // NORTH
		double maxZ = r;    // SOUTH
		double minX = -r; // WEST
		double maxX = r;   // EAST
		
		double t_minY = 0;  // DOWN
		double t_maxY = 1;  // UP
		double t_minZ = 0.3750;  // NORTH
		double t_maxZ = 0.6250;  // SOUTH
		double t_minX = 0.3750; // WEST
		double t_maxX = 0.6250; // EAST		
		
		ResourceLocation textures = new ResourceLocation("nedocomputers", "textures/blocks/Cable.png");
		GL11.glPushMatrix();
		if (type == ItemRenderType.ENTITY) {
			
		} else if (type == ItemRenderType.EQUIPPED) {
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
			GL11.glRotatef(80F, 1F, 0F, 0F);
		} else {
			GL11.glTranslatef(0.5F, 0.5F, 0.5F);
		}
		GL11.glScalef(1.7F, 1.0F, 1.7F);
		GL11.glDisable(GL11.GL_LIGHTING);
		Minecraft.getMinecraft().renderEngine.bindTexture(textures);
		Tessellator t = Tessellator.instance;
		t.startDrawingQuads();
		
		// Down.
		t.addVertexWithUV(maxX, -r, minZ, t_maxX, t_minZ);
		t.addVertexWithUV(maxX, -r, maxZ, t_maxX, t_maxZ);
		t.addVertexWithUV(minX, -r, maxZ, t_minX, t_maxZ);
		t.addVertexWithUV(minX, -r, minZ, t_minX, t_minZ);
		
		t.addVertexWithUV(maxX, minY, minZ, t_maxX, t_minZ);
		t.addVertexWithUV(maxX, minY, maxZ, t_maxX, t_maxZ);
		t.addVertexWithUV(minX, minY, maxZ, t_minX, t_maxZ);
		t.addVertexWithUV(minX, minY, minZ, t_minX, t_minZ);
		
		// Up.
		t.addVertexWithUV(minX, +r, minZ, t_minX, t_minZ);
		t.addVertexWithUV(minX, +r, maxZ, t_minX, t_maxZ);
		t.addVertexWithUV(maxX, +r, maxZ, t_maxX, t_maxZ);
		t.addVertexWithUV(maxX, +r, minZ, t_maxX, t_minZ);
		
		t.addVertexWithUV(minX, maxY, minZ, t_minX, t_minZ);
		t.addVertexWithUV(minX, maxY, maxZ, t_minX, t_maxZ);
		t.addVertexWithUV(maxX, maxY, maxZ, t_maxX, t_maxZ);
		t.addVertexWithUV(maxX, maxY, minZ, t_maxX, t_minZ);
		
		// North.
		t.addVertexWithUV(minX, minY, -r, t_minX, t_minY);
		t.addVertexWithUV(minX, maxY, -r, t_minX, t_maxY);
		t.addVertexWithUV(maxX, maxY, -r, t_maxX, t_maxY);
		t.addVertexWithUV(maxX, minY, -r, t_maxX, t_minY);
		
		// South.
		t.addVertexWithUV(maxX, minY, +r, t_maxX, t_minY);
		t.addVertexWithUV(maxX, maxY, +r, t_maxX, t_maxY);
		t.addVertexWithUV(minX, maxY, +r, t_minX, t_maxY);
		t.addVertexWithUV(minX, minY, +r, t_minX, t_minY);
		
		// West.
		t.addVertexWithUV(-r, minY, maxZ, t_minY, t_maxZ);
		t.addVertexWithUV(-r, maxY, maxZ, t_maxY, t_maxZ);
		t.addVertexWithUV(-r, maxY, minZ, t_maxY, t_minZ);
		t.addVertexWithUV(-r, minY, minZ, t_minY, t_minZ);
		
		// East.
		t.addVertexWithUV(+r, minY, minZ, t_minY, t_minZ);
		t.addVertexWithUV(+r, maxY, minZ, t_maxY, t_minZ);
		t.addVertexWithUV(+r, maxY, maxZ, t_maxY, t_maxZ);
		t.addVertexWithUV(+r, minY, maxZ, t_minY, t_maxZ);
		
		t.draw();
		GL11.glPopMatrix();
	}

}
