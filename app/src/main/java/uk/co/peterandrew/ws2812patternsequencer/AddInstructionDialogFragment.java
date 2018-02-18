package uk.co.peterandrew.ws2812patternsequencer;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;


public class AddInstructionDialogFragment extends DialogFragment {

    private int selectedInstruction = 0;

    private RadioGroup radioGroupInstructions;

    private Button okButton;

    private TextView textViewInstruction;

    private ConstraintLayout wrapperLedColour;
    private LedColourView ledColour;

    private ConstraintLayout wrapperLedNumber;
    private EditText editTextLedNumber;

    private ConstraintLayout wrapperRed;
    private EditText editTextRed;
    private SeekBar seekBarRed;

    private ConstraintLayout wrapperGreen;
    private EditText editTextGreen;
    private SeekBar seekBarGreen;

    private ConstraintLayout wrapperBlue;
    private EditText editTextBlue;
    private SeekBar seekBarBlue;

    private ConstraintLayout wrapperDelay;
    private EditText editTextDelay;

    public interface AddInstructionDialogListener {
        void onAddInstruction(Instruction instruction);
    }

    private void updateLedColourChip() {
        Integer red = 0;
        try {
            red = Integer.valueOf(editTextRed.getText().toString());
        } catch (NumberFormatException e) {}

        Integer green = 0;
        try {
            green = Integer.valueOf(editTextGreen.getText().toString());
        } catch (NumberFormatException e) {}

        Integer blue = 0;
        try {
            blue = Integer.valueOf(editTextBlue.getText().toString());
        } catch (NumberFormatException e) {}

        ledColour.setColor(red, green, blue);
        ledColour.invalidate();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_add_instruction, null);

        textViewInstruction = view.findViewById(R.id.textViewInstruction);

        wrapperLedColour = view.findViewById(R.id.wrapperLedColour);
        ledColour = view.findViewById(R.id.ledColour);

        wrapperLedNumber = view.findViewById(R.id.wrapperLedNumber);
        editTextLedNumber = view.findViewById(R.id.txtLedNumber);

        wrapperRed = view.findViewById(R.id.wrapperRed);
        editTextRed = view.findViewById(R.id.txtRed);
        seekBarRed = view.findViewById(R.id.seekBarRed);

        wrapperGreen = view.findViewById(R.id.wrapperGreen);
        editTextGreen = view.findViewById(R.id.txtGreen);
        seekBarGreen = view.findViewById(R.id.seekBarGreen);

        wrapperBlue = view.findViewById(R.id.wrapperBlue);
        editTextBlue = view.findViewById(R.id.txtBlue);
        seekBarBlue = view.findViewById(R.id.seekBarBlue);

        wrapperDelay = view.findViewById(R.id.wrapperDelay);
        editTextDelay = view.findViewById(R.id.txtDelay);

        radioGroupInstructions = view.findViewById(R.id.radioGroupInstructions);
        radioGroupInstructions.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radioButInsClear:
                        selectedInstruction = 1;
                        textViewInstruction.setText("Instruction: Clear all");
                        break;
                    case R.id.radioButInsSetSingleValue:
                        selectedInstruction = 2;
                        textViewInstruction.setText("Instruction: Set single value");
                        wrapperLedColour.setVisibility(View.VISIBLE);
                        wrapperLedNumber.setVisibility(View.VISIBLE);
                        wrapperRed.setVisibility(View.VISIBLE);
                        wrapperGreen.setVisibility(View.VISIBLE);
                        wrapperBlue.setVisibility(View.VISIBLE);
                        updateLedColourChip();
                        break;
                    case R.id.radioButInsDelay:
                        selectedInstruction = 3;
                        textViewInstruction.setText("Instruction: Delay");
                        wrapperDelay.setVisibility(View.VISIBLE);
                        break;
                }

                radioGroupInstructions.setVisibility(View.GONE);
                textViewInstruction.setVisibility(View.VISIBLE);

                okButton.setEnabled(true);
            }
        });

        editTextLedNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Integer ledNumber = 0;
                try {
                    ledNumber = Integer.valueOf(s.toString());
                } catch (NumberFormatException e) {}
                if (ledNumber > 63) {
                    ledNumber = 63;
                    editTextLedNumber.setText(ledNumber.toString());
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        editTextRed.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Integer red = 0;
                try {
                    red = Integer.valueOf(s.toString());
                } catch (NumberFormatException e) {}
                if (red > 255) {
                    red = 255;
                    editTextRed.setText(red.toString());
                }

                seekBarRed.setProgress(red);

                updateLedColourChip();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        seekBarRed.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editTextRed.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editTextGreen.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Integer green = 0;
                try {
                    green = Integer.valueOf(s.toString());
                } catch (NumberFormatException e) {}
                if (green > 255) {
                    green = 255;
                    editTextGreen.setText(green.toString());
                }

                seekBarGreen.setProgress(green);

                updateLedColourChip();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        seekBarGreen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editTextGreen.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        editTextBlue.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                Integer blue = 0;
                try {
                    blue = Integer.valueOf(s.toString());
                } catch (NumberFormatException e) {}
                if (blue > 255) {
                    blue = 255;
                    editTextBlue.setText(blue.toString());
                }

                seekBarBlue.setProgress(blue);

                updateLedColourChip();
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
        });

        seekBarBlue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    editTextBlue.setText(String.valueOf(progress));
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        builder.setView(view)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Instruction instruction;
                        switch (selectedInstruction) {
                            case 1:
                            default:
                                instruction = new ClearInstruction();
                                break;

                            case 2:
                                Integer ledNumber = 0;
                                try {
                                    ledNumber = Integer.valueOf(editTextLedNumber.getText().toString());
                                } catch (NumberFormatException e) {}

                                Integer red = 0;
                                try {
                                    red = Integer.valueOf(editTextRed.getText().toString());
                                } catch (NumberFormatException e) {}

                                Integer green = 0;
                                try {
                                    green = Integer.valueOf(editTextGreen.getText().toString());
                                } catch (NumberFormatException e) {}

                                Integer blue = 0;
                                try {
                                    blue = Integer.valueOf(editTextBlue.getText().toString());
                                } catch (NumberFormatException e) {}

                                instruction = new SetValueInstruction(
                                        ledNumber.byteValue(),
                                        red.byteValue(),
                                        green.byteValue(),
                                        blue.byteValue());

                                break;

                            case 3:
                                Integer delay = 0;
                                try {
                                    delay = Integer.valueOf(editTextDelay.getText().toString());
                                } catch (NumberFormatException e) {}

                                instruction = new DelayInstruction(delay.byteValue());

                                break;
                        }

                        AddInstructionDialogListener listener = (AddInstructionDialogListener) getActivity();
                        listener.onAddInstruction(instruction);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) { }
                });

        Dialog dialog = builder.create();

        dialog.setOnShowListener(new DialogInterface.OnShowListener()
        {
            @Override
            public void onShow(DialogInterface dialog)
            {
                okButton = ((AlertDialog)dialog).getButton(AlertDialog.BUTTON_POSITIVE);
                okButton.setEnabled(false);
            }
        });

        return dialog;
    }

}