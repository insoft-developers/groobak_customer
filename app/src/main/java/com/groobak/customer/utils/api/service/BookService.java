package com.groobak.customer.utils.api.service;

import com.groobak.customer.json.CheckStatusTransaksiRequest;
import com.groobak.customer.json.CheckStatusTransaksiResponse;
import com.groobak.customer.json.DetailRequestJson;
import com.groobak.customer.json.DetailTransResponseJson;
import com.groobak.customer.json.GetItemResponseJson;
import com.groobak.customer.json.GetNearRideCarRequestJson;
import com.groobak.customer.json.GetNearRideCarResponseJson;
import com.groobak.customer.json.ItemRequestIkanJson;
import com.groobak.customer.json.ItemRequestJson;
import com.groobak.customer.json.KatalogRequestJson;
import com.groobak.customer.json.KatalogResponseJson;
import com.groobak.customer.json.LokasiDriverRequest;
import com.groobak.customer.json.LokasiDriverResponse;
import com.groobak.customer.json.RideCarRequestJson;
import com.groobak.customer.json.RideCarResponseJson;
import com.groobak.customer.json.SendRequestJson;
import com.groobak.customer.json.SendResponseJson;
import com.groobak.customer.json.fcm.CancelBookRequestJson;
import com.groobak.customer.json.fcm.CancelBookResponseJson;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;


public interface BookService {
    @POST("pelanggan/list_ride_perikan")
    Call<GetNearRideCarResponseJson> groobakperikan(@Body GetNearRideCarRequestJson param);

    @POST("pelanggan/getikanbyjumlahgroobak")
    Call<KatalogResponseJson> katalogikan(@Body KatalogRequestJson param);

    @POST("pelanggan/listitembyid")
    Call<GetItemResponseJson> getItem(@Body ItemRequestIkanJson param);

    @POST("pelanggan/list_ride")
    Call<GetNearRideCarResponseJson> getNearRide(@Body GetNearRideCarRequestJson param);

    @POST("pelanggan/list_ikan_terbanyak")
    Call<GetNearRideCarResponseJson> getGroobakTerlengkap(@Body GetNearRideCarRequestJson param);

    @GET("pelanggan/daftar_makanan")
    Call<GetItemResponseJson> getDaftarMakanan();

    @POST("pelanggan/list_car")
    Call<GetNearRideCarResponseJson> getNearCar(@Body GetNearRideCarRequestJson param);

    @POST("pelanggan/request_transaksi")
    Call<RideCarResponseJson> requestTransaksi(@Body RideCarRequestJson param);

    @POST("pelanggan/inserttransaksimerchant")
    Call<RideCarResponseJson> requestTransaksiMerchant(@Body ItemRequestJson param);

    @POST("pelanggan/request_transaksi_send")
    Call<SendResponseJson> requestTransaksisend(@Body SendRequestJson param);

    @POST("pelanggan/check_status_transaksi")
    Call<CheckStatusTransaksiResponse> checkStatusTransaksi(@Body CheckStatusTransaksiRequest param);

    @POST("pelanggan/user_cancel")
    Call<CancelBookResponseJson> cancelOrder(@Body CancelBookRequestJson param);

    @POST("pelanggan/liat_lokasi_driver")
    Call<LokasiDriverResponse> liatLokasiDriver(@Body LokasiDriverRequest param);

    @POST("pelanggan/detail_transaksi")
    Call<DetailTransResponseJson> detailtrans(@Body DetailRequestJson param);


}
