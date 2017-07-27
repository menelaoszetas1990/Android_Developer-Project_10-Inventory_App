package com.example.android.android_developer_project_10_inventory_app;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.android_developer_project_10_inventory_app.data.ItemContract.ItemEntry;

import static com.example.android.android_developer_project_10_inventory_app.data.ItemContract.ItemEntry.CONTENT_URI;

public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {


        TextView tvName = (TextView) view.findViewById(R.id.item_name);
        TextView tvPrice = (TextView) view.findViewById(R.id.item_price);
        TextView tvQuantity = (TextView) view.findViewById(R.id.item_quantity);
        Button saleBtn = (Button) view.findViewById(R.id.item_sell_button);
        Button buyBtn = (Button) view.findViewById(R.id.item_buy_button);

        int idColumnIndex = cursor.getColumnIndex(ItemEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_NAME);
        int imageColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_IMAGE);
        int priceColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ItemEntry.COLUMN_ITEM_QUANTITY);

        // Extract properties from cursor
        String itemName = cursor.getString(nameColumnIndex);
        String itemPrice = cursor.getString(priceColumnIndex);
        final int itemQuantity = cursor.getInt(quantityColumnIndex);
        final int itemId = cursor.getInt(idColumnIndex);

        tvName.setText(itemName);
        tvPrice.setText(itemPrice);
        tvQuantity.setText(context.getString(R.string.quantity) + itemQuantity);

        saleBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                if (itemQuantity > 0) {
                    int tempQuantity = itemQuantity;
                    tempQuantity--;

                    ContentValues values = new ContentValues();
                    values.put(ItemEntry.COLUMN_ITEM_QUANTITY, tempQuantity);
                    Uri currentItemUri = ContentUris.withAppendedId(CONTENT_URI, itemId);

                    // create the Uri of the current product
                    context.getContentResolver().update(currentItemUri, values, null, null);
                } else {
                    Toast.makeText(context, "You try to reach quantity below 0", Toast.LENGTH_LONG).show();
                }
            }
        });
        buyBtn.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {
                int tempQuantity = itemQuantity;
                tempQuantity++;

                ContentValues values = new ContentValues();
                values.put(ItemEntry.COLUMN_ITEM_QUANTITY, tempQuantity);
                Uri currentItemUri = ContentUris.withAppendedId(CONTENT_URI, itemId);

                // create the Uri of the current product
                context.getContentResolver().update(currentItemUri, values, null, null);

            }
        });
    }
}
