package che.teamextension.teamextension.repo;

import java.util.List;

import che.teamextension.teamextension.data.ExchangeRate;
import che.teamextension.teamextension.data.Transaction;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;

public class DataRepository {

    private static ApiService service;

    static {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://merch.android.dev.testapi.online/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(ApiService.class);
    }

    public static ApiService getService() {
        return service;
    }

    public interface ApiService {
        @GET("rates.json")
        Call<List<ExchangeRate>> requestRates();

        @GET("transactions.json")
        Call<List<Transaction>> requestTransactions();
    }
}
