package nedocomputers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityIOExpander extends TileEntity implements INedoPeripheral {
	private int busId = 1;
	public short output_new = 0;
	public short output = 0;
	
	@Override
	public void updateEntity() {
		if (output != output_new) {
			output = output_new;
			worldObj.notifyBlocksOfNeighborChange(xCoord, yCoord, zCoord, NedoComputers.IOExpanderBlock);
		}
	}
    
	@Override
	public boolean Connectable(int side) {
		if (ForgeDirection.getOrientation(side).getOpposite() == ForgeDirection.getOrientation(getBlockMetadata()))
			return true;
		else
			return false;
	}

	@Override
	public short busRead(int addr) {
		return 0;
	}

	@Override
	public void busWrite(int addr, short data) {
		if ((addr & 0xFFFE) == 2) {
			output_new = data;
		}
	}
	
	@Override
	public int getBusId() {
		return busId;
	}

	@Override
	public void setBusId(int i) {
		busId = i;
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
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		super.readFromNBT(nbtTag);
		busId = nbtTag.getInteger("i");
		output_new = nbtTag.getShort("n");
		output = nbtTag.getShort("o");
	}
	
    @Override
    public void writeToNBT(NBTTagCompound nbtTag) {
    	super.writeToNBT(nbtTag);
    	nbtTag.setInteger("i", busId);
    	nbtTag.setShort("n", output_new);
    	nbtTag.setShort("o", output);
    }
	
/*	@Override
	public Packet getDescriptionPacket() {
		NBTTagCompound nbtTag = new NBTTagCompound();
		nbtTag.setShort("o", output);
		return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbtTag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		NBTTagCompound nbtTag = packet.func_148857_g();
		output = nbtTag.getShort("o");
	}*/
}
