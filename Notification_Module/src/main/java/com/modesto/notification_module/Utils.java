package com.modesto.notification_module;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;

public class Utils {

    @NonNull
    @SuppressLint("DefaultLocale")
    public static String getTitle(int id){
        return String.format("Character Unlocked %d", id);
    }

    @NonNull
    @SuppressLint("DefaultLocale")
    public static String getText(int id){
        return String.format("You have unlocked character %d", id);
    }

    public static int getIcon(int id){

        switch (id){
            case 1:
                return  R.drawable.ic_stat_char_1;
            case 2:
                return  R.drawable.ic_stat_char_2;
            case 3:
                return  R.drawable.ic_stat_char_3;
            case 4:
                return  R.drawable.ic_stat_char_4;
            case 5:
                return  R.drawable.ic_stat_char_5;
            default:
                return android.R.drawable.ic_notification_overlay;
        }
    }

    public  static int[] getIconPixel(Context context, int id){

        Bitmap bitmap = getBitmap(context, id);
        int[] pixels = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(pixels, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        return pixels;
    }

    public  static int getBitmapSize(Context context, int id) {
        Bitmap bitmap = getBitmap(context, id);
        return bitmap.getWidth();
    }

    private static Bitmap getBitmap(Context context, int id){
        int iconId = getIcon(id);
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, iconId);
    }

    public static boolean arraysHaveSameItems(int[] array1, int[] array2) {
        // Controlla se gli array hanno lunghezze diverse
        if (array1.length != array2.length) {
            return false;
        }

        // Confronta elemento per elemento
        for (int i = 0; i < array1.length; i++) {
            if (array1[i] != array2[i]) {
                return false; // Esce immediatamente se trova una differenza
            }
        }

        return true; // Restituisce true se tutti gli elementi corrispondono
    }
}
