package nedocomputers;

import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import mods.immibis.redlogic.api.wiring.IBundledEmitter;
import mods.immibis.redlogic.api.wiring.IBundledWire;
import mods.immibis.redlogic.api.wiring.IConnectable;
import mods.immibis.redlogic.api.wiring.IWire;

public class TileEntityIOExpanderRL extends TileEntityIOExpander implements IConnectable, IBundledEmitter {
	
	@Override
	public byte[] getBundledCableStrength(int blockFace, int toDirection) {
		if (blockFace != -1) return null;
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
		TileEntity te = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
		if ((addr & 0xFFFE) == 0 && te instanceof IBundledEmitter) {
			byte[] input = ((IBundledEmitter)te).getBundledCableStrength(-1, dir.getOpposite().ordinal());
			short result = 0;
			for (int i = 0; i < 16; i++)
				result |= (input[i] != 0) ? (1 << i) : 0;
			return result;
		}
		return 0;
	}
	
	@Override
	public boolean connects(IWire wire, int blockFace, int fromDirection) {
		if (fromDirection == getBlockMetadata())
			return (/*blockFace == -1 &&*/ wire instanceof IBundledWire);
		else
			return false;
	}
	
	@Override
	public boolean connectsAroundCorner(IWire arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		return false;
	}
}
