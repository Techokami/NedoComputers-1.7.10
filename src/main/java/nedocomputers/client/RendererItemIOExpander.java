package nedocomputers.client;

import org.lwjgl.opengl.GL11;

import nedocomputers.Settings;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;

public class RendererItemIOExpander implements IItemRenderer {
	ModelIOExpander model;
	
	public RendererItemIOExpander() {
		model = new ModelIOExpander();
	}
	
	@Override
	public boolean handleRenderType(ItemStack item, ItemRenderType type) {
		return true;
	}

	@Override
	public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item,
			ItemRendererHelper helper) {
		return true;
	}

	@Override
	public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
		ResourceLocation textures = new ResourceLocation("nedocomputers", "textures/blocks/expander.png");
		GL11.glPushMatrix();
		GL11.glTranslatef(0.5F, 0.4F, 0.5F);
		GL11.glScalef(0.0625F, 0.0625F, 0.0625F);
		Minecraft.getMinecraft().renderEngine.bindTexture(textures);
		model.renderAll();
		GL11.glPopMatrix();
	}

}
