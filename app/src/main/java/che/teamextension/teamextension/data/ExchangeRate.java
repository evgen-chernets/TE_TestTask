package che.teamextension.teamextension.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ExchangeRate {

    @Currency.Name
    private final String from;
    @Currency.Name
    private final String to;
    private final float rate;

    public ExchangeRate(String from, String to, float rate) {
        this.from = from;
        this.to = to;
        this.rate = rate;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public float getRate() {
        return rate;
    }

    public String getId() {
        return from + to;
    }

    @NonNull
    @Override
    public String toString() {
        return "from:" + from + " to:" + to + " rate:" + rate;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        return obj instanceof ExchangeRate && ((ExchangeRate)obj).getId().equals(getId());
    }
}
