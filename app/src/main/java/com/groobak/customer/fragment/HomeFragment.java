package com.groobak.customer.fragment;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.google.android.gms.maps.model.Marker;
import com.groobak.customer.R;
import com.groobak.customer.activity.AllBeritaActivity;
import com.groobak.customer.activity.IntroActivity;
import com.groobak.customer.activity.PpobActivity;
import com.groobak.customer.activity.PromoActivity;
import com.groobak.customer.activity.TopupSaldoActivity;
import com.groobak.customer.activity.WaActivity;
import com.groobak.customer.activity.WalletActivity;
import com.groobak.customer.constants.BaseApp;
import com.groobak.customer.constants.Constants;
import com.groobak.customer.item.AllFiturItem;
import com.groobak.customer.item.BeritaItem;
import com.groobak.customer.item.CatMerchantItem;
import com.groobak.customer.item.CatMerchantNearItem;
import com.groobak.customer.item.DriverItem;
import com.groobak.customer.item.FiturItem;
import com.groobak.customer.item.MerchantItem;
import com.groobak.customer.item.MerchantNearItem;
import com.groobak.customer.item.RatingItem;
import com.groobak.customer.item.SliderItem;
import com.groobak.customer.json.GetHomeRequestJson;
import com.groobak.customer.json.GetHomeResponseJson;
import com.groobak.customer.json.GetMerchantbyCatRequestJson;
import com.groobak.customer.json.GetNearRideCarRequestJson;
import com.groobak.customer.json.GetNearRideCarResponseJson;
import com.groobak.customer.json.MerchantByCatResponseJson;
import com.groobak.customer.json.MerchantByNearResponseJson;
import com.groobak.customer.models.AllFiturModel;
import com.groobak.customer.models.CatMerchantModel;
import com.groobak.customer.models.DriverModel;
import com.groobak.customer.models.FiturDataModel;
import com.groobak.customer.models.FiturModel;
import com.groobak.customer.models.MerchantModel;
import com.groobak.customer.models.MerchantNearModel;
import com.groobak.customer.models.User;
import com.groobak.customer.utils.Log;
import com.groobak.customer.utils.SettingPreference;
import com.groobak.customer.utils.Utility;
import com.groobak.customer.utils.api.AyoPulsaApiHelper;
import com.groobak.customer.utils.api.ServiceGenerator;
import com.groobak.customer.utils.api.service.BookService;
import com.groobak.customer.utils.api.service.UserService;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import io.realm.Realm;
import me.relex.circleindicator.CircleIndicator;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();
    private CircleIndicator circleIndicator, circleIndicatorreview;
    private Context context;
    private SliderItem adapter;
    private Integer[] colors = null;
    private FiturItem fiturItem;
    private DriverItem driverItem;
    private BeritaItem beritaItem;
    private MerchantItem merchantItem;
    private MerchantNearItem merchantNearItem;
    private CatMerchantNearItem catMerchantNearItem;
    private CatMerchantItem catMerchantItem;
    private SettingPreference sp;
    private List<MerchantModel> click;
    private List<MerchantNearModel> clicknear;
    private ArrayList<FiturDataModel> fiturlist;
    private List<FiturModel> fiturdata;
    private List<AllFiturModel> allfiturdata;
    private BottomSheetBehavior mBehavior;
    private BottomSheetDialog mBottomSheetDialog;
    private TextView txt_lokasi_saya, txt_welcome;
    private ViewPager rvgroobak;
    private List<DriverModel> driverAvailable;

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View getView = inflater.inflate(R.layout.fragment_home, container, false);
        context = getContext();
        View bottom_sheet = getView.findViewById(R.id.bottom_sheet);
        mBehavior = BottomSheetBehavior.from(bottom_sheet);
        RelativeLayout topup = getView.findViewById(R.id.topup);
        RelativeLayout fitur = getView.findViewById(R.id.fitur);
        txt_lokasi_saya = getView.findViewById(R.id.txt_lokasi_saya);
        txt_welcome = getView.findViewById(R.id.txt_welcome);
        rvgroobak = getView.findViewById(R.id.viewPagerGroobak);
        circleIndicatorreview = getView.findViewById(R.id.indicator_unselected_background_review);
        RelativeLayout detail = getView.findViewById(R.id.detail);
        sp = new SettingPreference(context);
        RelativeLayout promo = getView.findViewById(R.id.promo);
        fiturlist = new ArrayList<>();
        driverAvailable = new ArrayList<>();


        Integer[] colors_temp = {
                getResources().getColor(R.color.transparent),
                getResources().getColor(R.color.transparent),
                getResources().getColor(R.color.transparent),
                getResources().getColor(R.color.transparent)
        };

        topup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, TopupSaldoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        promo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, PromoActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        fitur.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WaActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(context, WalletActivity.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);

            }
        });

        rvgroobak.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

                if (position < (driverItem.getCount() - 1) && position < (colors.length - 1)) {
                    rvgroobak.setBackgroundColor(

                            (Integer) argbEvaluator.evaluate(
                                    positionOffset,
                                    colors[position],
                                    colors[position + 1]
                            )
                    );
                } else {
                    rvgroobak.setBackgroundColor(colors[colors.length - 1]);
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        FusedLocationProviderClient mFusedLocation = LocationServices.getFusedLocationProviderClient(context);
        mFusedLocation.getLastLocation().addOnSuccessListener(requireActivity(), new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                String fitur_saya = "1";
                if (location != null) {
                    gethome(location);
                    fetchNearDriver(location.getLatitude(), location.getLongitude(), fitur_saya);
                    Constants.LATITUDE = location.getLatitude();
                    Constants.LONGITUDE = location.getLongitude();
                    Log.e("BEARING:", String.valueOf(location.getBearing()));
                }else{
                    fetchNearDriver(3.5290314, 98.7345363, fitur_saya);
                }
            }
        });

        colors = colors_temp;
        return getView;
    }


    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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



    private void gethome(final Location location) {
        String lokasi_saya = getCompleteAddressString(location.getLatitude(), location.getLongitude());
        txt_lokasi_saya.setText(lokasi_saya);
        User loginUser = BaseApp.getInstance(context).getLoginUser();
        UserService userService = ServiceGenerator.createService(
                UserService.class, loginUser.getNoTelepon(), loginUser.getPassword());
        GetHomeRequestJson param = new GetHomeRequestJson();
        param.setId(loginUser.getId());
        param.setLat(String.valueOf(location.getLatitude()));
        param.setLon(String.valueOf(location.getLongitude()));
        param.setPhone(loginUser.getNoTelepon());
        userService.home(param).enqueue(new Callback<GetHomeResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<GetHomeResponseJson> call, @NonNull Response<GetHomeResponseJson> response) {
                if (response.isSuccessful()) {
                    if (Objects.requireNonNull(response.body()).getMessage().equalsIgnoreCase("success")) {
                        Log.v("TAG", "" + response.body());
                        sp.updateCurrency(response.body().getCurrency());
                        sp.updateabout(response.body().getAboutus());
                        sp.updateemail(response.body().getEmail());
                        sp.updatephone(response.body().getPhone());
                        sp.updateweb(response.body().getWebsite());
                        sp.updatempstatus(response.body().getMpstatus());
                        sp.updatempactive(response.body().getMpactive());
                        sp.updateMobilepulsausername(response.body().getMobilepulsausername());
                        sp.updateMobilepulsaapikey(response.body().getMobilepulsaapikey());
                        sp.updatecurrencytext(response.body().getCurrencytext());
                        sp.updatehargapulsa(response.body().getHargaPulsa());
                        AyoPulsaApiHelper.getInstance().setPassword(response.body().getAyoPesanApiPassword());
                        AyoPulsaApiHelper.getInstance().setHeader("Bearer "+ response.body().getAyoPesanApiToken());

                        fiturdata = response.body().getFitur();
                        allfiturdata = response.body().getAllfitur();
                        for (int i = 0; i < fiturdata.size(); i++) {
                            FiturDataModel fiturmodel = new FiturDataModel();
                            fiturmodel.setIdFitur(fiturdata.get(i).getIdFitur());
                            fiturmodel.setFitur(fiturdata.get(i).getFitur());
                            fiturmodel.setIcon(fiturdata.get(i).getIcon());
                            fiturmodel.setHome(fiturdata.get(i).getHome());
                            fiturlist.add(fiturmodel);
                        }

                        if (fiturdata.size() > 6) {
                            FiturDataModel fiturmodel = new FiturDataModel();
                            fiturmodel.setIdFitur(100);
                            fiturmodel.setFitur("Semua");
                            fiturmodel.setHome("0");
                            fiturlist.add(fiturmodel);
                        }

                        fiturItem = new FiturItem(getActivity(), fiturlist, R.layout.item_fitur, new FiturItem.OnItemClickListener() {
                            @Override
                            public void onItemClick(FiturDataModel item) {
                                sheetlist();
                            }
                        });

                        User user = response.body().getData().get(0);
                        saveUser(user);
                        if (HomeFragment.this.getActivity() != null) {
                            Realm realm = BaseApp.getInstance(HomeFragment.this.getActivity()).getRealmInstance();
                            User loginUser = BaseApp.getInstance(HomeFragment.this.getActivity()).getLoginUser();
                            realm.beginTransaction();
                            if (response.body().getSaldo() != null && !response.body().getSaldo().isEmpty()) {
                                loginUser.setWalletSaldo(Long.parseLong(response.body().getSaldo()));
                            }
                            realm.commitTransaction();
                        }
                    } else {
                        Realm realm = BaseApp.getInstance(context).getRealmInstance();
                        realm.beginTransaction();
                        realm.delete(User.class);
                        realm.commitTransaction();
                        BaseApp.getInstance(context).setLoginUser(null);
                        startActivity(new Intent(context, IntroActivity.class)
                                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
                        requireActivity().finish();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetHomeResponseJson> call, @NonNull Throwable t) {

            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        User loginUser = BaseApp.getInstance(context).getLoginUser();
        txt_welcome.setText("Selamat Datang, "+loginUser.getFullnama());

    }

    private void saveUser(User user) {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.delete(User.class);
        realm.copyToRealm(user);
        realm.commitTransaction();
        BaseApp.getInstance(context).setLoginUser(user);
    }

    private void sheetlist() {
        if (mBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        @SuppressLint("InflateParams") final View mDialog = getLayoutInflater().inflate(R.layout.sheet_category, null);
        RecyclerView view = mDialog.findViewById(R.id.category);

        view.setHasFixedSize(true);
        view.setLayoutManager(new GridLayoutManager(getActivity(), 4));

        AllFiturItem allfiturItem = new AllFiturItem(getActivity(), allfiturdata, R.layout.item_fitur);
        view.setAdapter(allfiturItem);

        mBottomSheetDialog = new BottomSheetDialog(context);
        mBottomSheetDialog.setContentView(mDialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Objects.requireNonNull(mBottomSheetDialog.getWindow()).addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        mBottomSheetDialog.show();
        mBottomSheetDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                mBottomSheetDialog = null;
            }
        });
    }


    private void fetchNearDriver(double latitude, double longitude, String fitur) {
        if (driverAvailable != null) {
            driverAvailable.clear();
        }

        User loginUser = BaseApp.getInstance(context).getLoginUser();
        BookService service = ServiceGenerator.createService(BookService.class, loginUser.getEmail(), loginUser.getPassword());
        GetNearRideCarRequestJson param = new GetNearRideCarRequestJson();
        param.setLatitude(latitude);
        param.setLongitude(longitude);
        param.setFitur(fitur);

        service.getNearRide(param).enqueue(new Callback<GetNearRideCarResponseJson>() {
            @Override
            public void onResponse(@NonNull Call<GetNearRideCarResponseJson> call, @NonNull Response<GetNearRideCarResponseJson> response) {
                if (response.isSuccessful()) {
                    driverAvailable = Objects.requireNonNull(response.body()).getData();
                    if (driverAvailable.isEmpty()) {
                        rvgroobak.setVisibility(View.GONE);
                    } else {
                        driverItem = new DriverItem(driverAvailable, context);
                        rvgroobak.setAdapter(driverItem);
                        circleIndicatorreview.setViewPager(rvgroobak);
                        rvgroobak.setPadding(10, 0, 320, 0);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<GetNearRideCarResponseJson> call, @NonNull Throwable t) {

            }
        });

    }


}
