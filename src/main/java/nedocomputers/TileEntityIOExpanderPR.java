package nedocomputers;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import mrtjp.projectred.api.IBundledEmitter;
import mrtjp.projectred.api.IBundledTile;
import mrtjp.projectred.api.ProjectRedAPI;

public class TileEntityIOExpanderPR extends TileEntityIOExpander implements IBundledTile {

	@Override
	public byte[] getBundledSignal(int side) {
    	byte[] b = new byte[16];
        for (int i = 0; i < 16; i++)
        	b[i] = ((output & (1 << i)) != 0) ? (byte)255 : 0;
		return b;
	}
	
	@Override
	public short busRead(int addr) {
		if ((addr & 0xFFFE) == 2) {
			return output_new;
		}
		ForgeDirection dir = ForgeDirection.getOrientation(getBlockMetadata());
		if ((addr & 0xFFFE) == 0) {
			short result = 0;
			try {
				byte[] input = ProjectRedAPI.transmissionAPI.getBundledInput(getWorldObj(), xCoord, yCoord, zCoord, getBlockMetadata());
				for (int i = 0; i < 16; i++)
					result |= (input[i] != 0) ? (1 << i) : 0;
			} catch (Exception ex) {}
			return result;
		}
		return 0;
	}
	
	@Override
	public boolean canConnectBundled(int side) {
		ForgeDirection dir = ForgeDirection.getOrientation(side);
		if (side == getBlockMetadata())
			return true;
		else
			return false;
	}
}
