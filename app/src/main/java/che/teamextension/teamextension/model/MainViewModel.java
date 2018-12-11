package che.teamextension.teamextension.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import che.teamextension.teamextension.TEApplication;
import che.teamextension.teamextension.data.ExchangeRate;
import che.teamextension.teamextension.data.Transaction;
import che.teamextension.teamextension.db.TransactionDAO;
import che.teamextension.teamextension.repo.DataRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainViewModel extends ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();

    private MutableLiveData<Map<String, Float>> currenciesData = new MutableLiveData<>();
    private MutableLiveData<List<Transaction>> transactionsData = new MutableLiveData<>();
    private TransactionDAO transactionDAO = TEApplication.getInstance().getDatabase().transactionsDAO();

    public MainViewModel() {
        currenciesData.setValue(new HashMap<>());
        transactionsData.setValue(new ArrayList<>());
        loadRatesData();
    }

    public MutableLiveData<Map<String, Float>> getRatesData() {
        return currenciesData;
    }

    public MutableLiveData<List<Transaction>> getTransactionsData() {
        return transactionsData;
    }

    private void loadRatesData() {
        DataRepository.getService().requestRates().enqueue(new Callback<List<ExchangeRate>>() {
            @Override
            public void onResponse(@NonNull Call<List<ExchangeRate>> call, @NonNull Response<List<ExchangeRate>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "loadRatesData onResponse isSuccessful " + response.code());
                    calculateAbsentRates(response.body());
                    loadTransactionsData();
                } else
                    Log.d(TAG, "loadRatesData onResponse error " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<List<ExchangeRate>> call, @NonNull Throwable t) {
                Log.d(TAG, "loadRatesData onFailure error " + t.getMessage());
            }
        });
    }

    private void loadTransactionsData() {
        DataRepository.getService().requestTransactions().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "loadTransactionsData onResponse isSuccessful " + response.code());
                    List<Transaction> transactions = response.body();
                    Log.d(TAG, "loadTransactionsData onResponse transactions " + transactions.size());
                    for (Transaction transaction : transactions)
                        transactionDAO.insert(transaction);
                    transactionsData.postValue(transactions);
                } else
                    Log.d(TAG, "loadTransactionsData onResponse error " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<List<Transaction>> call, @NonNull Throwable t) {
                Log.d(TAG, "loadTransactionsData onFailure error " + t.getMessage());
            }
        });
    }

    private void calculateAbsentRates(List<ExchangeRate> rates) {
        HashMap<String, ExchangeRate> ratesMap = new HashMap<>();
        for (ExchangeRate rate : rates)
            ratesMap.put(rate.getId(), rate);
        HashMap<String, Float> currencies = new HashMap<>();
        while (currencies.size() < ExchangeRate.CURRENCIES_COUNT - 1) {
            ExchangeRate[] ratesArray = new ExchangeRate[ratesMap.size()];
            ratesMap.values().toArray(ratesArray);
            for (ExchangeRate rate : ratesArray)
                if (rate.getTo().equals(ExchangeRate.GOLD))
                    currencies.put(rate.getFrom(), rate.getRate());
                else if (!rate.getFrom().equals(ExchangeRate.GOLD)) {
                    ExchangeRate crossRate = ratesMap.get(rate.getTo() + ExchangeRate.GOLD);
                    if (crossRate != null)
                        ratesMap.put(rate.getFrom() + ExchangeRate.GOLD,
                                new ExchangeRate(rate.getFrom(), ExchangeRate.GOLD, 1 / rate.getRate() / crossRate.getRate()));
                }
        }
        currenciesData.postValue(currencies);
    }

    public void applyTransactionsFilter(String sku) {
        Log.d(TAG, "applyTransactionsFilter " + sku);
        transactionsData.postValue(transactionDAO.getBySku(sku));
    }

    public void resetTransactions() {
        Log.d(TAG, "resetTransactions");
        transactionsData.postValue(transactionDAO.getAll());
    }

    @Override
    protected void onCleared() {
        Log.d(TAG, "onCleared");
        TEApplication.getInstance().getDatabase().clearAllTables();
        super.onCleared();
    }
}
