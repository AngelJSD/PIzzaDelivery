package com.example.angel.pizzadelivery;

import android.content.ClipData;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

public class NewOrderActivity extends AppCompatActivity {

    EditText et_dni, et_name, et_pizza, et_number;
    private static List<EditText> editTexts = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_order2);

        setTitle("New Order");
        List<String> pizzas = new ArrayList<>();
        pizzas.add("Pepperoni");
        pizzas.add("Hawaiana");
        pizzas.add("Chicken BBQ");
        pizzas.add("Bacon");
        Button bt_next = findViewById(R.id.bt_next);
        final Spinner sp = findViewById(R.id.spinner);
        ArrayAdapter<String> sp_adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, pizzas);
        sp.setAdapter(sp_adapter);

        bt_next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean validation = true;
                et_dni = findViewById(R.id.et_dni);
                et_name = findViewById(R.id.et_name);
                et_number = findViewById(R.id.et_number);

                editTexts.add(et_dni);
                editTexts.add(et_name);
                editTexts.add(et_number);

                for (int i=0; i<editTexts.size(); i++) {

                    if(editTexts.get(i).getText().toString().equalsIgnoreCase("")){
                        editTexts.get(i).setError("Required");
                        validation = false;
                    }
                }

                if(validation) {
                    Intent intent = new Intent(NewOrderActivity.this, MapsActivity.class);
                    intent.putExtra("DNI", Integer.parseInt(et_dni.getText().toString()));
                    intent.putExtra("NAME", et_name.getText().toString());
                    intent.putExtra("PIZZA", sp.getSelectedItem().toString());
                    intent.putExtra("NUMBER", Integer.parseInt(et_number.getText().toString()));

                    startActivity(intent);
                }
            }
        });

    }


}
