package com.groobak.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.constants.TopUpType;
import com.groobak.customer.json.GetAyoPulsaBaseResponse;
import com.groobak.customer.json.TopUpAyoPulsaRequestJson;
import com.groobak.customer.json.TopUpAyoPulsaResponseModel;
import com.groobak.customer.json.fcm.FCMMessage;
import com.groobak.customer.models.Notif;
import com.groobak.customer.models.User;
import com.groobak.customer.models.ayopulsa.PriceListPulsaGeneralModel;
import com.groobak.customer.models.ayopulsa.PriceListDetailModel;
import com.groobak.customer.utils.ProjectUtils;
import com.groobak.customer.utils.SettingPreference;
import com.groobak.customer.utils.Utility;
import com.groobak.customer.utils.api.AyoPulsaApiHelper;
import com.groobak.customer.utils.api.FCMHelper;
import com.groobak.customer.utils.api.MobilePulsaApiHelper;
import com.groobak.customer.utils.api.service.ApiListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpPulsaActivity extends AppCompatActivity {
    EditText phoneNumber, productTitle;
    TextView tvOperatorName, notif;
    Button submit;
    RelativeLayout rlProgress,rlnotif;
    PriceListDetailModel topUpDetailModel;
    SettingPreference sp;
    public static final int ACTIVITY_RESULT = 0x123;
    String disableBack;

    Spinner spinnerDataType;
    TopUpType topUpType = TopUpType.pulsa;
    List<String> dataType = new ArrayList<>(Arrays.asList("Pulsa", "Paket Data"));
    Map<String, PriceListPulsaGeneralModel> priceListMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_up_pulsa);
        phoneNumber = findViewById(R.id.etPhoneNumber);
        submit= findViewById(R.id.submit);
        rlnotif = findViewById(R.id.rlnotif);
        notif = findViewById(R.id.textnotif);
        rlProgress= findViewById(R.id.rlprogress);
        tvOperatorName = findViewById(R.id.tvOperator);
        productTitle = findViewById(R.id.etProduct);
        sp = new SettingPreference(this);
        spinnerDataType = findViewById(R.id.spinnerPulsaType);

        initSpinner();
        getAyoPulsaData();

        phoneNumber.addTextChangedListener(Utility.listenOperatorName(phoneNumber, TopUpPulsaActivity.this,
                tvOperatorName));

        productTitle.setOnClickListener(v -> {
            if (tvOperatorName.getText().toString().isEmpty()) return;
            String operatorName = tvOperatorName.getText().toString();
            Intent i = new Intent(this, OperatorProviderListActivity.class);
            i.putExtra(Constants.OPERATOR, operatorName);
            if (priceListMap != null) {
                if (priceListMap.containsKey(operatorName.toLowerCase())) {
                    i.putExtra("data", topUpType == TopUpType.data ? Objects.requireNonNull(priceListMap.get(operatorName.toLowerCase())).getPaket() :
                            Objects.requireNonNull(priceListMap.get(operatorName.toLowerCase())).getPulsa());
                }
            }
            startActivityForResult(i, ACTIVITY_RESULT);
        });

        submit.setOnClickListener(v -> {
            User userLogin = BaseApp.getInstance(TopUpPulsaActivity.this).getLoginUser();

            if (topUpDetailModel == null) {
                Toast.makeText(this, "Please pick product first", Toast.LENGTH_LONG).show();
            } else if(userLogin.getWalletSaldo() < topUpDetailModel.getPrice()) {
                Toast.makeText(this, "Your wallet is not enough. Please top up first", Toast.LENGTH_LONG).show();
            } else if (!ProjectUtils.isPhoneNumberValid(phoneNumber.getText().toString())) {
                Toast.makeText(this, "Phone Number is not valid", Toast.LENGTH_LONG).show();
            } else {
                requestTopUp();
            }
        });
    }

    private void getAyoPulsaData() {
        progressShow();
        AyoPulsaApiHelper.getInstance().getAyoPesanPulsaPriceList().enqueue(new Callback<GetAyoPulsaBaseResponse>() {
            @Override
            public void onResponse(@NonNull Call<GetAyoPulsaBaseResponse> call, @NonNull Response<GetAyoPulsaBaseResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    for (int i = 0; i < response.body().getDataX().size(); i++) {
                        priceListMap.put(response.body().getDataX().get(i).getOperatorName().toLowerCase(), response.body().getDataX().get(i));
                    }
                }
                progressHide();
            }

            @Override
            public void onFailure(@NonNull Call<GetAyoPulsaBaseResponse> call, @NonNull Throwable t) {
                progressHide();
            }
        });
    }

    private void initSpinner() {
        ArrayAdapter<String> nominalListAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, dataType);
        nominalListAdapter.setDropDownViewResource(R.layout.dialog_spinner_dropdown_item);
        spinnerDataType.setAdapter(nominalListAdapter);
        spinnerDataType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                topUpType = position == 1 ? TopUpType.data : TopUpType.pulsa;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void progressShow() {
        rlProgress.setVisibility(View.VISIBLE);
        disableBack = "true";
    }

    private void progressHide() {
        rlProgress.setVisibility(View.GONE);
        disableBack = "false";
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        notif.setText(text);

        new Handler().postDelayed(() -> rlnotif.setVisibility(View.GONE), 3000);
    }

    private void trackTopUp() {
        int totalAmount = topUpDetailModel.getPrice() +Integer.parseInt(sp.getSetting()[12]);
        MobilePulsaApiHelper.trackUserTopUp(String.valueOf(totalAmount), tvOperatorName.getText().toString(), phoneNumber.getText().toString(),
                this, sp, new ApiListener() {
                    @Override
                    public void onSuccess() {
                        progressHide();
                        final User user = BaseApp.getInstance(TopUpPulsaActivity.this).getLoginUser();
                        Notif notif = new Notif();
                        notif.title = "Topup";
                        notif.message = "Permintaan pengisian telah berhasil, kami akan mengirimkan pemberitahuan setelah kami mengkonfirmasi";
                        sendNotif(user.getToken(), notif);
                        new Handler().postDelayed(() -> {
                            Intent intent = new Intent(TopUpPulsaActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }, 1000);
                    }

                    @Override
                    public void onError() {
                        progressHide();
                        notif("User error");
                    }
                });
    }


    private void requestTopUp() {
        progressShow();

        TopUpAyoPulsaRequestJson params = new TopUpAyoPulsaRequestJson(
                phoneNumber.getText().toString(), "",topUpDetailModel.getCode(), null);
        AyoPulsaApiHelper.getInstance().topUpRequest(params).enqueue(new Callback<TopUpAyoPulsaResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<TopUpAyoPulsaResponseModel> call, @NonNull Response<TopUpAyoPulsaResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    notif(response.body().getMessage());
                    trackTopUp();
                } else {
                    notif("error, silahkan cek data akun anda!");
                }
                progressHide();
            }

            @Override
            public void onFailure(@NonNull Call<TopUpAyoPulsaResponseModel> call, @NonNull Throwable t) {
                progressHide();
            }
        });
    }

    private void sendNotif(final String regIDTujuan, final Notif notif) {

        final FCMMessage message = new FCMMessage();
        message.setTo(regIDTujuan);
        message.setData(notif);

        FCMHelper.sendMessage(Constants.FCM_KEY, message).enqueue(new okhttp3.Callback() {
            @Override
            public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) {
            }

            @Override
            public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTIVITY_RESULT && resultCode == RESULT_OK) {
            if (data != null) {
                topUpDetailModel = (PriceListDetailModel) data.getSerializableExtra(Constants.OPERATOR);
                if (topUpDetailModel!=null) {
                    Utility.currencyET(productTitle,
                            String.valueOf(topUpDetailModel.getPrice()), this);
                }
            }
        }
    }
}
