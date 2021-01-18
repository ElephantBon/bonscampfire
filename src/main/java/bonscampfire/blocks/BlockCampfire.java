package bonscampfire.blocks;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Random;
import org.lwjgl.opengl.GL11;
import bonscampfire.MainRegistry;
import bonscampfire.blocks.tiles.TileCampfire;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockCampfire extends BlockContainer
{
    private final Random field_149933_a = new Random();
    private final int litLevel;	// 0=unlit, 1=dying, 2=lit
    private final int maxRotation = 8;
    private static boolean changingBlock;
    @SideOnly(Side.CLIENT)
    private IIcon field_149935_N;
    @SideOnly(Side.CLIENT)
    private IIcon field_149936_O;
    public int renderId = -1;
    
    public BlockCampfire(int litLevel)
    {
        super(Material.rock);
        this.litLevel = litLevel;
    }

    public Item getItemDropped(int p_149650_1_, Random p_149650_2_, int p_149650_3_)
    {
        return Item.getItemFromBlock(Blocks.furnace);
    }

    /**
     * Called whenever the block is added into the world. Args: world, x, y, z
     */
    public void onBlockAdded(World p_149726_1_, int p_149726_2_, int p_149726_3_, int p_149726_4_)
    {
        super.onBlockAdded(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
//        this.func_149930_e(p_149726_1_, p_149726_2_, p_149726_3_, p_149726_4_);
    }

//    private void func_149930_e(World p_149930_1_, int p_149930_2_, int p_149930_3_, int p_149930_4_)
//    {
//        if (!p_149930_1_.isRemote)
//        {
//            Block block = p_149930_1_.getBlock(p_149930_2_, p_149930_3_, p_149930_4_ - 1);
//            Block block1 = p_149930_1_.getBlock(p_149930_2_, p_149930_3_, p_149930_4_ + 1);
//            Block block2 = p_149930_1_.getBlock(p_149930_2_ - 1, p_149930_3_, p_149930_4_);
//            Block block3 = p_149930_1_.getBlock(p_149930_2_ + 1, p_149930_3_, p_149930_4_);
//            byte b0 = 3;
//
//            if (block.func_149730_j() && !block1.func_149730_j())
//            {
//                b0 = 3;
//            }
//
//            if (block1.func_149730_j() && !block.func_149730_j())
//            {
//                b0 = 2;
//            }
//
//            if (block2.func_149730_j() && !block3.func_149730_j())
//            {
//                b0 = 5;
//            }
//
//            if (block3.func_149730_j() && !block2.func_149730_j())
//            {
//                b0 = 4;
//            }
//
//            p_149930_1_.setBlockMetadataWithNotify(p_149930_2_, p_149930_3_, p_149930_4_, b0, 2);
//        }
//    }

    /**
     * Gets the block's texture. Args: side, meta
     */
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int p_149691_1_, int p_149691_2_)
    {
        return p_149691_1_ == 1 ? this.field_149935_N : (p_149691_1_ == 0 ? this.field_149935_N : (p_149691_1_ != p_149691_2_ ? this.blockIcon : this.field_149936_O));
    }

    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister p_149651_1_)
    {
        this.blockIcon = p_149651_1_.registerIcon("furnace_side");
        this.field_149936_O = p_149651_1_.registerIcon("furnace_front_off");//this.field_149936_O = p_149651_1_.registerIcon(this.litLevel ? "furnace_front_on" : "furnace_front_off");
        this.field_149935_N = p_149651_1_.registerIcon("furnace_top");
    }

    /**
     * Called upon block activation (right click on the block.)
     */
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int p_149727_6_, float p_149727_7_, float p_149727_8_, float p_149727_9_)
    {
        if (world.isRemote) {
            return true;
        }
        else {
            TileCampfire tile = (TileCampfire)world.getTileEntity(x, y, z);

            if (tile != null) {
            	// Lit campfire if player holding flint or flint and steel when campfire is unlit
            	if (this.litLevel == 0) {
	            	ItemStack item = player.inventory.getCurrentItem();
	            	if (item != null) {            		
	            		boolean litSuccess = false;
	            		if (item.getItem() == Items.flint) {
	            			litSuccess = true;
	            			if (!player.capabilities.isCreativeMode) {
	            				--item.stackSize;
	            				if (item.stackSize <= 0) {
	            					player.destroyCurrentEquippedItem();
	            				}
	            			}
	            		}
	            		else 
	            		if (item.getItem() == Items.flint_and_steel) {
	            			litSuccess = true;
	            			item.damageItem(1, player);
	            		}
	            		
	            		if (litSuccess) {
	            			// Replace unlit campfire to dying campfire
	            			tile.campfireBurnTime += 2;	// To consume fuel in TileCampfire.updateEntity
	            			updateCampfireBlockState(1, world, x, y, z);
	            			return true;	// Event handled, don't open gui
	            		}
	            	}
            	}

                player.openGui(MainRegistry.modInstance, 0, world, x, y, z);//player.func_146101_a(tile);
            }

            return true;
        }
    }

    /**
     * Update which block the campfire is using depending on whether or not it is burning
     */
    public static void updateCampfireBlockState(int litLevel, World world, int x, int y, int z)
    {
        //int l = world.getBlockMetadata(x, y, z);
        TileEntity tile = world.getTileEntity(x, y, z);
        changingBlock = true;

        if (litLevel == 0) {
            world.setBlock(x, y, z, MainRegistry.blockCampfireUnlit);
        }
        else
        if (litLevel == 1) {
            world.setBlock(x, y, z, MainRegistry.blockCampfireDying);
        }
        else
        if (litLevel == 2) {
            world.setBlock(x, y, z, MainRegistry.blockCampfireLit);
        }

        changingBlock = false;
        //world.setBlockMetadataWithNotify(x, y, z, l, 2);

        if (tile != null) {
            tile.validate();
            world.setTileEntity(x, y, z, tile);
        }
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(World p_149915_1_, int p_149915_2_)
    {
        return new TileCampfire();
    }

    /**
     * Called when the block is placed in the world.
     */
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack itemStack)
    {
        int l = MathHelper.floor_double((double)(entity.rotationYaw * (float)this.maxRotation / 360.0F) + 0.5D) & (this.maxRotation - 1);
        l %= this.maxRotation;

        TileCampfire tile = (TileCampfire)world.getTileEntity(x, y, z);
        tile.rotation = l;        
        
        if (itemStack.hasDisplayName()) {
        	tile.func_145951_a(itemStack.getDisplayName());
        }
        		
//        if (l == 0)
//        {
//            world.setBlockMetadataWithNotify(x, y, z, 2, 2);
//        }
//
//        if (l == 1)
//        {
//            world.setBlockMetadataWithNotify(x, y, z, 5, 2);
//        }
//
//        if (l == 2)
//        {
//            world.setBlockMetadataWithNotify(x, y, z, 3, 2);
//        }
//
//        if (l == 3)
//        {
//            world.setBlockMetadataWithNotify(x, y, z, 4, 2);
//        }
//
//        if (itemStack.hasDisplayName())
//        {
//            ((TileCampfire)world.getTileEntity(x, y, z)).func_145951_a(itemStack.getDisplayName());
//        }
        
//        int l = MathHelper.floor_double((double)(par5EntityLivingBase.rotationYaw * (float)this.maxRotation() / 360.0F) + 0.5D) & this.maxRotation() - 1;
//        l %= this.maxRotation();
//        TileColorable tile = (TileColorable)par1World.getTileEntity(par2, par3, par4);
//        tile.rotation = l;
    }

    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_)
    {
        if (!changingBlock)
        {
            TileCampfire tile = (TileCampfire)world.getTileEntity(x, y, z);

            if (tile != null)
            {
                for (int i1 = 0; i1 < tile.getSizeInventory(); ++i1)
                {
                    ItemStack itemstack = tile.getStackInSlot(i1);

                    if (itemstack != null)
                    {
                        float f = this.field_149933_a.nextFloat() * 0.8F + 0.1F;
                        float f1 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;
                        float f2 = this.field_149933_a.nextFloat() * 0.8F + 0.1F;

                        while (itemstack.stackSize > 0)
                        {
                            int j1 = this.field_149933_a.nextInt(21) + 10;

                            if (j1 > itemstack.stackSize)
                            {
                                j1 = itemstack.stackSize;
                            }

                            itemstack.stackSize -= j1;
                            EntityItem entityitem = new EntityItem(world, (double)((float)x + f), (double)((float)y + f1), (double)((float)z + f2), new ItemStack(itemstack.getItem(), j1, itemstack.getItemDamage()));

                            if (itemstack.hasTagCompound())
                            {
                                entityitem.getEntityItem().setTagCompound((NBTTagCompound)itemstack.getTagCompound().copy());
                            }

                            float f3 = 0.05F;
                            entityitem.motionX = (double)((float)this.field_149933_a.nextGaussian() * f3);
                            entityitem.motionY = (double)((float)this.field_149933_a.nextGaussian() * f3 + 0.2F);
                            entityitem.motionZ = (double)((float)this.field_149933_a.nextGaussian() * f3);
                            world.spawnEntityInWorld(entityitem);
                        }
                    }
                }

                world.func_147453_f(x, y, z, p_149749_5_);
            }
        }

        super.breakBlock(world, x, y, z, p_149749_5_, p_149749_6_);
    }

    /**
     * A randomly called display update to be able to add particles or other items for display
     */
    @SideOnly(Side.CLIENT)
    public void randomDisplayTick(World world, int x, int y, int z, Random random)
    {
        int meta = world.getBlockMetadata(x, y, z);
        if(meta != 1) {
        	if(this.litLevel > 0) {
	           if(random.nextInt(36) == 0) {
	              world.playSound((double)((float)x + 0.5F), (double)((float)y + 0.5F), (double)((float)z + 0.5F), "fire.fire", 1.0F + random.nextFloat(), 0.3F + random.nextFloat() * 0.7F, false);
	           }
	
//	           TileCampfire tile = (TileCampfire)world.getTileEntity(x, y, z);
	           float xOffset = 0.5F;
	           float yOffset = 0.7F;
	           float zOffset = 0.5F;
	           double d0 = (double)((float)x + xOffset);
	           double d1 = (double)((float)y + yOffset);
	           double d2 = (double)((float)z + zOffset);
	           GL11.glPushMatrix();
	           MainRegistry.proxy.spawnParticle("largesmoke", d0, d1, d2, 0.0D, 0.0D, 0.0D, 1.5F * this.litLevel);
	           MainRegistry.proxy.spawnParticle("flame", d0, d1, d2, 0.0D, 0.0D, 0.0D, 2.5F * this.litLevel);
	           GL11.glPopMatrix();
	        }
        }
    }

    /**
     * If this returns true, then comparators facing away from this block will use the value from
     * getComparatorInputOverride instead of the actual redstone signal strength.
     */
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    /**
     * If hasComparatorInputOverride returns true, the return value from this is used instead of the redstone signal
     * strength when this block inputs to a comparator.
     */
    public int getComparatorInputOverride(World p_149736_1_, int p_149736_2_, int p_149736_3_, int p_149736_4_, int p_149736_5_)
    {
        return Container.calcRedstoneFromInventory((IInventory)p_149736_1_.getTileEntity(p_149736_2_, p_149736_3_, p_149736_4_));
    }

    /**
     * Gets an item for the block being called on. Args: world, x, y, z
     */
    @SideOnly(Side.CLIENT)
    public Item getItem(World p_149694_1_, int p_149694_2_, int p_149694_3_, int p_149694_4_)
    {
        return Item.getItemFromBlock(MainRegistry.blockCampfireUnlit);
    }

    public int getRenderType() {
       return this.renderId;
    }

    public boolean isOpaqueCube() {
       return false;
    }

    public boolean renderAsNormalBlock() {
       return false;
    }

    public boolean isSideSolid(IBlockAccess world, int x, int y, int z, ForgeDirection side) {
       return true;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {}
    
    
//   public int getLightValue(IBlockAccess world, int x, int y, int z) {
//      int meta = world.getBlockMetadata(x, y, z);
//      return meta == 0?14:0;
//   }
}