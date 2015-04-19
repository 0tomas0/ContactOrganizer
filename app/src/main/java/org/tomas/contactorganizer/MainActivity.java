package org.tomas.contactorganizer;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity {

    EditText nameTxt, phoneTxt, emailTxt, addressTxt;
    List<Contact> ContactList = new ArrayList<>();
    ListView contactListView;
    private String file = "zaloha";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.nameTxt = (EditText) findViewById(R.id.txtName);
        this.phoneTxt = (EditText) findViewById(R.id.txtPhone);
        this.emailTxt = (EditText) findViewById(R.id.txtEmail);
        this.addressTxt = (EditText) findViewById(R.id.txtAddress);
        this.contactListView = (ListView) findViewById(R.id.listView);
        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        this.getFromFile();

        tabHost.setup();
        TabHost.TabSpec tabSpec = tabHost.newTabSpec("creator");
        tabSpec.setContent(R.id.tabCreator);
        tabSpec.setIndicator("Creator");
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("list");
        tabSpec.setContent(R.id.tabContactList);
        tabSpec.setIndicator("List");
        tabHost.addTab(tabSpec);

        final Button addBtn = (Button) findViewById(R.id.btnAdd);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addContacts(nameTxt.getText().toString(), phoneTxt.getText().toString(), emailTxt.getText().toString(), addressTxt.getText().toString());
                populateList();
                Toast.makeText(getApplicationContext(), nameTxt.getText().toString() + " has been added to your Contacts!", Toast.LENGTH_SHORT).show();
            }
        });

        this.nameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addBtn.setEnabled(!nameTxt.getText().toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void populateList() {
        ArrayAdapter<Contact> adapter = new ContactListAdapter();
        contactListView.setAdapter(adapter);
    }

    private void addContacts(String name, String phone, String email, String address) {
        ContactList.add(new Contact(name, phone, email, address));
    }

    private void getFromFile() {
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(file)));
            String input;
            while ((input = br.readLine()) != null) {
                String[] pom = input.split(";");
                if (pom.length == 4) {
                    ContactList.add(new Contact(pom[0], pom[1], pom[2], pom[3]));
                }
            }
            br.close();
            populateList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ContactListAdapter extends ArrayAdapter<Contact> {
        public ContactListAdapter() {
            super(MainActivity.this, R.layout.listview_item, ContactList);
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.listview_item, parent, false);
            }

            Contact currentContact = ContactList.get(position);

            TextView name = (TextView) view.findViewById(R.id.contactName);
            name.setText(currentContact.getName());
            TextView phone = (TextView) view.findViewById(R.id.phoneNumber);
            phone.setText(currentContact.getPhone());
            TextView email = (TextView) view.findViewById(R.id.emailAddress);
            email.setText(currentContact.getEmail());
            TextView address = (TextView) view.findViewById(R.id.cAddress);
            address.setText(currentContact.getAddress());

            return view;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        switch (id) {
            case R.id.action_settings:
                break;
            case R.id.action_delete_file:
                if (this.deleteFile()) {
                    Toast.makeText(getApplicationContext(), "Successful clear file !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error whit clear file !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_save_file:
                if (this.saveFile()) {
                    Toast.makeText(getApplicationContext(), "Successful save data to file !", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "Error whit save data to file !", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_exit:
                System.exit(0);
                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean saveFile() {
        try {
            FileOutputStream fos = openFileOutput(file, MODE_PRIVATE);
            for (Contact con : this.ContactList) {
                String write = con.getName() + ";" + con.getPhone() + ";" + con.getEmail() + ";" + con.getAddress() + "\r\n";
                fos.write(write.getBytes());
            }
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean deleteFile() {
        try {
            FileOutputStream fos = openFileOutput(file, MODE_PRIVATE);
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
