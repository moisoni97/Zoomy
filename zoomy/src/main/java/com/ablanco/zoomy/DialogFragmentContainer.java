package com.ablanco.zoomy;

import androidx.fragment.app.DialogFragment;

import androidx.annotation.NonNull;

public class DialogFragmentContainer extends DialogContainer {

    DialogFragmentContainer(@NonNull DialogFragment dialog) {
        super(dialog.requireDialog());
    }
}
