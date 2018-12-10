package che.teamextension.teamextension.db;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import che.teamextension.teamextension.data.Transaction;

@Database(entities = {Transaction.class}, version = 1)
public abstract class TransactionsDatabase extends RoomDatabase {
    public abstract TransactionDAO transactionsDAO();
}
