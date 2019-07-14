package myapplication.callthelist;

import android.Manifest;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import java.util.ArrayList;
import myapplication.callthelist.Adapter.MyAdapter;
import myapplication.callthelist.Data.CallListProvider;
import myapplication.callthelist.EventBusModel.EventBusModels;
import myapplication.callthelist.Model.Person;

public class MainActivity extends RuntimePermissionsActivity {


    static final Uri CONTENT_URI = CallListProvider.CONTENT_URI;
    private Toolbar mToolbar;
    private MyRecyclerView mRecyclerView;
    private View mEmptyView;
    private FloatingActionButton mFloatingActionButton;
    private ArrayList<Person> personList;
    private MyAdapter myAdapter;
    private int PERMISSIONS = 50;
    private int i = 0;
    private final int REQUESET_CODE = 1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        defineViews();
        setToolbar();
        fillTheList();
        updateList();
    }

    @Override
    public void izinVerildi(int requestCode) {
        if (requestCode == PERMISSIONS) {
            makePhoneCall();
        }
    }

    public void makePhoneCall() {

        if (i < getAllPerson().size()) {
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + "+90" + personList.get(i).getPhoneNumber()));
            startActivityForResult(callIntent, REQUESET_CODE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.call:
                if (getAllPerson().isEmpty()) {
                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Kayıt Yok!!")
                            .create().show();
                } else {
                    i = 0;
                    getPermissions();

                }

                break;
        }
        return true;
    }

    private void defineViews() {

        mToolbar = findViewById(R.id.toolbar);
        mRecyclerView = findViewById(R.id.rc_list);
        mEmptyView = findViewById(R.id.empty_layout);
        mFloatingActionButton = findViewById(R.id.fab);

        mFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentDialog fragmentDialog = new FragmentDialog();
                fragmentDialog.show(getSupportFragmentManager(), "DialogFragment");
            }
        });

    }

    private void fillTheList() {

        personList = getAllPerson();
        mRecyclerView.setEmptyView(mEmptyView);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        myAdapter = new MyAdapter(this, personList);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(myAdapter);

    }

    private void setToolbar() {
        setSupportActionBar(mToolbar);
        mToolbar.setTitle("CallTheList");
    }

    private ArrayList<Person> getAllPerson() {
        personList = new ArrayList<>();

        Cursor cursor = getContentResolver().query(CONTENT_URI, new String[]{"*"}, null, null, "name ASC");

        if (cursor != null) {
            while (cursor.moveToNext()) {
                personList.add(new Person(cursor.getInt(cursor.getColumnIndex("id"))
                        , cursor.getString(cursor.getColumnIndex("name"))
                        , cursor.getString(cursor.getColumnIndex("number"))));
            }
        }

        if (cursor != null) {
            cursor.close();
        }

        return personList;
    }

    private void updateList() {
        personList.clear();
        personList = getAllPerson();
        myAdapter.update(personList);

    }

    //Gerekli izinlerin istenmesi
    private void getPermissions() {

        String[] istenilenIzinler = {Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.PROCESS_OUTGOING_CALLS};
        super.izinIste(istenilenIzinler, PERMISSIONS);
    }

    @Subscribe(sticky = true)
    public void phoneCall(EventBusModels.MakePhoneCall event) {
        if (event.getId() == 1) {
            Log.i("MyCallLog", "Arama Bitti yakalandı" + i);
            EventBus.getDefault().removeStickyEvent(event);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            i++;
            makePhoneCall();

        }
    }

    @Subscribe
    public void updateDataList(EventBusModels.UpdateList event) {
        if (event.getId() == 1) {
            updateList();
        }
    }

    @Subscribe
    public void deleteSelectedItem(EventBusModels.DeleteSelectedItem event) {
        if (event.getId() != -1) {
            int numberOfDeletedRows = getContentResolver().delete(CONTENT_URI, "id = ?", new String[]{String.valueOf(event.getId())});
            updateList();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }
}
