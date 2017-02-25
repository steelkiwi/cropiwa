package com.steelkiwi.cropiwa;

/**
 * Created by yarolegovich on 06.02.2017.
 */

public class AspectRatio {

    public static final AspectRatio IMG_SRC = new AspectRatio(-1, -1);

    private int width;
    private int height;

    public AspectRatio(int w, int h) {
        this.width = w;
        this.height = h;
    }

    public int getWidth() {
        return width;
    }

    public boolean isSquare() {
        return width == height;
    }

    public int getHeight() {
        return height;
    }

    public float getRatio() {
        return ((float) width) / height;
    }
}

