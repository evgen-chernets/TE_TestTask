package che.teamextension.teamextension;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.persistence.room.Room;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import che.teamextension.teamextension.adapter.TransactionsListAdapter;
import che.teamextension.teamextension.data.ExchangeRate;
import che.teamextension.teamextension.data.Transaction;
import che.teamextension.teamextension.model.MainViewModel;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    private TransactionsListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel model = ViewModelProviders.of(this).get(MainViewModel.class);

        LiveData<List<ExchangeRate>> ratesData = model.getRatesData();
        ratesData.observe(this, rates -> {
            for (ExchangeRate rate : rates)
                Log.d(TAG,"rates " + rate.toString());
        });

        LiveData<List<Transaction>> transactionsData = model.getTransactionsData();
        transactionsData.observe(this, transactions -> {
            adapter.notifyDataSetChanged();
//            for (Transaction transaction : transactions)
            Log.d(TAG, "transactions.size() " + transactions.size());
        });

        adapter = new TransactionsListAdapter(transactionsData);
        ListView transactionsList = findViewById(R.id.transactions_list);
        transactionsList.setAdapter(adapter);
        transactionsList.setOnItemClickListener((adapterView, view, i, l) -> model.applyTransactionsFilter((String) view.getTag()));

        findViewById(R.id.button_reset).setOnClickListener(view -> model.resetTransactions());
    }
}
