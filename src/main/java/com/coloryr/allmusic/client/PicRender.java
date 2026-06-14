package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.PictureFrameBuffer;
import com.coloryr.allmusic.codec.HudPosType;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.joml.Quaternionf;

import java.io.ByteArrayInputStream;

public class PicRender extends PictureFrameBuffer {
    private static final ResourceLocation SOURCE_LOC = ResourceLocation.fromNamespaceAndPath("allmusic_client", "pic_source");
    private static final ResourceLocation ROTATE_LOC = ResourceLocation.fromNamespaceAndPath("allmusic_client", "pic_rotate");

    private final DynamicTexture sourceTexture;
    private final DynamicTexture rotateTexture;
    private final int size;

    public PicRender(int size) {
        this.size = size;
        sourceTexture = new DynamicTexture(size, size, false);
        rotateTexture = new DynamicTexture(size, size, false);
        Minecraft.getInstance().getTextureManager().register(SOURCE_LOC, sourceTexture);
        Minecraft.getInstance().getTextureManager().register(ROTATE_LOC, rotateTexture);
    }

    @Override
    public void update(byte[] source, byte[] rotate) {
        try {
            if (source != null) {
                ByteArrayInputStream stream = new ByteArrayInputStream(source);
                var image1 = NativeImage.read(stream);
                sourceTexture.setPixels(image1);
                sourceTexture.upload();
            }
            if (rotate != null) {
                ByteArrayInputStream stream = new ByteArrayInputStream(rotate);
                var image2 = NativeImage.read(stream);
                rotateTexture.setPixels(image2);
                rotateTexture.upload();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void draw(boolean rotate, int size, float x, float y, int ang, HudPosType dir, float alpha) {
        GuiGraphics gui = AllMusicClient.getContext();
        if (gui == null) return;

        Point2f point = AllMusicHud.getPos(this.size, this.size, x, y, dir);
        drawAtImpl(rotate, point.x, point.y, ang, alpha, gui);
    }

    @Override
    public void drawAt(boolean rotate, float screenX, float screenY, int ang, float alpha) {
        GuiGraphics gui = AllMusicClient.getContext();
        if (gui == null) return;
        drawAtImpl(rotate, screenX, screenY, ang, alpha, gui);
    }

    @Override
    public void drawAt(boolean rotate, float screenX, float screenY, float displaySize, int ang, float alpha) {
        GuiGraphics gui = AllMusicClient.getContext();
        if (gui == null) return;
        ResourceLocation loc = rotate ? ROTATE_LOC : SOURCE_LOC;

        var pose = gui.pose();
        pose.pushPose();
        pose.translate(screenX + displaySize / 2f, screenY + displaySize / 2f, 0);
        if (rotate) {
            pose.mulPose(new Quaternionf().fromAxisAngleDeg(0, 0, 1, ang));
        }
        pose.translate(-displaySize / 2f, -displaySize / 2f, 0);
        pose.scale(displaySize / (float) this.size, displaySize / (float) this.size, 1);

        RenderSystem.enableBlend();
        gui.blit(RenderType::guiTextured, loc, 0, 0, 0.0f, 0.0f, this.size, this.size, this.size, this.size);
        RenderSystem.disableBlend();

        pose.popPose();
    }

    private void drawAtImpl(boolean rotate, float screenX, float screenY, int ang, float alpha, GuiGraphics gui) {
        ResourceLocation loc = rotate ? ROTATE_LOC : SOURCE_LOC;

        var pose = gui.pose();
        pose.pushPose();
        pose.translate(screenX + this.size / 2f, screenY + this.size / 2f, 0);
        if (rotate) {
            pose.mulPose(new Quaternionf().fromAxisAngleDeg(0, 0, 1, ang));
        }
        pose.translate(-this.size / 2f, -this.size / 2f, 0);

        RenderSystem.enableBlend();
        gui.blit(RenderType::guiTextured, loc, 0, 0, 0.0f, 0.0f, this.size, this.size, this.size, this.size);
        RenderSystem.disableBlend();

        pose.popPose();
    }
}
