package che.teamextension.teamextension.model;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;

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

    private TransactionDAO transactionDAO = TEApplication.getInstance().getDatabase().transactionsDAO();
    private MutableLiveData<List<Transaction>> transactionsData = new MutableLiveData<>();
    private MutableLiveData<Boolean> dataProcessingFinished = new MutableLiveData<>();
    private MutableLiveData<Float> totalGold = new MutableLiveData<>();
    private HashMap<String, Float> currencies;
    private String skuFilter;

    public MainViewModel() {
        transactionsData.setValue(new ArrayList<>());
        totalGold.setValue(0f);
        loadRatesData();
    }

    public MutableLiveData<List<Transaction>> getTransactionsData() {
        return transactionsData;
    }

    public MutableLiveData<Boolean> getProcessingFinished() {
        return dataProcessingFinished;
    }

    public MutableLiveData<Float> getTotalGoldData() {
        return totalGold;
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

    private void calculateAbsentRates(List<ExchangeRate> rates) {
        HashMap<String, ExchangeRate> ratesMap = new HashMap<>();
        for (ExchangeRate rate : rates)
            ratesMap.put(rate.getId(), rate);
        currencies = new HashMap<>();
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
                                new ExchangeRate(rate.getFrom(), ExchangeRate.GOLD, rate.getRate() * crossRate.getRate()));
                }
        }
        for (String name : currencies.keySet())
            Log.d(TAG, "rates " + name + " " + currencies.get(name));
    }

    private void loadTransactionsData() {
        DataRepository.getService().requestTransactions().enqueue(new Callback<List<Transaction>>() {
            @Override
            public void onResponse(@NonNull Call<List<Transaction>> call, @NonNull Response<List<Transaction>> response) {
                if (response.isSuccessful()) {
                    Log.d(TAG, "loadTransactionsData onResponse isSuccessful " + response.code());
                    List<Transaction> transactions = response.body();
                    Log.d(TAG, "loadTransactionsData onResponse transactions " + transactions.size());
                    transactionsData.postValue(transactions);
                    Executors.newSingleThreadExecutor().execute(new InsertJob(transactions));
                } else
                    Log.d(TAG, "loadTransactionsData onResponse error " + response.code());
            }

            @Override
            public void onFailure(@NonNull Call<List<Transaction>> call, @NonNull Throwable t) {
                Log.d(TAG, "loadTransactionsData onFailure error " + t.getMessage());
            }
        });
    }

    private class InsertJob implements Runnable {

        List<Transaction> transactions;

        InsertJob(List<Transaction> transactions) {
            this.transactions = transactions;
        }

        @Override
        public void run() {
            for (Transaction transaction : transactions)
                transactionDAO.insert(transaction);
            dataProcessingFinished.postValue(true);
            if (skuFilter != null) {
                String filter = skuFilter;      //this strange moves needed for reapplying the same
                skuFilter = null;               //filter on completely filled db, so this reapplying
                applyTransactionsFilter(filter);//may return bigger list of items
            } else
                resetTransactionsFilter();
        }
    }

    public void applyTransactionsFilter(String sku) {
        if (sku.equals(skuFilter)) return;
        skuFilter = sku;
        List<Transaction> filterdList = transactionDAO.getBySku(skuFilter);
        updateTotal(filterdList);
        transactionsData.postValue(filterdList);
        Log.d(TAG, "applyTransactionsFilter " + skuFilter);
    }

    public void resetTransactionsFilter() {
        Log.d(TAG, "resetTransactionsFilter");
        skuFilter = null;
        List<Transaction> transactions = transactionDAO.getAll();
        updateTotal(transactions);
        transactionsData.postValue(transactions);
    }

    private void updateTotal(List<Transaction> transactions) {
        float total = 0;
        for (Transaction transaction : transactions)
            total += transaction.currency.equals(ExchangeRate.GOLD) ? transaction.amount :
                    transaction.amount * currencies.get(transaction.currency);
        totalGold.postValue(total);
    }
}