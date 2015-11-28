/**
 *  Android Processing + Network Boilerplate
 *  Copyright 16.11.2015 by Michael Peter Christen, @0rb1t3r
 *
 *  This library is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Lesser General Public
 *  License as published by the Free Software Foundation; either
 *  version 2.1 of the License, or (at your option) any later version.
 *
 *  This library is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program in the file lgpl21.txt
 *  If not, see <http://www.gnu.org/licenses/>.
 */

/*
 * This is a boilerplate which can be used to easily start an android app
 * using processing (from processing.org) and json network operations.
 */

package com.example.admin.processingboilerplate;

import android.annotation.TargetApi;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

import processing.core.PApplet;
import processing.core.PFont;

@TargetApi(11)
public class MainActivity extends AppCompatActivity {

    // defined as global static objects to prevent that they are computed again when device is turned
    public static JSONObject client_info = null;
    public static PFont font = null;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new Sketch();
        fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();

        // detect device type
        context = this.getApplicationContext();
    }

    public static boolean isConnectedWifi() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm == null ? null : cm.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected() && networkInfo.getType() == ConnectivityManager.TYPE_WIFI);
    }

    public static class Sketch extends PApplet {

        int num = 2000;
        int range = 24;
        int fontsize = 16;
        Buttons buttons;

        float[] ax = new float[num];
        float[] ay = new float[num];

        boolean dataView = false;

        @Override
        public void settings() {
            fullScreen();
            size(width, height, JAVA2D);
            for (String font: PFont.list()) Log.d("setup", "font = " + font);
            fontsize = Math.min(width, height) / 38; // computes to a font size of 20 for a 768 width
            if (font == null) font = createFont("DroidSansMono.ttf", fontsize * 4, true); // at a height of 20, this font has a width of 12
            // with this settings, we have exactly space for 64 characters on a horizontal-oriented phone
        }

        @Override
        public void setup() {
            for(int i = 0; i < num; i++) {
                ax[i] = width/2;
                ay[i] = height/2;
            }
            frameRate(200);
            buttons = new Buttons(this);
            Buttons.Button testButton = buttons.createButton();
            testButton
                    .setCenter(width / 2, 3 * height / 4)
                    .setWidth(fontsize * 9)
                    .setFontsize(fontsize)
                    .setOffText("PRESS", "TO", "START")
                    .setOnText("PRESS", "TO", "STOP")
                    .setBorderWidth(8)
                    .setBorderColor(128, 128, 128)
                    .setOnColor(32, 128, 64)
                    .setOffColor(64, 0, 128)
                    .setTextColor(255, 255, 255)
                    .setTransitionTime(300);
            Buttons.Button toggleVisibleButton = (Buttons.Button) testButton.clone();
            toggleVisibleButton
                    .setCenter(width / 3, 3 * height / 4)
                    .setWidth(fontsize * 7)
                    .setOnColor(0, 128, 0)
                    .setOffColor(0, 0, 0)
                    .setOffText("SHOW", "CENTER", "BUTTON")
                    .setOnText("HIDE", "CENTER", "BUTTON")
                    .setStatus(255);
            Buttons.Button toggleEnableButton = (Buttons.Button) testButton.clone();
            toggleEnableButton
                    .setCenter(2 * width / 3, 3 * height / 4)
                    .setWidth(fontsize * 7)
                    .setOnColor(0, 128, 0)
                    .setOffColor(0, 0, 0)
                    .setOffText("ENABLE", "CENTER", "BUTTON")
                    .setOnText("DISABLE", "CENTER", "BUTTON")
                    .setStatus(255);
            buttons.addButton("visible", toggleVisibleButton);
            buttons.addButton("enable", toggleEnableButton);
            buttons.addButton("test", testButton);
        }

        @Override
        public void draw() {

            // react on button status
            if (buttons.getStatus("visible") == 255) buttons.getButton("test").visible(); else buttons.getButton("test").invisible();
            if (buttons.getStatus("enable") == 255) buttons.getButton("test").enable(); else buttons.getButton("test").disable();

            // make a background
            translate(0, 0);
            background(0, 128, 64);

            // draw a headline

            textAlign(LEFT, TOP);
            textFont(font, fontsize * 2);
            fill(128, 255, 128);
            text("Processing / Network Boilerplate", 10, fontsize);

            // draw lines
            System.arraycopy(ax, 1, ax, 0, num - 1);
            System.arraycopy(ay, 1, ay, 0, num - 1);

            ax[num-1] = constrain(ax[num-1] + random(-range, range), 0, width);
            ay[num-1] = constrain(ay[num-1] + random(-range, range), 0, height);

            strokeWeight(3);
            for(int i=1; i<num; i++) {
                float val = ((float)i) / num * 204.0f + 51;
                stroke(val);
                if (abs(ax[i - 1] - ax[i]) <= range && abs(ay[i - 1] - ay[i]) <= range)
                    line(ax[i - 1], ay[i - 1], ax[i], ay[i]);
            }

            // at some time load data from the newtork
            if (frameCount == 3) {
                thread("loadData");
            }

            // if data was loaded, print it on the screen
            textFont(font, fontsize);
            int y = fontsize * 3;
            for (Map.Entry<Object, Object> entry : System.getProperties().entrySet()) {
                fill(128, 0, 0); text(entry.getKey().toString(), 10, y);
                fill(0, 0, 128); text(entry.getValue().toString(), 180, y);
                y += fontsize;
            }
            fill(128, 0, 0); text("fingerprint", 10, y); fill(0, 0, 128); text(Build.FINGERPRINT, 180, y); y += fontsize;
            fill(128, 0, 0); text("device", 10, y); fill(0, 0, 128); text(Build.DEVICE, 180, y); y += fontsize;
            fill(128, 0, 0); text("model", 10, y); fill(0, 0, 128); text(Build.MODEL, 180, y); y += fontsize;
            fill(128, 0, 0); text("product", 10, y); fill(0, 0, 128); text(Build.PRODUCT, 180, y); y += fontsize;
            fill(128, 0, 0); text("width", 10, y); fill(0, 0, 128); text(width, 180, y); y += fontsize;
            fill(128, 0, 0); text("height", 10, y); fill(0, 0, 128); text(height, 180, y); y += fontsize;
            fill(128, 0, 0); text("fontsize", 10, y); fill(0, 0, 128); text(fontsize, 180, y); y += fontsize;
            fill(128, 0, 0); text("fontwidth", 10, y); fill(0, 0, 128); text(textWidth('X'), 180, y); y += fontsize;
            if (client_info != null) {
                Iterator<String> i = client_info.keys();
                textSize(fontsize);
                while (i.hasNext()) try {
                    String key = i.next();
                    fill(128, 0, 0); text(key, 10, y);
                    fill(0, 0, 128); text(client_info.getString(key), 180, y);
                    y += fontsize;
                } catch (JSONException e) {
                    Log.e("drawData", e.getMessage(), e);
                }
            }

            textFont(font, fontsize);
            fill(255, 255, 255);
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm == null ? null : cm.getActiveNetworkInfo();
            if (networkInfo == null) {
                text("Network Info: no network info available", 10, height - 2 * fontsize);
            } else {
                text("Network Info: type = " + networkInfo.getType() + ", subtype = " + networkInfo.getSubtype() + (isConnectedWifi() ? ", wifi connected" : ""), 10, height - 2 * fontsize);
            }
            text("Frame rate: " + frameRate + ", button is " + (buttons.getStatus("test") == 255 ? "on" : "off"), 10, height - fontsize);
            // draw the buttons (always at last to make them visible at all cost)
            buttons.draw();
        }

        @Override
        public void mousePressed() {
            ax[num-1] = mouseX; ay[num-1] = mouseY;
            buttons.mousePressed(mouseX, mouseY);
        }

        @Override
        public void mouseDragged() {ax[num-1] = mouseX; ay[num-1] = mouseY;}

        public void loadData() {
            if (dataView) return;
            dataView = true;
            if (client_info != null) return;
            Log.d("loadData", "started");
            JSONObject json = JsonIO.loadJson("http://loklak.org/api/status.json");
            Log.d("loadData", "loaded, " + json.length() + " objects");
            if (json != null) try {
                client_info = json.getJSONObject("client_info");
            } catch (JSONException e) {
                Log.e("loadData", e.getMessage(), e);
            }

        }
    }

}
