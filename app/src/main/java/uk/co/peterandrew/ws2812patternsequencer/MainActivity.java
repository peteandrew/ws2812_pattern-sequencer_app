package uk.co.peterandrew.ws2812patternsequencer;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements AddInstructionDialogFragment.AddInstructionDialogListener {

    private Map<String, BluetoothDevice> mScanResults;

    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private ScanCallback mScanCallback;
    private BluetoothGatt mGatt;

    private TextViewDialogFragment dialogTextView;

    private List<Instruction> instructions = new ArrayList<>();
    private InstructionListAdapter instructionsListAdapter;

    private boolean btReady = false;

    private static final int INSTRUCTIONS_BUFFER_SIZE=2500;

    private static final int REQUEST_ENABLE_BT=1;
    private static final int REQUEST_FINE_LOCATION=2;

    private static final String SERVICE_STRING = "00001110-0000-1000-8000-00805F9B34FB";
    private static final String CHARACTERISTIC_STRING = "00000001-0000-1000-8000-00805F9B34FB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        Instruction newInstruction = new ClearInstruction();
        instructions.add(newInstruction);

        ListView instructionList = (ListView) findViewById(R.id.listView3);
        instructionsListAdapter = new InstructionListAdapter(this, instructions);
        instructionList.setAdapter(instructionsListAdapter);

        updateStatusField();
    }

    private void initDeviceConnect() {
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            dialogTextView.addText("Device does not support Bluetooth LE");
            return;
        }

        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (mBluetoothAdapter == null || !mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            startScan();
        } else {
            dialogTextView.addText("Requesting ACCESS_FINE_LOCATION permission");
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_FINE_LOCATION);
        }
    }

    public void onAddInstruction(Instruction instruction) {
        instructionsListAdapter.add(instruction);
        updateStatusField();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_add_instruction:
                DialogFragment dialog = new AddInstructionDialogFragment();
                dialog.show(getSupportFragmentManager(), "addInstructionDialog");
                return true;

            case R.id.action_clear_instructions:
                instructionsListAdapter.clear();
                instructionsListAdapter.add(new ClearInstruction());
                updateStatusField();
                return true;

            case R.id.action_connect:
                dialogTextView = new TextViewDialogFragment();
                dialogTextView.show(getSupportFragmentManager(), "connectDialog");
                initDeviceConnect();
                return true;

            case R.id.action_send:
                dialogTextView = new TextViewDialogFragment();
                dialogTextView.show(getSupportFragmentManager(), "sendDialog");
                sendInstructions();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateStatusField() {
        String instructionStatus = String.valueOf(instructions.size()) + " instructions";

        int numBytes = 0;
        for (Instruction instruction : instructions) {
            numBytes += instruction.numBytes();
        }
        instructionStatus += " (" + String.valueOf(numBytes) + " bytes used)";

        int bytesRemaining = INSTRUCTIONS_BUFFER_SIZE - numBytes;
        instructionStatus += "\n" + String.valueOf(bytesRemaining) + " bytes remaining";

        TextView statusField = (TextView) findViewById(R.id.textView3);
        statusField.setText(instructionStatus);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        if (requestCode == REQUEST_FINE_LOCATION) {
            dialogTextView.addText("Received response for location permission request.");

            // Check if the only required permission has been granted
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dialogTextView.addText("ACCESS_FINE_LOCATION permission has now been granted.");
                startScan();
            } else {
                dialogTextView.addText("ACCESS_FINE_LOCATION permission was NOT granted.");
            }

        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startScan() {
        disconnectGattServer();

        mScanResults = new HashMap<>();
        mScanCallback = new BtleScanCallback(mScanResults);

        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();

        mBluetoothLeScanner.startScan(mScanCallback);

        Handler mHandler = new Handler();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mBluetoothLeScanner.stopScan(mScanCallback);
                dialogTextView.addText("Stop scan");

                scanComplete();
            }
        }, 5000);

        dialogTextView.addText("Start scan");
    }

    private void scanComplete() {
        if (mScanResults.isEmpty()) {
            return;
        }

        for (String deviceName : mScanResults.keySet()) {
            if (deviceName == null) {
                continue;
            }
            if (deviceName.startsWith("Custom BLE")) {
                dialogTextView.addText("Device found");

                BluetoothDevice device = mScanResults.get(deviceName);
                connectDevice(device);
            }
        }
    }

    public void disconnectGattServer() {
        if (mGatt != null) {
            dialogTextView.addText("Closing Gatt connection");
            mGatt.disconnect();
            mGatt.close();
        }
    }


    private void connectDevice(BluetoothDevice device) {
        GattClientCallback gattClientCallback = new GattClientCallback();
        mGatt = device.connectGatt(this, false, gattClientCallback);
    }


    private void sendVals(byte[] vals) {
        while (!btReady) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {}
        }

        String text = "";
        for (byte val : vals) {
            text += Byte.toUnsignedInt(val);
        }
        dialogTextView.addText(text);

        writeCustomCharacteristic(vals);
    }


    private void sendInstructions() {
        if (mBluetoothAdapter == null || mGatt == null) {
            dialogTextView.addText("BluetoothAdapter not initialized");
            return;
        }

        byte[] vals = new byte[5];

        // Clear current instructions
        vals[0] = (byte)1;
        sendVals(vals);

        // Enter command entry mode
        vals[0] = (byte)4;
        sendVals(vals);

        // Send intructions
        for (Instruction instruction : instructions) {
            vals[0] = instruction.instructionByte();

            for (int i=1; i < instruction.numBytes(); i++) {
                vals[i] = instruction.argumentsBytes().get(i-1);
            }

            sendVals(vals);
        }

        // Exit command entry mode
        vals[0] = (byte)0;
        sendVals(vals);

        // Run
        vals[0] = (byte)2;
        sendVals(vals);
    }


    private void writeCustomCharacteristic(byte[] value) {

        BluetoothGattService mCustomService = mGatt.getService(UUID.fromString(SERVICE_STRING));
        if (mCustomService == null){
            dialogTextView.addText("Custom BLE Service not found");
            return;
        }

        BluetoothGattCharacteristic mWriteCharacteristic = mCustomService.getCharacteristic(UUID.fromString(CHARACTERISTIC_STRING));
        mWriteCharacteristic.setValue(value);
        btReady = false;
        if (mGatt.writeCharacteristic(mWriteCharacteristic) == false){
            dialogTextView.addText("Failed to write characteristic");
        }
    }


    private class BtleScanCallback extends ScanCallback {

        private Map<String, BluetoothDevice> mScanResults;

        BtleScanCallback(Map<String, BluetoothDevice> scanResults) {
            mScanResults = scanResults;
        }

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            addScanResult(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                addScanResult(result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            dialogTextView.addText("BLE Scan Failed with code " + errorCode);
        }

        private void addScanResult(ScanResult result) {
            BluetoothDevice device = result.getDevice();
            String deviceName = device.getName();
            String deviceAddress = device.getAddress();

            dialogTextView.addText(deviceName + " " + deviceAddress);

            mScanResults.put(deviceName, device);
        }
    }


    private class GattClientCallback extends BluetoothGattCallback {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            super.onConnectionStateChange(gatt, status, newState);
//            dialogTextView.addText("onConnectionStateChange newState: " + newState);

            if (status == BluetoothGatt.GATT_FAILURE) {
//                dialogTextView.addText("Connection Gatt failure status " + status);
                disconnectGattServer();
                return;
            } else if (status != BluetoothGatt.GATT_SUCCESS) {
                // handle anything not SUCCESS as failure
//                dialogTextView.addText("Connection not GATT sucess status " + status);
                disconnectGattServer();
                return;
            }

            if (newState == BluetoothProfile.STATE_CONNECTED) {
//                dialogTextView.addText("Connected to device " + gatt.getDevice().getAddress());
                gatt.discoverServices();
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
//                dialogTextView.addText("Disconnected from device");
                disconnectGattServer();
            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            if (status != BluetoothGatt.GATT_SUCCESS) {
//                dialogTextView.addText("Device service discovery unsuccessful, status " + status);
                return;
            }

            btReady = true;
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                btReady = true;
//                dialogTextView.addText("Characteristic written successfully");
            } else {
//                dialogTextView.addText("Characteristic write unsuccessful, status: " + status);
            }
        }

    }


    private class InstructionListAdapter extends ArrayAdapter<Instruction> {
        private Context mContext;
        private List<Instruction> mInstructions = new ArrayList<>();

        public InstructionListAdapter(Context context, List<Instruction> instructions) {
            super(context, 0, instructions);

            this.mContext = context;
            this.mInstructions = instructions;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View listItem = view;

            if (listItem == null) {
                LayoutInflater inflater = MainActivity.this.getLayoutInflater();
                listItem = inflater.inflate(R.layout.listitem_instruction, null);
            }

            Instruction instruction = instructions.get(i);

            TextView instructionNumberTextView = listItem.findViewById(R.id.instructionNumberTextView);
            instructionNumberTextView.setText(String.valueOf(i+1));

            TextView instructionTextView = listItem.findViewById(R.id.instructionTextView);
            instructionTextView.setText(instruction.instructionName());

            TextView argumentsTextView = listItem.findViewById(R.id.argumentsTextView);
            argumentsTextView.setText(instruction.argumentsToString());

            return listItem;
        }
    }
}