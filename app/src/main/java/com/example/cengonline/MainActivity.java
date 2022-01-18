package com.example.cengonline;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cengonline.model.Course;
import com.example.cengonline.model.User;
import com.example.cengonline.ui.course.CourseFragment;
import com.example.cengonline.ui.dialog.JoinClassDialog;
import com.example.cengonline.ui.dialog.NewClassDialog;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;
    private FirebaseAuth firebaseAuth;
    private GoogleSignInClient googleSignInClient;
    private User user;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        this.toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_messages, R.id.nav_admin)
                .setDrawerLayout(drawerLayout)
                .build();


        this.navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, this.navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, this.navController);

        navigationView.setNavigationItemSelectedListener(this);

        googleSignInClient = GoogleSignIn.getClient(this, GoogleSignInOptions.DEFAULT_SIGN_IN);
        firebaseAuth = FirebaseAuth.getInstance();

        if(getIntent().hasExtra("user")){
            this.user = (User)getIntent().getSerializableExtra("user");
        }


        if(this.user == null){
            readUserFromDatabase();
        }
        else{
            setAttributes();
        }


        createNavigationMenuItems();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item){

        switch(item.getItemId()){
            case R.id.action_logout:
                signOut();
                return true;
            case R.id.join_class:
                showJoinClassDialog();
                return true;
            case R.id.new_class:
                showNewClassDialog();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu){

        if(!this.user.getRoles().contains(User.Role.TEACHER)){
            invalidateOptionsMenu();
            menu.findItem(R.id.new_class).setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void signOut() {
        // Firebase sign out
        firebaseAuth.signOut();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }

    private void readUserFromDatabase(){

        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

        if(firebaseUser != null){
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference ref = database.getReference();
            DatabaseReference userRef = ref.child(("users"));

            Query query = userRef.orderByChild("uid").equalTo(firebaseUser.getUid());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        user = ds.getValue(User.class);
                    }
                    setAttributes();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

    }

    private void setAttributes(){
        View header = navigationView.getHeaderView(0);
        ((TextView)header.findViewById(R.id.navbarEmail)).setText(user.getEmail());
        ((TextView)header.findViewById(R.id.navbarDisplayName)).setText(user.getDisplayName().toUpperCase());
        ((TextView)header.findViewById(R.id.navbarEmailImage)).setText(String.valueOf(user.getDisplayName().toUpperCase().charAt(0)));
    }

    private void showJoinClassDialog(){
        JoinClassDialog joinD = new JoinClassDialog(this);
        joinD.show();
    }

    private void showNewClassDialog(){
        NewClassDialog newD = new NewClassDialog(this);
        newD.show();
    }

    public static void startActivity(Context context, User user) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("user", user);
        context.startActivity(intent);
    }


    private void createNavigationMenuItems(){

        final Menu menu = navigationView.getMenu();
        if(this.user.getRoles().contains(User.Role.ADMIN)){
            menu.add(0, 0, 0, "User Roles")
                    .setIcon(R.drawable.ic_data_settings)
                    .setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            navController.navigate(R.id.nav_admin);
                            return false;
                        }
                    })
                    .setCheckable(true);
        }

        final SubMenu subMenu = menu.addSubMenu("Enrolled");


        DatabaseUtility.getInstance().getAllCourses(new DatabaseCallback() {
            @Override
            public void onSuccess(Object result) {
                List<Course> courses = (List<Course>)result;
                subMenu.removeGroup(1);

                for(final Course course : courses){
                    MenuItem item = subMenu.add(1, 0 , 200, course.getClassName());
                    item.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {

                            //navController.navigate(R.id.nav_course, args);
                            Intent intent = new Intent(getBaseContext(), CourseFragment.class);
                            intent.putExtra("course", course);
                            startActivity(intent);
                            return false;
                        }
                    });
                    item.setCheckable(true);
                }
            }

            @Override
            public void onFailed(String message) {

            }
        });
    }

    public void makeToastText(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        item.setChecked(true);
        this.drawerLayout.closeDrawers();

        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home: navController.navigate(R.id.nav_home); break;
            case R.id.nav_messages: navController.navigate(R.id.nav_messages); break;
            case R.id.nav_about: navController.navigate(R.id.nav_about); break;
        }

        return true;
    }
}
