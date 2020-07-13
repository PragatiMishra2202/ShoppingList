package com.deeksha.shoppinglist;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.deeksha.shoppinglist.model.common.ShopItem;
import com.deeksha.shoppinglist.model.ui.ExpandShopGroup;
import com.deeksha.shoppinglist.ui.CreateShoppingList;
import com.deeksha.shoppinglist.ui.ListShoppingList;
import com.deeksha.shoppinglist.ui.UpdateShoppingList;
import com.google.firebase.database.FirebaseDatabase;

public class CreateAndFillValuesInListShoppingRows extends BaseExpandableListAdapter {

    private Context context;
    private ArrayList<ExpandShopGroup> groups;
    private FirebaseDatabase firebaseDatabase;

    public CreateAndFillValuesInListShoppingRows(Context context, ArrayList<ExpandShopGroup> groups) {
        this.context = context;
        this.groups = groups;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        ExpandShopGroup group = (ExpandShopGroup) getGroup(groupPosition);
        if (view == null) {
            LayoutInflater inf = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = inf.inflate(R.layout.expandlist_shopping_group, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tvGroup);
        tv.setText(group.getName());

        TextView tvDate = (TextView) view.findViewById(R.id.tvGroupDate);
        tvDate.setText(group.getDate().split("T")[0]);

        View viewById = view.findViewById(R.id.deleteRow);
        if (viewById != null) {
            viewById.setTag(group.getShoppingId());
        }
        final TextView tvMenu = (TextView) view.findViewById(R.id.tv_menu);
        tvMenu.setTag(group.getShoppingId());
        tvMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickMenu(tvMenu);
            }
        });

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view,
                             ViewGroup parent) {
        ShopItem child = (ShopItem) getChild(groupPosition, childPosition);
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.expandlist_shopping_item, null);
        }
        TextView tv = (TextView) view.findViewById(R.id.tvChild1);
        tv.setText(child.getCategory().toString());

        TextView tv2 = (TextView) view.findViewById(R.id.tvChild2);
        tv2.setText(child.getProduct());

        TextView tv3 = (TextView) view.findViewById(R.id.tvChild3);
        tv3.setText(String.valueOf(child.getQuantity()));

        TextView tv4 = (TextView) view.findViewById(R.id.tvChild4);
        tv4.setText(child.getUnit());
        return view;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        ArrayList<ShopItem> chList = groups.get(groupPosition).getItems();
        return chList.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        ArrayList<ShopItem> chList = groups.get(groupPosition).getItems();
        return chList.size();
    }

    @Override
    public int getGroupCount() {
        // TODO Auto-generated method stub
        return groups.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        // TODO Auto-generated method stub
        return groupPosition;
    }

    @Override
    public boolean hasStableIds() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        // TODO Auto-generated method stub
        return true;
    }

    public void showPickMenu(final View anchor) {
        final PopupMenu popupMenu = new PopupMenu(anchor.getContext(), anchor);
        popupMenu.inflate(R.menu.sale_item_options);

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                View actionView = item.getActionView();
                String shoppingId = (String) anchor.getTag();
                switch (item.getItemId()) {
                    /*case R.id.editRow:
                        Intent intent = new Intent(anchor.getContext(), UpdateShoppingList.class);
                        anchor.getContext().startActivity(intent);
                        break;*/

                    case R.id.deleteRow:
                        FirebaseDatabase.getInstance().getReference("shopping")
                                .child(MainActivity.getSubscriberId(anchor.getContext())).child(shoppingId).removeValue();
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    public void addItem(ShopItem item, ExpandShopGroup group) {
        if (!groups.contains(group)) {
            groups.add(group);
        }
        int index = groups.indexOf(group);
        ArrayList<ShopItem> ch = groups.get(index).getItems();
        ch.add(item);
        groups.get(index).setItems(ch);
    }
}
