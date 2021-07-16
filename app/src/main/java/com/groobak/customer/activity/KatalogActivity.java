package com.groobak.customer.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.item.IkanItem;
import com.groobak.customer.item.KatalogItem;
import com.groobak.customer.item.PromoIkanItem;
import com.groobak.customer.json.GetItemResponseJson;
import com.groobak.customer.json.KatalogRequestJson;
import com.groobak.customer.json.KatalogResponseJson;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.models.KatalogModel;
import com.groobak.customer.models.User;
import com.groobak.customer.utils.api.ServiceGenerator;
import com.groobak.customer.utils.api.service.BookService;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;

public class KatalogActivity extends AppCompatActivity {
    private ImageView backbtn;
    private EditText carikatalogikan;
    private RecyclerView rvkatalogikan;
    private List<KatalogModel> itemAvailable;
    private ShimmerFrameLayout shimmerkatalog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_katalog);
        backbtn = findViewById(R.id.back_btn);
        carikatalogikan = findViewById(R.id.cari_katalog_ikan);
        rvkatalogikan = findViewById(R.id.rv_katalog_ikan);
        itemAvailable = new ArrayList<>();
        shimmerkatalog = findViewById(R.id.shimmerkatalog);

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        katalogikan("");

        carikatalogikan.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String s = editable.toString();
                katalogikan(s);
            }
        });

        showsimmer();
    }

    private void katalogikan(String nama_ikan) {
        if (itemAvailable != null) {
            itemAvailable.clear();
        }
        User loginUser = BaseApp.getInstance(KatalogActivity.this).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        KatalogRequestJson param = new KatalogRequestJson();
        param.setNama_ikan(nama_ikan);
        service.katalogikan(param).enqueue(new Callback<KatalogResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<KatalogResponseJson> call, @NonNull Response<KatalogResponseJson> response) {
                if (response.isSuccessful()) {
                    shimmertutup();
                    itemAvailable = Objects.requireNonNull(response.body()).getData();
                    if (itemAvailable.isEmpty()) {
                        rvkatalogikan.setVisibility(GONE);
                    } else {

                        KatalogItem katalogItem = new KatalogItem(KatalogActivity.this, itemAvailable);
                        rvkatalogikan.setAdapter(katalogItem);

                        rvkatalogikan.setHasFixedSize(true);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(KatalogActivity.this);
                        rvkatalogikan.setLayoutManager(layoutManager);
                        rvkatalogikan.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<KatalogResponseJson> call, @NonNull Throwable t) {

            }
        });
    }

    private void showsimmer() {
        rvkatalogikan.setVisibility(GONE);
        shimmerkatalog.startShimmerAnimation();
    }

    private void shimmertutup() {
        shimmerkatalog.stopShimmerAnimation();
        shimmerkatalog.setVisibility(View.GONE);
        rvkatalogikan.setVisibility(View.VISIBLE);
    }
}