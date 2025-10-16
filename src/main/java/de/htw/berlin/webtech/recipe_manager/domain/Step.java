package de.htw.berlin.webtech.recipe_manager.domain;

public class Step {

    private int index;
    private String text;

    protected Step() { }
    public Step(int index, String text) { this.index = index; this.text = text; }
    public int getIndex() { return index; }
    public String getText() { return text; }
}
