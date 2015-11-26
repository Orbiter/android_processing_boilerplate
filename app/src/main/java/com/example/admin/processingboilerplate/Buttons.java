package com.example.admin.processingboilerplate;

import java.util.ArrayList;
import java.util.List;

import processing.core.PApplet;

public class Buttons {

    private PApplet sketch;
    private List<Button> buttons;

    public Buttons(PApplet sketch) {
        this.sketch = sketch;
        this.buttons = new ArrayList<Button>();
    }

    public Button addButton(int x, int y, int w, int h, String name) {
        Button button = new Button(x, y, w, h, name);
        this.buttons.add(button);
        return button;
    }

    public String getButton(int x, int y) {
        for (Button button: buttons) {
            if (button.inside(x, y)) return button.name;
        }
        return null;
    }

    public class Button {
        int x, y, w, h;
        String name;
        public Button(int x, int y, int w, int h, String name) {
            this.x = x; this.y = y; this.w = w; this.h = h; this.name = name;
        }

        public boolean inside(int x, int y) {
            return this.x <= x && this.x + this.w > x && this.y < y && this.y + this.h > y;
        }

    }

}
