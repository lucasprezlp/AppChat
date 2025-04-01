package com.example.appchat.view;
import com.example.appchat.R;
import android.app.Application;
import android.util.Log;


import com.example.appchat.model.Comentario;
import com.example.appchat.model.Message;
import com.example.appchat.model.Post;
import com.example.appchat.model.User;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseObject;


public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);

        // Registrar subclases de ParseObject
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Post.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Comentario.class);

        // Inicializar Parse
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId(getString(R.string.back4app_app_id))
                .clientKey(getString(R.string.back4app_client_key))
                .server(getString(R.string.back4app_server_url))
                .build()
        );

        // Verificar inicialización
        Log.d("MyApplication", "Parse inicializado correctamente");

        // Configurar ACL por defecto
        ParseACL defaultACL = new ParseACL();
        defaultACL.setPublicReadAccess(true);
        defaultACL.setPublicWriteAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);
    }
}
