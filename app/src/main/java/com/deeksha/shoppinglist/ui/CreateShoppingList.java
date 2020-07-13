package com.deeksha.shoppinglist.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.deeksha.shoppinglist.MainActivity;
import com.deeksha.shoppinglist.R;
import com.deeksha.shoppinglist.model.firebase.AddShoppingModel;
import com.deeksha.shoppinglist.model.common.ShopItem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.widget.Toast.LENGTH_LONG;

public class CreateShoppingList extends AppCompatActivity {
    TableLayout tableLayoutForCreateShoppingList;
    DatabaseReference databaseReference;
    Button submitToDB;
    Button addMoreItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_shopping_view);
        tableLayoutForCreateShoppingList = findViewById(R.id.addOrderinTable);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        addMoreItem = findViewById(R.id.addMoreItem);
        addMoreItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AddItemInShopTable.addRow(view.getContext(), tableLayoutForCreateShoppingList, databaseReference);
            }
        });
        submitToDB = findViewById(R.id.submit);
        submitToDB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, ShopItem> sales = new HashMap<>();
                Map<String, String> newCatAndProd = new HashMap<>();
                boolean error = false;
                for (int i = 1, j = tableLayoutForCreateShoppingList.getChildCount() - 2; i < j; i++) {
                    View rowView = tableLayoutForCreateShoppingList.getChildAt(i);
                    if (rowView instanceof TableRow) {
                        TableRow row = (TableRow) rowView;

                        String category;
                        String product;

                        int itemDetailChildCount = row.getChildCount();

                        String newCategory = null;
                        String newProduct = null;
                        Spinner categorySpinner = (Spinner) row.getChildAt(0);
                        int quantityIndex;
                        int unitIndex;
                        category = (String) categorySpinner.getSelectedItem();
                        if (itemDetailChildCount == 5) {
                            Spinner productSpinner = (Spinner) row.getChildAt(1);
                            product = (String) productSpinner.getSelectedItem();
                            quantityIndex = 2;
                            unitIndex = 3;
                        } else {
                            if (category.equals("Others")) {
                                EditText newCategoryEditText = (EditText) row.getChildAt(1);
                                category = newCategoryEditText.getText().toString();
                                newCategory = category;
                                EditText newProductEditText = (EditText) row.getChildAt(2);
                                product = newProductEditText.getText().toString();
                                newProduct = product;
                            } else {
                                EditText newProductEditText = (EditText) row.getChildAt(2);
                                product = newProductEditText.getText().toString();
                                newProduct = product;
                            }
                            quantityIndex = 3;
                            unitIndex = 4;
                        }
                        EditText quantityEditText = (EditText) row.getChildAt(quantityIndex);
                        Spinner unitSpinner = (Spinner) row.getChildAt(unitIndex);
                        if (quantityEditText.getText().length() == 0 || Integer.valueOf(quantityEditText.getText().toString()) < 1) {
                            quantityEditText.setError("Quantity must be provided and greater than 0");
                            error = true;
                            break;
                        }
                        ShopItem shopItem = new ShopItem(category, product,
                                Integer.valueOf(quantityEditText.getText().toString())
                                , (String) unitSpinner.getSelectedItem());
                        sales.put(UUID.randomUUID().toString(), shopItem);
                        if (newCategory != null) {
                            newCatAndProd.put(newCategory, newProduct);
                        } else if (newProduct != null) {
                            newCatAndProd.put(category, newProduct);
                        }
                    }
                }

                if (sales.size() == 0) {
                    DynamicToast.makeError(getApplicationContext(), "Provide sales.", Toast.LENGTH_SHORT).show();
                } else if (!error) {
                    String saleId = "startup_splash_cart-" + UUID.randomUUID().toString();
                    Date c = Calendar.getInstance().getTime();
                    System.out.println("Current time => " + c);

                    SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy'T'HH:mm:ssZ");
                    String formattedDate = df.format(c);
                    AddShoppingModel shoppingModel = new AddShoppingModel(sales, formattedDate);
                    databaseReference.child("shopping").child(MainActivity.getSubscriberId(getApplicationContext()))
                            .child(saleId)
                            .setValue(shoppingModel);

                    DynamicToast.makeSuccess(getApplicationContext(), "Shopping List Added Successfully", LENGTH_LONG).show();
                    int count = tableLayoutForCreateShoppingList.getChildCount();
                    for (int i = 1; i < count - 2; i++) {
                        tableLayoutForCreateShoppingList.removeView(tableLayoutForCreateShoppingList.getChildAt(1));
                    }
                }
                if (newCatAndProd.size() > 0) {
                    for (String newCat : newCatAndProd.keySet()) {
                        databaseReference.child("categories").child(toCamelCase(newCat))
                                .child(UUID.randomUUID().toString())
                                .setValue(toCamelCase(newCatAndProd.get(newCat)));
                    }
                }
            }
        });

    }

    static String toCamelCase(String s) {
        String[] parts = s.split("_");
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() +
                s.substring(1).toLowerCase();
    }
}