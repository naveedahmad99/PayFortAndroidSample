package com.android.payfortandroidsample;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.payfort.fort.android.sdk.base.callbacks.FortCallBackManager;
import com.payfort.fort.android.sdk.base.callbacks.FortCallback;


public class MainActivity extends AppCompatActivity implements IPaymentRequestCallBack, View.OnClickListener {

    private TextView tvPurchage;
    private EditText etAmount;

    public FortCallBackManager fortCallback = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initilizePayFortSDK();
        setReferences();
        setListeneres();
    }

    private void initilizePayFortSDK() {
        fortCallback = FortCallback.Factory.create();
    }

    private void setReferences() {
        etAmount = (EditText) findViewById(R.id.etAmount);
        tvPurchage = (TextView) findViewById(R.id.tvPurchage);
    }

    private void setListeneres() {
        tvPurchage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvPurchage:
                if(validated())
                    requestForPayfortPayment();
                break;
        }
    }

    private boolean validated() {
        if(PayFortPayment.MERCHANT_IDENTIFIER.equals("")){
            showToast(MainActivity.this,"Please add MERCHANT_IDENTIFIER in class: "+ PayFortPayment.class.getSimpleName());
            return false;
        }else if(PayFortPayment.ACCESS_CODE.equals("")){
            showToast(MainActivity.this,"Please add ACCESS_CODE in class: "+ PayFortPayment.class.getSimpleName());
            return false;
        }else if(PayFortPayment.SHA_RESPONSE_PHRASE.equals("")){
            showToast(MainActivity.this,"Please add SHA_RESPONSE_PHRASE in class: "+ PayFortPayment.class.getSimpleName());
            return false;
        }else if(PayFortPayment.SHA_RESPONSE_PHRASE.equals("")){
            showToast(MainActivity.this,"Please add SHA_RESPONSE_PHRASE in class: "+ PayFortPayment.class.getSimpleName());
            return false;
        }else if(isNetworkAvailable(MainActivity.this)){
            showToast(MainActivity.this,"Please Check your Internet Connection");
            return false;
        }
        return true;
    }

    private void showToast(Context context, String message) {
        Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PayFortPayment.RESPONSE_PURCHASE) {
            fortCallback.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void requestForPayfortPayment() {
        PayFortData payFortData = new PayFortData();
        if (!TextUtils.isEmpty(etAmount.getText().toString())) {
            payFortData.amount = String.valueOf((int) (Float.parseFloat(etAmount.getText().toString()) * 100));// Multiplying with 100, bcz amount should not be in decimal format
            payFortData.command = PayFortPayment.PURCHASE;
            payFortData.currency = PayFortPayment.CURRENCY_TYPE;
            payFortData.customerEmail = "readyandroid@gmail.com";
            payFortData.language = PayFortPayment.LANGUAGE_TYPE;
            payFortData.merchantReference = String.valueOf(System.currentTimeMillis());

            PayFortPayment payFortPayment = new PayFortPayment(this, this.fortCallback, this);
            payFortPayment.requestForPayment(payFortData);
        }
    }

    @Override
    public void onPaymentRequestResponse(int responseType, final PayFortData responseData) {
        if (responseType == PayFortPayment.RESPONSE_GET_TOKEN) {
            Toast.makeText(this, "Token not generated", Toast.LENGTH_SHORT).show();
            Log.e("onPaymentResponse", "Token not generated");
        } else if (responseType == PayFortPayment.RESPONSE_PURCHASE_CANCEL) {
            Toast.makeText(this, "Payment cancelled", Toast.LENGTH_SHORT).show();
            Log.e("onPaymentResponse", "Payment cancelled");
        } else if (responseType == PayFortPayment.RESPONSE_PURCHASE_FAILURE) {
            Toast.makeText(this, "Payment failed", Toast.LENGTH_SHORT).show();
            Log.e("onPaymentResponse", "Payment failed");
        } else {
            Toast.makeText(this, "Payment successful", Toast.LENGTH_SHORT).show();
            Log.e("onPaymentResponse", "Payment successful");
        }
    }

    public static Boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            return true;
        }
        return false;
    }

}