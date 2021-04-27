package com.groobak.customer.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.groobak.customer.R;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.item.PromoItem;
import com.groobak.customer.json.PromoRequestJson;
import com.groobak.customer.json.PromoResponseJson;
import com.groobak.customer.models.User;
import com.groobak.customer.utils.api.ServiceGenerator;
import com.groobak.customer.utils.api.service.UserService;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PromoActivity extends AppCompatActivity {

    ShimmerFrameLayout shimmer;
    RecyclerView recycle;
    PromoItem promoItem;
    RelativeLayout rlnodata;
    ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promo);
        shimmer = findViewById(R.id.shimmer);
        recycle = findViewById(R.id.inboxlist);
        rlnodata = findViewById(R.id.rlnodata);
        backButton = findViewById(R.id.back_btn);
        recycle.setHasFixedSize(true);
        recycle.setNestedScrollingEnabled(false);
        recycle.setLayoutManager(new GridLayoutManager(this, 1));
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        getData();
    }

    private void shimmershow() {
        rlnodata.setVisibility(View.GONE);
        recycle.setVisibility(View.GONE);
        shimmer.setVisibility(View.VISIBLE);
        shimmer.startShimmerAnimation();
    }

    private void shimmertutup() {

        recycle.setVisibility(View.VISIBLE);
        shimmer.setVisibility(View.GONE);
        shimmer.stopShimmerAnimation();
    }

    private void getData() {
        shimmershow();
        User loginUser = BaseApp.getInstance(this).getLoginUser();
        UserService service = ServiceGenerator.createService(UserService.class, loginUser.getEmail(), loginUser.getPassword());
        PromoRequestJson param = new PromoRequestJson();

        service.listpromocode(param).enqueue(new Callback<PromoResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<PromoResponseJson> call, @NonNull Response<PromoResponseJson> response) {
                if (response.isSuccessful()) {
                    shimmertutup();
                    if (Objects.requireNonNull(response.body()).getData().isEmpty()) {
                        rlnodata.setVisibility(View.VISIBLE);
                    } else {
                        promoItem = new PromoItem(PromoActivity.this, response.body().getData(), R.layout.item_promo);
                        recycle.setAdapter(promoItem);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<PromoResponseJson> call, @NonNull Throwable t) {

            }
        });

    }
}