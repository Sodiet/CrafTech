/*
 * Copyright (C) 2014 Philip Hansen and CrafTech contributors.
 *
 * This file is part of CrafTech.
 *
 * CrafTech is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CrafTech is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with CrafTech.  If not, see <http://www.gnu.org/licenses/>.
 */

package dk.philiphansen.craftech.block;

import cpw.mods.fml.common.network.internal.FMLNetworkHandler;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import dk.philiphansen.craftech.CrafTech;
import dk.philiphansen.craftech.reference.BlockInfo;
import dk.philiphansen.craftech.reference.GuiInfo;
import dk.philiphansen.craftech.reference.ModInfo;
import dk.philiphansen.craftech.tileentity.TileEntityBlastFurnace;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Defines the blast furnace block.
 */
public class BlockBlastFurnace extends BlockContainer {

	@SideOnly(Side.CLIENT)
	private IIcon verticalIcon;
	@SideOnly(Side.CLIENT)
	private IIcon sidesIcon;
	@SideOnly(Side.CLIENT)
	private IIcon frontOffIcon;
	@SideOnly(Side.CLIENT)
	private IIcon frontOnIcon;

	/**
	 * Make the blast furnace block.
	 * Sets basic block values.
	 */
	BlockBlastFurnace() {
		super(Material.rock);
		setBlockName(BlockInfo.BLAST_FURNACE_NAME);
		setCreativeTab(CrafTech.tabCrafTech);
		setHardness(3.5F);
		setStepSound(soundTypePiston);
	}

	/**
	 * Registers the icons for the block.
	 *
	 * @param register
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister register) {
		verticalIcon = register.registerIcon(ModInfo.ID + ":" + BlockInfo.BLAST_FURNACE_TEXTURE_VERTICAL);
		frontOffIcon = register.registerIcon(ModInfo.ID + ":" + BlockInfo.BLAST_FURNACE_TEXTURE_FRONT_OFF);
		frontOnIcon = register.registerIcon(ModInfo.ID + ":" + BlockInfo.BLAST_FURNACE_TEXTURE_FRONT_ON);
		sidesIcon = register.registerIcon(ModInfo.ID + ":" + BlockInfo.BLAST_FURNACE_TEXTURE_SIDES);
	}

	/**
	 * Called by the client to find the icon texture for the block.
	 * Passes along side of block and block metadata, gets back the appropriate icon to display.
	 *
	 * @param side The side of the block.
	 * @param meta The current block metadata value.
	 * @return icon Gets the icon to display.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(int side, int meta) {

    	/* For top and bottom */
		if (side == 0 || side == 1) {
			return verticalIcon;
		}

        /*
         * Calculate from metadata which side is the front side.
         *
         * Divide by 2 and floor to remove data about on / off state.
         * Add 1 as metadata will result in 0 - 4 and side icons are on sides 1 - 5.
         *
         * If meta is 0, block is likely an item.
         * Then set side 3 to be front (3 is the one displayed towards you on the hotbar).
         */
		else if (side == MathHelper.floor_float(meta / 2) + 1 || (meta == 0 && side == 3)) {
			return (meta % 2 == 0) ? frontOffIcon : frontOnIcon;
		} else {
			return sidesIcon;
		}
	}

	/**
	 * Called by the block when it's placed, creates the tile entity that goes with the block.
	 *
	 * @param var1
	 * @param var2
	 * @return TileEntityBlastFurnace
	 */
	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new TileEntityBlastFurnace();
	}

	/**
	 * Called when a player places the block.
	 *
	 * @param world  World of the block.
	 * @param x      x coordinate of the block.
	 * @param y      y coordinate of the block.
	 * @param z      z coordinate of the block.
	 * @param player player placing the block.
	 * @param item
	 */
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase player, ItemStack item) {

        /*
         * Calculate which direction the block should face, based on player's rotation
         * when placing the block.
         */
		int l = MathHelper.floor_double((double) (player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;

		if (l == 0) {
			world.setBlockMetadataWithNotify(x, y, z, 2, 2);
		}

		if (l == 1) {
			world.setBlockMetadataWithNotify(x, y, z, 8, 2);
		}

		if (l == 2) {
			world.setBlockMetadataWithNotify(x, y, z, 4, 2);
		}

		if (l == 3) {
			world.setBlockMetadataWithNotify(x, y, z, 6, 2);
		}

	}

	/**
	 * Called when the block is right clicked by a player.
	 *
	 * @param world  World of the block.
	 * @param x      x coordinate of the block.
	 * @param y      y coordinate of the block.
	 * @param z      z coordinate of the block.
	 * @param player player right clicking.
	 * @param side   side right clicked.
	 * @param hitX   x coordinate on side of the click.
	 * @param hitY   y coordinate on side of the click.
	 * @param hitZ
	 * @return
	 */
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX,
	                                float hitY, float hitZ) {
	    /* On the server, call network handler to open GUI */
		if (!world.isRemote) {
			FMLNetworkHandler.openGui(player, CrafTech.instance, GuiInfo.BLAST_FURNACE, world, x, y, z);
		}
		return true;
	}

	/**
	 * Called when a player breaks the block.
	 * Takes care of everything that needs to be done as the block is removed.
	 * (Eg. dropping items)
	 *
	 * @param world The world in which the block is located.
	 * @param x     The x coordinate of the block.
	 * @param y     The y coordinate of the block.
	 * @param z     The z coordinate of the block.
	 * @param block The block being broken.
	 * @param meta  The metadata of the block.
	 */
	@Override
	public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
		TileEntity tileentity = world.getTileEntity(x, y, z);

		if (tileentity != null && tileentity instanceof IInventory) {
			IInventory inventory = (IInventory) tileentity;

            /* For each stack in the inventory */
			for (int i = 0; i < inventory.getSizeInventory(); i++) {
				ItemStack stack = inventory.getStackInSlotOnClosing(i);

				if (stack != null) {
					float spawnX = x + world.rand.nextFloat();
					float spawnY = y + world.rand.nextFloat();
					float spawnZ = z + world.rand.nextFloat();

					EntityItem droppedItem = new EntityItem(world, spawnX, spawnY, spawnZ, stack);

					float mult = 0.05F;

                    /* Calculate random(ish) direction */
					droppedItem.motionX = (-0.5F + world.rand.nextFloat()) * mult;
					droppedItem.motionY = (4 + world.rand.nextFloat()) * mult;
					droppedItem.motionZ = (-0.5F + world.rand.nextFloat()) * mult;

                    /* Drop stack */
					world.spawnEntityInWorld(droppedItem);
				}
			}
		}
        /* Make sure the block actually gets removed */
		super.breakBlock(world, x, y, z, block, meta);
	}

	/**
	 * Called by the client do display effects.
	 *
	 * @param world  World of the block.
	 * @param x      x coordinate of the block.
	 * @param y      y coordinate of the block.
	 * @param z      z coordinate of the block.
	 * @param random random generator to use for effects.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, int x, int y, int z, Random random) {
		int meta = world.getBlockMetadata(x, y, z);
		float fX = x + 0.5F;
		float fY = y + random.nextFloat() * 6.0F / 16.0F;
		float fZ = z + 0.5F;
		float f3 = 0.52F;
		float f4 = random.nextFloat() * 0.6F - 0.3F;

		switch (meta) {
			case 3:
				world.spawnParticle("smoke", (double) (fX + f4), (double) fY, (double) (fZ - f3), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double) (fX + f4), (double) fY, (double) (fZ - f3), 0.0D, 0.0D, 0.0D);
				break;
			case 5:
				world.spawnParticle("smoke", (double) (fX - f4), (double) fY, (double) (fZ + f3), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double) (fX - f4), (double) fY, (double) (fZ + f3), 0.0D, 0.0D, 0.0D);
				break;
			case 7:
				world.spawnParticle("smoke", (double) (fX - f3), (double) fY, (double) (fZ + f4), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double) (fX - f3), (double) fY, (double) (fZ + f4), 0.0D, 0.0D, 0.0D);
				break;
			case 9:
				world.spawnParticle("smoke", (double) (fX + f3), (double) fY, (double) (fZ + f4), 0.0D, 0.0D, 0.0D);
				world.spawnParticle("flame", (double) (fX + f3), (double) fY, (double) (fZ + f4), 0.0D, 0.0D, 0.0D);
				break;
		}
	}

	/**
	 * Called by the client to find the light value of the block.
	 *
	 * @param world World of the block.
	 * @param x     x coordinate of the block.
	 * @param y     y coordinate of the block.
	 * @param z     z coordinate of the block.
	 * @return light value.
	 */
	@Override
	public int getLightValue(IBlockAccess world, int x, int y, int z) {
		return (world.getBlockMetadata(x, y, z) % 2 == 0) ? 0 : 13;
	}
}
