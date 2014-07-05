package nedocomputers;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.ForgeDirection;

public class TileEntityCPU extends TileEntity implements IInventory, INedoPeripheral {
	private final int ram_size_in_bytes = 0x4000;
	private final int kbd_buf_head_addr = 0x7000;
	private final int kbd_buf_tail_addr = 0x7002;
	private final int kbd_buf_out_addr = 0x7004;
	private final int wai_addr = 0x7008;
	private final int utime_addr = 0x700A;
	private final int bus_output_addr = 0x7500;
	private final int bus_input_addr = 0x7600;
	
	// Video RAM.
	public byte[] video_ram = new byte[10240];
	// Main RAM.
	public short[] ram = new short[ram_size_in_bytes / 2];
	// Bus input window.
	public short[] cpu_bus_window = new short[128];
	
	// Ring buffer keyboard.
	public byte[] keyboard_buf = new byte[16];
	public byte keyboard_buf_head = 0;
	public byte keyboard_buf_tail = 0;
	
	// Address input and output windows on bus.
	public int peripheral_id = 1;
	public int cpu_id = 0;
	
	// CPU.
	// Program counter.
	public short pc = 0;
	// Return stack.
	public short[] rstack = new short[32];
	public byte rstack_pointer = 0;
	// Data stack.
	public short[] dstack = new short[32];
	public short dstack_T = 0;
	public short dstack_N = 0;
	public byte dstack_depth = 0;
	
	private final short forth_true = (short)0xFFFF;
	private final short forth_false = 0;
	
	private boolean wai_flag = false;
	
	public boolean isTurnedOn = false;
	
	private String InventoryName = "CPU";
	public ItemStack[] inventory;
	
	private boolean busTimeout = false;
	private INedoPeripheral PeripheralCache = null;
	
	private int conMask = 0;
	
	public TileEntityCPU() {
		for (int i = 0; i < video_ram.length; i++) video_ram[i] = ' ';
		video_ram[0x2600] = 0; video_ram[0x2602] = 0; video_ram[0x2604] = 0;
		inventory = new ItemStack[getSizeInventory()];
	}
	
	private void write_mem(int addr, short data) {
		if (addr < ram_size_in_bytes) {
			ram[addr >>> 1] = data;
		} else if ((addr >= ram_size_in_bytes) && (addr < ram_size_in_bytes + video_ram.length)) {
			video_ram[addr - ram_size_in_bytes] = (byte)data;
		} else if ((addr & 0xFFFE) == kbd_buf_head_addr) {
			keyboard_buf_head = (byte)(data & 0x0F);
		} else if ((addr & 0xFFFE) == wai_addr) {
			wai_flag = true;
		} else if ((addr & 0xFFFE) == 0x7012) {
			// Map device to bus window.
			if (peripheral_id != data) {
				if (PeripheralCache != null) busTimeout = true;
				peripheral_id = data & 0xFF;
			}
		} else if ((addr >= bus_output_addr) && (addr <= bus_output_addr + 0xFF) && (cpu_id != peripheral_id)) {
			if (PeripheralCache == null) {
				PeripheralCache = NedoBusUtils.searchPeripheralBlock(worldObj,
						new ChunkCoordinates(xCoord, yCoord, zCoord), conMask, peripheral_id);
			}
			if (PeripheralCache == null) {
				busTimeout = true;
			} else {
				PeripheralCache.busWrite(addr - bus_output_addr, data);
			}
		} else if ((addr >= bus_input_addr) && (addr <= bus_input_addr + 0xFF)) {
			cpu_bus_window[(addr - bus_input_addr) >>> 1] = data;
		}
	}
	
	private short read_mem(int addr) {
		if (addr < ram_size_in_bytes) return ram[addr >>> 1];
		if ((addr & 0xFFFE) == kbd_buf_head_addr) return keyboard_buf_head;
		if ((addr & 0xFFFE) == kbd_buf_tail_addr) return keyboard_buf_tail;
		if ((addr & 0xFFFE) == kbd_buf_out_addr) return keyboard_buf[keyboard_buf_head];
		if ((addr >= bus_output_addr) && (addr <= bus_output_addr + 0xFF) && (cpu_id != peripheral_id)) {
			if (PeripheralCache == null) {
				PeripheralCache = NedoBusUtils.searchPeripheralBlock(worldObj,
						new ChunkCoordinates(xCoord, yCoord, zCoord), conMask, peripheral_id);
			}
			if (PeripheralCache == null) {
				busTimeout = true;
				return 0;
			} else {
				return PeripheralCache.busRead(addr - bus_output_addr);
			}
		} else if ((addr >= bus_input_addr) && (addr <= bus_input_addr + 0xFF)) {
			return cpu_bus_window[(addr - bus_input_addr) >>> 1];
		}
		if ((addr & 0xFFFE) == utime_addr) return (short)((System.currentTimeMillis() / 1000L) & 0xFFFF);
		if ((addr & 0xFFFE) == utime_addr + 2) return (short)((System.currentTimeMillis() / 1000L) >>> 16 );
		return 0;
	}
	
	private short ALU(int op) {
		switch (op) {
			case 0: return dstack_T;
			case 1: return dstack_N;
			case 2: return (short)(dstack_T + dstack_N);
			case 3: return (short)(dstack_T & dstack_N);
			case 4: return (short)(dstack_T | dstack_N);
			case 5: return (short)((dstack_T << 8) | ((dstack_T >> 8) & 0xFF));
			case 6: return (short)~dstack_T;
			case 7: return (dstack_N == dstack_T) ? forth_true : forth_false;
			case 8: return (dstack_N < dstack_T) ? forth_true : forth_false;
			case 9: return (short)((dstack_T >> 1) & 0x7FFF);
			case 10: return (short)(dstack_T - 1);
			case 11: return rstack[rstack_pointer];
			case 12: return read_mem(((int)dstack_T) & 0xFFFF);
			case 13: return (short)(dstack_T << 1);
			case 14: return dstack_depth;
			case 15: return (((int)dstack_N & 0xFFFF) < ((int)dstack_T & 0xFFFF)) ? forth_true : forth_false;
			default: return 0;
		}
	}
	
	private void execute_insn() {
		short dstack_T_buf = dstack_T;
		short istr = read_mem(pc << 1);
		if ((istr & 0x8000) != 0) {
			// literal.
			dstack_depth = (byte)((dstack_depth + 1) & 0x1F);
			dstack[dstack_depth] = dstack_N;
			dstack_N = dstack_T;
			dstack_T = (short)(istr & 0x7FFF);
			pc = (short)((pc + 1) & 0x1FFF);
		} else if ((istr & 0xE000) == 0) {
			// jump.
			pc = (short)(istr & 0x1FFF);
		} else if ((istr & 0xE000) == 0x2000) {
			// conditional jump.
			if (dstack_T != 0) {
				pc = (short)((pc + 1) & 0x1FFF);
			} else {
				pc = (short)(istr & 0x1FFF);
			}
			dstack_T = dstack_N;
			dstack_N = dstack[dstack_depth];
			dstack_depth = (byte)((dstack_depth - 1) & 0x1F);
		} else if ((istr & 0xE000) == 0x4000) {
			// call.
			rstack_pointer = (byte)((rstack_pointer + 1) & 0x1F);
			rstack[rstack_pointer] = (short)(((pc + 1) & 0x1FFF) << 1);
			pc = (short)(istr & 0x1FFF);
		} else if ((istr & 0xE000) == 0x6000) {
			// ALU.
			// N -> [T]
			if ((istr & (1 << 5)) != 0 ) {
				write_mem(((int)dstack_T) & 0xFFFF, dstack_N);
			}
			// dstack+-
			if ((istr & 3) == 0) {
				dstack_T = ALU((istr >> 8) & 0x0F);
				if ((istr & (1 << 7)) != 0 ) dstack_N = dstack_T_buf; // T -> N
			} else if ((istr & 3) == 1) {
				dstack_T = ALU((istr >> 8) & 0x0F);
				dstack_depth = (byte)((dstack_depth + 1) & 0x1F);
				dstack[dstack_depth] = dstack_N;
				if ((istr & (1 << 7)) != 0 ) dstack_N = dstack_T_buf; // T -> N
			} else if ((istr & 3) == 2) {
				// Error.
			} else if ((istr & 3) == 3) {
				dstack_T = ALU((istr >> 8) & 0x0F);
				dstack_N = dstack[dstack_depth];
				dstack_depth = (byte)((dstack_depth - 1) & 0x1F);
			}
			// R -> PC
			if ((istr & (1 << 12)) != 0 ) {
				pc = (short)(rstack[rstack_pointer] >> 1);
			} else {
				pc = (short)((pc + 1) & 0x1FFF);
			}
			// rstack+-
			if (((istr >> 2) & 3) == 1) {
				rstack_pointer = (byte)((rstack_pointer + 1) & 0x1F);
				rstack[rstack_pointer] = dstack_T_buf;
			} else if (((istr >> 2) & 3) == 3) {
				rstack_pointer = (byte)((rstack_pointer - 1) & 0x1F);
			}
		}
	}
	
	public void turnOn() {
		if (inventory[0] == null) return;
		if (!(inventory[0].getItem() instanceof ItemEEPROM ||
				inventory[0].getItem() instanceof ItemForthROM)) return;

		for (int i = 0; i < video_ram.length; i++) video_ram[i] = ' ';
		video_ram[0x2600] = 0; video_ram[0x2602] = 0; video_ram[0x2604] = 0;
		keyboard_buf_head = 0; keyboard_buf_tail = 0;
		for (int i = 0; i < keyboard_buf.length; i++) keyboard_buf[i] = 0;
		pc = 0;
		rstack_pointer = 0;
		for (int i = 0; i < rstack.length; i++) rstack[i] = 0;
		dstack_depth = 0; dstack_N = 0; dstack_N = 0;
		for (int i = 0; i < dstack.length; i++) dstack[i] = 0;
		
		if (inventory[0].getItem() instanceof ItemForthROM) {
			InputStream input =
					getClass().getResourceAsStream("/assets/nedocomputers/forth/forth.bin");
			DataInputStream d_stream = new DataInputStream(input);
			try {
				for (int i = 0; i < ram.length; i++) {
					byte b1 = d_stream.readByte();
					byte b2 = d_stream.readByte();
					ram[i] = (short)(((int)b1 & 0xFF) + ((int)b2 & 0xFF) * 256);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.isTurnedOn = true;
		} else if (inventory[0].getItem() instanceof ItemEEPROM) {
			if (inventory[0].hasTagCompound()) {
				NBTTagCompound nbtTag = inventory[0].getTagCompound();
				byte[] ram_buf = nbtTag.getByteArray("ram");
				ByteBuffer.wrap(ram_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(ram);
				this.isTurnedOn = true;
			}
		}
	}
	
	public void turnOff() {
		for (int i = 0; i < video_ram.length; i++) video_ram[i] = ' ';
		video_ram[0x2600] = 0; video_ram[0x2602] = 0; video_ram[0x2604] = 0;
		this.isTurnedOn = false;
	}
	
	public void reset() {
		for (int i = 0; i < video_ram.length; i++) video_ram[i] = ' ';
		video_ram[0x2600] = 0; video_ram[0x2602] = 0; video_ram[0x2604] = 0;
		pc = 0;
		rstack_pointer = 0;
		for (int i = 0; i < rstack.length; i++) rstack[i] = 0;
		dstack_depth = 0; dstack_N = 0; dstack_N = 0;
		for (int i = 0; i < dstack.length; i++) dstack[i] = 0;
	}
	
	public void SaveToEEPROM(String name) {
		if (inventory[0].getItem() instanceof ItemEEPROM) {
			NBTTagCompound nbtTag = new NBTTagCompound();
			byte[] ram_buf = new byte[ram.length * 2];
			ByteBuffer.wrap(ram_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(ram);
			nbtTag.setByteArray("ram", ram_buf);
			inventory[0].setTagCompound(nbtTag);
			if (name.isEmpty())
				name = new SimpleDateFormat("dd.MM.yy HH:mm:ss").format(new Date());
			inventory[0].setStackDisplayName(name);
		}
	}
	
	@Override
	public void readFromNBT(NBTTagCompound nbtTag) {
		super.readFromNBT(nbtTag);
		video_ram = nbtTag.getByteArray("video_ram");
		byte[] ram_buf = nbtTag.getByteArray("ram");
		ByteBuffer.wrap(ram_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(ram);
		keyboard_buf = nbtTag.getByteArray("kbd_buf");
		keyboard_buf_head = nbtTag.getByte("buf_head");
		keyboard_buf_tail = nbtTag.getByte("buf_tail");
		peripheral_id = nbtTag.getInteger("peripheral_addr");
		cpu_id = nbtTag.getInteger("cpu_addr");
		pc = nbtTag.getShort("pc");
		byte[] rstack_buf = nbtTag.getByteArray("rstack");
		ByteBuffer.wrap(rstack_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(rstack);
		rstack_pointer = nbtTag.getByte("r_sp");
		byte[] dstack_buf = nbtTag.getByteArray("dstack");
		ByteBuffer.wrap(dstack_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(dstack);
		dstack_T = nbtTag.getShort("T");
		dstack_N = nbtTag.getShort("N");
		dstack_depth = nbtTag.getByte("depth");
		isTurnedOn = nbtTag.getBoolean("isTurnOn");
		conMask = nbtTag.getInteger("cm");
		NBTTagList nbttaglist = nbtTag.getTagList("Items", Constants.NBT.TAG_COMPOUND);
		inventory = new ItemStack[getSizeInventory()];
		for (int i = 0; i < nbttaglist.tagCount(); i++) {
			NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound1.getByte("Slot") & 0xff;
			if (j >= 0 && j < inventory.length) {
				inventory[j] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
			}
		}
	}
	
	@Override
	public void writeToNBT(NBTTagCompound nbtTag) {
		super.writeToNBT(nbtTag);
		nbtTag.setByteArray("video_ram", video_ram);
		byte[] ram_buf = new byte[ram.length * 2];
		ByteBuffer.wrap(ram_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(ram);
		nbtTag.setByteArray("ram", ram_buf);
		nbtTag.setByteArray("kbd_buf", keyboard_buf);
		nbtTag.setByte("buf_head", keyboard_buf_head);
		nbtTag.setByte("buf_tail", keyboard_buf_tail);
		nbtTag.setInteger("peripheral_addr", peripheral_id);
		nbtTag.setInteger("cpu_addr", cpu_id);
		nbtTag.setShort("pc", pc);
		byte[] rstack_buf = new byte[ram.length * 2];
		ByteBuffer.wrap(rstack_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(rstack);
		nbtTag.setByteArray("rstack", rstack_buf);
		nbtTag.setByte("r_sp", rstack_pointer);
		byte[] dstack_buf = new byte[ram.length * 2];
		ByteBuffer.wrap(dstack_buf).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(dstack);
		nbtTag.setByteArray("dstack", dstack_buf);
		nbtTag.setShort("T", dstack_T);
		nbtTag.setShort("N", dstack_N);
		nbtTag.setByte("depth", dstack_depth);
		nbtTag.setBoolean("isTurnOn", isTurnedOn);
		nbtTag.setInteger("cm", conMask);
		NBTTagList nbttaglist = new NBTTagList();
		for (int i = 0; i < inventory.length; i++) {
			if (inventory[i] != null) {
				NBTTagCompound nbttagcompound1 = new NBTTagCompound();
				nbttagcompound1.setByte("Slot", (byte)i);
				inventory[i].writeToNBT(nbttagcompound1);
				nbttaglist.appendTag(nbttagcompound1);
			}
		}
		nbtTag.setTag("Items", nbttaglist);
	}
	
	@Override
	public void updateEntity() {
		int cnt = Settings.INSTRUCTIONS_PER_TICK;
		wai_flag = false;
		busTimeout = false;
		PeripheralCache = null;
		while ((cnt > 0) && !wai_flag && !busTimeout && isTurnedOn) {
			execute_insn();
			cnt--;
		}
	}
	
	public void KeyTyped(byte chr) {
		if (isTurnedOn) {
			if ((chr != 0xFF) && (((keyboard_buf_tail + 1) % keyboard_buf.length) != keyboard_buf_head)) {
				keyboard_buf_tail = (byte)((keyboard_buf_tail + 1) % keyboard_buf.length);
				keyboard_buf[keyboard_buf_tail] = chr;
			}
		}
	}
	
	// ========================================================================================
	// Inventory.
	
	@Override
	public int getSizeInventory() {
		return 1;
	}
	
	@Override
	public ItemStack getStackInSlot(int slot) {
		return inventory[slot];
	}
	
	@Override
	public ItemStack decrStackSize(int slot, int amount) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if(stack.stackSize > amount){
				stack = stack.splitStack(amount);
				this.markDirty();
			}
			else {
				setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}
	
	@Override
	public ItemStack getStackInSlotOnClosing(int slot) {
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}
	
	@Override
	public void setInventorySlotContents(int slot, ItemStack ist) {
		inventory[slot] = ist;
		if (ist != null && ist.stackSize > getInventoryStackLimit()) {
			ist.stackSize = getInventoryStackLimit();
		}
		this.markDirty();
	}
	
	@Override
	public String getInventoryName() {
		return InventoryName;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}
	
	@Override
	public int getInventoryStackLimit() {
		return 1;
	}
	
	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return true;
	}
	
	@Override
	public void openInventory() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void closeInventory() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack) {
		return false;
	}
	
	// ========================================================================================
	// Bus.
	
	@Override
	public short busRead(int addr) {
		if ((addr >= bus_input_addr) && (addr < (bus_input_addr + 2 * cpu_bus_window.length)))
			return cpu_bus_window[(addr - bus_input_addr) >>> 1];
		else return 0;
	}
	
	@Override
	public void busWrite(int addr, short data) {
		if ((addr >= bus_input_addr) && (addr < (bus_input_addr + 2 * cpu_bus_window.length)))
			cpu_bus_window[(addr - bus_input_addr) >>> 1] = data;
	}

	@Override
	public int getBusId() {
		return cpu_id;
	}
	
	@Override
	public void setBusId(int id) {
		cpu_id = id;
	}
	
	@Override
	public boolean Connectable(int side) {
		return (getBlockMetadata() != side);
	}
	
	public int getConMask() {
		return conMask;
	}
	
	public void updateConMask() {
		for (int i = 0; i < 6; i++) {
			ForgeDirection dir = ForgeDirection.VALID_DIRECTIONS[i];
			TileEntity tile = worldObj.getTileEntity(xCoord + dir.offsetX, yCoord + dir.offsetY, zCoord + dir.offsetZ);
			if ((tile instanceof TileEntityCable) && (i != getBlockMetadata()))
				conMask |= (1 << i);
			else
				conMask &= ~(1 << i);
			if ((tile instanceof INedoPeripheral) && (i != getBlockMetadata()))
				if (((INedoPeripheral)tile).Connectable(ForgeDirection.OPPOSITES[i]))
					conMask |= (1 << i);
				else
					conMask &= ~(1 << i);
		}
	}
}
