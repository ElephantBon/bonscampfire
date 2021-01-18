package bonscampfire;

import bonscampfire.blocks.BlockCampfire;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;

@Mod(modid = MainRegistry.MODID, name = MainRegistry.name, version = MainRegistry.version)

public class MainRegistry {

	public static final String MODID = "bonscampfire";
	public static final String name = "Bon's Campfire";
	public static final String version = "1.0";
	
	@SidedProxy(clientSide = "bonscampfire.ClientProxy", serverSide = "bonscampfire.ServerProxy") 
	public static ServerProxy proxy;
	
	
	@Instance(MODID)
	public static MainRegistry modInstance;
	

	public static Block blockCampfireUnlit;	
	public static Block blockCampfireDying;
	public static Block blockCampfireLit;
	
	/**
	 * Loads before
	 * @param PreEvent
	 */
	@EventHandler
	public static void PreLoad(FMLPreInitializationEvent PreEvent){	
		// initialize blocks
		blockCampfireUnlit = new BlockCampfire(0).setBlockName("CampfireUnlit").setCreativeTab(CreativeTabs.tabDecorations);
		blockCampfireDying = new BlockCampfire(1).setBlockName("CampfireDying").setLightLevel(0.4F);
		blockCampfireLit = new BlockCampfire(2).setBlockName("CampfireLit").setLightLevel(0.8F);

		// register blocks
		GameRegistry.registerBlock(blockCampfireUnlit, blockCampfireUnlit.getUnlocalizedName());
		GameRegistry.registerBlock(blockCampfireDying, blockCampfireDying.getUnlocalizedName());
		GameRegistry.registerBlock(blockCampfireLit, blockCampfireLit.getUnlocalizedName());
		
		// register tile entities at server side
		proxy.registerTileEntities();
		
		// register render things at client side
		proxy.registerRenderThings();
	}
	
	/**
	 * Loads during
	 * @param event
	 */
	@EventHandler
	public static void load(FMLInitializationEvent event){
		// register gui handler at server side
		proxy.registerNetworkStuff();
	}
	
	/**
	 * Loads after
	 * @param PostEvent
	 */
	@EventHandler
	public static void PostLoad(FMLPostInitializationEvent PostEvent){
		
	}
	
}
