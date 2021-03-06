package bonscampfire.blocks.tiles;

import bonscampfire.blocks.BlockCampfire;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;

public class TileCampfire extends TileEntity implements ISidedInventory
{
    private static final int[] slotsTop = new int[] {0};
    private static final int[] slotsBottom = new int[] {2, 1};
    private static final int[] slotsSides = new int[] {1};
    /** The ItemStacks that hold the items currently being used in the campfire */
    private ItemStack[] campfireItemStacks = new ItemStack[3];
    /** The number of ticks that the campfire will keep burning */
    public int campfireBurnTime;
    /** The number of ticks that a fresh copy of the currently-burning item would keep the campfire burning for */
    public int currentItemBurnTime;
    /** The number of ticks that the current item has been cooking for */
    public int campfireCookTime;
    /** The threshold of ticks that the campfire becomes dying when burning time below this value */ 
    private int campfireDyingThreshold = 150;
    private String field_145958_o;
    public int rotation;
    
    /**
     * Returns the number of slots in the inventory.
     */
    public int getSizeInventory()
    {
        return this.campfireItemStacks.length;
    }

    /**
     * Returns the stack in slot i
     */
    public ItemStack getStackInSlot(int p_70301_1_)
    {
        return this.campfireItemStacks[p_70301_1_];
    }

    /**
     * Removes from an inventory slot (first arg) up to a specified number (second arg) of items and returns them in a
     * new stack.
     */
    public ItemStack decrStackSize(int p_70298_1_, int p_70298_2_)
    {
        if (this.campfireItemStacks[p_70298_1_] != null)
        {
            ItemStack itemstack;

            if (this.campfireItemStacks[p_70298_1_].stackSize <= p_70298_2_)
            {
                itemstack = this.campfireItemStacks[p_70298_1_];
                this.campfireItemStacks[p_70298_1_] = null;
                return itemstack;
            }
            else
            {
                itemstack = this.campfireItemStacks[p_70298_1_].splitStack(p_70298_2_);

                if (this.campfireItemStacks[p_70298_1_].stackSize == 0)
                {
                    this.campfireItemStacks[p_70298_1_] = null;
                }

                return itemstack;
            }
        }
        else
        {
            return null;
        }
    }

    /**
     * When some containers are closed they call this on each slot, then drop whatever it returns as an EntityItem -
     * like when you close a workbench GUI.
     */
    public ItemStack getStackInSlotOnClosing(int p_70304_1_)
    {
        if (this.campfireItemStacks[p_70304_1_] != null)
        {
            ItemStack itemstack = this.campfireItemStacks[p_70304_1_];
            this.campfireItemStacks[p_70304_1_] = null;
            return itemstack;
        }
        else
        {
            return null;
        }
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    public void setInventorySlotContents(int p_70299_1_, ItemStack p_70299_2_)
    {
        this.campfireItemStacks[p_70299_1_] = p_70299_2_;

        if (p_70299_2_ != null && p_70299_2_.stackSize > this.getInventoryStackLimit())
        {
            p_70299_2_.stackSize = this.getInventoryStackLimit();
        }
    }

    /**
     * Returns the name of the inventory
     */
    public String getInventoryName()
    {
        return this.hasCustomInventoryName() ? this.field_145958_o : "container.furnace";
    }

    /**
     * Returns if the inventory is named
     */
    public boolean hasCustomInventoryName()
    {
        return this.field_145958_o != null && this.field_145958_o.length() > 0;
    }

    public void func_145951_a(String p_145951_1_)
    {
        this.field_145958_o = p_145951_1_;
    }

    public void readFromNBT(NBTTagCompound compound)
    {
        super.readFromNBT(compound);
        NBTTagList nbttaglist = compound.getTagList("Items", 10);
        this.campfireItemStacks = new ItemStack[this.getSizeInventory()];
        this.rotation = compound.getInteger("TileRotation");

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
            byte b0 = nbttagcompound1.getByte("Slot");

            if (b0 >= 0 && b0 < this.campfireItemStacks.length) {
                this.campfireItemStacks[b0] = ItemStack.loadItemStackFromNBT(nbttagcompound1);
            }
        }

        this.campfireBurnTime = compound.getShort("BurnTime");
        this.campfireCookTime = compound.getShort("CookTime");
        this.currentItemBurnTime = getItemBurnTime(this.campfireItemStacks[1]);

        if (compound.hasKey("CustomName", 8)) {
            this.field_145958_o = compound.getString("CustomName");
        }
    }

    public void writeToNBT(NBTTagCompound compound)
    {
        super.writeToNBT(compound);
        compound.setShort("BurnTime", (short)this.campfireBurnTime);
        compound.setShort("CookTime", (short)this.campfireCookTime);
        compound.setInteger("TileRotation", this.rotation);
        NBTTagList nbttaglist = new NBTTagList();

        for (int i = 0; i < this.campfireItemStacks.length; ++i) {
            if (this.campfireItemStacks[i] != null) {
                NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                nbttagcompound1.setByte("Slot", (byte)i);
                this.campfireItemStacks[i].writeToNBT(nbttagcompound1);
                nbttaglist.appendTag(nbttagcompound1);
            }
        }

        compound.setTag("Items", nbttaglist);

        if (this.hasCustomInventoryName()) {
            compound.setString("CustomName", this.field_145958_o);
        }
    }

    /**
     * Returns the maximum stack size for a inventory slot.
     */
    public int getInventoryStackLimit()
    {
        return 64;
    }

    /**
     * Returns an integer between 0 and the passed value representing how close the current item is to being completely
     * cooked
     */
    @SideOnly(Side.CLIENT)
    public int getCookProgressScaled(int p_145953_1_)
    {
        return this.campfireCookTime * p_145953_1_ / 200;
    }

    /**
     * Returns an integer between 0 and the passed value representing how much burn time is left on the current fuel
     * item, where 0 means that the item is exhausted and the passed value means that the item is fresh
     */
    @SideOnly(Side.CLIENT)
    public int getBurnTimeRemainingScaled(int p_145955_1_)
    {
        if (this.currentItemBurnTime == 0)
        {
            this.currentItemBurnTime = 200;
        }

        return this.campfireBurnTime * p_145955_1_ / this.currentItemBurnTime;
    }

    /**
     * Campfire isBurning
     */
    public boolean isBurning()
    {
        return this.campfireBurnTime > 0;
    }
    
    /**
     * Campfire isDying
     */
    public boolean isDying()
    {
    	return this.campfireBurnTime > 0 && this.campfireBurnTime <= this.campfireDyingThreshold;
    }
    
    public void updateEntity()
    {
        int oldLitLevel = getLitLevel();        
        boolean stateChanged = false;

        if (this.campfireBurnTime > 0) {
            --this.campfireBurnTime;
        }

        if (!this.worldObj.isRemote) {
        	// Comsume fuel if campfire is dying
        	if (this.campfireItemStacks[1] != null && this.isDying()) {
        		int fuelBurnTime = getItemBurnTime(this.campfireItemStacks[1]);
        		if (fuelBurnTime > 0) {
        			stateChanged = true;
        			this.campfireBurnTime += fuelBurnTime;
                    
                    --this.campfireItemStacks[1].stackSize;

                    if (this.campfireItemStacks[1].stackSize == 0) {
                        this.campfireItemStacks[1] = campfireItemStacks[1].getItem().getContainerItem(campfireItemStacks[1]);
                    }                    
                }
        	}
        	
        	// Update item cook time
            if (this.isBurning() && this.canSmelt()) {
                ++this.campfireCookTime;

                if (this.campfireCookTime == 200) {
                    this.campfireCookTime = 0;
                    this.smeltItem();
                    stateChanged = true;
                }
            }
            else {
                this.campfireCookTime = 0;
            }            
        	
//            if (this.campfireBurnTime != 0 || this.campfireItemStacks[1] != null && this.campfireItemStacks[0] != null) {
//                if (this.campfireBurnTime == 0 && this.canSmelt()) {
//                    this.currentItemBurnTime = this.campfireBurnTime = getItemBurnTime(this.campfireItemStacks[1]);
//
//                    if (this.campfireBurnTime > 0) {
//                        stateChanged = true;
//
//                        if (this.campfireItemStacks[1] != null) {
//                            --this.campfireItemStacks[1].stackSize;
//
//                            if (this.campfireItemStacks[1].stackSize == 0) {
//                                this.campfireItemStacks[1] = campfireItemStacks[1].getItem().getContainerItem(campfireItemStacks[1]);
//                            }
//                        }
//                    }
//                }
//
//                if (this.isBurning() && this.canSmelt()) {
//                    ++this.campfireCookTime;
//
//                    if (this.campfireCookTime == 200) {
//                        this.campfireCookTime = 0;
//                        this.smeltItem();
//                        stateChanged = true;
//                    }
//                }
//                else {
//                    this.campfireCookTime = 0;
//                }
//            }

            // Update block state
            int newLitLevel = this.getLitLevel();
            if (oldLitLevel != newLitLevel) {
                stateChanged = true;
                BlockCampfire.updateCampfireBlockState(newLitLevel, this.worldObj, this.xCoord, this.yCoord, this.zCoord);
            }
        }

        if (stateChanged) {
            this.markDirty();
        }
    }
    
    private int getLitLevel()
    {
        if (this.campfireBurnTime > this.campfireDyingThreshold) {
        	return 2;
        }
        else
        if (this.campfireBurnTime > 0) {
        	return 1;
        }
        else {
        	return 0;
        }    	
    }

    /**
     * Returns true if the campfire can smelt an item, i.e. has a source item, destination stack isn't full, etc.
     */
    private boolean canSmelt()
    {
        if (this.campfireItemStacks[0] == null)
        {
            return false;
        }
        else
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.campfireItemStacks[0]);
            if (itemstack == null) return false;
            if (this.campfireItemStacks[2] == null) return true;
            if (!this.campfireItemStacks[2].isItemEqual(itemstack)) return false;
            int result = campfireItemStacks[2].stackSize + itemstack.stackSize;
            return result <= getInventoryStackLimit() && result <= this.campfireItemStacks[2].getMaxStackSize(); //Forge BugFix: Make it respect stack sizes properly.
        }
    }

    /**
     * Turn one item from the campfire source stack into the appropriate smelted item in the campfire result stack
     */
    public void smeltItem()
    {
        if (this.canSmelt())
        {
            ItemStack itemstack = FurnaceRecipes.smelting().getSmeltingResult(this.campfireItemStacks[0]);

            if (this.campfireItemStacks[2] == null)
            {
                this.campfireItemStacks[2] = itemstack.copy();
            }
            else if (this.campfireItemStacks[2].getItem() == itemstack.getItem())
            {
                this.campfireItemStacks[2].stackSize += itemstack.stackSize; // Forge BugFix: Results may have multiple items
            }

            --this.campfireItemStacks[0].stackSize;

            if (this.campfireItemStacks[0].stackSize <= 0)
            {
                this.campfireItemStacks[0] = null;
            }
        }
    }

    /**
     * Returns the number of ticks that the supplied fuel item will keep the campfire burning, or 0 if the item isn't
     * fuel
     */
    public static int getItemBurnTime(ItemStack p_145952_0_)
    {
        if (p_145952_0_ == null)
        {
            return 0;
        }
        else
        {
        	int moddedBurnTime = net.minecraftforge.event.ForgeEventFactory.getFuelBurnTime(p_145952_0_);
        	if (moddedBurnTime >= 0) return moddedBurnTime;
        	
            Item item = p_145952_0_.getItem();

            if (item instanceof ItemBlock && Block.getBlockFromItem(item) != Blocks.air)
            {
                Block block = Block.getBlockFromItem(item);

                if (block == Blocks.wooden_slab)
                {
                    return 150;
                }

                if (block.getMaterial() == Material.wood)
                {
                    return 300;
                }

                if (block == Blocks.coal_block)
                {
                    return 16000;
                }
            }

            if (item instanceof ItemTool && ((ItemTool)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemSword && ((ItemSword)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item instanceof ItemHoe && ((ItemHoe)item).getToolMaterialName().equals("WOOD")) return 200;
            if (item == Items.stick) return 100;
            if (item == Items.coal) return 1600;
            if (item == Items.lava_bucket) return 20000;
            if (item == Item.getItemFromBlock(Blocks.sapling)) return 100;
            if (item == Items.blaze_rod) return 2400;
            return GameRegistry.getFuelValue(p_145952_0_);
        }
    }

    public static boolean isItemFuel(ItemStack p_145954_0_)
    {
        /**
         * Returns the number of ticks that the supplied fuel item will keep the campfire burning, or 0 if the item isn't
         * fuel
         */
        return getItemBurnTime(p_145954_0_) > 0;
    }

    /**
     * Do not make give this method the name canInteractWith because it clashes with Container
     */
    public boolean isUseableByPlayer(EntityPlayer p_70300_1_)
    {
        return this.worldObj.getTileEntity(this.xCoord, this.yCoord, this.zCoord) != this ? false : p_70300_1_.getDistanceSq((double)this.xCoord + 0.5D, (double)this.yCoord + 0.5D, (double)this.zCoord + 0.5D) <= 64.0D;
    }

    public void openInventory() {}

    public void closeInventory() {}

    /**
     * Returns true if automation is allowed to insert the given stack (ignoring stack size) into the given slot.
     */
    public boolean isItemValidForSlot(int p_94041_1_, ItemStack p_94041_2_)
    {
        return p_94041_1_ == 2 ? false : (p_94041_1_ == 1 ? isItemFuel(p_94041_2_) : true);
    }

    /**
     * Returns an array containing the indices of the slots that can be accessed by automation on the given side of this
     * block.
     */
    public int[] getAccessibleSlotsFromSide(int p_94128_1_)
    {
        return p_94128_1_ == 0 ? slotsBottom : (p_94128_1_ == 1 ? slotsTop : slotsSides);
    }

    /**
     * Returns true if automation can insert the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    public boolean canInsertItem(int p_102007_1_, ItemStack p_102007_2_, int p_102007_3_)
    {
        return this.isItemValidForSlot(p_102007_1_, p_102007_2_);
    }

    /**
     * Returns true if automation can extract the given item in the given slot from the given side. Args: Slot, item,
     * side
     */
    public boolean canExtractItem(int p_102008_1_, ItemStack p_102008_2_, int p_102008_3_)
    {
        return p_102008_3_ != 0 || p_102008_1_ != 1 || p_102008_2_.getItem() == Items.bucket;
    }
    
//    public boolean canUpdate() {
//    	return false;
//    }
    
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
    	NBTTagCompound compound = pkt.func_148857_g();//pkt.getNbtCompound();
    	this.readFromNBT(compound);
    }
   
    public Packet getDescriptionPacket() {
    	NBTTagCompound compound = new NBTTagCompound();
    	this.writeToNBT(compound);
    	compound.removeTag("Items");
    	S35PacketUpdateTileEntity packet = new S35PacketUpdateTileEntity(super.xCoord, super.yCoord, super.zCoord, 0, compound);
    	return packet;
    }

    public AxisAlignedBB getRenderBoundingBox() {
    	return AxisAlignedBB.getBoundingBox((double)super.xCoord, (double)super.yCoord, (double)super.zCoord, (double)(super.xCoord + 1), (double)(super.yCoord + 1), (double)(super.zCoord + 1));
    }

    public int powerProvided() {
    	return 0;
    }
}
