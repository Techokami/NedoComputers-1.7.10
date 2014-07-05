package nedocomputers.client;

import org.lwjgl.opengl.GL11;

import nedocomputers.Settings;
import nedocomputers.TileEntityCable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class RendererCable extends TileEntitySpecialRenderer {
	
	@Override
	public void renderTileEntityAt(TileEntity te, double x, double y,
			double z, float var8) {
		TileEntityCable tile = (TileEntityCable)te;
		double r = 0.125;
		
		double minY = ((tile.conMask & 1) != 0) ? -0.5 : -r;  // DOWN
		double maxY = ((tile.conMask & 2) != 0) ? 0.5 : r;    // UP
		double minZ = ((tile.conMask & 4) != 0) ? -0.5 : -r;  // NORTH
		double maxZ = ((tile.conMask & 8) != 0) ? 0.5 : r;    // SOUTH
		double minX = ((tile.conMask & 16) != 0) ? -0.5 : -r; // WEST
		double maxX = ((tile.conMask & 32) != 0) ? 0.5 : r;   // EAST
		
		double t_minY = ((tile.conMask & 1) != 0) ? 0 : 0.3750;  // DOWN
		double t_maxY = ((tile.conMask & 2) != 0) ? 1 : 0.6250;  // UP
		double t_minZ = ((tile.conMask & 4) != 0) ? 0 : 0.3750;  // NORTH
		double t_maxZ = ((tile.conMask & 8) != 0) ? 1 : 0.6250;  // SOUTH
		double t_minX = ((tile.conMask & 16) != 0) ? 0 : 0.3750; // WEST
		double t_maxX = ((tile.conMask & 32) != 0) ? 1 : 0.6250; // EAST
		
		//double d = 0.002;
		//t_minX +=d; t_minY +=d; t_minZ +=d;
		//t_maxX -=d; t_maxY -=d; t_maxZ -=d;
		
		ResourceLocation textures = new ResourceLocation("nedocomputers", "textures/blocks/Cable.png");
		Minecraft.getMinecraft().renderEngine.bindTexture(textures);
		Tessellator t = Tessellator.instance;
		GL11.glPushMatrix();
		GL11.glTranslated(x + 0.5, y + 0.5, z + 0.5);
		GL11.glDisable(GL11.GL_LIGHTING);
		t.startDrawingQuads();
		
		// Down.
		t.addVertexWithUV(maxX, -r, minZ, t_maxX, t_minZ);
		t.addVertexWithUV(maxX, -r, maxZ, t_maxX, t_maxZ);
		t.addVertexWithUV(minX, -r, maxZ, t_minX, t_maxZ);
		t.addVertexWithUV(minX, -r, minZ, t_minX, t_minZ);
		
		// Up.
		t.addVertexWithUV(minX, +r, minZ, t_minX, t_minZ);
		t.addVertexWithUV(minX, +r, maxZ, t_minX, t_maxZ);
		t.addVertexWithUV(maxX, +r, maxZ, t_maxX, t_maxZ);
		t.addVertexWithUV(maxX, +r, minZ, t_maxX, t_minZ);
		
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
