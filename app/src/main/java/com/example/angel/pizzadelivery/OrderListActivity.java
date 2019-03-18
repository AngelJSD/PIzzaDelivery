package com.example.angel.pizzadelivery;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Order {

    int dni = 0;
    int number = 0;
    String name = "";
    String pizza = "";
    String lat = "";
    String lng = "";

    Order(int dni, String name, String pizza, int number, String lat, String lng){
        this.dni = dni;
        this.number = number;
        this.name = name;
        this.pizza = pizza;
        this.lat = lat;
        this.lng = lng;
    }

}

public class OrderListActivity extends AppCompatActivity {

    private static List<Order> orders = new ArrayList<>();
    private static List<Map<String,String>> ordersShow = new ArrayList<>();
    private ListView lv_orders;
    private ArrayAdapter<String> adapter;
    SimpleAdapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Orders List");

        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(OrderListActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(OrderListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(OrderListActivity.this,
                    "Please, enable the access to your phone location",
                    Toast.LENGTH_SHORT).show();

            // Asking user if explanation is needed
            if (ActivityCompat.shouldShowRequestPermissionRationale(OrderListActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an expanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

                //Prompt the user once explanation has been shown
                ActivityCompat.requestPermissions(OrderListActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(OrderListActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        99);
            }

            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
        }
        else{
            Toast.makeText(OrderListActivity.this,
                    "The phone already has permissions",
                    Toast.LENGTH_SHORT).show();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(OrderListActivity.this, NewOrderActivity.class);
                startActivity(intent);
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        query();
        lv_orders = findViewById(R.id.ListOrder);
        //adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_2, ordersShow);
        adapter1 = new SimpleAdapter(this, ordersShow, android.R.layout.simple_list_item_2, new String[] {"title","date"}, new int[] {android.R.id.text1, android.R.id.text2});
        lv_orders.setAdapter(adapter1);

        lv_orders.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> adapter, View v, int position,
                                    long arg3)
            {

                // inflate the layout of the popup window
                LayoutInflater inflater = (LayoutInflater)
                        getSystemService(LAYOUT_INFLATER_SERVICE);
                View popupView = inflater.inflate(R.layout.popup_window, null);

                // create the popup window
                int width = LinearLayout.LayoutParams.WRAP_CONTENT;
                int height = LinearLayout.LayoutParams.WRAP_CONTENT;
                boolean focusable = true; // lets taps outside the popup also dismiss it
                final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);
                popupWindow.setAnimationStyle(R.style.DialogAnimation);
                // show the popup window
                TextView tv_dni = popupWindow.getContentView().findViewById(R.id.tv_show_dni);
                TextView tv_name = popupWindow.getContentView().findViewById(R.id.tv_show_name);
                TextView tv_pizza = popupWindow.getContentView().findViewById(R.id.tv_show_pizza);
                TextView tv_quantity = popupWindow.getContentView().findViewById(R.id.tv_show_quantity);
                TextView tv_lat = popupWindow.getContentView().findViewById(R.id.tv_show_lat);
                TextView tv_lng = popupWindow.getContentView().findViewById(R.id.tv_show_lng);
                tv_dni.setText(Integer.toString(orders.get(position).dni));
                Log.i("TEXT: ",(String) tv_dni.getText());
                tv_name.setText(orders.get(position).name);
                tv_pizza.setText(orders.get(position).pizza);
                tv_quantity.setText(Integer.toString(orders.get(position).number));
                tv_lat.setText(orders.get(position).lat);
                tv_lng.setText(orders.get(position).lng);
                // which view you pass in doesn't matter, it is only used for the window tolken
                popupWindow.showAtLocation(lv_orders, Gravity.CENTER, 0, 0);

                final ConstraintLayout back_dim_layout = (ConstraintLayout) findViewById(R.id.fadeBackground);
                //back_dim_layout.setVisibility(View.GONE);
                //back_dim_layout.setAlpha(0.2f);

                // dismiss the popup window when touched
                popupView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        popupWindow.dismiss();
                        //back_dim_layout.setAlpha(1.0f);
                        //back_dim_layout.setVisibility(View.GONE);
                        return true;
                    }
                });
            }
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Permission was granted.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                    }

                } else {

                    // Permission denied, Disable the functionality that depends on this permission.

                }

            }

            // other 'case' lines to check for other permissions this app might request.
            //You can add here other case statements according to your requirement.
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_order_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void query(){

        AdminSQLite admin = new AdminSQLite(this, "admin", null, 1);

        SQLiteDatabase db = admin.getReadableDatabase();

        Cursor result = db.rawQuery("SELECT * FROM 'order'", null);

        if(result.moveToFirst()){

            orders.clear();
            ordersShow.clear();
            int count = 1;
            do {
                Order newOrder = new Order(result.getInt(0),
                        result.getString(1),
                        result.getString(2),
                        result.getInt(3),
                        result.getString(4),
                        result.getString(5));
                orders.add(newOrder);
                Map<String,String> data = new HashMap<>(2);
                data.put("title", "Order "+ Integer.toString(count));
                data.put("date", "DNI: "+Integer.toString(result.getInt(0)) + " | Show details...");
                ordersShow.add(data);
                count += 1;
            }
            while (result.moveToNext());
        }
    }
}
