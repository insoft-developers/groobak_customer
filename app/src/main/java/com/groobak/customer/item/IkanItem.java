package com.groobak.customer.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.groobak.customer.R;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.DriverModel;
import com.groobak.customer.models.ItemModel;
import com.groobak.customer.utils.PicassoTrustAll;

import java.util.List;


public class IkanItem extends PagerAdapter {

    private List<ItemModel> models;
    private Context context;

    public IkanItem(List<ItemModel> models, Context context) {
        this.models = models;
        this.context = context;
    }

    @Override
    public int getCount() {
        return models.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view.equals(object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.item_ikan, container, false);

        TextView namaikan;
        ImageView images;

        images = view.findViewById(R.id.image);
        namaikan = view.findViewById(R.id.namaikan);

        final ItemModel singleItem = models.get(position);
        namaikan.setText(singleItem.getNama_item());
        if (!singleItem.getFoto_item().isEmpty()) {
            PicassoTrustAll.getInstance(context)
                    .load(Constants.IMAGESITEM + singleItem.getFoto_item())
                    .resize(250, 250)
                    .into(images);
        }
        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
