package nedocomputers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.tileentity.TileEntity;

public class ContainerBusId extends Container {
	private INedoPeripheral tile;
	
	public ContainerBusId(TileEntity te) {
		tile = (INedoPeripheral)te;
	}

	@Override
	public boolean canInteractWith(EntityPlayer var1) {
		return true;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int i, int j) {
    	switch(i) {
			case 0:
				tile.setBusId(j);
				break;
    	}
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting)super.crafters.get(i);
			ic.sendProgressBarUpdate(this, 0, tile.getBusId());
		}
	}
}
