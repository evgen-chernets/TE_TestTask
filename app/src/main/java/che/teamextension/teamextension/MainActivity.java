package che.teamextension.teamextension;

import android.arch.lifecycle.ViewModelProviders;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;


import che.teamextension.teamextension.adapter.TransactionsListAdapter;
import che.teamextension.teamextension.model.MainViewModel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MainViewModel model = ViewModelProviders.of(this).get(MainViewModel.class);

        TransactionsListAdapter adapter = new TransactionsListAdapter(model.getTransactionsData());
        ListView transactionsList = findViewById(R.id.transactions_list);
        transactionsList.setAdapter(adapter);
        transactionsList.setOnItemClickListener((adapterView, view, i, l) ->
                model.applyTransactionsFilter((String) view.getTag()));

        findViewById(R.id.button_reset).setOnClickListener(view -> model.resetTransactionsFilter());

        model.getTransactionsData().observe(this, transactions -> adapter.notifyDataSetChanged());

        model.getTotalGoldData().observe(this, total ->
                ((TextView)findViewById(R.id.total_text_view)).setText(getString(R.string.total, total)));

        model.getProcessingFinished().observe(this, finished ->
                findViewById(R.id.progress_group).setVisibility(View.GONE));
    }
}