package com.ablanco.zoomy;

import android.app.Dialog;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

public class DialogContainer implements TargetContainer {

    private final Dialog mDialog;

    DialogContainer(Dialog dialog) {
        this.mDialog = dialog;
    }

    @Nullable
    @Override
    public final ViewGroup getDecorView() {
        return mDialog.getWindow() != null ? (ViewGroup) mDialog.getWindow().getDecorView() : null;
    }
}
