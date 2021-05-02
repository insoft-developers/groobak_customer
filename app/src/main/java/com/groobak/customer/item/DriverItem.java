package com.groobak.customer.item;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.ornolfr.ratingview.RatingView;
import com.groobak.customer.R;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.DriverModel;
import com.groobak.customer.models.RatingModel;
import com.groobak.customer.utils.PicassoTrustAll;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class DriverItem extends PagerAdapter {

    private List<DriverModel> models;
    private Context context;

    public DriverItem(List<DriverModel> models, Context context) {
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
        View view = layoutInflater.inflate(R.layout.item_groobak, container, false);

        TextView namagroobak;
        ImageView images;

        images = view.findViewById(R.id.image);
        namagroobak = view.findViewById(R.id.txtnamagroobak);

        final DriverModel singleItem = models.get(position);
        namagroobak.setText(singleItem.getNamaDriver());
        if (!singleItem.getFoto().isEmpty()) {
            PicassoTrustAll.getInstance(context)
                    .load(singleItem.getFoto())
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
