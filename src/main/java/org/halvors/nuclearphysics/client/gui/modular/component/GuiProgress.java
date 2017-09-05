package org.halvors.nuclearphysics.client.gui.modular.component;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.halvors.nuclearphysics.client.gui.modular.IGuiWrapper;
import org.halvors.nuclearphysics.client.utility.RenderUtility;
import org.halvors.nuclearphysics.common.utility.ResourceUtility;
import org.halvors.nuclearphysics.common.utility.type.ResourceType;

@SideOnly(Side.CLIENT)
public class GuiProgress extends GuiComponent {
    private int xLocation;
    private int yLocation;

    private int width = 22;
    private int height = 15;
    private int progress;

    public GuiProgress(IGuiWrapper gui, int x, int y, int progress) {
        super(ResourceUtility.getResource(ResourceType.GUI_COMPONENT, "progress.png"), gui, ResourceUtility.getResource(ResourceType.GUI_COMPONENT, "progress.png"));

        this.xLocation = x;
        this.yLocation = y;
        this.progress = progress;
    }

    @Override
    public Rectangle4i getBounds(int guiWidth, int guiHeight) {
        return new Rectangle4i(guiWidth + xLocation, guiHeight + yLocation, width, height);
    }

    @Override
    public void renderBackground(int xAxis, int yAxis, int guiWidth, int guiHeight) {
        RenderUtility.bindTexture(RESOURCE);

        int innerOffsetX = 2;

        guiObj.drawTexturedRect(guiWidth + xLocation, guiHeight + yLocation, 0, 0, width, height);

        //int displayInt = (int)(handler.getProgress() * (type.width - 2 * innerOffsetX));

        guiObj.drawTexturedRect(guiWidth + xLocation + innerOffsetX, guiHeight + yLocation, width + innerOffsetX, 0, progress, height);

        RenderUtility.bindTexture(defaultLocation);
    }

    @Override
    public void renderForeground(int xAxis, int yAxis) {

    }

    @Override
    public void preMouseClicked(int xAxis, int yAxis, int button) {

    }

    @Override
    public void mouseClicked(int xAxis, int yAxis, int button) {

    }
}