package com.groobak.customer.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.groobak.customer.R;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.item.ItemItem;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.models.PesananMerchant;
import com.groobak.customer.utils.Log;
import com.groobak.customer.utils.Utility;
import com.groobak.customer.utils.api.MapDirectionAPI;
import com.mikepenz.fastadapter.FastAdapter;
import com.mikepenz.fastadapter.adapters.FastItemAdapter;
import com.mikepenz.fastadapter.listeners.ClickEventHook;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;

public class ListOrderActivity extends AppCompatActivity implements ItemItem.OnCalculatePrice {
    private TextView txtalamatpengantaran, txtgantialamat, txtharga, txtdiskon, txttotalpembayaran, txttotalinbutton, txtnamagerobak;
    private EditText etvoucher;
    private RecyclerView rv_pesanan;
    private Realm realm;
    private FastItemAdapter<ItemItem> itemAdapter;
    private final int DESTINATION_ID = 1;
    private ImageView backbtn;
    private LinearLayout llpesan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_order);
        realm = Realm.getDefaultInstance();

        txtalamatpengantaran = findViewById(R.id.txtalamatpengantaran);
        txtgantialamat = findViewById(R.id.txtgantialamat);
        txtharga = findViewById(R.id.txtharga);
        txtdiskon = findViewById(R.id.txtdiskon);
        txttotalpembayaran = findViewById(R.id.txttotalpembayaran);
        txttotalinbutton = findViewById(R.id.txttotalinbutton);
        etvoucher = findViewById(R.id.etvoucher);
        rv_pesanan = findViewById(R.id.rv_pesanan);
        backbtn = findViewById(R.id.back_btn);
        txtnamagerobak = findViewById(R.id.txt_nama_groobak);
        llpesan = findViewById(R.id.llpesan);

        String namagroobak = getIntent().getStringExtra("nama_groobak_intent");
        txtnamagerobak.setText("Groobak "+namagroobak);

        itemAdapter = new FastItemAdapter<>();
        itemAdapter.notifyDataSetChanged();
        itemAdapter.withSelectable(true);
        itemAdapter.withItemEvent(new ClickEventHook<ItemItem>() {
            @Nullable
            @Override
            public View onBind(@NonNull RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof ItemItem.ViewHolder) {
                    return ((ItemItem.ViewHolder) viewHolder).itemView;
                }
                return null;
            }

            @Override
            public void onClick(View v, int position, FastAdapter<ItemItem> fastAdapter, ItemItem item) {
                //sheetlist(position);
            }
        });
        rv_pesanan.setLayoutManager(new LinearLayoutManager(this));
        rv_pesanan.setAdapter(itemAdapter);
        updateEstimatedItemCost();
        loadItem();
        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(ListOrderActivity.this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocation.getLastLocation().addOnSuccessListener(ListOrderActivity.this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    String lokasi_saya = getCompleteAddressString(location.getLatitude(), location.getLongitude());
                    txtalamatpengantaran.setText(lokasi_saya);
                    Constants.LATITUDE = location.getLatitude();
                    Constants.LONGITUDE = location.getLongitude();
                    Log.e("BEARING:", String.valueOf(location.getBearing()));
                } else {
                    Toast.makeText(ListOrderActivity.this, "Maaf Sistem Masih Belum Menemukan Lokasi Anda..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        txtgantialamat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ListOrderActivity.this, PicklocationActivity.class);
                intent.putExtra(PicklocationActivity.FORM_VIEW_INDICATOR, DESTINATION_ID);
                startActivityForResult(intent, PicklocationActivity.LOCATION_PICKER_ID);
            }
        });

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        llpesan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListOrderActivity.this, WaitingActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadItem() {
        List<PesananMerchant> pesananFoods = realm.copyFromRealm(realm.where(PesananMerchant.class).findAll());
        itemAdapter.clear();
        for (PesananMerchant pesanan : pesananFoods) {
            ItemItem makananItem = new ItemItem(this, this);
            makananItem.quantity = pesanan.getQty();
            makananItem.id = pesanan.getIdItem();
            makananItem.namaMenu =pesanan.getNamaItem();
            makananItem.deskripsiMenu = "";
            makananItem.foto = pesanan.getFoto();
            makananItem.harga = pesanan.getHargaSatuan();
            makananItem.catatan = pesanan.getCatatan();
            itemAdapter.add(makananItem);
        }

        itemAdapter.notifyDataSetChanged();


    }

    @Override
    public void calculatePrice() {
        updateEstimatedItemCost();
    }

    private void updateEstimatedItemCost() {
        List<PesananMerchant> existingFood = realm.copyFromRealm(realm.where(PesananMerchant.class).findAll());
        long cost = 0;
        long diskon = 0;
        for (int p = 0; p < existingFood.size(); p++) {
            cost += existingFood.get(p).getTotalHarga();
        }
        long foodCostLong = cost;
        long paidamount = cost - diskon;
        Utility.currencyTXT(txtharga, String.valueOf(foodCostLong), this);
        Utility.currencyTXT(txtdiskon, String.valueOf(diskon), this);
        Utility.currencyTXT(txttotalinbutton, String.valueOf(paidamount), this);
        Utility.currencyTXT(txttotalpembayaran, String.valueOf(paidamount), this);
//        MapDirectionAPI.getDirection(pickUpLatLang, destinationLatLang).enqueue(updateRouteCallback);
    }

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(ListOrderActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                Log.w("My Current loction address", strReturnedAddress.toString());
            } else {
                Log.w("My Current loction address", "No Address returned!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PicklocationActivity.LOCATION_PICKER_ID) {
            if (resultCode == Activity.RESULT_OK) {
                String addressset = data.getStringExtra(PicklocationActivity.LOCATION_NAME);
                LatLng latLng = data.getParcelableExtra(PicklocationActivity.LOCATION_LATLNG);
                txtalamatpengantaran.setText(addressset);
            }
        }
    }
}