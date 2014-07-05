package nedocomputers;

import java.util.List;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockCable extends Block implements ITileEntityProvider {
	
	protected BlockCable() {
		super(Material.circuits);
		setBlockName(Settings.MODID + "." + "cable");
		setHardness(0.25F);
        setCreativeTab(CreativeTabs.tabRedstone);
	}
	
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityCable();
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
	
    @SideOnly(Side.CLIENT)
    @Override
    public void registerBlockIcons(IIconRegister iconReg) {
    	blockIcon = iconReg.registerIcon("nedocomputers:Cable");
    }
    
	@Override
    public void setBlockBoundsBasedOnState(IBlockAccess p, int x, int y, int z) {
		setBlockBounds(0.2f, 0.2f, 0.2f, 0.8f, 0.8f, 0.8f);
		int mask = ((TileEntityCable)p.getTileEntity(x, y, z)).getConMask();
		if ((mask & 1) != 0) minY = 0; else minY = 0.375;
		if ((mask & 2) != 0) maxY = 1; else maxY = 0.625;
		if ((mask & 4) != 0) minZ = 0; else minZ = 0.375;
		if ((mask & 8) != 0) maxZ = 1; else maxZ = 0.625;
		if ((mask & 16) != 0) minX = 0; else minX = 0.375;
		if ((mask & 32) != 0) maxX = 1; else maxX = 0.625;
    }
	
	@SuppressWarnings("rawtypes")
	@Override
	public void addCollisionBoxesToList(World world, int x, int y, int z, AxisAlignedBB AABB, List list, Entity entity) {
		int mask = ((TileEntityCable)world.getTileEntity(x, y, z)).getConMask();
		float r = 0.12F;
		setBlockBounds(0.5F - r, 0.5F - r, 0.5F - r, 0.5F + r, 0.5F + r, 0.5F + r);
		super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		if ((mask & 1) != 0) {
			setBlockBounds(0.5F - r, 0.0F, 0.5F - r, 0.5F + r, 0.5F + r, 0.5F + r);
			super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		}
		if ((mask & 2) != 0) {
			setBlockBounds(0.5F - r, 0.5F - r, 0.5F - r, 0.5F + r, 1F, 0.5F + r);
			super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		}
		if ((mask & 4) != 0) {
			setBlockBounds(0.5F - r, 0.5F - r, 0F, 0.5F + r, 0.5F + r, 0.5F + r);
			super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		}
		if ((mask & 8) != 0) {
			setBlockBounds(0.5F - r, 0.5F - r, 0.5F - r, 0.5F + r, 0.5F + r, 1F);
			super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		}
		if ((mask & 16) != 0) {
			setBlockBounds(0F, 0.5F - r, 0.5F - r, 0.5F + r, 0.5F + r, 0.5F + r);
			super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		}
		if ((mask & 32) != 0) {
			setBlockBounds(0.5F - r, 0.5F - r, 0.5F - r, 1F, 0.5F + r, 0.5F + r);
			super.addCollisionBoxesToList(world, x, y, z, AABB, list, entity);
		}
		setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
	}
	
	@Override
	public void onPostBlockPlaced(World world, int x, int y, int z, int p_149714_5_) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null && !world.isRemote) {
			((TileEntityCable)tile).updateConMask();
			world.markBlockForUpdate(x, y, z);
		}
	}
	
	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		TileEntity tile = world.getTileEntity(x, y, z);
		if (tile != null)
			((TileEntityCable)tile).onNeighbourBlockChange();
	}
}
