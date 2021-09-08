package com.ablanco.zoomy;

import android.app.DialogFragment;

public class DialogFragmentContainer extends DialogContainer {

    DialogFragmentContainer(DialogFragment dialog) {
        super(dialog.getDialog());
    }
}
