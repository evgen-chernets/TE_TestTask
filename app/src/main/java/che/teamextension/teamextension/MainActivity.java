package che.teamextension.teamextension;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

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

        LiveData<Map<String, Float>> currencies = model.getRatesData();
        currencies.observe(this, rates -> {
            for (String name : currencies.getValue().keySet())
                Log.d(TAG, "rates " + name + " " + currencies.getValue().get(name));
        });

        LiveData<List<Transaction>> transactionsData = model.getTransactionsData();
        transactionsData.observe(this, transactions -> {
            adapter.notifyDataSetChanged();
            float total = 0;
            for (Transaction transaction : transactions)
                total += transaction.currency.equals(ExchangeRate.GOLD) ? transaction.amount :
                        transaction.amount * currencies.getValue().get(transaction.currency);
            ((TextView)findViewById(R.id.total_text_view)).setText(getString(R.string.total, total));
            Log.d(TAG, "transactions.size() " + transactions.size());
        });

        adapter = new TransactionsListAdapter(transactionsData);
        ListView transactionsList = findViewById(R.id.transactions_list);
        transactionsList.setAdapter(adapter);
        transactionsList.setOnItemClickListener((adapterView, view, i, l) -> model.applyTransactionsFilter((String) view.getTag()));

        findViewById(R.id.button_reset).setOnClickListener(view -> model.resetTransactions());

        model.getProcessingFinished().observe(this, finished -> findViewById(R.id.progress_group).setVisibility(View.GONE));
    }
}
