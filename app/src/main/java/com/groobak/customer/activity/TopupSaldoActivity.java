package com.groobak.customer.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.json.ResponseJson;
import com.groobak.customer.json.WithdrawRequestJson;
import com.groobak.customer.json.fcm.FCMMessage;
import com.groobak.customer.models.Notif;
import com.groobak.customer.models.User;
import com.groobak.customer.utils.SettingPreference;
import com.groobak.customer.utils.Utility;
import com.groobak.customer.utils.api.FCMHelper;
import com.groobak.customer.utils.api.ServiceGenerator;
import com.groobak.customer.utils.api.service.UserService;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopupSaldoActivity extends AppCompatActivity {
    EditText nominal;
    ImageView text1, text2, text3, text4;
    RelativeLayout rlnotif, rlprogress;
    TextView textnotif;
    String disableback;
    LinearLayout banktransfer;
    SettingPreference sp;
    ImageView backBtn;
    boolean debug;
    private String paymentAmount;

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte[] messageDigest = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topup);
        View bottom_sheet = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior.from(bottom_sheet);
        sp = new SettingPreference(this);

        nominal = findViewById(R.id.saldo);
        text1 = findViewById(R.id.text1);
        text2 = findViewById(R.id.text2);
        text3 = findViewById(R.id.text3);
        text4 = findViewById(R.id.text4);
        rlnotif = findViewById(R.id.rlnotif);
        textnotif = findViewById(R.id.textnotif);
        rlprogress = findViewById(R.id.rlprogress);
        backBtn = findViewById(R.id.back_btn);
        banktransfer = findViewById(R.id.banktransfer);

        nominal.addTextChangedListener(Utility.currencyTW(nominal, this));

        banktransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!nominal.getText().toString().isEmpty()) {
                    Intent i = new Intent(TopupSaldoActivity.this, TopupviewActivity.class);
                    i.putExtra("type", "topup");
                    i.putExtra("nominal", convertAngka(nominal.getText().toString()));
                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(i);
                } else {
                    notif("nominal tidak boleh kosong!");
                }
            }
        });


        text1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                nominal.setText("10000");
            }
        });

        text2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                nominal.setText("20000");
            }
        });

        text3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                nominal.setText("50000");
            }
        });

        text4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onClick(View v) {
                nominal.setText("100000");
            }
        });

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        disableback = "false";
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        textnotif.setText(text);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                rlnotif.setVisibility(View.GONE);
            }
        }, 3000);
    }

    @Override
    public void onBackPressed() {
        if (!disableback.equals("true")) {
            finish();
        }
    }

    public String convertAngka(String value) {
        return (((((value + "")
                .replaceAll(sp.getSetting()[0], ""))
                .replaceAll(" ", ""))
                .replaceAll(",", ""))
                .replaceAll("[$.]", ""));
    }

    private void submit() {
        progressshow();
        paymentAmount = nominal.getText().toString();
        final User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();
        request.setId(user.getId());
        request.setBank("midtrans");
        request.setName(user.getFullnama());
        request.setAmount(convertAngka(paymentAmount.replace(sp.getSetting()[0], "")));
        request.setCard("1234");
        request.setNotelepon(user.getNoTelepon());
        request.setEmail(user.getEmail());

        UserService service = ServiceGenerator.createService(UserService.class, user.getNoTelepon(), user.getPassword());
        service.withdraw(request).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<ResponseJson> call, @NonNull Response<ResponseJson> response) {
                progresshide();
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Intent i = new Intent(TopupSaldoActivity.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);

                        Notif notif = new Notif();
                        notif.title = "Topup";
                        notif.message = "topup berhasil masuk";
                        sendNotif(user.getToken(), notif);

                    } else {
                        notif("error, silahkan cek data akun anda!");
                    }
                } else {
                    notif("error!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseJson> call, @NonNull Throwable t) {
                progresshide();
                t.printStackTrace();
                notif("error");
            }
        });
    }

    public void progressshow() {
        rlprogress.setVisibility(View.VISIBLE);
        disableback = "true";
    }

    public void progresshide() {
        rlprogress.setVisibility(View.GONE);
        disableback = "false";
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


}
