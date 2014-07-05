package nedocomputers;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerCPU extends Container {
	private TileEntityCPU tile;
	private EntityPlayer player;
	
	public ContainerCPU(EntityPlayer p, TileEntityCPU te) {
		tile = te; player = p;
		this.addSlotToContainer(new SlotROM(tile, 0, 143, 75));
		for (int i = 0; i < 9; i++)
			addSlotToContainer(new Slot(player.inventory, i, 8 + i * 18, 182));
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
            	addSlotToContainer(new Slot(player.inventory, j + i * 9 + 9, 8 + j * 18, 124 + i * 18));
	}
	
	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slotID) {
		return null;
	}
	
	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}
	
	@Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int i, int j) {
    	switch(i) {
			case 0:
				tile.isTurnedOn = (j != 0);
				break;
			case 1:
				tile.cpu_id = j;
				break;
    	}
    }
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		for(int i = 0; i < super.crafters.size(); ++i) {
			ICrafting ic = (ICrafting)super.crafters.get(i);
			ic.sendProgressBarUpdate(this, 0, (tile.isTurnedOn) ? 1 : 0);
			ic.sendProgressBarUpdate(this, 1, tile.cpu_id);
		}
	}
	
	private class SlotROM extends Slot {
		public SlotROM(IInventory par1iInventory, int par2, int par3, int par4) {
			super(par1iInventory, par2, par3, par4);
		}
		
		@Override
		public boolean isItemValid(ItemStack itemStack) {
			if (itemStack.getItem() instanceof ItemEEPROM ||
					itemStack.getItem() instanceof ItemForthROM)
				return true;
			else
				return false;
		}
	}
}
