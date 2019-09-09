package com.zofers.zofers.staff;

import android.view.View;
import android.widget.EditText;

import com.google.android.material.textfield.TextInputLayout;

import java.util.HashSet;
import java.util.Set;

public class InputLayoutValidator implements View.OnFocusChangeListener {
    private Set<Block> blocks;

    public boolean validate(Block block) {
        boolean isValid = true;
        if (block.editText.getText().toString().trim().length() == 0) {
            block.inputLayout.setError(block.errorText);
            isValid = false;
        } else {
            block.inputLayout.setError("");
        }
        return isValid;
    }

    public boolean validateAll() {
        boolean isValid = true;
        for (Block b : blocks) {
            isValid = validate(b) && isValid; //isValid value stays false after once it false
        }
        return isValid;
    }

    private Block findBlock(EditText editText) {
        for (Block b : blocks) {
            if (b.editText.equals(editText)) {
                return b;
            }
        }
        return null;
    }

    public void add(EditText editText, TextInputLayout inputLayout, String error) {
        if (blocks == null) {
            blocks = new HashSet<>();
        }
        editText.setOnFocusChangeListener(this);
        blocks.add(new Block(editText, inputLayout, error));
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus && v instanceof EditText) {
            Block block = findBlock((EditText) v);
            if (block != null) {
                block.inputLayout.setError(null);
            }
        }
    }

    public static class Block {
        private EditText editText;
        private TextInputLayout inputLayout;
        private String errorText;

        public Block(EditText editText, TextInputLayout inputLayout, String errorText) {
            this.editText = editText;
            this.inputLayout = inputLayout;
            this.errorText = errorText;
        }
    }
}