package com.groobak.customer.item;

import android.content.Context;
import android.content.Intent;
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
import com.groobak.customer.activity.PilihGroobakActivity;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.DriverModel;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.models.PesananMerchant;
import com.groobak.customer.utils.PicassoTrustAll;
import com.groobak.customer.utils.Utility;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

import io.realm.Realm;


public class PilihGroobakItem extends RecyclerView.Adapter<PilihGroobakItem.ItemRowHolder> {

    private List<ItemModel> dataList;
    private Context mContext;
    private Realm realm;
    private OnCalculatePrice calculatePrice;


    public PilihGroobakItem(Context context, List<ItemModel> dataList,  OnCalculatePrice calculatePrice) {
        this.dataList = dataList;
        this.mContext = context;
        this.calculatePrice = calculatePrice;
    }

    @NonNull
    @Override
    public ItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ikan_tersedia, parent, false);
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemRowHolder holder, final int position) {
        realm = BaseApp.getInstance(mContext).getRealmInstance();
        final ItemModel singleItem = dataList.get(position);
        holder.txt_nama_ikan.setText(singleItem.getNama_item());
        holder.quantity_text.setText("0");


        Utility.currencyTXT(holder.txt_harga_ikan, singleItem.getHarga_item(), mContext);
        PicassoTrustAll.getInstance(mContext)
                .load(Constants.IMAGESITEM+singleItem.getFoto_item())
                .placeholder(R.drawable.image_placeholder)
                .resize(120, 65)
                .into(holder.image);


        holder.add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantity_text.getText().toString());
                quantity++;
                int id_item = singleItem.getId_item();
                String namaitem = singleItem.getNama_item();
                String foto = singleItem.getFoto_item();
                long cost = Integer.parseInt(singleItem.getHarga_item()) * quantity;
                int hargasaatuan = Integer.parseInt(singleItem.getHarga_item());
                holder.quantity_text.setText(String.valueOf(quantity));
//                Toast.makeText(mContext, "Qty "+quantity, Toast.LENGTH_SHORT).show();
                if (quantity == 1) {
                    AddPesanan(id_item, cost, quantity, namaitem, foto, hargasaatuan);
                } else if (quantity > 1) {
                    UpdatePesanan(id_item, cost, quantity, namaitem, foto, hargasaatuan);
                }

                if (calculatePrice != null) calculatePrice.calculatePrice();
            }
        });

        holder.remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int quantity = Integer.parseInt(holder.quantity_text.getText().toString());
                int id_item = singleItem.getId_item();
                String namaitem = singleItem.getNama_item();
                String foto = singleItem.getFoto_item();
                long cost = Integer.parseInt(singleItem.getHarga_item()) * quantity;
                int hargasaatuan = Integer.parseInt(singleItem.getHarga_item());
//                Toast.makeText(mContext, "Qty "+quantity, Toast.LENGTH_SHORT).show();
                if (quantity - 1 >= 0) {
                    quantity--;
                    holder.quantity_text.setText(String.valueOf(quantity));
                    UpdatePesanan(id_item, cost, quantity, namaitem, foto, hargasaatuan);

                    if (quantity == 0) {
                        DeletePesanan(id_item);
                    }
                }

                if (calculatePrice != null) calculatePrice.calculatePrice();
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }

    public interface OnCalculatePrice {
        void calculatePrice();
    }

    static class ItemRowHolder extends RecyclerView.ViewHolder {
        TextView txt_nama_ikan, txt_harga_ikan;
        TextView add, remove, quantity_text;
        ImageView image;

        ItemRowHolder(View itemView) {
            super(itemView);
            txt_nama_ikan = itemView.findViewById(R.id.txt_nama_ikan);
            txt_harga_ikan = itemView.findViewById(R.id.txt_harga_ikan);
            image = itemView.findViewById(R.id.image);
            add = itemView.findViewById(R.id.add_quantity);
            remove = itemView.findViewById(R.id.remove_quantity);
            quantity_text = itemView.findViewById(R.id.quantity_text);

        }
    }

    private void AddPesanan(int idMakanan, long totalHarga, int qty, String namaitem, String foto, int hargasatuan) {
        PesananMerchant pesananfood = new PesananMerchant();
        pesananfood.setIdItem(idMakanan);
        pesananfood.setNamaItem(namaitem);
        pesananfood.setTotalHarga(totalHarga);
        pesananfood.setQty(qty);
        pesananfood.setHargaSatuan(hargasatuan);
        pesananfood.setFoto(foto);
        realm.beginTransaction();
        realm.copyToRealm(pesananfood);
        realm.commitTransaction();

    }

    private void UpdatePesanan(int idMakanan, long totalHarga, int qty, String namaitem, String foto, int hargasatuan) {
        realm.beginTransaction();
        PesananMerchant updateFood = realm.where(PesananMerchant.class).equalTo("idItem", idMakanan).findFirst();
        Objects.requireNonNull(updateFood).setTotalHarga(totalHarga);
        updateFood.setQty(qty);
        updateFood.setNamaItem(namaitem);
        updateFood.setFoto(foto);
        updateFood.setHargaSatuan(hargasatuan);
        realm.copyToRealm(updateFood);
        realm.commitTransaction();

    }

    private void DeletePesanan(int idMakanan) {
        realm.beginTransaction();
        PesananMerchant deleteFood = realm.where(PesananMerchant.class).equalTo("idItem", idMakanan).findFirst();
        Objects.requireNonNull(deleteFood).deleteFromRealm();
        realm.commitTransaction();
    }
}
