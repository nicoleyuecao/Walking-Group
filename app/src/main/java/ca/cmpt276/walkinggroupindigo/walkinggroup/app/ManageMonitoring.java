package ca.cmpt276.walkinggroupindigo.walkinggroup.app;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import ca.cmpt276.walkinggroupindigo.walkinggroup.R;
import ca.cmpt276.walkinggroupindigo.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroupindigo.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroupindigo.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class ManageMonitoring extends AppCompatActivity {

    public static final int MASSAGE_CODE = 100;
    private WGServerProxy proxy;
    private User user;
    private List<User> monitorsUser = new ArrayList<>();
    private List<User> monitoredByUser = new ArrayList<>();

    public static Intent makeIntent (Context context){
        return new Intent (context, ManageMonitoring.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_monitoring);
        user = User.getInstance();
        setUpAddMonitoringButton();
        getApiKey();
        populateMonitorsUser();
        populateMonitorsListView();
        populateMonitoredByUsers();
        populateMonitoredByListView();
    }

    private void setUpAddMonitoringButton() {
        Button addMonitoring = findViewById(R.id.add_monitoring_btn);
        addMonitoring.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = AddMonitoringActivity.makeIntent(ManageMonitoring.this);
                //startActivityForResult(intent, MASSAGE_CODE);
                startActivity(intent);
            }
        });
    }

    private void getApiKey() {
        String apiKey = getString(R.string.apikey);
        // Token is not getting properly
        proxy = ProxyBuilder.getProxy(apiKey,  LoginActivity.getLogInToken(ManageMonitoring.this));
    }

    private void populateMonitorsUser() {
        Call<List<User>> userCaller = proxy.getMonitorsUsers(user.getId());
        ProxyBuilder.callProxy(ManageMonitoring.this, userCaller,
                returnedUsers -> { // Returns in the Call <parameter>
                    monitorsUser.addAll(returnedUsers);
                });
    }

    private void populateMonitoredByUsers() {
        Call<List<User>> userCaller = proxy.getMonitoredByUsers(user.getId());
        ProxyBuilder.callProxy(ManageMonitoring.this, userCaller,
                returnedUsers -> { // Returns in the Call <parameter>
                    monitoredByUser.addAll(returnedUsers);
                });
    }

    private void populateMonitorsListView() {
        ArrayAdapter<User> adapter = new MyListMonitors();
        ListView monitoringList = findViewById(R.id.monitoring_listview);
        monitoringList.setAdapter(adapter);
        new ArrayAdapter<>(this,
                R.layout.monitoring_layout);
        monitoringList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MonitoringUsersActivity.makeIntent(ManageMonitoring.this);
                startActivity(intent);
            }
        });

    }

    private void populateMonitoredByListView() {
        ArrayAdapter<User> adapter = new MyListMonitoredBy();
        ListView monitoringList = findViewById(R.id.monitored_listview);
        monitoringList.setAdapter(adapter);
        new ArrayAdapter<>(this,
                R.layout.monitored_layout);
    }

    private class MyListMonitors extends ArrayAdapter<User>{
        public MyListMonitors(){
            super(ManageMonitoring.this, R.layout.monitoring_layout
            , monitorsUser);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(convertView == null){
                itemView = getLayoutInflater().inflate(R.layout.monitoring_layout,
                        parent,
                        false);
            }

            User currentUser;

            // Find the current User
            if(monitorsUser.isEmpty()){
                currentUser = new User();
                currentUser.setName("No one is being monitored.");
                currentUser.setEmail(" ");
            }
            else {
                currentUser = monitorsUser.get(position);
            }
            TextView nameText = (TextView) itemView.findViewById(R.id.monitoring_name_txtview);
            nameText.setText(currentUser.getName());

            TextView emailText = (TextView) itemView.findViewById(R.id.monitoring_email_txtview);
            emailText.setText(currentUser.getEmail());

            return itemView;
        }
    }

    private class MyListMonitoredBy extends ArrayAdapter<User> {
        public MyListMonitoredBy(){
            super(ManageMonitoring.this, R.layout.monitored_layout
                    , monitoredByUser);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View itemView = convertView;
            if(convertView == null){
                itemView = getLayoutInflater().inflate(R.layout.monitoring_layout,
                        parent,
                        false);
            }

            User currentUser;

            // Find the current User
            if(monitoredByUser.isEmpty()){
                currentUser = new User();
                currentUser.setName("No one is monitoring you.");
                currentUser.setEmail(" ");
            }
            else {
                currentUser = monitorsUser.get(position);
            }
            TextView nameText = (TextView) itemView.findViewById(R.id.txtMonitedByName);
            nameText.setText(currentUser.getName());

            TextView emailText = (TextView) itemView.findViewById(R.id.txtMonitedByEmail);
            emailText.setText(currentUser.getEmail());

            return itemView;
        }
    }

}
