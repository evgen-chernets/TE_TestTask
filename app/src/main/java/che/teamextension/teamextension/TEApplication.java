package che.teamextension.teamextension;

import android.app.Application;
import android.arch.persistence.room.Room;

import che.teamextension.teamextension.db.TransactionsDatabase;

public class TEApplication extends Application {

    private static TEApplication instance;

    private TransactionsDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, TransactionsDatabase.class, "database")
                .allowMainThreadQueries()
                .build();
    }

    public static TEApplication getInstance() {
        return instance;
    }

    public TransactionsDatabase getDatabase() {
        return database;
    }

}
