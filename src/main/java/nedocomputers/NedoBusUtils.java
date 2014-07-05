package nedocomputers;

import java.util.HashSet;
import java.util.LinkedList;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChunkCoordinates;
import net.minecraft.world.World;

public class NedoBusUtils {
	
	public static INedoPeripheral searchPeripheralBlock(World world, ChunkCoordinates sourceBlock, int sourceConMask, int addr) {
		LinkedList<ChunkCoordinates> queue = new LinkedList<ChunkCoordinates>();
		HashSet<ChunkCoordinates> visited = new HashSet<ChunkCoordinates>();
		ChunkCoordinates neighbor = new ChunkCoordinates();
		
		if ((sourceConMask & 1) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY - 1, sourceBlock.posZ));
		if ((sourceConMask & 2) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY + 1, sourceBlock.posZ));
		if ((sourceConMask & 4) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY, sourceBlock.posZ - 1));
		if ((sourceConMask & 8) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY, sourceBlock.posZ + 1));
		if ((sourceConMask & 16) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX - 1, sourceBlock.posY, sourceBlock.posZ));
		if ((sourceConMask & 32) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX + 1, sourceBlock.posY, sourceBlock.posZ));
		
		while (!queue.isEmpty()) {
			ChunkCoordinates block = queue.removeFirst();
			visited.add(block);
			TileEntity blockTile = world.getTileEntity(block.posX, block.posY, block.posZ);
			if (blockTile instanceof TileEntityCable) {
				int con = ((TileEntityCable)blockTile).getConMask();
				if ((con & 1) != 0) {
					neighbor.set(block.posX, block.posY - 1, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 2) != 0) {
					neighbor.set(block.posX, block.posY + 1, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 4) != 0) {
					neighbor.set(block.posX, block.posY, block.posZ - 1);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 8) != 0) {
					neighbor.set(block.posX, block.posY, block.posZ + 1);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 16) != 0) {
					neighbor.set(block.posX - 1, block.posY, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 32) != 0) {
					neighbor.set(block.posX + 1, block.posY, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
			} else {
				if (!block.equals(sourceBlock) && blockTile instanceof INedoPeripheral) {
					INedoPeripheral p = (INedoPeripheral)blockTile;
					if (p.getBusId() == addr) return p;
				}
			}
		}
		return null;
	}
	
	/*public static List<ChunkCoordinates> searchPeripheralBlocks(World world, ChunkCoordinates sourceBlock, int sourceConMask) {
		LinkedList<ChunkCoordinates> queue = new LinkedList<ChunkCoordinates>();
		//queue.add(sourceBlock);
		HashSet<ChunkCoordinates> visited = new HashSet<ChunkCoordinates>();
		List<ChunkCoordinates> foundBlocks = new ArrayList<ChunkCoordinates>();
		ChunkCoordinates neighbor = new ChunkCoordinates();
		
		//int sourceConMask = ((TileEntityCPU)world.getTileEntity(sourceBlock.posX, sourceBlock.posY, sourceBlock.posZ)).getConMask();
		if ((sourceConMask & 1) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY - 1, sourceBlock.posZ));
		if ((sourceConMask & 2) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY + 1, sourceBlock.posZ));
		if ((sourceConMask & 4) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY, sourceBlock.posZ - 1));
		if ((sourceConMask & 8) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX, sourceBlock.posY, sourceBlock.posZ + 1));
		if ((sourceConMask & 16) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX - 1, sourceBlock.posY, sourceBlock.posZ));
		if ((sourceConMask & 32) != 0)
			queue.addLast(new ChunkCoordinates(sourceBlock.posX + 1, sourceBlock.posY, sourceBlock.posZ));
		
		while (!queue.isEmpty()) {
			ChunkCoordinates block = queue.removeFirst();
			visited.add(block);
			TileEntity blockTile = world.getTileEntity(block.posX, block.posY, block.posZ);
			if (blockTile instanceof TileEntityCable) {
				int con = ((TileEntityCable)blockTile).getConMask();
				if ((con & 1) != 0) {
					neighbor.set(block.posX, block.posY - 1, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 2) != 0) {
					neighbor.set(block.posX, block.posY + 1, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 4) != 0) {
					neighbor.set(block.posX, block.posY, block.posZ - 1);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 8) != 0) {
					neighbor.set(block.posX, block.posY, block.posZ + 1);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 16) != 0) {
					neighbor.set(block.posX - 1, block.posY, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
				if ((con & 32) != 0) {
					neighbor.set(block.posX + 1, block.posY, block.posZ);
					if (!visited.contains(neighbor) && !queue.contains(neighbor))
						queue.addLast(new ChunkCoordinates(neighbor));
				}
			} else {
				// Периферия.
				// Добавляем блок в список найденных.
				if (!block.equals(sourceBlock)) {
					foundBlocks.add(block);
				}
			}
		}
		return foundBlocks;
	}*/
}
