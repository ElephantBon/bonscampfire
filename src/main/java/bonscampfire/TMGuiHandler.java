package bonscampfire;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import bonscampfire.blocks.tiles.TileCampfire;
import bonscampfire.gui.GuiCampfire;
import bonscampfire.inventory.ContainerCampfire;
import cpw.mods.fml.common.network.IGuiHandler;

public class TMGuiHandler implements IGuiHandler {

	public TMGuiHandler (){
		
	}
	
	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == 0){
			TileCampfire tile = (TileCampfire) world.getTileEntity(x, y, z);
			return new ContainerCampfire(player.inventory, tile);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		if(ID == 0){
			TileCampfire tile = (TileCampfire) world.getTileEntity(x, y, z);
			return new GuiCampfire(player.inventory, tile);
		}
		return null;
	}

}
