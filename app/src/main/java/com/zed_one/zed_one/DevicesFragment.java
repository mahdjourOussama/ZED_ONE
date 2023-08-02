package com.zed_one.zed_one;

import static android.content.ContentValues.TAG;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

public class DevicesFragment extends Fragment {

    private FirebaseDatabase db =null;
    private ListView listview =null;
    private ArrayList<String> list =null;
    private ArrayAdapter adapter =null;

    private String UserID;
    private Button add_btn = null;
    private EditText ID_TextEdit= null,Serial_TextEdit=null;
    private int Devices_count=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_devices, container, false);
        // setting up the ADD section
        add_btn = rootView.findViewById(R.id.add_btn);
        ID_TextEdit =rootView.findViewById(R.id.Device_Name_EditText);
        Serial_TextEdit=rootView.findViewById(R.id.Serial_code_EditText);

        // Setting up the ListView
        listview = (ListView) rootView.findViewById(R.id.listview);
        list= new ArrayList<>();
        adapter = new ArrayAdapter<>(getActivity(),R.layout.devices_list,list);
        listview.setAdapter(adapter);

        // Retrieve the UserID from the arguments
        UserID = getArguments().getString("UserID");
        // Handling database connection
        db =FirebaseDatabase.getInstance();
        DatabaseReference reference= db.getReference().child(UserID).child("Devices");



        // Fetching Data and Updating ListView
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.

                list.clear();
                for (DataSnapshot s : dataSnapshot.getChildren()){
                    Device device = s.getValue(Device.class);
                    list.add("Device Name: "+device.getName()+" WIth ID :"+ device.getDeviceID());

                }
                adapter.notifyDataSetChanged();
                Devices_count = (int) dataSnapshot.getChildrenCount();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });

        // Adding Element To Database
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID_text = ID_TextEdit.getText().toString(),
                        SCode = Serial_TextEdit.getText().toString();
                if(ID_text.isEmpty())
                    Toast.makeText(getContext(),"Enter ID", Toast.LENGTH_SHORT).show();
                else if (SCode.isEmpty()) {
                    Toast.makeText(getContext(),"Enter Serial Code", Toast.LENGTH_SHORT).show();
                }else {
                    String deviceID="D" + (int)(Devices_count + 1);
                    Device device = new Device(ID_text,deviceID,SCode);
                    //Toast.makeText(getContext(),""+device.getLoc().size(), Toast.LENGTH_SHORT).show();
                    reference.child(deviceID).setValue(device);
                    HashMap<String,String> dict = new HashMap<>();
                    dict.put("DeviceID",deviceID);
                    dict.put("UserID",UserID);
                    db.getReference().child("Devices").child(SCode).setValue(dict);
                }
            }
        });
        return rootView;
    }

    public static DevicesFragment newInstance(String userID) {
        DevicesFragment fragment = new DevicesFragment();
        Bundle args = new Bundle();
        args.putString("UserID", userID);
        fragment.setArguments(args);
        return fragment;
    }
   
}