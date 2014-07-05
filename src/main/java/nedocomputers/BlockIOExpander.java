package nedocomputers;

import cpw.mods.fml.common.Loader;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockIOExpander extends BlockContainer {

	protected BlockIOExpander() {
        super(Material.iron);
        setBlockName(Settings.MODID + "." + "IOExpander");
        setHardness(3.0F);
        setCreativeTab(CreativeTabs.tabRedstone);
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconReg) {
    	blockIcon = iconReg.registerIcon("nedocomputers:expander");
    }
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		if (NedoComputers.isRedLogicLoaded) {
			try {
				return (TileEntity)TileEntityIOExpanderRL.class.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}	
		} else if (NedoComputers.isProjectRedLoaded) {
			try {
				return (TileEntity)TileEntityIOExpanderPR.class.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}	
		}
		return new TileEntityIOExpander();
		
		/*if (NedoComputers.isProjectRedLoaded) {
			return new TileEntityIOExpanderPR();
			//return new TileEntityIOExpander();
		} else if (NedoComputers.isRedLogicLoaded) {
			//return new TileEntityIOExpanderRL();
			return new TileEntityIOExpander();
		} else {
			return new TileEntityIOExpander();
		}*/
	}
	
	@Override
	public int getRenderType() {
		return -1;
	}
	
	@Override
	public boolean isOpaqueCube() {
		return false;
	}
	
	@Override
	public boolean renderAsNormalBlock() {
		return false;
	}
	
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
    		EntityLivingBase entity, ItemStack itemStack) {
    	super.onBlockPlacedBy(world, x, y, z, entity, itemStack);
        int dir = (MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        if (entity.rotationPitch > 65F) {
        	world.setBlockMetadataWithNotify(x, y, z, 1, 3);
        } else if (entity.rotationPitch < -65F) {
        	world.setBlockMetadataWithNotify(x, y, z, 0, 3);
        } else {
	        int[] r = {2, 5, 3, 4};
	        world.setBlockMetadataWithNotify(x, y, z, r[dir], 3);
        }
    }
    
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		if (!world.isRemote) {
			player.openGui(NedoComputers.instance, Settings.GUI_BUS_ID, world, x, y, z);
		}
		return true;
	}
}
