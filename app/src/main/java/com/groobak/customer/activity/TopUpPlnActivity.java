package com.groobak.customer.activity;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.item.TopUpPlnHistoryItem;
import com.groobak.customer.json.TopUpAyoPulsaRequestJson;
import com.groobak.customer.json.TopUpAyoPulsaResponseModel;
import com.groobak.customer.json.fcm.FCMMessage;
import com.groobak.customer.models.Notif;
import com.groobak.customer.models.TopUpPlnHistoryModel;
import com.groobak.customer.models.User;
import com.groobak.customer.models.ayopulsa.PriceListDataModel;
import com.groobak.customer.models.ayopulsa.PriceListDetailModel;
import com.groobak.customer.models.ayopulsa.TopUpStatusModel;
import com.groobak.customer.utils.ProjectUtils;
import com.groobak.customer.utils.SettingPreference;
import com.groobak.customer.utils.Utility;
import com.groobak.customer.utils.api.AyoPulsaApiHelper;
import com.groobak.customer.utils.api.MobilePulsaApiHelper;
import com.groobak.customer.utils.api.FCMHelper;
import com.groobak.customer.utils.api.service.ApiListener;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopUpPlnActivity extends AppCompatActivity implements TopUpPlnHistoryItem.onRecyclerViewOnClicked {
    RelativeLayout rlProgress,rlnotif;
    String disableBack;
    EditText etIdPln;
    Spinner spinner;
    TextView notif;
    Button submit;
    SettingPreference sp;
    ImageView ivBack;
    RecyclerView rcView;
    LinearLayout llListPastTopUp;
    List<TopUpPlnHistoryModel> listOfPreviousPLNTopUpRequest= new ArrayList<>();
    List<PriceListDetailModel> dataList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.top_up_pln_activity);
        rlnotif = findViewById(R.id.rlnotif);
        rlProgress = findViewById(R.id.rlprogress);
        etIdPln = findViewById(R.id.etIdPln);
        spinner = findViewById(R.id.listNominal);
        notif = findViewById(R.id.textnotif);
        submit = findViewById(R.id.submit);
        ivBack = findViewById(R.id.ivBack);
        rcView = findViewById(R.id.rcPlnTopUpList);
        llListPastTopUp = findViewById(R.id.llListPastTopUp);

        sp = new SettingPreference(this);

        getAyoPesanPlnPriceListData();

        ivBack.setOnClickListener(v -> finish());

        submit.setOnClickListener(v -> {
            User userLogin = BaseApp.getInstance(TopUpPlnActivity.this).getLoginUser();
            if(userLogin.getWalletSaldo() < dataList.get(spinner.getSelectedItemPosition()).getPrice() + 600) {
                Toast.makeText(this, "Your wallet is not enough. Please top up first", Toast.LENGTH_LONG).show();
            } else {
                topUpPln();
            }
        });
    }

    private void getAyoPesanPlnPriceListData() {
        progressShow();
        AyoPulsaApiHelper.getInstance().getAyoPesanPlnPriceList().enqueue(new Callback<PriceListDataModel>() {
            @Override
            public void onResponse(@NonNull Call<PriceListDataModel> call, @NonNull Response<PriceListDataModel> response) {
                if (response.body() != null && response.isSuccessful()) {
                    List<String> categoryList = new ArrayList<>();
                    for (PriceListDetailModel priceListDataModel: response.body().getDataX()) {
                        int price = priceListDataModel.getPrice() + 600;
                        categoryList.add(Utility.convertCurrency(String.valueOf(price), TopUpPlnActivity.this));
                    }
                    dataList.addAll(response.body().getDataX());
                    initSpinner(categoryList);
                    initPastPLNPaidBill(sp.getSetting()[10]);
                }
                progressHide();
            }

            @Override
            public void onFailure(@NonNull Call<PriceListDataModel> call, @NonNull Throwable t) {
                progressHide();
            }
        });
    }

    private void initPastPLNPaidBill(String list) {
        if (!list.isEmpty()) {
            llListPastTopUp.setVisibility(View.VISIBLE);
            Type listOfMyClassObject = new TypeToken<List<TopUpPlnHistoryModel>>() {}.getType();
            listOfPreviousPLNTopUpRequest.addAll(new Gson().fromJson(list, listOfMyClassObject));

            rcView.setHasFixedSize(true);
            rcView.setNestedScrollingEnabled(false);
            rcView.setLayoutManager(new LinearLayoutManager(this, RecyclerView.VERTICAL, false));

            TopUpPlnHistoryItem item = new TopUpPlnHistoryItem(listOfPreviousPLNTopUpRequest, this);
            rcView.setAdapter(item);
        } else {
            llListPastTopUp.setVisibility(View.GONE);
        }
    }

    private void initSpinner(List<String> data) {
        ArrayAdapter<String> nominalListAdapter =
                new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
        nominalListAdapter.setDropDownViewResource(R.layout.dialog_spinner_dropdown_item);
        spinner.setAdapter(nominalListAdapter);
    }

    private void topUpPln() {
        progressShow();
        String refId = ProjectUtils.createTransactionID();
        TopUpAyoPulsaRequestJson params = new TopUpAyoPulsaRequestJson(etIdPln.getText().toString(), "",
                dataList.get(spinner.getSelectedItemPosition()).getCode(), refId);
        AyoPulsaApiHelper.getInstance().topUpRequest(params).enqueue(new Callback<TopUpAyoPulsaResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<TopUpAyoPulsaResponseModel> call, @NonNull Response<TopUpAyoPulsaResponseModel> response) {
                if (response.body() != null && response.isSuccessful() && response.body().getData() != null) {
                    sp.updatePlnList(new TopUpPlnHistoryModel(
                            etIdPln.getText().toString(), dataList.get(spinner.getSelectedItemPosition()).getPrice() + 600,refId));
                    trackTopUp();
                } else {
                    notif(Objects.requireNonNull(response.body()).getMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<TopUpAyoPulsaResponseModel> call,@NonNull Throwable t) {
                progressHide();
            }
        });
    }

    private void trackTopUp() {
        MobilePulsaApiHelper.trackUserTopUp(String.valueOf(dataList.get(spinner.getSelectedItemPosition()).getPrice() + 600), "PLN", etIdPln.getText().toString(),
                TopUpPlnActivity.this, sp, new ApiListener() {
                    @Override
                    public void onSuccess() {
                        Notif notif = new Notif();
                        notif.title = "Topup";
                        notif.message = "Permintaan pengisian telah berhasil, kami akan mengirimkan pemberitahuan setelah kami mengkonfirmasi";
                        final User user = BaseApp.getInstance(TopUpPlnActivity.this).getLoginUser();
                        sendNotif(user.getToken(), notif);
                        progressHide();
                        finish();
                    }

                    @Override
                    public void onError() {
                        progressHide();
                        notif("error, silahkan cek data akun anda!");
                    }
                });
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        notif.setText(text);

        new Handler().postDelayed(() -> rlnotif.setVisibility(View.GONE), 3000);
    }

    private void progressShow() {
        rlProgress.setVisibility(View.VISIBLE);
        disableBack = "true";
    }

    private void progressHide() {
        rlProgress.setVisibility(View.GONE);
        disableBack = "false";
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

    private void showHistoryDetailDialog(TopUpStatusModel data, TopUpPlnHistoryModel model) {
        final Dialog dialog = new Dialog(TopUpPlnActivity.this);
        dialog.setContentView(R.layout.dialog_payment_details);
        TextView tvPel = dialog.findViewById(R.id.idPelanggan);
        TextView tvTitle = dialog.findViewById(R.id.tvTitle);
        TextView tvTotal = dialog.findViewById(R.id.tvTotal);
        TextView titlePel = dialog.findViewById(R.id.tvIdPel);

        tvTitle.setText(data.getMessage());
        tvTitle.setTextColor(ContextCompat.getColor(this, data.getMessage().contains("Failed") ? R.color.red: R.color.green));

        if (data.getData().getCustomerNo() != null && !data.getData().getCustomerNo().isEmpty()) {
            tvPel.setText(data.getData().getCustomerNo());
        } else {
            titlePel.setText("STATUS");
            tvPel.setText(data.getMessage());
        }

        int total = model.getPrice();
        tvTotal.setText(Utility.convertCurrency(String.valueOf(total), TopUpPlnActivity.this));
        dialog.show();
    }

    @Override
    public void onClick(TopUpPlnHistoryModel previousResponse) {
        progressShow();
        AyoPulsaApiHelper.getInstance().checkAyoPesanPaymentStatus(previousResponse.getRefId()).enqueue(new Callback<TopUpStatusModel>() {
            @Override
            public void onResponse(@NonNull Call<TopUpStatusModel> call, @NonNull Response<TopUpStatusModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    progressHide();
                    showHistoryDetailDialog(response.body(), previousResponse);
                }
            }

            @Override
            public void onFailure(@NonNull Call<TopUpStatusModel> call, @NonNull Throwable t) {
                progressHide();
            }
        });

    }
}
