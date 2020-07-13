package com.deeksha.shoppinglist.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.deeksha.shoppinglist.CreateAndFillValuesInListShoppingRows;
import com.deeksha.shoppinglist.MainActivity;
import com.deeksha.shoppinglist.model.ui.ExpandShopGroup;
import com.deeksha.shoppinglist.R;
import com.deeksha.shoppinglist.model.firebase.AddShoppingModel;
import com.deeksha.shoppinglist.model.common.ShopItem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;

public class ListShoppingList extends AppCompatActivity {
    ImageButton addShoppingButton;
    private CreateAndFillValuesInListShoppingRows createAndFillValuesInListShoppingRows;
    private ExpandableListView expandListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        addShoppingButton = findViewById(R.id.addButton);
        expandListView = findViewById(R.id.ExpList);
        fetchAndPopulateEntriesInExpandableListView();
        addShoppingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListShoppingList.this, CreateShoppingList.class);
                startActivity(intent);
            }
        });
    }

    // Before this, talk about firebase layout

    public void fetchAndPopulateEntriesInExpandableListView() {
        FirebaseDatabase.getInstance().getReference().child("shopping")
                .child(MainActivity.getSubscriberId(ListShoppingList.this))
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        final ArrayList<ExpandShopGroup> groupList = new ArrayList<ExpandShopGroup>();
                        for (DataSnapshot areaSnapshot : dataSnapshot.getChildren()) {
                            AddShoppingModel value = areaSnapshot.getValue(AddShoppingModel.class);
                            String orderId = areaSnapshot.getKey();
                            ExpandShopGroup gru1 = new ExpandShopGroup();
                            gru1.setShoppingId(orderId);
                            gru1.setDate(value.getDate());
                            ArrayList<ShopItem> childList = new ArrayList<ShopItem>();

                            for (ShopItem shopItem : value.getSales().values()) {
                                ShopItem rowChild = new ShopItem();
                                rowChild.setCategory(shopItem.getCategory());
                                rowChild.setProduct(shopItem.getProduct());
                                rowChild.setQuantity(shopItem.getQuantity());
                                rowChild.setUnit(shopItem.getUnit());
                                childList.add(rowChild);
                            }
                            gru1.setItems(childList);
                            groupList.add(gru1);
                        }

                        Collections.sort(groupList);

                        for (int k = 1; k <= groupList.size(); k++) {
                            groupList.get(k - 1).setName("Shopping List - " + k);
                        }
                        createAndFillValuesInListShoppingRows = new CreateAndFillValuesInListShoppingRows
                                (ListShoppingList.this, groupList);
                        expandListView.setAdapter(createAndFillValuesInListShoppingRows);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        // up and down signs easily cannot be shifted to right side with xml, hence, a workaround to do that.
        int width = metrics.widthPixels;
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR2) {
            expandListView.setIndicatorBounds(width - GetPixelFromDips(80), width - GetPixelFromDips(55));
        } else {
            expandListView.setIndicatorBoundsRelative(width - GetPixelFromDips(85), width - GetPixelFromDips(55));
        }
    }

    private int GetPixelFromDips(float pixels) {
        // Get the screen's density scale
        final float scale = getResources().getDisplayMetrics().density;
        // Convert the dps to pixels, based on density scale
        return (int) (pixels * scale + 0.5f);
    }
}