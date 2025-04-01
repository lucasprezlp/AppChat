package com.example.appchat.providers;

import com.example.appchat.model.User;

import com.parse.ParseUser;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
public class AuthProvider {

    public AuthProvider() {

    }
    public LiveData<String> signIn(String email, String password) {
        MutableLiveData<String> authResult = new MutableLiveData<>();
        ParseUser.logInInBackground(email, password, (user, e) -> {
            if (e == null) {
                // Login exitoso
                authResult.setValue(user.getObjectId());
                Log.d("AuthProvider", "Usuario autenticado exitosamente: " + user.getObjectId());
            } else {
                // Error en el login
                Log.e("AuthProvider", "Error en inicio de sesión: ", e);
                authResult.setValue(null);
            }
        });
        return authResult;
    }
    // Registro con Parse
    public LiveData<String> signUp(User user) {
        MutableLiveData<String> authResult = new MutableLiveData<>();

        if (user.getUsername() == null || user.getPassword() == null || user.getEmail() == null) {
            Log.e("AuthProvider", "Uno o más valores son nulos: " +
                    "Username=" + user.getUsername() + ", " +
                    "Password=" + user.getPassword() + ", " +
                    "Email=" + user.getEmail());
            authResult.setValue(null);
            return authResult;
        }

        ParseUser parseUser = new ParseUser();
        parseUser.setUsername(user.getUsername());
        parseUser.setPassword(user.getPassword());
        parseUser.setEmail(user.getEmail());

        parseUser.signUpInBackground(e -> {
            if (e == null) {
                // Registro exitoso
                authResult.setValue(parseUser.getObjectId());
                Log.d("AuthProvider", "Usuario registrado exitosamente: " + parseUser.getObjectId());
            } else {
                // Error en el registro
                Log.e("AuthProvider", "Error en registro: " + e.getMessage(), e);
                authResult.setValue(null);
            }
        });
        return authResult;
    }


    public LiveData<Boolean> logout() {
        MutableLiveData<Boolean> logoutResult = new MutableLiveData<>();
        ParseUser.logOutInBackground(e -> {
            if (e == null) {
                logoutResult.setValue(true);
                Log.d("AuthProvider", "Caché eliminada y usuario desconectado.");

            } else {

                logoutResult.setValue(false);
                Log.e("AuthProvider", "Error al desconectar al usuario: ", e);
            }
        });
        return logoutResult;
    }
}

