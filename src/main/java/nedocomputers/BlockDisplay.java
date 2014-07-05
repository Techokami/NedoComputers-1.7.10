package nedocomputers;

import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockDisplay extends Block {
	@SideOnly(Side.CLIENT)
    private IIcon blockIcon_side;
    private IIcon blockIcon_back;
    private IIcon blockIcon_front;
    private IIcon blockIcon_top;
    private IIcon blockIcon_bottom;
	
	BlockDisplay() {
		super(Material.iron);
		setBlockName(Settings.MODID + "." + "display");
		setHardness(3.0F);
		setCreativeTab(CreativeTabs.tabRedstone);
	}

    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconReg) {
    	blockIcon_side = iconReg.registerIcon("nedocomputers:display_side");
    	blockIcon_back = iconReg.registerIcon("nedocomputers:display_back");
    	blockIcon_front = iconReg.registerIcon("nedocomputers:display_front");
    	blockIcon_top = iconReg.registerIcon("nedocomputers:display_top");
    	blockIcon_bottom = iconReg.registerIcon("nedocomputers:display_bottom");
    }
	
    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIcon(int side, int meta) {
    	ForgeDirection dir = ForgeDirection.getOrientation(side);
    	ForgeDirection block_dir;
    	if (meta != 0)
    		block_dir = ForgeDirection.getOrientation(meta);
    	else
    		block_dir = ForgeDirection.WEST;
    		//block_dir = ForgeDirection.SOUTH;
    	if (block_dir == dir) return blockIcon_front;
    	if (block_dir.getOpposite() == dir) return blockIcon_back;
    	if (dir == ForgeDirection.UP) return blockIcon_top;
    	if (dir == ForgeDirection.DOWN) return blockIcon_bottom;
        return blockIcon_side;
    }
    
    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z,
    		EntityLivingBase entity, ItemStack itemStack) {
    	super.onBlockPlacedBy(world, x, y, z, entity, itemStack);
        int dir = (MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        int[] r = {2, 5, 3, 4};
        world.setBlockMetadataWithNotify(x, y, z, r[dir], 3);
    }
    
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		//if (FMLCommonHandler.instance().getEffectiveSide().isServer()) {
		if (!world.isRemote) {
			int cnt = 0;
			int comp_x = 0, comp_y = 0, comp_z = 0;
			for (int i = 0; i < ForgeDirection.VALID_DIRECTIONS.length; i++) {
				if (world.getBlock(x + ForgeDirection.VALID_DIRECTIONS[i].offsetX,
						y + ForgeDirection.VALID_DIRECTIONS[i].offsetY,
						z + ForgeDirection.VALID_DIRECTIONS[i].offsetZ) instanceof BlockCPU) {
					cnt++;
					comp_x = x + ForgeDirection.VALID_DIRECTIONS[i].offsetX;
					comp_y = y + ForgeDirection.VALID_DIRECTIONS[i].offsetY;
					comp_z = z + ForgeDirection.VALID_DIRECTIONS[i].offsetZ;
				}
			}
			if (cnt == 1) {
				player.openGui(NedoComputers.instance, Settings.GUI_DISPLAY, world, comp_x, comp_y, comp_z);
			}
		}
		return true;
	}
}
