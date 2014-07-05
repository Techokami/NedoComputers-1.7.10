package nedocomputers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCable extends TileEntity {
	public int conMask = 0;
	
	public TileEntityCable() {
		
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		super.readFromNBT(nbtTag);
		conMask = nbtTag.getInteger("cm");
	}
	
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
    	super.writeToNBT(nbtTag);
    	nbtTag.setInteger("cm", conMask);
    }
	
	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setInteger("cm", conMask);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbtTag = packet.func_148857_g();
		conMask = nbtTag.getInteger("cm");
	}
    
	public void onNeighbourBlockChange() {
		if (!worldObj.isRemote) {
			((TileEntityCable)worldObj.getTileEntity(xCoord, yCoord, zCoord)).updateConMask();
			worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
		}
	}
	
	@Override
	public void updateEntity() {
		//updateConMask();
	}
	
	public void updateConMask() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			int opposite = dir.getOpposite().ordinal();
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if (tile instanceof TileEntityCable)
				conMask |= (1 << i); else conMask &= ~(1 << i);
			if (tile instanceof INedoPeripheral)
				if (((INedoPeripheral)tile).Connectable(opposite))
					conMask |= (1 << i); else conMask &= ~(1 << i);
		}
	}
	
	public int getConMask() {
		return conMask;
	}
	
	public boolean Connectable(int side) {
		return true;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public double getMaxRenderDistanceSquared() {
		return 16384;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public AxisAlignedBB getRenderBoundingBox() {
		return AxisAlignedBB.getBoundingBox(xCoord, yCoord, zCoord, xCoord + 1, yCoord + 1, zCoord + 1);
	}
}
