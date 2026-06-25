package com.prograavanzada.recordatoriodemedicina;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etEmail, etPassword, etPasswordConfirm;
    private TextInputLayout nombreLayout, emailLayout, passLayout, confirmLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etEmail = findViewById(R.id.etEmailReg);
        etPassword = findViewById(R.id.etPasswordReg);
        etPasswordConfirm = findViewById(R.id.etPasswordConfirm);

        nombreLayout = findViewById(R.id.nombreLayoutId);
        emailLayout = findViewById(R.id.emailRegLayoutId);
        passLayout = findViewById(R.id.passRegLayoutId);
        confirmLayout = findViewById(R.id.passConfirmLayoutId);

        Button btnRegistrar = findViewById(R.id.btnRegistrar);
        ImageView btnVolver = findViewById(R.id.btnVolver);

        btnVolver.setOnClickListener(v -> finish());

        btnRegistrar.setOnClickListener(v -> {
            if (validarRegistro()) {
                SharedPreferences prefs = getSharedPreferences("PillMinderPrefs", Context.MODE_PRIVATE);
                prefs.edit().putString("user_nombre", etNombre.getText().toString().trim())
                        .putString("user_email", etEmail.getText().toString().trim())
                        .putString("user_password", etPassword.getText().toString().trim())
                        .apply();

                mostrarMensaje("¡Registro exitoso!", true);
                v.postDelayed(this::finish, 1500);
            }
        });
    }

    private boolean validarRegistro() {
        boolean isValid = true;

        if (etNombre.getText().toString().trim().isEmpty()) {
            nombreLayout.setError("Ingresa tu nombre completo");
            isValid = false;
        } else { nombreLayout.setError(null); }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(etEmail.getText().toString()).matches()) {
            emailLayout.setError("Ingresa un correo válido");
            isValid = false;
        } else { emailLayout.setError(null); }

        if (etPassword.getText().toString().length() < 6) {
            passLayout.setError("La contraseña es obligatoria (mín. 6 caracteres)");
            isValid = false;
        } else { passLayout.setError(null); }

        if (!etPassword.getText().toString().equals(etPasswordConfirm.getText().toString())) {
            confirmLayout.setError("Las contraseñas no coinciden");
            isValid = false;
        } else { confirmLayout.setError(null); }

        return isValid;
    }

    private void mostrarMensaje(String mensaje, boolean esExito) {
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), mensaje, Snackbar.LENGTH_LONG);
        View view = snackbar.getView();
        view.setBackgroundResource(esExito ? R.drawable.bg_azul_redondeado : R.drawable.bg_rojo_redondeado);
        view.setBackgroundTintList(null);
        snackbar.show();
// Comentario para commit 3
    }
}