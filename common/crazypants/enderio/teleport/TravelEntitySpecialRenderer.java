package crazypants.enderio.teleport;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Icon;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import crazypants.enderio.EnderIO;
import crazypants.render.BoundingBox;
import crazypants.render.CubeRenderer;
import crazypants.render.RenderUtil;
import crazypants.util.BlockCoord;
import crazypants.util.Util;
import crazypants.vecmath.Vector3d;
import crazypants.vecmath.Vector4f;

public class TravelEntitySpecialRenderer extends TileEntitySpecialRenderer {

  private final Vector4f selectedColor;
  private final Vector4f highlightColor;

  public TravelEntitySpecialRenderer() {
    this(new Vector4f(1, 0.25f, 0, 0.5f), new Vector4f(1, 1, 1, 0.25f));
  }

  public TravelEntitySpecialRenderer(Vector4f selectedColor, Vector4f highlightColor) {
    this.selectedColor = selectedColor;
    this.highlightColor = highlightColor;
  }

  @Override
  public void renderTileEntityAt(TileEntity tileentity, double x, double y, double z, float f) {

    if(!TravelController.instance.showTargets()) {
      return;
    }

    Vector3d eye = Util.getEyePositionEio(Minecraft.getMinecraft().thePlayer);
    Vector3d loc = new Vector3d(tileentity.xCoord, tileentity.yCoord, tileentity.zCoord);
    if(eye.distanceSquared(loc) > TravelController.instance.getMaxTravelDistanceSq()) {
      return;
    }

    BlockCoord bc = new BlockCoord(tileentity);
    TravelController.instance.addCandidate(bc);

    RenderUtil.bindBlockTexture();
    GL11.glPushAttrib(GL11.GL_ENABLE_BIT);
    GL11.glPushAttrib(GL11.GL_LIGHTING_BIT);

    GL11.glEnable(GL12.GL_RESCALE_NORMAL);

    GL11.glDisable(GL11.GL_DEPTH_TEST);
    //GL11.glDepthMask(false);
    GL11.glDisable(GL11.GL_LIGHTING);

    GL11.glEnable(GL11.GL_BLEND);
    GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

    GL11.glEnable(GL11.GL_CULL_FACE);

    GL11.glPushMatrix();
    GL11.glTranslated(x, y, z);
    GL11.glColor3f(1, 1, 1);

    Minecraft.getMinecraft().entityRenderer.disableLightmap(0);

    Tessellator.instance.startDrawingQuads();
    renderBlock();
    Tessellator.instance.draw();

    Tessellator.instance.startDrawingQuads();
    if(TravelController.instance.isBlockSelected(bc)) {

      Tessellator.instance.setColorRGBA_F(selectedColor.x, selectedColor.y, selectedColor.z, selectedColor.w);
      CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(1.05, 1.05, 1.05), getSelectedIcon());
    } else {
      Tessellator.instance.setColorRGBA_F(highlightColor.x, highlightColor.y, highlightColor.z, highlightColor.w);
      CubeRenderer.render(BoundingBox.UNIT_CUBE.scale(1.05, 1.05, 1.05), getHighlightIcon());
    }
    Tessellator.instance.draw();

    Minecraft.getMinecraft().entityRenderer.enableLightmap(0);

    //GL11.glDepthMask(true);
    GL11.glPopMatrix();
    GL11.glPopAttrib();
    GL11.glPopAttrib();

  }

  protected void renderBlock() {
    Tessellator.instance.setColorRGBA_F(1, 1, 1, 0.75f);
    CubeRenderer.render(BoundingBox.UNIT_CUBE, EnderIO.blockTravelPlatform.getIcon(0, 0));
  }

  public Vector4f getSelectedColor() {
    return selectedColor;
  }

  public Icon getSelectedIcon() {
    return EnderIO.blockTravelPlatform.selectedOverlayIcon;
  }

  public Vector4f getHighlightColor() {
    return highlightColor;
  }

  public Icon getHighlightIcon() {
    return EnderIO.blockTravelPlatform.highlightOverlayIcon;
  }

}
