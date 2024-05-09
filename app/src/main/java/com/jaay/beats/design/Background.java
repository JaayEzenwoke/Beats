package com.jaay.beats.design;

import android.graphics.drawable.GradientDrawable;

public class Background {

    private GradientDrawable walllpaper;

    private int shade;

    private float[] radii;
    private float radius;

    public Background(){
        walllpaper = new GradientDrawable();
    }

    public GradientDrawable getWalllpaper() {
        return walllpaper;
    }

    public void setWallpaper() {
        if (walllpaper != null) {
            if(radius != 0){
                walllpaper.setCornerRadius(radius);
            }else {
                setRadii(radii[0], radii[1], radii[2], radii[3]);
            }
            walllpaper.setColor(shade);
            walllpaper.setCornerRadius(radius);
        }
    }

    public void setShade(int shade) {
        this.shade = shade;
    }

    public void setRadius(float radius, float[] radii) {
        this.radius = radius;
        this.radii = radii;
    }

    public void setRadii(float left, float top, float right, float bottom) {
        float[] radii = {
                left, left,
                top, top,
                right, right,
                bottom, bottom
        };
        walllpaper.setCornerRadii(radii);
    }

    public void setStroke(int width, int shade) {
        walllpaper.setStroke(width, shade);
    }

    public int getShade() {
        return shade;
    }
}
