package com.coloryr.allmusic.codec;

public class HudQueuePosObj extends HudBasePosObj {
    public int color;
    public boolean shadow;
    public int gap;

    public HudQueuePosObj copy() {
        HudQueuePosObj obj = new HudQueuePosObj();
        obj.x = x;
        obj.y = y;
        obj.alpha = alpha;
        obj.color = color;
        obj.enable = enable;
        obj.gap = gap;
        obj.shadow = shadow;
        obj.pos = pos;
        return obj;
    }
}
