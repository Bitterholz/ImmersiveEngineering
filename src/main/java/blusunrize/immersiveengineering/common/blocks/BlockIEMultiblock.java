package blusunrize.immersiveengineering.common.blocks;

import java.util.ArrayList;
import java.util.List;

import blusunrize.immersiveengineering.api.IEProperties;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public abstract class BlockIEMultiblock<E extends Enum<E> & BlockIEBase.IBlockEnum> extends BlockIETileProvider<E>
{
	public BlockIEMultiblock(String name, Material material, PropertyEnum<E> mainProperty, Class<? extends ItemBlockIEBase> itemBlock, Object... additionalProperties)
	{
		super(name, material, mainProperty, itemBlock, combineProperties(additionalProperties, IEProperties.FACING_HORIZONTAL,IEProperties.MULTIBLOCKSLAVE));
	}
	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		state = super.getActualState(state, world, pos);
		return state;
	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		TileEntity tileEntity = world.getTileEntity(pos);
		if(tileEntity instanceof TileEntityMultiblockPart && world.getGameRules().getBoolean("doTileDrops"))
		{
			TileEntityMultiblockPart tile = (TileEntityMultiblockPart)tileEntity;
			if(!tile.formed && tile.pos==-1 && tile.getOriginalBlock()!=null)
				world.spawnEntityInWorld(new EntityItem(world, pos.getX()+.5,pos.getY()+.5,pos.getZ()+.5, tile.getOriginalBlock().copy()));

			if(tileEntity instanceof IInventory)
				InventoryHelper.dropInventoryItems(world, pos, (IInventory)tile);
		}
		if(tileEntity instanceof TileEntityMultiblockPart)
			((TileEntityMultiblockPart)tileEntity).disassemble();
		super.breakBlock(world, pos, state);
	}
	@Override
	public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
	{
		return new ArrayList<ItemStack>();
	}
	@Override
	public ItemStack getPickBlock(MovingObjectPosition target, World world, BlockPos pos, EntityPlayer player)
	{
		return getOriginalBlock(world, pos);
	}
	public ItemStack getOriginalBlock(World world, BlockPos pos)
	{
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntityMultiblockPart)
			return ((TileEntityMultiblockPart)te).getOriginalBlock();
		return new ItemStack(this, 1, this.getMetaFromState(world.getBlockState(pos)));
	}
}