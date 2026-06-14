package com.coloryr.allmusic.client;

import com.coloryr.allmusic.client.core.AllMusicHud;
import com.coloryr.allmusic.client.core.Point2f;
import com.coloryr.allmusic.client.core.render.TextureRender;
import com.coloryr.allmusic.codec.HudPosType;
import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;

public class TexRender extends TextureRender {
    private final ResourceLocation location;

    public TexRender(String texture) {
        super(texture);
        location = ResourceLocation.fromNamespaceAndPath(AllMusicClient.MODID, texture);
        try {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            var resource = resourceManager.getResource(location).orElseThrow();
            try (var input = resource.open()) {
                var nativeImage = NativeImage.read(input);
                width = nativeImage.getWidth();
                height = nativeImage.getHeight();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void drawPic(float x, float y, float alpha) {
        GuiGraphics gui = AllMusicClient.getContext();
        if (gui == null) return;
        RenderSystem.enableBlend();
        gui.blit(RenderType::guiTextured, location, (int) x, (int) y, 0.0f, 0.0f, width, height, width, height);
        RenderSystem.disableBlend();
    }

    @Override
    public void drawPic(float x, float y, float width, float alpha) {
        GuiGraphics gui = AllMusicClient.getContext();
        if (gui == null) return;
        int drawWidth = (int) (this.width * width);
        RenderSystem.enableBlend();
        gui.blit(RenderType::guiTextured, location, (int) x, (int) y, 0.0f, 0.0f, drawWidth, this.height, this.width, this.height);
        RenderSystem.disableBlend();
    }

    @Override
    public void drawPic(float x, float y, float width, float height, HudPosType dir, float alpha) {
        GuiGraphics gui = AllMusicClient.getContext();
        if (gui == null) return;
        Point2f point = AllMusicHud.getPos(width, height, x, y, dir);
        RenderSystem.enableBlend();
        var pose = gui.pose();
        pose.pushPose();
        pose.translate(point.x, point.y, 0);
        pose.scale(width / (float) this.width, height / (float) this.height, 1);
        gui.blit(RenderType::guiTextured, location, 0, 0, 0.0f, 0.0f, this.width, this.height, this.width, this.height);
        pose.popPose();
        RenderSystem.disableBlend();
    }
}
