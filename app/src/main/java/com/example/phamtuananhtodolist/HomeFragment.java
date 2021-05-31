package com.example.phamtuananhtodolist;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class HomeFragment extends Fragment {

    private ListView mListView;
    private List<Task> mtasks;
    private Task mtask;

    private TextView mTextStatus;

    private FloatingActionButton addButton;
    private FloatingActionButton clearButton;

    FirebaseAuth firebaseAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Binding variables to UI elements
        mTextStatus = (TextView) view.findViewById(R.id.list_status);
        mListView = (ListView) view.findViewById(R.id.list);
        addButton = (FloatingActionButton)  view.findViewById(R.id.addButton);
        clearButton = (FloatingActionButton) view.findViewById(R.id.clearButton);

        firebaseAuth = FirebaseAuth.getInstance();
        mtasks = generateData();
        refreshList();

        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                addDialog();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                clearDialog();
            }
        });

        mListView.setLongClickable(true);

        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                deleteOne(pos);
                return true;
            }
        });

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                modifyOne(position);
            }
        });
        return view;
    }


    // ALERTS DIALOGS
    private void deleteOne(int pos) {
        final int position = pos;
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.deleteOne_title);
        alert.setMessage(R.string.deleteOne_message);

        alert.setPositiveButton(R.string.app_yes, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteOnePos(position);
                refreshList();
            }
        });

        alert.setNegativeButton(R.string.app_no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void clearDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Notification");
        alert.setMessage("Bạn có muốn xóa task này không?");

        alert.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                deleteAll();
                refreshList();
            }
        });

        alert.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void addDialog() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.addOne_title);
        alert.setMessage(R.string.addOne_message);

        // Create TextView
        final EditText name = new EditText (getContext());
        name.setHint(R.string.addOne_name);

        final EditText text = new EditText(getContext());
        text.setHint(R.string.addOne_task);

        // Checkbox
        final CheckBox importantCheck = new CheckBox(getContext());
        importantCheck.setText(R.string.addOne_important);

        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 70, 0);

        layout.addView(name, layoutParams);
        layout.addView(text, layoutParams);
        layout.addView(importantCheck, layoutParams);

        alert.setView(layout);

        alert.setPositiveButton(R.string.app_ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                // Random color & add to list
                Random rnd = new Random();
                int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));

                String important;
                if(importantCheck.isChecked()) {
                    important = "y";
                }
                else {
                    important = "n";
                }

                if(name.length() > 0 || text.length() > 0) {
                    mtask = new Task(color, name.getText().toString(), text.getText().toString(), important);
                    AddItem(mtask);
                    refreshList();
                }
            }
        });

        alert.setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void modifyOne(final int position) {

        mtask = mtasks.get(position);

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(R.string.modifyOne_title);
        alert.setMessage(R.string.modifyOne_message);

        // Create TextView
        final EditText name = new EditText (getContext());
        name.setText(mtask.getName());

        final EditText text = new EditText(getContext());
        text.setText(mtask.getText());

        // Checkbox
        final CheckBox importantCheck = new CheckBox(getContext());
        importantCheck.setText(R.string.addOne_important);

        if(mtask.getImportant().equals("y")) {
            importantCheck.setChecked(true);
        }

//        Context context = getApplicationContext();
        LinearLayout layout = new LinearLayout(getContext());
        layout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(70, 0, 70, 0);

        layout.addView(name, layoutParams);
        layout.addView(text, layoutParams);
        layout.addView(importantCheck, layoutParams);

        alert.setView(layout);


        alert.setPositiveButton(R.string.app_modify, new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            public void onClick(DialogInterface dialog, int whichButton) {

                String important;
                if(importantCheck.isChecked()) {
                    important = "y";
                }
                else {
                    important = "n";
                }

                if(name.length() > 0 || text.length() > 0) {
                    mtask = new Task(mtask.getColor(), name.getText().toString(), text.getText().toString(), important);
                    ModifyItem(position, mtask);
                    refreshList();
                }
            }
        });

        alert.setNegativeButton(R.string.app_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    // LIST REFRESH
    private void refreshList() {
        TaskAdapter adapter = new TaskAdapter(getContext(), mtasks);
        mListView.setAdapter(adapter);

        if(mtasks.size() > 0 ) {
            mTextStatus.setText(R.string.app_listNoEmpty);
        }
        else {
            mTextStatus.setText(R.string.app_listEmpty);
        }
    }

    // GENERATE INITIAL DATA
    private List<Task> generateData() {
        mtasks = new ArrayList<>();
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        if(myData != null)
        {
            try {
                JSONArray jsonArray = new JSONArray(myData);
                for (int i = 0; i < jsonArray.length(); i++)
                {
                    String data  = jsonArray.getString(i);
                    String[] splitData = data.split("\\.");

                    mtasks.add(new Task(Integer.parseInt(splitData[0]), splitData[1], splitData[2], splitData[3]));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            mtask = new Task(Color.BLACK, "Florent", getString(R.string.app_example), "y");
            AddItem(mtask);
        }

        return mtasks;
    }

    // JSON SAVE & ACTIONS
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void ModifyItem(int position, Task e) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray(myData);
            jsonArray.remove(position);
            jsonArray.put(e.getColor() + "." + e.getName() + "." + e.getText() + "." + e.getImportant());
        } catch (JSONException e1) {
            e1.printStackTrace();
        }
        mtasks.remove(position);
        mtasks.add(e);

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    private void AddItem(Task e) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;
        if(myData == null) {
            jsonArray = new JSONArray();
            jsonArray.put(e.getColor() + "." + e.getName() + "." + e.getText() + "." + e.getImportant());
            mtasks.add(e);
        } else {
            try {
                jsonArray = new JSONArray(myData);
                jsonArray.put(e.getColor() + "." + e.getName() + "." + e.getText() + "." + e.getImportant());
                mtasks.add(e);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void deleteOnePos(int pos) {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        String myData = myPrefs.getString("myTodoData",null);

        JSONArray jsonArray = null;
        try {
            jsonArray = new JSONArray(myData);

            jsonArray.remove(pos);
            mtasks.remove(pos);
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray != null ? jsonArray.toString() : null);
        editor.apply();
    }

    private void deleteAll() {
        SharedPreferences myPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        JSONArray jsonArray = new JSONArray();
        mtasks = new ArrayList<>();

        SharedPreferences.Editor editor = myPrefs.edit();
        editor.putString("myTodoData", jsonArray.toString());
        editor.apply();
    }


    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
        } else {
            startActivity(new Intent(getActivity(), LoginActivity.class));
            getActivity().finish();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_main, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.actionLogout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }

        return super.onOptionsItemSelected(item);
    }

}