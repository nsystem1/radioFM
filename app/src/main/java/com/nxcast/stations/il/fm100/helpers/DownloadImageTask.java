package com.nxcast.stations.il.fm100.helpers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.LightingColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import java.io.InputStream;
import java.net.URL;

/**
 * Created by leonidangarov on 30/11/15.
 */
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    private static final float BITMAP_SCALE = 0.7f;
    private static final float BLUR_RADIUS = 1.5f;

    ImageView bmImage;
    int color;
    Context ctx;

    public DownloadImageTask(ImageView bmImage, Context c) {
        this.bmImage = bmImage;
        this.ctx = c;
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        Bitmap mIcon11 = null;
        try {
            InputStream in = new URL(urldisplay).openStream();
            mIcon11 = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        return mIcon11;
    }

    public static Bitmap blur(Context context, Bitmap image) {
        if( context == null || image == null ) {
            return null;
        }
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        if( height == 0 || width == 0 ) {
            return null;
        }

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height,
                false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs,
                Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    protected void onPostExecute(Bitmap result) {
        if( bmImage == null ) {
            return;
        }
        Animation a = new AlphaAnimation(0.00f, 1.00f);

        a.setDuration(300);
        a.setAnimationListener(new Animation.AnimationListener() {

            public void onAnimationStart(Animation animation) {
            }

            public void onAnimationRepeat(Animation animation) {
            }

            public void onAnimationEnd(Animation animation) {
                bmImage.setVisibility(View.VISIBLE);
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            bmImage.setImageBitmap(blur(ctx, result));
        }
        bmImage.clearAnimation();
        bmImage.startAnimation(a);
    }
}