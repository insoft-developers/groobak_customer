package com.groobak.customer.utils.api.service;

import com.groobak.customer.json.AllMerchantByNearResponseJson;
import com.groobak.customer.json.AllMerchantbyCatRequestJson;
import com.groobak.customer.json.AllTransResponseJson;
import com.groobak.customer.json.BankResponseJson;
import com.groobak.customer.json.BeritaDetailRequestJson;
import com.groobak.customer.json.BeritaDetailResponseJson;
import com.groobak.customer.json.ChangePassRequestJson;
import com.groobak.customer.json.DetailRequestJson;
import com.groobak.customer.json.EditprofileRequestJson;
import com.groobak.customer.json.GetAllMerchantbyCatRequestJson;
import com.groobak.customer.json.GetFiturResponseJson;
import com.groobak.customer.json.GetHomeRequestJson;
import com.groobak.customer.json.GetHomeResponseJson;
import com.groobak.customer.json.GetMerchantbyCatRequestJson;
import com.groobak.customer.json.GetAyoPulsaBaseResponse;
import com.groobak.customer.json.LoginRequestJson;
import com.groobak.customer.json.LoginResponseJson;
import com.groobak.customer.json.MerchantByCatResponseJson;
import com.groobak.customer.json.MerchantByIdResponseJson;
import com.groobak.customer.json.MerchantByNearResponseJson;
import com.groobak.customer.json.MerchantbyIdRequestJson;
import com.groobak.customer.json.MobilePulsaHealthBPJSBaseResponse;
import com.groobak.customer.json.MobilePulsaPLNCheckResponse;
import com.groobak.customer.json.MobileTopUpPostPaidStatusJson;
import com.groobak.customer.json.MobileTopUpRequestModel;
import com.groobak.customer.json.PrivacyRequestJson;
import com.groobak.customer.json.PrivacyResponseJson;
import com.groobak.customer.json.PromoRequestJson;
import com.groobak.customer.json.PromoResponseJson;
import com.groobak.customer.json.RateRequestJson;
import com.groobak.customer.json.RateResponseJson;
import com.groobak.customer.json.RegisterRequestJson;
import com.groobak.customer.json.RegisterResponseJson;
import com.groobak.customer.json.ResponseJson;
import com.groobak.customer.json.SearchMerchantbyCatRequestJson;
import com.groobak.customer.json.TopUpAyoPulsaRequestJson;
import com.groobak.customer.json.TopUpAyoPulsaResponseModel;
import com.groobak.customer.json.TopUpBaseResponse;
import com.groobak.customer.json.TopUpRequestResponse;
import com.groobak.customer.json.TopupRequestJson;
import com.groobak.customer.json.TopupResponseJson;
import com.groobak.customer.json.WalletRequestJson;
import com.groobak.customer.json.WalletResponseJson;
import com.groobak.customer.json.WithdrawRequestJson;
import com.groobak.customer.json.MobileTopUpResponseModel;
import com.groobak.customer.json.XenditPaymentRequestJson;
import com.groobak.customer.models.ayopulsa.PriceListDataModel;
import com.groobak.customer.models.ayopulsa.TopUpStatusModel;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Url;


public interface UserService {

    @POST("pelanggan/login")
    Call<LoginResponseJson> login(@Body LoginRequestJson param);

    @POST("pelanggan/kodepromo")
    Call<PromoResponseJson> promocode(@Body PromoRequestJson param);

    @POST("pelanggan/listkodepromo")
    Call<PromoResponseJson> listpromocode(@Body PromoRequestJson param);

    @POST("pelanggan/list_bank")
    Call<BankResponseJson> listbank(@Body WithdrawRequestJson param);

    @POST("pelanggan/changepass")
    Call<LoginResponseJson> changepass(@Body ChangePassRequestJson param);

    @POST("pelanggan/register_user")
    Call<RegisterResponseJson> register(@Body RegisterRequestJson param);

    @GET("pelanggan/detail_fitur")
    Call<GetFiturResponseJson> getFitur();

    @POST("pelanggan/forgot")
    Call<LoginResponseJson> forgot(@Body LoginRequestJson param);

    @POST("pelanggan/privacy")
    Call<PrivacyResponseJson> privacy(@Body PrivacyRequestJson param);

    @POST("pelanggan/home")
    Call<GetHomeResponseJson> home(@Body GetHomeRequestJson param);

    @POST("pelanggan/topupstripe")
    Call<TopupResponseJson> topup(@Body TopupRequestJson param);

    @POST("pelanggan/withdraw")
    Call<ResponseJson> withdraw(@Body WithdrawRequestJson param);

    @POST("pelanggan/rate_driver")
    Call<RateResponseJson> rateDriver(@Body RateRequestJson param);

    @POST("pelanggan/edit_profile")
    Call<RegisterResponseJson> editProfile(@Body EditprofileRequestJson param);

    @POST("pelanggan/wallet")
    Call<WalletResponseJson> wallet(@Body WalletRequestJson param);

    @POST("pelanggan/history_progress")
    Call<AllTransResponseJson> history(@Body DetailRequestJson param);

    @POST("pelanggan/detail_berita")
    Call<BeritaDetailResponseJson> beritadetail(@Body BeritaDetailRequestJson param);

    @POST("pelanggan/all_berita")
    Call<BeritaDetailResponseJson> allberita(@Body BeritaDetailRequestJson param);

    @POST("pelanggan/merchantbykategoripromo")
    Call<MerchantByCatResponseJson> getmerchanbycat(@Body GetMerchantbyCatRequestJson param);

    @POST("pelanggan/merchantbykategori")
    Call<MerchantByNearResponseJson> getmerchanbynear(@Body GetMerchantbyCatRequestJson param);

    @POST("pelanggan/allmerchantbykategori")
    Call<AllMerchantByNearResponseJson> getallmerchanbynear(@Body GetAllMerchantbyCatRequestJson param);

    @POST("pelanggan/itembykategori")
    Call<MerchantByIdResponseJson> getitembycat(@Body GetAllMerchantbyCatRequestJson param);

    @POST("pelanggan/searchmerchant")
    Call<AllMerchantByNearResponseJson> searchmerchant(@Body SearchMerchantbyCatRequestJson param);

    @POST("pelanggan/allmerchant")
    Call<AllMerchantByNearResponseJson> allmerchant(@Body AllMerchantbyCatRequestJson param);

    @POST("pelanggan/merchantbyid")
    Call<MerchantByIdResponseJson> merchantbyid(@Body MerchantbyIdRequestJson param);

    @POST()
    Call<MobileTopUpResponseModel> getAllMobileTopUpType(@Url String url, @Body MobileTopUpRequestModel param);

    @POST()
    Call<MobilePulsaPLNCheckResponse> checkMobilePulsaPlnSubscriber(@Url String url, @Body MobileTopUpRequestModel param);

    @Headers({"Content-Type: application/json"})
    @POST("https://mobilepulsa.net/api/v1/bill/check")
    Call<MobilePulsaHealthBPJSBaseResponse> checkBPJSKesSubscriber(@Body MobileTopUpRequestModel param);

    @POST()
    Call<TopUpRequestResponse> requestTopUp(@Url String url, @Body MobileTopUpRequestModel param);

    @Headers({"Content-Type: application/json"})
    @POST()
    Call<TopUpBaseResponse> requestTopUpWithBaseResponse(@Url String url, @Body MobileTopUpRequestModel param);

    @Headers({"Content-Type: application/json"})
    @POST("https://mobilepulsa.net/api/v1/bill/check")
    Call<MobileTopUpPostPaidStatusJson> checkPostPaidStatus(@Body MobileTopUpRequestModel param);

    @POST("http://share.karinesia.com/api/xendit/data_post")
    Call<ResponseBody> xenditPaymentRequest(@Body XenditPaymentRequestJson param);

    @GET("https://ayo-pesan.com/api/v1/prabayar/pulsa/operator")
    Call<GetAyoPulsaBaseResponse> getAyoPesanPulsaAndPaketDataPriceList(@Header("Authorization") String header);

    @GET("https://ayo-pesan.com/api/v1/prabayar/listrik/nominal")
    Call<PriceListDataModel> getAyoPesanPlnPriceList(@Header("Authorization") String header);

    @POST("https://ayo-pesan.com/api/v1/prabayar/pulsa/topup")
    Call<TopUpAyoPulsaResponseModel> ayoPesanTopUp(@Body TopUpAyoPulsaRequestJson params, @Header("Authorization") String header);

    @GET
    Call<TopUpStatusModel> checkAyoPesanStatusPayment(@Url String url, @Header("Authorization") String header);

}
