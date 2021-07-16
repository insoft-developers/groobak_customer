package com.groobak.customer.item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.groobak.customer.R;
import com.groobak.customer.activity.CariGroobakActivity2;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.models.ayopulsa.PriceListDetailModel;
import com.groobak.customer.utils.PicassoTrustAll;
import com.groobak.customer.utils.Utility;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class PromoIkanItem extends RecyclerView.Adapter<PromoIkanItem.ViewHolder> {
    Context context;
    List<ItemModel> mDataSet;

    public PromoIkanItem(List<ItemModel> dataSet, Context context) {
        this.context = context;
        mDataSet = dataSet;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_promo_ikan, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {


        if (!mDataSet.get(position).getFoto_item().isEmpty()) {
            PicassoTrustAll.getInstance(context)
                    .load(Constants.IMAGESITEM + mDataSet.get(position).getFoto_item())
                    .resize(250, 250)
                    .into(holder.image);
        }

        holder.txtnamaikan.setText("Promo "+mDataSet.get(position).getNama_item());
        holder.txtcontent.setText("");

        holder.rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(context, CariGroobakActivity2.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                in.putExtra("FiturKey", 1);
                in.putExtra("job", 7);
                in.putExtra("nama_ikan", mDataSet.get(position).getNama_item());
                context.startActivity(in);
            }
        });

    }


    @Override
    public int getItemCount() {
        return mDataSet.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        Context context;
        RelativeLayout rootlayout;
        TextView txtnamaikan, txtcontent;

        public ViewHolder(View view) {
            super(view);
            image = view.findViewById(R.id.image);
            rootlayout = view.findViewById(R.id.rootLayout);
            txtnamaikan = view.findViewById(R.id.txtnamaikan);
            txtcontent = view.findViewById(R.id.txtcontent);
        }
    }

}
