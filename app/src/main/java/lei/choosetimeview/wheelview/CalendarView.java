package lei.choosetimeview.wheelview;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;
import lei.choosetimeview.R;

/**
 * Created by Lei on 2017/6/13.
 * 日期选择器
 */

public class CalendarView implements View.OnClickListener {

  /**
   * 正常选择器
   */
  public static final String NORMAL = "normal";
  /**
   * 服务于展期的选择器
   */
  public static final String EXTEND = "extend";

  private Activity sActivity;
  private Dialog dialog;
  private LinearLayout timepickLinear;
  private ChooseTime chooseTimeView;

  private CalendarViewCallBack calendarViewCallBack;
  private RelativeLayout relatCalendar;

  private String type;

  private int defaultExtendDays;

  public CalendarView(Activity sActivity, String type) {
    this.sActivity = sActivity;
    this.type = type;
    createDialog();
  }

  public void createDialog() {
    if (dialog == null) {
      dialog = new Dialog(sActivity);
      dialog.setCancelable(false);
      dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
      dialog.setCanceledOnTouchOutside(false);
      dialog.setContentView(R.layout.yq_calendar_view);

      Window window = dialog.getWindow();
      window.setGravity(Gravity.BOTTOM);
      window.setWindowAnimations(R.style.yq_mypopwindow_anim_style);
      window.getDecorView().setPadding(0, 0, 0, 0);

      WindowManager.LayoutParams lp = window.getAttributes();
      lp.width = WindowManager.LayoutParams.MATCH_PARENT;
      lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
      window.setAttributes(lp);
    }
    initView();
    initListener();
  }

  private void initListener() {

  }

  public void initView() {
    timepickLinear = ((LinearLayout) dialog.findViewById(R.id.timepick));

    chooseTimeView = new ChooseTime(sActivity);

    timepickLinear.removeAllViews();

    relatCalendar = ((RelativeLayout) dialog.findViewById(R.id.relat_calendar_view));

    relatCalendar.setOnClickListener(this);
  }

  /**
   * 设置限制日期，默认为空表明日期为今天(控制选择器不能选择今天之前,今天也不行)
   */
  public void setTargetTime(String time, int days) {
    timepickLinear.addView(chooseTimeView.getDataPick(this, time, days));
  }

  public void showCalendar() {
    if (dialog != null && !dialog.isShowing()) {
      dialog.show();
    }
  }

  public void dismissCalendar() {
    if (dialog != null && dialog.isShowing()) {
      dialog.dismiss();
    }
  }

  @Override public void onClick(View v) {
    switch (v.getId()) {
      case R.id.done:
        selectDay();
        break;
      case R.id.cancel:
        dismissCalendar();
        break;
      case R.id.relat_calendar_view:
        dismissCalendar();
        break;
    }
  }

  public void selectDay() {
    if (chooseTimeView.compareDate()) {
      dismissCalendar();
      //时间
      String time = chooseTimeView.getTxt();
      if (type.equals(NORMAL)) {
        calendarViewCallBack.setCalendarTime(time + "  " + chooseTimeView.getDays() + "天");
      }
      if (type.equals(EXTEND)) {
        calendarViewCallBack.setCalendarTime(time + "，展期" + chooseTimeView.getDays() + "天");
      }
    } else {
      if (type.equals(NORMAL)) {
        if (chooseTimeView.getDays() == 0) {
          Toast.makeText(sActivity,"还款日期不能选择今天",Toast.LENGTH_LONG).show();
        } else {
          Toast.makeText(sActivity,"还款日期不能选择今天之前",Toast.LENGTH_LONG).show();
        }
      }
      if (type.equals(EXTEND)) {
        Toast.makeText(sActivity,"展期日必须大于还款日",Toast.LENGTH_LONG).show();
      }
    }
  }

  /**
   * 设定指定日期
   */
  public void setTargetDay(String days) {
    if (chooseTimeView != null) {
      chooseTimeView.setTargetDays(days);
      selectDay();
    }
  }

  public void setDefaultExtendDays(int days) {
    this.defaultExtendDays = days;
  }

  public String getTimeText() {
    return chooseTimeView.getTxt();
  }

  public String getTimeStr() {
    chooseTimeView.getTxt();
    return chooseTimeView.getCTime();
  }

  public long getDays() {
    return chooseTimeView.getDays();
  }

  public int getDayInt() {
    return (int) chooseTimeView.getDays();
  }

  public interface CalendarViewCallBack {
    void setCalendarTime(String time);
  }

  public void setCalendarViewCallBack(CalendarViewCallBack calendarViewCallBack) {
    this.calendarViewCallBack = calendarViewCallBack;
  }
}
