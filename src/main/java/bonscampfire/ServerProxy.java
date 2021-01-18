package bonscampfire;

import bonscampfire.blocks.tiles.TileCampfire;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;

public class ServerProxy {

	public void registerRenderThings() {

	}	
	
	public void registerNetworkStuff(){
		NetworkRegistry.INSTANCE.registerGuiHandler(MainRegistry.modInstance, new TMGuiHandler());
	}

	public void registerTileEntities(){
		//GameRegistry.registerTileEntity(TileEntityTutChest.class, Strings.MODID);
		GameRegistry.registerTileEntity(TileCampfire.class, "TileCampfire");
	}
	
	public void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {}
}
