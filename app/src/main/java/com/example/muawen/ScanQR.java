package com.example.muawen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ScanQR  extends AppCompatActivity implements ZXingScannerView.ResultHandler {


    ZXingScannerView ScannerViwe;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScannerViwe=new ZXingScannerView(this);
        setContentView(ScannerViwe);

    }

    @Override
    public void handleResult(Result result) {
        //Log.e(TAG, result.getText());
        //Log.e(TAG, result.getBarcodeFormat().toString());
        AddItem.ScanQRCode= result.getText();
        //AddItem.barcode.setText(result.getText());
        onBackPressed();
        ScannerViwe.resumeCameraPreview(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        // ScannerViwe.stopCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerViwe.setResultHandler(this);
        ScannerViwe.startCamera();
    }
}