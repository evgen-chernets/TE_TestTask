package che.teamextension.teamextension.data;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "transactions")
public class Transaction {
    private static long counter;
    @PrimaryKey
    public long id;
    public String sku;
    public float amount;

    @ExchangeRate.Currency
    public String currency;

    public Transaction() {
        id = counter++;
    }

    @NonNull
    @Override
    public String toString() {
        return "id:" + id + " sku:" + sku + " amount:" + amount + " currency:" + currency;
    }
}
