package com.example.appchat.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appchat.R;
import com.example.appchat.adapters.ChatAdapter;
import com.example.appchat.model.Message;
import com.example.appchat.viewmodel.ChatViewModel;

import java.util.ArrayList;
import java.util.List;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerViewChats;
    private EditText editTextMessage;
    private Button buttonSend;
    private ChatAdapter chatAdapter;
    private ChatViewModel chatViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        recyclerViewChats = view.findViewById(R.id.recyclerViewChats);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        chatAdapter = new ChatAdapter(getContext(), new ArrayList<>());
        recyclerViewChats.setAdapter(chatAdapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(getContext()));

        chatViewModel = new ViewModelProvider(this).get(ChatViewModel.class);

        // Observar cambios en la lista de mensajes
        chatViewModel.getMessagesLiveData().observe(getViewLifecycleOwner(), messages -> {
            chatAdapter.updateMessages(messages); // Actualizar el adaptador
            recyclerViewChats.scrollToPosition(messages.size() - 1); // Desplazarse al último mensaje
        });

        buttonSend.setOnClickListener(v -> {
            String content = editTextMessage.getText().toString();
            chatViewModel.sendMessage(content); // Enviar mensaje
            editTextMessage.setText(""); // Limpiar el EditText
        });

        return view;
    }

    private void updateMessages(List<Message> messages) {
        chatAdapter.updateMessages(messages);
        recyclerViewChats.scrollToPosition(messages.size() - 1);
    }
}