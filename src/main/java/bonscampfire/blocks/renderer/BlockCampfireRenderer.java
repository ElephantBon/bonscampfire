package bonscampfire.blocks.renderer;

import cpw.mods.fml.client.registry.RenderingRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.tileentity.TileEntity;
import org.lwjgl.opengl.GL11;

import bonscampfire.MainRegistry;
import bonscampfire.blocks.BlockCampfire;
import bonscampfire.blocks.model.ModelCampfire;
import bonscampfire.blocks.tiles.TileCampfire;

public class BlockCampfireRenderer extends BlockRendererInterface {

   private final ModelCampfire model = new ModelCampfire();


   public BlockCampfireRenderer() {
      ((BlockCampfire)MainRegistry.blockCampfireLit).renderId = RenderingRegistry.getNextAvailableRenderId();
      ((BlockCampfire)MainRegistry.blockCampfireDying).renderId = ((BlockCampfire)MainRegistry.blockCampfireLit).renderId;
      ((BlockCampfire)MainRegistry.blockCampfireUnlit).renderId = ((BlockCampfire)MainRegistry.blockCampfireLit).renderId;
      RenderingRegistry.registerBlockHandler(this);
   }

   public void renderTileEntityAt(TileEntity var1, double var2, double var4, double var6, float var8) {
      TileCampfire tile = (TileCampfire)var1;
      GL11.glDisable('\u803a');
      GL11.glPushMatrix();
      GL11.glTranslatef((float)var2 + 0.5F, (float)var4 + 1.5F, (float)var6 + 0.5F);
      GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef((float)(45 * tile.rotation), 0.0F, 1.0F, 0.0F);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(BlockRendererInterface.PlanksOak);
      this.model.renderLog(0.0625F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(BlockRendererInterface.Stone);
      this.model.renderRock(0.0625F);
      GL11.glPopMatrix();
   }

   public void renderInventoryBlock(Block block, int metadata, int modelId, RenderBlocks renderer) {
      GL11.glPushMatrix();
      GL11.glTranslatef(0.0F, 1.2F, 0.0F);
      GL11.glScalef(1.0F, 1.0F, 1.0F);
      GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
      GL11.glRotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GL11.glColor3f(1.0F, 1.0F, 1.0F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(BlockRendererInterface.PlanksOak);
      this.model.renderLog(0.0625F);
      Minecraft.getMinecraft().getTextureManager().bindTexture(BlockRendererInterface.Stone);
      this.model.renderRock(0.0625F);
      GL11.glPopMatrix();
   }

   public int getRenderId() {
      return MainRegistry.blockCampfireUnlit.getRenderType();
   }
}
