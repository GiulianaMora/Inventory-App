package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.View.OnClickListener;


import com.example.android.inventoryapp.data.ProductContract;
import com.example.android.inventoryapp.data.ProductDbHelper;
import com.example.android.inventoryapp.data.ProductItem;
import com.example.android.inventoryapp.data.ProductContract.StockEntry;



public class MainActivity extends AppCompatActivity {

    private final static String LOG_TAG = MainActivity.class.getCanonicalName();
    ProductDbHelper dbHelper;
    ProductCursorAdapter adapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new ProductDbHelper(this);

        final ImageButton addButton = (ImageButton) findViewById(R.id.add_product_button);
        addButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.list_view);
        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        Cursor cursor = dbHelper.readStock();

        adapter = new ProductCursorAdapter(this, cursor);
        listView.setAdapter(adapter);
        listView.setOnScrollListener(new AbsListView.OnScrollListener(){
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState){
                if(scrollState == 0) return;
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();
                if (currentFirstVisibleItem > lastVisibleItem){
                    addButton.setVisibility(View.VISIBLE);
                } else if (currentFirstVisibleItem < lastVisibleItem){
                    addButton.setVisibility(View.GONE);
                }
                lastVisibleItem = currentFirstVisibleItem;
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount){
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        adapter.swapCursor(dbHelper.readStock());
    }

    public void clickOnViewItem(long id){
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("itemId", id);
        startActivity(intent);
    }

    public void onClickOnSale(long id, int quantity){
        dbHelper.sellOneItem(id, quantity);
        adapter.swapCursor(dbHelper.readStock());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_add_dummy_data:
                addDummyData();
                adapter.swapCursor(dbHelper.readStock());
        }
        return super.onOptionsItemSelected(item);
    }
    private void addDummyData() {
        ProductItem teddyBear = new ProductItem(
                "teddy bear",
                "$ 6",
                6,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/teddy_bear");
        dbHelper.insertItem(teddyBear);

        ProductItem buzzLightyear = new ProductItem(
                "buzz lightyear",
                "$ 7",
                12,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/buzz_lightyear");
        dbHelper.insertItem(buzzLightyear);

        ProductItem playDoh = new ProductItem(
                "play doh",
                "$ 8",
                18,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/play_doh");
        dbHelper.insertItem(playDoh);

        ProductItem rubixCube = new ProductItem(
                "rubix cube",
                "$ 9",
                24,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/rubix_cube");
        dbHelper.insertItem(rubixCube);

        ProductItem stuffedElephant = new ProductItem(
                "stuffed elephant",
                "$ 10",
                30,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/stuffed_elephant");
        dbHelper.insertItem(stuffedElephant);

        ProductItem dinos = new ProductItem(
                "dinos",
                "$ 11",
                36,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/dinos");
        dbHelper.insertItem(dinos);

        ProductItem goFish = new ProductItem(
                "go fish",
                "$ 12",
                42,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/go_fish");
        dbHelper.insertItem(goFish);

        ProductItem fidgetSpinner = new ProductItem(
                "fidget spinner",
                "$ 14",
                48,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/fidget_spinner");
        dbHelper.insertItem(fidgetSpinner);

        ProductItem rubberDucky = new ProductItem(
                "rubber ducky",
                "$ 15",
                54,
                "Hasbro Toys",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/rubber_ducky");
        dbHelper.insertItem(rubberDucky);

        ProductItem elmo = new ProductItem(
                "elmo",
                "$ 16",
                60,
                "Ditmars Flowers",
                "360-792-4015",
                "hasbrotoys@yahoo.com",
                "android.resource://com.example.android.inventoryapp/drawable/elmo");
        dbHelper.insertItem(elmo);
    }
}