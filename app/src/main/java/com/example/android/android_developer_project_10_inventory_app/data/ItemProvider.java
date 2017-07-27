package com.example.android.android_developer_project_10_inventory_app.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.android_developer_project_10_inventory_app.data.ItemContract.ItemEntry;

public class ItemProvider extends ContentProvider {

    // Tag for log messages
    public static final String LOG_TAG = ItemProvider.class.getSimpleName();

    private ItemDbHelper mDbHelper;

    // URI matcher code for the content URI for the items table
    public static final int ITEMS = 100;

    // URI matcher code for the content URI for a single item in the items table
    public static final int ITEM_ID = 101;

    // URI matcher object to match a context URI to a corresponding code.
    // The input passed into the constructor represents the code to return for the root URI.
    // It's common to use NO_MATCH as the input for this case.
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // The content URI of the form will map to the integer code {@link #ITEMS}. This URI is used
        // to provide access to MULTIPLE rows of the items table.
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);


        // The content URI of the form will map to the integer code {@link #ITEM_ID}. This URI is
        // used to provide access to ONE single row of the items table.
        //
        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        sUriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    // Initialize the provider and the database helper object.
    @Override
    public boolean onCreate() {
        mDbHelper = new ItemDbHelper(getContext());
        return true;
    }

    // Perform the query for the given URI. Use the given projection, selection, selection arguments
    // and sort order.
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        // This  cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                // For the ITEMS code, query the items table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the items table.
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
                // For the ITEM_ID code, extract out the ID from the URI.
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the items table where the _id equals the parsedId to
                // return a Cursor containing that row of the table.
                cursor = database.query(ItemEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        // Set notification URI on the Cursor,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI changes, then we know we need to update the Cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    // Insert new data into the provider with the given ContentValues.
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    // Insert a item into the database with the given content values. Return the new content URI
    //for that specific row in the database.
    private Uri insertItem(Uri uri, ContentValues values) {

        // initialize all the variables needed
        String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
        String price = values.getAsString(ItemEntry.COLUMN_ITEM_PRICE);
        String quantity = values.getAsString(ItemEntry.COLUMN_ITEM_QUANTITY);
        String image = values.getAsString(ItemEntry.COLUMN_ITEM_IMAGE);
        String supplier = values.getAsString(ItemEntry.COLUMN_ITEM_SUPPLIER);
        String email = values.getAsString(ItemEntry.COLUMN_SUPPLIER_EMAIL);

        // check the validation of each item
        if (price == null) {
            throw new IllegalArgumentException("Price required");
        }
        if (quantity == null || Integer.parseInt(quantity) < 0) {
            throw new IllegalArgumentException("Quantity required or value lower than 0");
        }
        if (name == null) {
            throw new IllegalArgumentException("Item name required");
        }
        if (image == null) {
            throw new IllegalArgumentException("Item image required");
        }
        if (supplier == null) {
            throw new IllegalArgumentException("Supplier required");
        }
        if (email == null) {
            throw new IllegalArgumentException("Supplier's email required");
        }

        // Get writable database
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // insert the new item with the given values
        long id = db.insert(ItemEntry.TABLE_NAME, null, values);

        // If the ID is -1, then the insertion failed. Log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        // Notify all listeners that the data has changed for the item content URI
        getContext().getContentResolver().notifyChange(uri, null);

        // Return the new URI with the ID (of the newly inserted row) appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    // Updates the data at the given selection and selection arguments, with the new ContentValues.
        @Override
        public int update(Uri uri, ContentValues contentValues, String selection,
                String[] selectionArgs) {
            final int match = sUriMatcher.match(uri);
            switch (match) {
                case ITEMS:
                    return updateItem(uri, contentValues, selection, selectionArgs);
                case ITEM_ID:
                    // For the ITEM_ID code, extract out the ID from the URI,
                    // so we know which row to update. Selection will be "_id=?" and selection
                    // arguments will be a String array containing the actual ID.
                    selection = ItemEntry._ID + "=?";
                    selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                    int rowsUpdated = updateItem(uri, contentValues, selection, selectionArgs);
                    if (rowsUpdated != 0)
                        getContext().getContentResolver().notifyChange(uri, null);
                    return rowsUpdated;
                default:
                    throw new IllegalArgumentException("Update is not supported for " + uri);
            }
        }


    // Update items in the database with the given content values. Apply the changes to the rows
    //specified in the selection and selection arguments (which could be 0 or 1 or more items).
    //Return the number of rows that were successfully updated.
    private int updateItem(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link ItemEntry#COLUMN_ITEM_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(ItemEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(ItemEntry.COLUMN_ITEM_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Item name required");
            }
        }

        // If the {@link ItemEntry#COLUMNT_ITEM_QUANTITY} key is present,
        // check that the gender value is valid.
        if (values.containsKey(ItemEntry.COLUMN_ITEM_QUANTITY)) {
            Integer quantity = values.getAsInteger(ItemEntry.COLUMN_ITEM_QUANTITY);
            if (quantity == null) {
                throw new IllegalArgumentException("Item quantity required");
            }
        }

        // If the {@link ItemEntry#COLUMN_ITEM_PRICE} key is present,
        // check that the weight value is valid.
        if (values.containsKey(ItemEntry.COLUMN_ITEM_PRICE)) {
            // Check that the weight is greater than or equal to 0 kg
            Integer price = values.getAsInteger(ItemEntry.COLUMN_ITEM_PRICE);
            if (price != null && price < 0) {
                throw new IllegalArgumentException("Valid price required");
            }
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Otherwise, get writable database to update the data
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        return database.update(ItemEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    // Delete the data at the given selection and selection arguments.
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        switch (match) {
            case ITEMS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            case ITEM_ID:
                // Delete a single row given by the ID in the URI
                selection = ItemEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(ItemEntry.TABLE_NAME, selection, selectionArgs);
                if (rowsDeleted != 0)
                    getContext().getContentResolver().notifyChange(uri, null);
                return rowsDeleted;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    // Returns the MIME type of data for the content URI.
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case ITEMS:
                return ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
