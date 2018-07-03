package ca.cmpt276.walkinggroupindigo.walkinggroup.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import ca.cmpt276.walkinggroupindigo.walkinggroup.R;
import ca.cmpt276.walkinggroupindigo.walkinggroup.dataobjects.User;
import ca.cmpt276.walkinggroupindigo.walkinggroup.proxy.ProxyBuilder;
import ca.cmpt276.walkinggroupindigo.walkinggroup.proxy.WGServerProxy;
import retrofit2.Call;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginScreen";
    private WGServerProxy proxy;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getApiKey();
        checkIfUserIsLoggedIn();
        setUpLoginButton();
    }

    private void checkIfUserIsLoggedIn() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.signUpButton:
                Intent intent = SignUpActivity.makeIntent(LoginActivity.this);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
    
    private void getApiKey() {
        String apiKey = getString(R.string.apikey);
        proxy = ProxyBuilder.getProxy(apiKey,null);

    }

    private void setUpLoginButton() {
        Button button = findViewById(R.id.login_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginUser();
            }
        });
    }
    
    private void LoginUser() {
        User user = new User();
        String userEmail = getInputText(R.id.emailEdit);
        String userPass = getInputText(R.id.passEdit);
        if (userEmail.matches("")) {
            Toast.makeText(LoginActivity.this, R.string.email_empty_login, Toast.LENGTH_SHORT).show();
        } else if (userPass.matches("")) {
            Toast.makeText(LoginActivity.this, R.string.password_empty_login, Toast.LENGTH_SHORT).show();
        } else {
            user.setEmail(userEmail);
            user.setPassword(userPass);
            ProxyBuilder.setOnTokenReceiveCallback(token -> onReceiveToken(token));
            Call<Void> caller = proxy.login(user);
            ProxyBuilder.callProxy(LoginActivity.this, caller, returnedNothing -> logIn(returnedNothing));
        }
    }

    private void logIn(Void returnedNothing) {
        Toast.makeText(LoginActivity.this, "Successfully logged in", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }

    private String getInputText(int id){
        EditText text = findViewById(id);
        return text.getText().toString();
    }

    private void onReceiveToken(String token) {
        proxy = ProxyBuilder.getProxy(getString(R.string.apikey), token);
    }

}