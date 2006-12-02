package com.luntsys.luntbuild.luntclipse.wizards;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;

/**
 * Info of RadioGroup (radio button, text)
 *
 * @author lubosp
 *
 */
public class RadioGroupInfo {
    /** buttons */
    public Button[] buttons;
    /** text fields */
    public Text[] fields;

    /** Create RadioGroupInfo
     * @param dim dimension
     */
    public RadioGroupInfo(int dim) {
        this.buttons = new Button[dim];
        this.fields = new Text[dim];
    }

    /**
     * @return index of selected item
     */
    public int getSelectionIndex() {
        for (int i = 0; i < this.buttons.length; i++) {
            Button bt = this.buttons[i];
            if (bt.getSelection()) {
                return i;
            }
        }
        return -1;
    }

    /** Select which button
     * @param which button to select
     */
    public void select(int which) {
        if (which < 0 || which >= this.buttons.length) return;
        for (int i = 0; i < this.buttons.length; i++) {
            Button bt = this.buttons[i];
            bt.setSelection(i == which);
        }
    }

    /**
     * @return text of selected button or empty string
     */
    public String getSelectionData() {
        for (int i = 0; i < this.buttons.length; i++) {
            Button bt = this.buttons[i];
            if (bt.getSelection()) {
                if (this.fields[i] != null) return this.fields[i].getText().trim();
            }
        }
        return "";
    }

    /** Set text field for selected button
     * @param value text to set
     */
    public void setSelectionData(String value) {
        for (int i = 0; i < this.buttons.length; i++) {
            Button bt = this.buttons[i];
            if (bt.getSelection()) {
                if (this.fields[i] != null) {
                    this.fields[i].setText(value);
                    return;
                }
            }
        }
    }

    /**
     * Deselect all buttons
     */
    public void deselectAll() {
        for (int i = 0; i < this.buttons.length; i++) {
            Button bt = this.buttons[i];
            bt.setSelection(false);
        }
    }

    /**
     * Clear text fields
     */
    public void clearSelectionData() {
        for (int i = 0; i < this.buttons.length; i++) {
            if (this.fields[i] != null) this.fields[i].setText("");
        }
    }
}