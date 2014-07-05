package nedocomputers;

import java.util.ArrayList;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCPU extends BlockContainer {
	@SideOnly(Side.CLIENT)
    private IIcon blockIcon_side;
    private IIcon blockIcon_back;
    private IIcon blockIcon_front;
    private IIcon blockIcon_top;
    private IIcon blockIcon_bottom;
    
    private ArrayList<ItemStack> ist = null;
	
	BlockCPU() {
        super(Material.iron);
        setBlockName(Settings.MODID + "." + "CPU");
        setHardness(3.0F);
        setCreativeTab(CreativeTabs.tabRedstone);
	}
	
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconReg) {
    	blockIcon_side = iconReg.registerIcon("nedocomputers:computer_side");
    	blockIcon_back = iconReg.registerIcon("nedocomputers:computer_back");
    	blockIcon_front = iconReg.registerIcon("nedocomputers:computer_front");
    	blockIcon_top = iconReg.registerIcon("nedocomputers:computer_top");
    	blockIcon_bottom = iconReg.registerIcon("nedocomputers:computer_bottom");
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
    	TileEntity te = world.getTileEntity(x, y, z);
    	if (te instanceof TileEntityCPU) {
    		TileEntityCPU tile = (TileEntityCPU)te;
    		if (itemStack.hasTagCompound()) {
    			NBTTagCompound nbtTag = itemStack.getTagCompound();
    			tile.readFromNBT(nbtTag);
    		}
    	}
        int dir = (MathHelper.floor_double((double)(entity.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3);
        int[] r = {2, 5, 3, 4};
        world.setBlockMetadataWithNotify(x, y, z, r[dir], 3);
        world.getTileEntity(x, y, z).markDirty();
    }
    
    @Override
    public ArrayList<ItemStack> getDrops(World world, int x, int y, int z, int metadata, int fortune) {
    	if (ist == null) ist = new ArrayList<ItemStack>();
    	return ist;
    }
    
	@Override
    public void breakBlock(World world, int x, int y, int z, Block block, int p_149749_6_) {
    	ist = new ArrayList<ItemStack>();
    	TileEntity te = world.getTileEntity(x, y, z);
		ItemStack istack = new ItemStack(NedoComputers.CPU_Block);
    	if (te instanceof TileEntityCPU) {
    		TileEntityCPU tile = (TileEntityCPU)te;
    		if (tile.inventory[0] != null) {
	    		NBTTagCompound nbtTag = new NBTTagCompound();
	    		tile.writeToNBT(nbtTag);
	    		istack.setTagCompound(nbtTag);
    		}
    	}
    	ist.add(istack);
		super.breakBlock(world, x, y, z, block, p_149749_6_);
	}
    
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
			int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_) {
		if (!world.isRemote)
			player.openGui(NedoComputers.instance, Settings.GUI_COMPUTER, world, x, y, z);
		return true;
	}
    
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCPU();
	}
	
	@Override
    public void onNeighborChange(IBlockAccess b, int x, int y, int z, int tileX, int tileY, int tileZ) {
		((TileEntityCPU)b.getTileEntity(x, y, z)).updateConMask();
    }
	
	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int p_149714_5_) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && !world.isRemote) {
			((TileEntityCPU)tile).updateConMask();
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && !world.isRemote) {
			((TileEntityCPU)tile).updateConMask();
		}
	}
}
