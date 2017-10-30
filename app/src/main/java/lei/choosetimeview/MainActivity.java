package lei.choosetimeview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import lei.choosetimeview.wheelview.CalendarView;

public class MainActivity extends AppCompatActivity {

  private Button button;
  private CalendarView calendarView;
  private TextView textView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    initView();
  }

  private void initView() {
    button = ((Button) findViewById(R.id.btn));
    textView = (TextView) findViewById(R.id.text);

    calendarView = new CalendarView(MainActivity.this, CalendarView.NORMAL);
    calendarView.createDialog();
    calendarView.setTargetTime("", 0);
    calendarView.setCalendarViewCallBack(new CalendarView.CalendarViewCallBack() {
      @Override public void setCalendarTime(String time) {
        textView.setText(time);
      }
    });

    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View v) {
          calendarView.showCalendar();
      }
    });
  }

  @Override protected void onDestroy() {
    super.onDestroy();

    if(calendarView != null){
      calendarView.setCalendarViewCallBack(null);
      calendarView.dismissCalendar();
    }
  }
}
