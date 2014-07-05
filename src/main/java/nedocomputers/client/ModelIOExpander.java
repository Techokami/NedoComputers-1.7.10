package nedocomputers.client;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;

public class ModelIOExpander extends ModelBase
{
  //fields
    ModelRenderer Shape2;
    ModelRenderer Shape3;
  
  public ModelIOExpander()
  {
	    textureWidth = 64;
	    textureHeight = 64;
	    
	      Shape2 = new ModelRenderer(this, 4, 2);
	      Shape2.addBox(-8F, -8F, -5F, 16, 16, 13);
	      Shape2.setRotationPoint(0F, 0F, 0F);
	      Shape2.setTextureSize(64, 64);
	      Shape2.mirror = true;
	      setRotation(Shape2, 0F, 0F, 0F);
	      Shape3 = new ModelRenderer(this, 16, 32);
	      Shape3.addBox(-4F, -4F, -8F, 8, 8, 3);
	      Shape3.setRotationPoint(0F, 0F, 0F);
	      Shape3.setTextureSize(64, 64);
	      Shape3.mirror = true;
	      setRotation(Shape3, 0F, 0F, 0F);
  }
  
  public void renderAll() {
		Shape2.render(1F);
		Shape3.render(1F);
  }
  
  private void setRotation(ModelRenderer model, float x, float y, float z)
  {
    model.rotateAngleX = x;
    model.rotateAngleY = y;
    model.rotateAngleZ = z;
  }
}

