package com.example.doska;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.doska.adapter.DataSender;
import com.example.doska.adapter.PostAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private NavigationView nav_view;
    private DrawerLayout drawerLayout;
    private FirebaseAuth mAuth;
    private TextView userEmail;
    private AlertDialog dialog;
    private Toolbar toolbar;
    private FloatingActionButton fb;
    private PostAdapter.OnItemClickCustom onItemClickCustom;
    private RecyclerView rcView;
    private PostAdapter postAdapter;
    private DataSender dataSender;
    private DbManager dbManager;
    public static String MAUTH = "";
    private String current_cat = "Машины";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(current_cat.equals("MyAds"))
        {
            dbManager.getMyAdsDataFromDb(mAuth.getUid());
        }
        else
        {
            dbManager.getDataFromDb(current_cat);
        }

    }

    private void init()
    {
        setOnItemClickCustom();
        rcView = findViewById(R.id.rcView);
        rcView.setLayoutManager(new LinearLayoutManager(this));
        List<NewPost> arrayPost = new ArrayList<>();
        postAdapter = new PostAdapter(arrayPost, this,onItemClickCustom);
        rcView.setAdapter(postAdapter);

        fb = findViewById(R.id.fb);
        nav_view = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawerLayout);
        toolbar = findViewById(R.id.toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle( this, drawerLayout, toolbar, R.string.toggle_open, R.string.toggle_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        nav_view.setNavigationItemSelectedListener(this);
        userEmail = nav_view.getHeaderView(0).findViewById(R.id.tvEmail);
        mAuth = FirebaseAuth.getInstance();

        getDataDB();
        dbManager = new DbManager(dataSender, this);
        dbManager.getMyAdsDataFromDb(mAuth.getUid());
        postAdapter.setDbManager(dbManager);


    }
    private void getDataDB()
    {
        dataSender = new DataSender() {
            @Override
            public void onDataRecived(List<NewPost> listData)
            {
                Collections.reverse(listData);
                postAdapter.updateAdapter(listData);

            }
        };
    }

    private void setOnItemClickCustom()
    {
        onItemClickCustom = new PostAdapter.OnItemClickCustom() {
            @Override
            public void onItemSelected(int position) {
                Log.d("MyLog", "Position " + position);

            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        getUserData();


    }
    public void onClickEdit(View view)
    {
        Intent i = new Intent(MainActivity.this, EditActivity.class);
        startActivity(i);
    }
    private void getUserData()
    {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null)
        {
            userEmail.setText(currentUser.getEmail());
            MAUTH = mAuth.getUid();

        }
        else
        {
            userEmail.setText(R.string.sign_in_or_sign_up);
            MAUTH = "";
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id)
        {
            case R.id.id_my_ads:
                current_cat = "MyAds";
                dbManager.getMyAdsDataFromDb(mAuth.getUid());
                break;

            case R.id.id_cars_ads:
                current_cat = "Машины";
                dbManager.getDataFromDb("Машины");
                break;

            case R.id.id_pc_ads:
                current_cat = "Компьютеры";
                dbManager.getDataFromDb("Компьютеры");
                break;

            case R.id.id_smartphone_ads:
                current_cat = "Смартфоны";
                dbManager.getDataFromDb("Смартфоны");
                break;

            case R.id.id_dm_ads:
                current_cat = "Бытовая техника";
                dbManager.getDataFromDb("Бытовая техника");

                break;
            case R.id.id_sign_up:

                signUpDialog(R.string.sign_up, R.string.sign_up_button,0);
                break;
            case R.id.id_sign_in:

                signUpDialog(R.string.sign_in,R.string.sign_in_button,1);
                break;
            case R.id.id_sign_out:
                signOut();
                break;
        }

        return true;
    }
    private void signUpDialog(int title, int buttonTitle, int index)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.sign_up_layout, null);
        dialogBuilder.setView(dialogView);
        TextView titleTextView = dialogView.findViewById(R.id.tvAlertTitle);
        titleTextView.setText(title);
        Button b = dialogView.findViewById(R.id.buttonSignUp);
        EditText edEmail = dialogView.findViewById(R.id.edEmail);
        EditText edPassword = dialogView.findViewById(R.id.edPassword);
        b.setText(buttonTitle);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0)
                {
                    signUp(edEmail.getText().toString(),edPassword.getText().toString());

                }
                else
                {
                    signIn(edEmail.getText().toString(), edPassword.getText().toString());
                }
                dialog.dismiss();
            }
        });
        dialog = dialogBuilder.create();
        dialog.show();



    }

    private void signUp(String email, String password)
    {
        if(!email.equals("") && !password.equals("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information

                                FirebaseUser user = mAuth.getCurrentUser();
                                getUserData();

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("MyLogMainActivity", "createUserWithEmail:failure", task.getException());
                                Toast.makeText(getApplicationContext(), "Вы ввели недопустимый пароль или логин!",
                                        Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
        }
        else {
            Toast.makeText(this, "Email или Password пустой!!", Toast.LENGTH_SHORT).show();
        }
}
    private void signIn(String email, String password)
    {
        if (!email.equals("") && !password.equals(""))
        {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                           getUserData();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MyLogMainActivity", "signInWithCustomToken:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Вы ввели неправильный пароль или логин!",
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        }
        else
        {
            Toast.makeText(this, "Email или Password пустой!!", Toast.LENGTH_SHORT).show();
        }
    }
    private void signOut()
    {
        mAuth.signOut();
        getUserData();
    }
}