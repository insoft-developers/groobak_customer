package com.groobak.customer.utils.api;

import com.groobak.customer.json.GetAyoPulsaBaseResponse;
import com.groobak.customer.json.TopUpAyoPulsaRequestJson;
import com.groobak.customer.json.TopUpAyoPulsaResponseModel;
import com.groobak.customer.models.ayopulsa.PriceListDataModel;
import com.groobak.customer.models.ayopulsa.TopUpStatusModel;
import com.groobak.customer.utils.api.service.UserService;

import javax.inject.Singleton;
import retrofit2.Call;

public class AyoPulsaApiHelper {

    private static AyoPulsaApiHelper obj;
    private UserService userService;
    private AyoPulsaApiHelper(){
        userService = ServiceGenerator.createService(UserService.class);
    }

    public static AyoPulsaApiHelper getInstance() {
        if (obj == null) {
            synchronized (Singleton.class) {
                if (obj == null) {
                    obj = new AyoPulsaApiHelper();
                }
            }
        }
        return obj;
    }

    private String header;
    private String password;

    public void setHeader(String value) {
        header = value;
    }

    public void setPassword(String value) {
        password = value;
    }

    public Call<GetAyoPulsaBaseResponse> getAyoPesanPulsaPriceList() {
        UserService service = ServiceGenerator.createService(UserService.class);
        return service.getAyoPesanPulsaAndPaketDataPriceList(header);
    }

    public Call<PriceListDataModel> getAyoPesanPlnPriceList() {
        return userService.getAyoPesanPlnPriceList(header);
    }

    public Call<TopUpAyoPulsaResponseModel> topUpRequest(TopUpAyoPulsaRequestJson params) {
        params.setPassword(password);
        return userService.ayoPesanTopUp(params, header);
    }

    public Call<TopUpStatusModel> checkAyoPesanPaymentStatus(String refId) {
        String url = "https://ayo-pesan.com/api/v1/prabayar/check-status/"+ refId;
        return userService.checkAyoPesanStatusPayment(url, header);
    }
}
