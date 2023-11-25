package com.xempre.pressurelesshealth.views.shared;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import com.xempre.pressurelesshealth.views.profile.goal.GoalAdapter;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class GetImageByUrl {

    public static void getBitmapFromURL(RecyclerView.ViewHolder holder, String src, Handler handler, ImageView imageView) {

        // Ejecutar operaciones de red en un hilo separado usando Runnable
        final Bitmap[] myBitmap = new Bitmap[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Coloca aquí tu código de operaciones de red

                // Ejemplo:
                // HttpURLConnection connection = ...
                // Realizar operaciones de red...
                try{
                    Log.e("src",src);
                    URL url = new URL(src);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setDoInput(true);
                    connection.connect();
                    InputStream input = connection.getInputStream();
                    myBitmap[0] = BitmapFactory.decodeStream(input);}
                catch (Exception e){}
                // Si necesitas actualizar la interfaz de usuario, usa el Handler
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        // Actualizar la interfaz de usuario si es necesario
                        imageView.setImageBitmap(myBitmap[0]);
                    }
                });
            }
        }).start();
    }
}
