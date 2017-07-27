package com.example.android.android_developer_project_10_inventory_app.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class ItemContract {

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private ItemContract() {}

     // The "Content authority" is a name for the entire content provider, similar to the
     // relationship between a domain name and its website.  A convenient string to use for the
     // content authority is the package name for the app, which is guaranteed to be unique on the
     // device.
    public static final String CONTENT_AUTHORITY = "com.example.android.android_developer_project_10_inventory_app";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


     // Possible path (appended to base content URI for possible URI's)
     // For instance, content://com.example.android.android_developer_project_10_inventory_app/items
     // is a valid path for looking at items data.
    public static final String PATH_ITEMS = "items";

    // Inner class that defines the table contents
    public static class ItemEntry implements BaseColumns {


        // The content URI to access the item data in the provider
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        // The MIME type of the {@link #CONTENT_URI} for a list of items.
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE +
                        "/" + CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // The MIME type of the {@link #CONTENT_URI} for a single item
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                        CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        // constants for the Database
        public static final String TABLE_NAME = "items";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_ITEM_NAME = "name";
        public static final String COLUMN_ITEM_QUANTITY = "quantity";
        public static final String COLUMN_ITEM_PRICE = "price";
        public static final String COLUMN_ITEM_IMAGE = "image";
        public static final String COLUMN_ITEM_SUPPLIER = "supplier";
        public static final String COLUMN_SUPPLIER_EMAIL = "email";
    }
}
