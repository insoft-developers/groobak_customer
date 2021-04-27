package com.groobak.customer.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.item.BankItem;
import com.groobak.customer.json.BankResponseJson;
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

import java.io.IOException;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopupviewActivity extends AppCompatActivity {

    EditText jumlah, bankkamu, namaakun, norek;
    Button submit;
    TextView notif;
    ImageView backbtn, images;
    RelativeLayout rlnotif, rlprogress;
    String disableback, type, nominal;
    SettingPreference sp;
    RecyclerView petunjuk;
    LinearLayout llpentunjuk;
    BankItem bankItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topupview);
        disableback = "false";
        jumlah = findViewById(R.id.jumlah);
        bankkamu = findViewById(R.id.bankkamu);
        norek = findViewById(R.id.norek);
        backbtn = findViewById(R.id.back_btn);
        submit = findViewById(R.id.submit);
        rlnotif = findViewById(R.id.rlnotif);
        notif = findViewById(R.id.textnotif);
        rlprogress = findViewById(R.id.rlprogress);
        namaakun = findViewById(R.id.namaakun);
        images = findViewById(R.id.imagebackground);
        sp = new SettingPreference(this);
        llpentunjuk = findViewById(R.id.llpentunjuk);
        petunjuk = findViewById(R.id.petunjuk);
        sp = new SettingPreference(this);

        Intent intent = getIntent();
        type = intent.getStringExtra("type");

        if (Objects.equals(type, "topup")) {
            images.setImageResource(R.drawable.atm);
            images.setScaleType(ImageView.ScaleType.FIT_XY);
            nominal = intent.getStringExtra("nominal");

            Utility.currencyTXT(jumlah, Objects.requireNonNull(nominal), TopupviewActivity.this);
            petunjuk.setHasFixedSize(true);
            petunjuk.setNestedScrollingEnabled(false);
            petunjuk.setLayoutManager(new GridLayoutManager(this, 1));
            getpetunjuk();
        }

        jumlah.addTextChangedListener(Utility.currencyTW(jumlah, this));

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User userLogin = BaseApp.getInstance(TopupviewActivity.this).getLoginUser();
                if (type.equals("withdraw")) {
                    if (jumlah.getText().toString().isEmpty()) {
                        notif("nominal tidak boleh kosong!");
                    } else if (Long.parseLong(jumlah.getText()
                            .toString()
                            .replace(".", "")
                            .replace(",", "")
                            .replace(sp.getSetting()[0], "")) > userLogin.getWalletSaldo()) {
                        notif("saldo tidak cukup!");
                    } else if (bankkamu.getText().toString().isEmpty()) {
                        notif("bank tidak boleh kosong!");
                    } else if (norek.getText().toString().isEmpty()) {
                        notif("nomor tidak boleh kosong");
                    } else {
                        submit();
                    }
                } else {
                    if (jumlah.getText().toString().isEmpty()) {
                        notif("jumlah tidak boleh kosong!");
                    } else if (bankkamu.getText().toString().isEmpty()) {
                        notif("bank tidak boleh kosong!");
                    } else if (norek.getText().toString().isEmpty()) {
                        notif("nomor tidak boleh kosong!");
                    } else {
                        submit();
                    }
                }
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void submit() {
        progressshow();
        final User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();
        request.setId(user.getId());
        request.setBank(bankkamu.getText().toString());
        request.setName(namaakun.getText().toString());
        request.setAmount(jumlah.getText().toString().replace(".", "").replace(sp.getSetting()[0], ""));
        request.setCard(norek.getText().toString());
        request.setNotelepon(user.getNoTelepon());
        request.setEmail(user.getEmail());
        request.setType(type);

        UserService service = ServiceGenerator.createService(UserService.class, user.getNoTelepon(), user.getPassword());
        service.withdraw(request).enqueue(new Callback<ResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<ResponseJson> call, @NonNull Response<ResponseJson> response) {
                progresshide();
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Intent intent = new Intent(TopupviewActivity.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();

                        Notif notif = new Notif();
                        if (type.equals("withdraw")) {
                            notif.title = "Pulsa";
                            notif.message = "Permintaan penarikan telah berhasil, kami akan mengirimkan pemberitahuan setelah kami mengirim dana ke akun Anda";
                        } else {
                            notif.title = "Topup";
                            notif.message = "Permintaan pengisian telah berhasil, kami akan mengirimkan pemberitahuan setelah kami mengkonfirmasi";
                        }
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

    public void onBackPressed() {
        if (!disableback.equals("true")) {
            finish();
        }
    }

    public void notif(String text) {
        rlnotif.setVisibility(View.VISIBLE);
        notif.setText(text);

        new Handler().postDelayed(new Runnable() {
            public void run() {
                rlnotif.setVisibility(View.GONE);
            }
        }, 3000);
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

    private void getpetunjuk() {
        User user = BaseApp.getInstance(this).getLoginUser();
        WithdrawRequestJson request = new WithdrawRequestJson();

        UserService service = ServiceGenerator.createService(UserService.class, user.getNoTelepon(), user.getPassword());
        service.listbank(request).enqueue(new Callback<BankResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<BankResponseJson> call, @NonNull Response<BankResponseJson> response) {
                if (response.isSuccessful()) {
                    llpentunjuk.setVisibility(View.VISIBLE);
                    bankItem = new BankItem(TopupviewActivity.this, Objects.requireNonNull(response.body()).getData(), R.layout.item_bank);
                    petunjuk.setAdapter(bankItem);
                }
            }

            @Override
            public void onFailure(@NonNull Call<BankResponseJson> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }


}

