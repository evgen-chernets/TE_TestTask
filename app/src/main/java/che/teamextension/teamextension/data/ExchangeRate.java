package che.teamextension.teamextension.data;

import android.support.annotation.NonNull;

public class ExchangeRate {

    @Currency.Name
    private String from;
    @Currency.Name
    private String to;
    private float rate;

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public float getRate() {
        return rate;
    }

    @NonNull
    @Override
    public String toString() {
        return "from:" + from + " to:" + to + " rate:" + rate;
    }
}
