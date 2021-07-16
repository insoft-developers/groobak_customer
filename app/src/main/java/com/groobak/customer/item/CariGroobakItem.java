package com.groobak.customer.item;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groobak.customer.R;
import com.groobak.customer.activity.BeritaDetailActivity;
import com.groobak.customer.activity.PilihGroobakActivity;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.BeritaModel;
import com.groobak.customer.models.DriverModel;
import com.groobak.customer.models.User;
import com.groobak.customer.utils.DatabaseHelper;
import com.groobak.customer.utils.PicassoTrustAll;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;


public class CariGroobakItem extends RecyclerView.Adapter<CariGroobakItem.ItemRowHolder> {

    private List<DriverModel> dataList;
    private Context mContext;

    public CariGroobakItem(Context context, List<DriverModel> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cari_groobak, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemRowHolder holder, final int position) {
        final DriverModel singleItem = dataList.get(position);
        holder.nama_groobak.setText(singleItem.getNamaDriver());
        Double jarak_d = Double.parseDouble(singleItem.getDistance());
        DecimalFormat df = new DecimalFormat("#.##");

        holder.jarak_groobak.setText(df.format(jarak_d)+" km dari Anda ");
        String jarak_kirim = df.format(jarak_d);

        holder.btn_pilih_groobak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, PilihGroobakActivity.class);
                intent.putExtra("id_driver_intent", singleItem.getId());
                intent.putExtra("nama_groobak_intent", singleItem.getNamaDriver());
                intent.putExtra("nama_orang_intent", singleItem.getNamaDriver());
                intent.putExtra("jarak_intent", jarak_kirim);
                intent.putExtra("foto_intent", singleItem.getFoto());
                intent.putExtra("jenis_ikan_intent", singleItem.getJenis_ikan());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView nama_groobak, jarak_groobak;
        Button btn_pilih_groobak;

        ItemRowHolder(View itemView) {
            super(itemView);
            nama_groobak = itemView.findViewById(R.id.nama_groobak);
            jarak_groobak = itemView.findViewById(R.id.jarak_groobak);
            btn_pilih_groobak = itemView.findViewById(R.id.btn_pilih_groobak);
        }
    }
}
