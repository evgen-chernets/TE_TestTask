package che.teamextension.teamextension.data;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

public class Currency {
    @Retention(SOURCE)
    @StringDef({GOLD, SILVER, BRONZE, COPPER})
    public @interface Name {
    }

    public static final String GOLD = "Gold";
    public static final String SILVER = "Silver";
    public static final String BRONZE = "Bronze";
    public static final String COPPER = "Copper";

    @Name
    public final String name;

    public final float toGoldRate;

    public Currency (@Name String name, float toGoldRate) {
        this.name = name;
        this.toGoldRate = toGoldRate;
    }
}
