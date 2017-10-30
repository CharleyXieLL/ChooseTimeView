package lei.choosetimeview.wheelview;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import lei.choosetimeview.R;
import lei.choosetimeview.util.Strings;
import lei.choosetimeview.wheelview.adapter.NumericWheelAdapter;

public class ChooseTime {
  private int mYear = 1996;
  private int mMonth = 0;
  private int mDay = 1;
  private TextView done;
  private WheelView year;
  private WheelView month;
  private WheelView day;
  private View view = null;
  private Activity mContext;
  private LayoutInflater inflater = null;
  /** 当前选中月份 **/
  private int nowMonth = 0;
  /** 当前选中年 **/
  private int nowYear = 0;
  /** 当前选中月份总天数 **/
  private int nowMonth_allday = 0;
  private TextView cancel;

  private String str_year;
  private String str_day;
  private String str_month;

  private String cYear;
  private String cMonth;
  private String cDay;
  private long dayCount;
  private int monthCount;
  private String cTime;
  private String targetTime;
  private int targetDays;
  private OnClickListener onClickListener;

  public ChooseTime(Activity context) {
    this.mContext = context;
    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  public View getDataPick(OnClickListener itemsOnClick, String time, int days) {
    this.onClickListener = itemsOnClick;
    this.targetDays = days;
    this.targetTime = time;
    Calendar calendar = Calendar.getInstance();
    if (!Strings.isNullOrEmpty(time)) {
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      long legalTime;
      Date date;
      Date legalDate = new Date();
      try {
        date = simpleDateFormat.parse(time);
        legalTime = date.getTime() + days * 24 * 60 * 60 * 1000;
        legalDate.setTime(legalTime);
      } catch (ParseException e) {
        e.printStackTrace();
      }
      calendar.setTime(legalDate);
    }

    mYear = calendar.get(Calendar.YEAR);
    mMonth = calendar.get(Calendar.MONTH);
    mDay = calendar.get(Calendar.DAY_OF_MONTH);

    int curMonth;
    int curDate;
    int curYear = mYear;
    if (!Strings.isNullOrEmpty(time)) {
      curDate = mDay;
    } else {
      curDate = mDay + 1;
    }
    curMonth = mMonth + 1;

    if (year == null || day == null || month == null) {
      view = inflater.inflate(R.layout.yq_wheel_date_picker, null);
      done = (TextView) view.findViewById(R.id.done);
      done.setOnClickListener(itemsOnClick);
      cancel = ((TextView) view.findViewById(R.id.cancel));
      cancel.setOnClickListener(itemsOnClick);
      year = (WheelView) view.findViewById(R.id.year);

      NumericWheelAdapter numericWheelAdapter1;

      if (!Strings.isNullOrEmpty(time)) {
        numericWheelAdapter1 = new NumericWheelAdapter(mContext, curYear, curYear + 1);
      } else {
        numericWheelAdapter1 = new NumericWheelAdapter(mContext, curYear, curYear + 4);
      }

      numericWheelAdapter1.setLabel("年");
      year.setViewAdapter(numericWheelAdapter1);
      year.setCyclic(true);// 是否可循环滑动
      year.addScrollingListener(scrollListener);

      month = (WheelView) view.findViewById(R.id.month);
      NumericWheelAdapter numericWheelAdapter2 = new NumericWheelAdapter(mContext, 1, 12, "%02d");
      numericWheelAdapter2.setLabel("月");
      month.setViewAdapter(numericWheelAdapter2);
      month.setCyclic(true);
      month.addScrollingListener(scrollListener);

      day = (WheelView) view.findViewById(R.id.day);
      initDay(curYear, curMonth);
      day.setCyclic(true);
      day.addScrollingListener(scrollListener);

      year.setVisibleItems(7);// 设置显示行数
      month.setVisibleItems(7);
      day.setVisibleItems(7);

      year.setCurrentItem(0);
      month.setCurrentItem(curMonth - 1);
      day.setCurrentItem(curDate - 1);
    }
    return view;
  }

  public void setTargetDays(String days) {
    if (month == null || year == null || day == null) {
      getDataPick(onClickListener, targetTime, targetDays);
    }
    int curYear = mYear;
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Date date = null;
    try {
      date = simpleDateFormat.parse(days);
    } catch (ParseException e) {
      e.printStackTrace();
    }
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    int cYear = calendar.get(Calendar.YEAR);
    int cMonth = calendar.get(Calendar.MONTH) + 1;
    int cDays = calendar.get(Calendar.DAY_OF_MONTH);
    for (int i = 0; i < 5; i++) {
      if ((curYear + i) == cYear) {
        year.setCurrentItem(i);
      }
    }
    for (int i = 0; i < 12; i++) {
      if ((cMonth - 1) == i) {
        month.setCurrentItem(i);
      }
    }
    int curMonthDays = getDay(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1);
    for (int i = 0; i < curMonthDays; i++) {
      if (cDays - 1 == i) {
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
      }
    }
  }

  OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
    @Override public void onScrollingStarted(WheelView wheel) {

    }

    @Override public void onScrollingFinished(WheelView wheel) {
      int n_year = year.getCurrentItem() + 1950;// 年
      int n_month = month.getCurrentItem() + 1;// 月
      nowYear = year.getCurrentItem() + 1950;// 年
      initDay(n_year, n_month);
    }
  };

  private void initDay(int n_year, int n_month) {
    nowMonth_allday = getDay(n_year, n_month);
    nowMonth = n_month;
    nowYear = n_year;
    NumericWheelAdapter numericWheelAdapter =
        new NumericWheelAdapter(mContext, 1, getDay(n_year, n_month), "%02d");
    numericWheelAdapter.setLabel("日");
    day.setViewAdapter(numericWheelAdapter);
  }

  /**
   *
   * @param year
   * @param month
   * @return
   */
  private int getDay(int year, int month) {
    int day;
    boolean flag;
    switch (year % 4) {
      case 0:
        flag = true;
        break;
      default:
        flag = false;
        break;
    }
    switch (month) {
      case 1:
        day = 31;
        break;
      case 2:
        day = flag ? 29 : 28;
        break;
      case 3:
        day = 31;
        break;
      case 4:
        day = 30;
        break;
      case 5:
        day = 31;
        break;
      case 6:
        day = 30;
        break;
      case 7:
        day = 31;
        break;
      case 8:
        day = 31;
        break;
      case 9:
        day = 30;
        break;
      case 10:
        day = 31;
        break;
      case 11:
        day = 30;
        break;
      case 12:
        day = 31;
        break;
      default:
        day = 30;
        break;
    }
    return day;
  }

  /**
   * 比较日期
   */
  public boolean compareDate() {
    Boolean boolean1 = true;

    if ((year.getCurrentItem() + mYear) == mYear && (month.getCurrentItem() + 1) < (mMonth + 1)) {
      boolean1 = false;
    }

    if (!Strings.isNullOrEmpty(targetTime)) {
      if ((year.getCurrentItem() + mYear) == mYear
          && (month.getCurrentItem() + 1) == (mMonth + 1)
          && (day.getCurrentItem() + 1) < mDay) {
        boolean1 = false;
      }
    } else {
      if ((year.getCurrentItem() + mYear) == mYear
          && (month.getCurrentItem() + 1) == (mMonth + 1)
          && (day.getCurrentItem() + 1) <= mDay) {
        boolean1 = false;
      }
    }

    return boolean1;
  }

  public String getTxt() {
    if (year != null && month != null && day != null) {
      str_year = year.getCurrentItem() + mYear + "年";
      cYear = String.valueOf(year.getCurrentItem() + mYear);
      if (day.getCurrentItem() + 1 > nowMonth_allday) {
        // 得到天数大于本月天数
        if (mMonth + 1 == 2) {
          if (day.getCurrentItem() + 1 == 31) {
            switch ((year.getCurrentItem() + mYear) % 4) {
              case 0:
                str_day = "02";
                break;
              default:
                str_day = "03";
                break;
            }
          } else if (day.getCurrentItem() + 1 == 30) {
            switch ((year.getCurrentItem() + mYear) % 4) {
              case 0:
                str_day = "01";
                break;
              default:
                str_day = "02";
                break;
            }
          } else if (day.getCurrentItem() + 1 == 29) {
            str_day = "01";
          }
        } else {
          str_day = "01";
        }
      } else {
        str_day = setDataFormat(day.getCurrentItem() + 1);
        cDay = String.valueOf(setDataFormat(day.getCurrentItem() + 1));
      }

      str_month = setDataFormat(month.getCurrentItem() + 1) + "月";
      cMonth = String.valueOf(setDataFormat(month.getCurrentItem() + 1));
      switch (mYear % 4) {
        case 0://闰年
          break;
        default:
          if (day.getCurrentItem() + 1 == 29 && mMonth + 1 == 2) {
            str_day = "01";
          }
          break;
      }

      String time = str_year + str_month + str_day + "日";

      //这里的天不能用cDay 一个月有31天切换到下个月没有31天的时候不动天数轮播器，天数是没法更新的
      cTime = cYear + "-" + cMonth + "-" + str_day;

      String timeCurrent = mYear + "-" + (mMonth + 1) + "-" + mDay;

      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      //得到毫秒数
      long timeStart = 0;
      long timeEnd = 0;
      try {
        timeStart = simpleDateFormat.parse(timeCurrent).getTime();
        timeEnd = simpleDateFormat.parse(cTime).getTime();
      } catch (ParseException e) {
        e.printStackTrace();
      }
      //两个日期相减得到天数
      dayCount = (timeEnd - timeStart) / (24 * 3600 * 1000);

      monthCount = (int) dayCount / 30;
      return time;
    }
    return null;
  }

  public String getCTime() {
    return cTime;
  }

  public long getDays() {
    if (!Strings.isNullOrEmpty(targetTime)) {
      return dayCount + targetDays;
    }
    return dayCount;
  }

  public int getMonthCount() {
    return monthCount;
  }

  public String getYearStr() {
    return cYear;
  }

  public String getMonthStr() {
    return cMonth;
  }

  public String getDayStr() {
    return cDay;
  }

  private String setDataFormat(int i) {
    String string;
    if (i < 10) {
      string = "0" + i;
    } else {
      string = "" + i;
    }
    return string;
  }
}
