package com.example.han.boostcamp3.data;

import android.provider.BaseColumns;

/**
 * Created by Han on 2017-07-19.
 */

public class ShopContract {

    public static final class ShopEntry implements BaseColumns{

        public static final String TABLE_NAME = "shop";

        public static final String SHOP_TITLE = "title";
        public static final String SHOP_ADDRESS = "address";
        public static final String SHOP_LAT = "lat";
        public static final String SHOP_LNG = "lng";
        public static final String SHOP_PHONE = "phone";
        public static final String SHOP_CONTENT = "content";

    }
}
