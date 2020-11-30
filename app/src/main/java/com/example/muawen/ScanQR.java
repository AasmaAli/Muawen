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
        AddItem.ScanQRCode= result.getText();
        onBackPressed();
        ScannerViwe.resumeCameraPreview(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
   }

    @Override
    protected void onResume() {
        super.onResume();
        ScannerViwe.setResultHandler(this);
        ScannerViwe.startCamera();
    }
}