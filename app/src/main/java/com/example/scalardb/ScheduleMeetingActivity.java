package com.example.scalardb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.scalardb.data.MyDbHandler;
import com.example.scalardb.model.Contact;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ScheduleMeetingActivity extends AppCompatActivity {
    Button SetDate;
    TimePickerDialog picker;
    TextView SelectedDate;
    Button SetTime;
    TextView SelectTime;
    Button Save;
    EditText Title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_meeting);
        SetDate = findViewById(R.id.SetDate);
        SelectedDate = findViewById(R.id.set_date);
        SelectedDate.setVisibility(View.GONE);
        SetTime = findViewById(R.id.SetTime);
        SelectTime = findViewById(R.id.set_time);
        SelectTime.setVisibility(View.GONE);
        Save = findViewById(R.id.save);
        Title = findViewById(R.id.et_name);



        SetDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar d = Calendar.getInstance();
                final int mHour = d.get(Calendar.HOUR_OF_DAY);
                final int mMinute = d.get(Calendar.MINUTE);

                picker = new TimePickerDialog(ScheduleMeetingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {
                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                SetDate.setVisibility(View.GONE);
                                SelectedDate.setVisibility(View.VISIBLE);
                                SelectedDate.setText("" + hourOfDay + ":" + minute);
                            }
                        }, mHour, mMinute, true);
                picker.show();
            }

        });

        SetTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Calendar c = Calendar.getInstance();
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(ScheduleMeetingActivity.this,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                SetTime.setVisibility(View.GONE);
                                SelectTime.setText("" + hourOfDay + ":" + minute);
                                SelectTime.setVisibility(View.VISIBLE);
                            }
                        }, mHour, mMinute, true);
                timePickerDialog.show();
            }
        });

        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (SelectedDate.getText().equals(null) || SelectedDate.getText().equals("")) {
                    Toast.makeText(ScheduleMeetingActivity.this, "Add Date", Toast.LENGTH_SHORT).show();
                } else if (SelectTime.getText().equals(null) || SelectTime.getText().equals("")) {
                    Toast.makeText(ScheduleMeetingActivity.this, "Add Time", Toast.LENGTH_SHORT).show();
                } else if (Title.getText().toString().equals(null) || Title.getText().toString().equals("")) {
                    Title.setError("Add Title");
                } else {
                    MyDbHandler db = new MyDbHandler(ScheduleMeetingActivity.this);
                    Contact aqsa = new Contact();
                    aqsa.setName(Title.getText().toString());
                    aqsa.setStart_time(SelectedDate.getText().toString());
                    aqsa.setEnd_time(SelectTime.getText().toString());

                    String startTime = aqsa.getStart_time();
                    String endTime = aqsa.getEnd_time();
                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                    Date d1 = null;
                    try {
                        d1 = sdf.parse(startTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    Date d2 = null;
                    try {
                        d2 = sdf.parse(endTime);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    assert d2 != null;
                    assert d1 != null;

                    Boolean b = true;
                    List<Contact> allContacts = db.getAllContacts();
                    for (Contact contact: allContacts){
                        Log.d("list", "Id: "+ contact.getId() + "\n" +
                                "Name: "+ contact.getName() + "\n" +
                                "start: "+ contact.getStart_time() + "\n" +
                                "end: "+ contact.getEnd_time());

                        String startTime2 = contact.getStart_time();
                        String endTime2 = contact.getEnd_time();
                        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
                        Date dc1 = null;
                        try {
                            dc1 = sdf2.parse(startTime2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        Date dc2 = null;
                        try {
                            dc2 = sdf2.parse(endTime2);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        long elapsed1 = dc1.getTime() - d2.getTime();
                        long elapsed2 = d1.getTime() - dc2.getTime();

                        if(!(elapsed1 >= 0 || elapsed2 >= 0)) {
                            b = false;
                            Toast.makeText(ScheduleMeetingActivity.this, "Interview clashed with id" + contact.getId(), Toast.LENGTH_SHORT).show();
                            break;
                        }
                    }

                    if(b)
                        db.addContact(aqsa);
                    Intent intent = new Intent(ScheduleMeetingActivity.this, MainActivity.class);
                    startActivity(intent);
                }
            }
        });
    }
}