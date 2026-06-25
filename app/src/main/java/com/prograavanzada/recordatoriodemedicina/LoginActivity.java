package com.prograavanzada.recordatoriodemedicina;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout; // Asegúrate de importar esto

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvIrRegistro;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPreferences = getSharedPreferences("PillMinderPrefs", Context.MODE_PRIVATE);

        if (sharedPreferences.getBoolean("isLoggedIn", false)) {
            irAPantallaPrincipal();
            return;
        }

        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvIrRegistro = findViewById(R.id.tvIrRegistro);

        // AQUÍ CONECTAMOS LA LÓGICA DE VALIDACIÓN CON EL CLIC
        btnLogin.setOnClickListener(v -> {
            if (validateInputs()) {
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                String usuarioGuardado = sharedPreferences.getString("user_email", "");
                String claveGuardada = sharedPreferences.getString("user_password", "");

                if (email.equals(usuarioGuardado) && password.equals(claveGuardada)) {
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply();
                    irAPantallaPrincipal();
                } else {
                    mostrarError("Correo o contraseña incorrectos");
                }
            }
        });

        tvIrRegistro.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
            startActivity(intent);
        });
    }

    private boolean validateInputs() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // IMPORTANTE: Asegúrate de que en tu activity_login.xml los TextInputLayout
        // tengan exactamente estos IDs: emailLayoutId y passwordLayoutId
        TextInputLayout emailLayout = findViewById(R.id.emailLayoutId);
        TextInputLayout passLayout = findViewById(R.id.passwordLayoutId);

        boolean isValid = true;

        if (email.isEmpty()) {
            emailLayout.setError("El correo es obligatorio");
            isValid = false;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLayout.setError("Formato de correo inválido");
            isValid = false;
        } else {
            emailLayout.setError(null);
        }

        if (password.length() < 6) {
            passLayout.setError("Mínimo 6 caracteres");
            isValid = false;
        } else {
            passLayout.setError(null);
        }

        return isValid;
    }

    private void irAPantallaPrincipal() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void mostrarError(String mensaje) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG);
        snackbar.setTextColor(Color.WHITE);
        View view = snackbar.getView();
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
        params.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        params.setMargins(0, 0, 0, 180);
        view.setLayoutParams(params);
        view.setBackgroundResource(R.drawable.bg_rojo_redondeado);
        view.setBackgroundTintList(null);
        snackbar.show();
    }
}