package com.groobak.customer.item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.groobak.customer.R;
import com.groobak.customer.activity.CariGroobakActivity;
import com.groobak.customer.activity.CariGroobakActivity2;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.models.KatalogModel;
import com.groobak.customer.models.PesananMerchant;
import com.groobak.customer.utils.PicassoTrustAll;
import com.groobak.customer.utils.Utility;

import java.util.List;
import java.util.Objects;

import io.realm.Realm;


public class KatalogItem extends RecyclerView.Adapter<KatalogItem.ItemRowHolder> {

    private List<KatalogModel> dataList;
    private Context mContext;

    public KatalogItem(Context context, List<KatalogModel> dataList) {
        this.dataList = dataList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_katalog, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        final KatalogModel singleItem = dataList.get(position);
        holder.txt_nama_ikan.setText(singleItem.getNama_item());
        holder.txt_bawa_ikan.setText(singleItem.getBawa()+ " Groobak Sedang Membawa Ikan Ini.");
        holder.txt_like.setText(singleItem.getLike()+ " Pelanggan Menyukai Ini.");

        PicassoTrustAll.getInstance(mContext)
                .load(Constants.IMAGESITEM+singleItem.getFoto())
                .placeholder(R.drawable.image_placeholder)
                .resize(120, 65)
                .into(holder.image);


        holder.listitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(mContext, CariGroobakActivity2.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("FiturKey", 1);
                in.putExtra("job", 7);
                in.putExtra("nama_ikan", singleItem.getNama_item());
                mContext.startActivity(in);

            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView txt_nama_ikan, txt_bawa_ikan, txt_like;
        LinearLayout listitem;
        ImageView image;

        ItemRowHolder(View itemView) {
            super(itemView);
            txt_nama_ikan = itemView.findViewById(R.id.txt_nama_ikan);
            txt_bawa_ikan = itemView.findViewById(R.id.txt_bawa_ikan);
            image = itemView.findViewById(R.id.image);
            txt_like = itemView.findViewById(R.id.txt_like);
            listitem = itemView.findViewById(R.id.list_item);
        }
    }

}
