package bonscampfire;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.particle.EntityFlameFX;
import net.minecraft.client.particle.EntitySmokeFX;
import net.minecraft.client.renderer.RenderGlobal;
import bonscampfire.blocks.renderer.BlockCampfireRenderer;
import bonscampfire.blocks.tiles.TileCampfire;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.ObfuscationReflectionHelper;

public class ClientProxy extends ServerProxy{
	
	public void registerRenderThings(){		

        ClientRegistry.bindTileEntitySpecialRenderer(TileCampfire.class, new BlockCampfireRenderer());
        
		//ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTutChest.class, new TutChestRenderer());
		//MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(TMBlock.tutChest), new ItemRenderTutChest());
	}	
	
	
   public void spawnParticle(String particle, double x, double y, double z, double motionX, double motionY, double motionZ, float scale) {
      RenderGlobal render = Minecraft.getMinecraft().renderGlobal;
      EntityFX fx = render.doSpawnParticle(particle, x, y, z, motionX, motionY, motionZ);
      if(fx != null) {
         if(particle.equals("flame")) {
            ObfuscationReflectionHelper.setPrivateValue(EntityFlameFX.class, (EntityFlameFX)fx, Float.valueOf(scale), 0);
         } else if(particle.equals("smoke")) {
            ObfuscationReflectionHelper.setPrivateValue(EntitySmokeFX.class, (EntitySmokeFX)fx, Float.valueOf(scale), 0);
         }

      }
   }
}
