package che.teamextension.teamextension.adapter;

import android.arch.lifecycle.LiveData;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import che.teamextension.teamextension.R;
import che.teamextension.teamextension.data.Transaction;

public class TransactionsListAdapter extends BaseAdapter {
    private LiveData<List<Transaction>> liveData;

    public TransactionsListAdapter(LiveData<List<Transaction>> liveData) {
        this.liveData = liveData;
    }

    @Override
    public int getCount() {
        return liveData.getValue().size();
    }

    @Override
    public Object getItem(int i) {
        return liveData.getValue().get(i);
    }

    @Override
    public long getItemId(int i) {
        return liveData.getValue().get(i).id;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        Transaction t = liveData.getValue().get(i);
        if (view == null)
            view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.list_item_transactions, null);
        view.setTag(t.sku);
        ((TextView) view.findViewById(R.id.item_sku)).setText(t.sku);
        ((TextView) view.findViewById(R.id.item_amount)).setText("" + t.amount);
        ((TextView) view.findViewById(R.id.item_currency)).setText(t.currency);
        return view;
    }
}
