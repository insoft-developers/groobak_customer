package com.groobak.customer.item;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.github.ornolfr.ratingview.RatingView;
import com.groobak.customer.R;
import com.groobak.customer.activity.PilihGroobakActivity;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.models.DriverModel;
import com.groobak.customer.models.RatingModel;
import com.groobak.customer.utils.PicassoTrustAll;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.groobak.customer.constants.Constants.df;


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

        TextView namagroobak, jenisikan;
        ImageView images;
        RelativeLayout rootlayout;

        images = view.findViewById(R.id.image);
        namagroobak = view.findViewById(R.id.txtnamagroobak);
        jenisikan = view.findViewById(R.id.txtjenisikan);
        rootlayout = view.findViewById(R.id.rootLayout);



        final DriverModel singleItem = models.get(position);
        namagroobak.setText(singleItem.getNamaDriver());
        jenisikan.setText(singleItem.getJenis_ikan()+" Jenis Ikan");
        if (!singleItem.getFoto().isEmpty()) {
            PicassoTrustAll.getInstance(context)
                    .load(singleItem.getFoto())
                    .resize(250, 250)
                    .into(images);
        }

        Double jarak_d = Double.parseDouble(singleItem.getDistance());
        DecimalFormat df = new DecimalFormat("#.##");
        String jarak_kirim = df.format(jarak_d);

        rootlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, PilihGroobakActivity.class);
                intent.putExtra("id_driver_intent", singleItem.getId());
                intent.putExtra("nama_groobak_intent", singleItem.getNamaDriver());
                intent.putExtra("nama_orang_intent", singleItem.getNamaDriver());
                intent.putExtra("jarak_intent", jarak_kirim);
                intent.putExtra("foto_intent", singleItem.getFoto());
                intent.putExtra("jenis_ikan_intent", singleItem.getJenis_ikan());
                context.startActivity(intent);
            }
        });

        container.addView(view, 0);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }
}
