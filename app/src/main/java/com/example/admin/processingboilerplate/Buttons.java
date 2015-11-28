package com.example.admin.processingboilerplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import processing.core.PApplet;

public class Buttons {

    private PApplet sketch;
    private Map<String, Button> buttons;

    public Buttons(PApplet sketch) {
        this.sketch = sketch;
        this.buttons = new HashMap<String, Button>();
    }

    public Button createButton() {
        return new Button(this.sketch);
    }

    public void addButton(String name, Button button) {
        this.buttons.put(name, button);
    }

    public int getStatus(String name) {
        Button button = this.buttons.get(name);
        return button == null ? -1 : button.getStatus();
    }

    public void draw() {
        for (Button button: this.buttons.values()) button.draw();
    }

    public void mousePressed(int x, int y) {
        for (Button button: this.buttons.values()) button.mousePressed(x, y);
    }

    public static class Button {
        private PApplet sketch;
        int x, y, g, fontsize = 24, r = 5 * fontsize;
        int borderWidth = 1;
        int col_r_text = 0, col_g_text  = 0, col_b_text = 0;
        int col_r_border = 0, col_g_border = 0, col_b_border = 0;
        int col_r_on = 0, col_g_on = 255, col_b_on = 0;
        int col_r_off = 255, col_g_off = 0, col_b_off = 0;
        String offtext0 = "", offtext1 = "SWITCH ON", offtext2 = "";
        String ontext0 = "", ontext1 = "SWITCH OFF", ontext2 = "";
        int status, delta;

        private Button(PApplet sketch) {
            this.sketch = sketch;
            this.status = 0; this.delta = 0;
        }

        public Object clone() {
            Button b = new Button(this.sketch);
            b.x = this.x; b.y = this.y; b.g = this.g; b.fontsize = this.fontsize; b.r = this.r;
            b.borderWidth = this.borderWidth;
            b.col_r_text = this.col_r_text; b.col_g_text = this.col_g_text; b.col_b_text = this.col_b_text;
            b.col_r_border = this.col_r_border; b.col_g_border = this.col_g_border; b.col_b_border = this.col_b_border;
            b.col_r_on = this.col_r_on; b.col_g_on = this.col_g_on; b.col_b_on = this.col_b_on;
            b.col_r_off = this.col_r_off; b.col_g_off = this.col_g_off; b.col_b_off = this.col_b_off;
            b.ontext0 = this.ontext0; b.ontext1 = this.ontext1; b.ontext2 = this.ontext2;
            b.offtext0 = this.offtext0; b.offtext1 = this.offtext1; b.offtext2 = this.offtext2;
            return b;
        }

        public Button setCenter(int x, int y) {
            this.x = x; this.y = y;
            return this;
        }

        public Button setRadius(int r) {
            this.r = r;
            return this;
        }

        public Button setSpeed(int speed) {
            this.g = speed;
            return this;
        }

        public Button setBorderWidth(int borderWidth) {
            this.borderWidth = borderWidth;
            return this;
        }

        public Button setOnColor(int r, int g, int b) {
            this.col_r_on = r; this.col_g_on = g; this.col_b_on = b;
            return this;
        }

        public Button setOffColor(int r, int g, int b) {
            this.col_r_off = r; this.col_g_off = g; this.col_b_off = b;
            return this;
        }

        public Button setBorderColor(int r, int g, int b) {
            this.col_r_border = r; this.col_g_border = g; this.col_b_border = b;
            return this;
        }

        public Button setTextColor(int r, int g, int b) {
            this.col_r_text = r; this.col_g_text = g; this.col_b_text = b;
            return this;
        }

        public Button setOnText(String text0, String text1, String text2) {
            this.ontext0 = text0; this.ontext1 = text1; this.ontext2 = text2;
            return this;
        }

        public Button setOffText(String text0, String text1, String text2) {
            this.offtext0 = text0;; this.offtext1 = text1; this.offtext2 = text2;
            return this;
        }

        public Button setFontsize(int fontsize) {
            this.fontsize = fontsize;
            return this;
        }

        public void draw() {
            // change delta
            if (this.delta != 0) {
                this.status = Math.max(0, Math.min(255, this.status + this.delta * g));
                if (this.status == 0 || this.status == 255) this.delta = 0;
            }
            // draw button
            if (borderWidth == 0) sketch.noStroke(); else {
                sketch.stroke(col_r_border, col_g_border, col_b_border);
                sketch.strokeWeight(borderWidth);
            }
            if (this.delta == 0) {
                if (this.status == 0) {
                    sketch.fill(col_r_off, col_g_off, col_b_off);
                } else if (this.status == 255) {
                    sketch.fill(col_r_on, col_g_on, col_b_on);
                }
                sketch.arc(x, y, r, r, 0, sketch.TWO_PI);
            } else {
                sketch.fill(col_r_off, col_g_off, col_b_off);
                sketch.arc(x, y, r, r, 0, sketch.TWO_PI);
                sketch.noStroke();
                sketch.fill(col_r_on, col_g_on, col_b_on);
                sketch.arc(x, y, r * status / 255, r * status / 255, 0, sketch.TWO_PI);
            }
            // draw text lines
            if (this.delta == 0) {
                sketch.fill(col_r_text, col_g_text, col_b_text);
                sketch.textSize(fontsize);
                sketch.textAlign(sketch.CENTER, sketch.CENTER);
                if (this.status == 0) {
                    sketch.text(offtext0, x, y - fontsize);
                    sketch.text(offtext1, x, y);
                    sketch.text(offtext2, x, y + fontsize);
                } else if (this.status == 255) {
                    sketch.text(ontext0, x, y - fontsize);
                    sketch.text(ontext1, x, y);
                    sketch.text(ontext2, x, y + fontsize);
                }
            }

        }

        public void setStatus(int status, int delta) {
            this.status = status; this.delta = delta;
        }

        /**
         * Get the button status. The status is a number from 0..255 where 0 means off and 255 means on.
         * There can be numbers in between which means that the button is currently is changing its status
         * @return the status
         */
        public int getStatus() {
            return this.status;
        }

        public boolean inside(int x, int y) {
            // compute the distance from center
            int xd = this.x - x;
            int yd = this.y - y;
            int distance = (int) Math.sqrt(xd * xd + yd * yd); // pythagoras!
            return distance < this.r;
        }

        public void mousePressed(int x, int y) {
            if (inside(x, y)) {
                if (this.delta == 0) this.delta = this.status == 0 ? 1 : -1; else this.delta = -this.delta;
            }
        }

    }

}
