package uk.co.peterandrew.ws2812patternsequencer;

import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

/**
 * Created by pete on 17/02/2018.
 */

public class TextViewDialogFragment extends DialogFragment {

    private TextView textViewContent;

    private String contentText = "";

    public void addText(String text) {
        contentText += "\n" + text;
        if (textViewContent != null) {
            textViewContent.setText(contentText);
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_textview, null);

        textViewContent = view.findViewById(R.id.textViewContent);
        textViewContent.setText(contentText);

        builder.setView(view)
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        Dialog dialog = builder.create();

        return dialog;
    }
}