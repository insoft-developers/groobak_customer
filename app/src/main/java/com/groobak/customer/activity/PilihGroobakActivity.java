package com.groobak.customer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.telephony.ims.ImsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.item.BeritaItem;
import com.groobak.customer.item.ItemItem;
import com.groobak.customer.item.PilihGroobakItem;
import com.groobak.customer.json.BeritaDetailResponseJson;
import com.groobak.customer.json.GetItemResponseJson;
import com.groobak.customer.json.ItemRequestIkanJson;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.models.PesananMerchant;
import com.groobak.customer.models.User;
import com.groobak.customer.utils.PicassoTrustAll;
import com.groobak.customer.utils.Utility;
import com.groobak.customer.utils.api.MapDirectionAPI;
import com.groobak.customer.utils.api.ServiceGenerator;
import com.groobak.customer.utils.api.service.BookService;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import io.realm.RealmResults;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PilihGroobakActivity extends AppCompatActivity implements PilihGroobakItem.OnCalculatePrice {
    private TextView txt_nama_groobak, txt_nama_orang, txt_jarak, txt_jenis_ikan, estimated_text;
    private ImageView foto_groobak;
    private RecyclerView rv_ikan_tersedia;
    private CardView price_container;
    private TextView qty_text, cost_text;
    private Realm realm;
    private ImageView backbtn;
    private EditText searchtext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pilih_groobak);
        realm = Realm.getDefaultInstance();
        txt_nama_groobak = findViewById(R.id.txt_nama_groobak);
        txt_nama_orang = findViewById(R.id.txt_nama_orang);
        txt_jenis_ikan = findViewById(R.id.txt_jenis_ikan);
        txt_jarak = findViewById(R.id.txt_jarak);
        estimated_text = findViewById(R.id.estimated_text);
        foto_groobak = findViewById(R.id.foto_groobak);
        rv_ikan_tersedia = findViewById(R.id.rv_jenis_ikan);
        price_container = findViewById(R.id.price_container);
        qty_text = findViewById(R.id.qty_text);
        cost_text = findViewById(R.id.cost_text);
        backbtn = findViewById(R.id.back_btn);
        searchtext = findViewById(R.id.searchtext);

        String id_driver_intent = getIntent().getStringExtra("id_driver_intent");
        String nama_groobak_intent = getIntent().getStringExtra("nama_groobak_intent");
        String nama_orang_intent = getIntent().getStringExtra("nama_orang_intent");
        String jarak_intent = getIntent().getStringExtra("jarak_intent");
        String foto_intent = getIntent().getStringExtra("foto_intent");
        String jenis_ikan = getIntent().getStringExtra("jenis_ikan_intent");

        txt_nama_groobak.setText("Groobak "+nama_groobak_intent);
        txt_nama_orang.setText(nama_orang_intent);
        txt_jarak.setText(jarak_intent+" km dari Anda");
        txt_jenis_ikan.setText(jenis_ikan+" Jenis Ikan");
        PicassoTrustAll.getInstance(PilihGroobakActivity.this)
                .load(foto_intent)
                .placeholder(R.drawable.image_placeholder)
                .resize(90, 100)
                .into(foto_groobak);

        getdataitem(id_driver_intent, "");
        PesananKosong();
        estimated_text.setText("Diantar Oleh Groobak "+nama_groobak_intent);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        searchtext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                getdataitem(id_driver_intent, s);

            }
        });

    }

    private void getdataitem(String id_driver_intent, String namaikan) {
        ItemRequestIkanJson param = new ItemRequestIkanJson();
        param.setIdDriver(id_driver_intent);
        param.setNamaIkan(namaikan);
        User loginUser = BaseApp.getInstance(this).getLoginUser();
        final BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        service.getItem(param).enqueue(new Callback<GetItemResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<GetItemResponseJson> call, @NonNull Response<GetItemResponseJson> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getData().isEmpty()) {
                    } else {

                        PilihGroobakItem pilihGroobakItem = new PilihGroobakItem(PilihGroobakActivity.this, response.body().getData(), PilihGroobakActivity.this::calculatePrice);
                        rv_ikan_tersedia.setAdapter(pilihGroobakItem);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(PilihGroobakActivity.this);
                        rv_ikan_tersedia.setLayoutManager(layoutManager);
                        rv_ikan_tersedia.setItemViewCacheSize(25);
                        pilihGroobakItem.notifyDataSetChanged();

                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<GetItemResponseJson> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void calculatePrice() {
        List<PesananMerchant> existingFood = realm.where(PesananMerchant.class).findAll();

        int quantity = 0;
        long cost = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            quantity += Objects.requireNonNull(existingFood.get(p)).getQty();
            cost += Objects.requireNonNull(existingFood.get(p)).getTotalHarga();
        }

        if (quantity > 0) {
            price_container.setVisibility(View.VISIBLE);
            price_container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent in = new Intent(PilihGroobakActivity.this, ListOrderActivity.class);
                    in.putExtra("nama_groobak_intent", getIntent().getStringExtra("nama_groobak_intent"));
                    startActivity(in);

//                    Intent i = new Intent(DetailMerchantActivity.this, DetailOrderActivity.class);
//                    i.putExtra("lat", lokasi.latitude);
//                    i.putExtra("lon", lokasi.longitude);
//                    i.putExtra("merlat", merlat);
//                    i.putExtra("merlon", merlon);
//                    i.putExtra("alamat", alamat);
//                    i.putExtra("FiturKey", idfitur);
//                    i.putExtra("distance", distance);
//                    i.putExtra("alamatresto", alamatresto);
//                    i.putExtra("idresto", idresto);
//                    i.putExtra("namamerchant", nama.getText().toString());
//                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                    startActivity(i);
                }

            });

        } else {
            price_container.setVisibility(View.GONE);
        }

        qty_text.setText("" + quantity + " Item");
        Utility.currencyTXT(cost_text, String.valueOf(cost), this);
    }

    private void PesananKosong() {
        RealmResults<PesananMerchant> deleteFood = realm.where(PesananMerchant.class).findAll();
        realm.beginTransaction();
        deleteFood.deleteAllFromRealm();
        realm.commitTransaction();
    }





}