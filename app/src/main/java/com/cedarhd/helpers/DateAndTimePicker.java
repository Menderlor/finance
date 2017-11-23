package com.cedarhd.helpers;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.cedarhd.R;
import com.cedarhd.utils.LogUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import kankan.wheel.widget.OnWheelChangedListener;
import kankan.wheel.widget.WheelView;
import kankan.wheel.widget.adapters.AbstractWheelTextAdapter;
import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.NumericWheelAdapter;

/**
 * 日期时间选择器
 *
 * @author BOHR
 */
public class DateAndTimePicker {

    private Context mContext;
    private final int startYear = 1910;

    public DateAndTimePicker(Context context) {
        super();
        this.mContext = context;
    }

    /**
     * 弹出日期时间选择对话框
     *
     * @param tvStartDate
     */
    public void showDateAndTimeDialog(final TextView tvStartDate) {
        Builder builder = new AlertDialog.Builder(mContext);
        View view = View.inflate(mContext, R.layout.date_time_picker, null);
        final DatePicker datePicker = (DatePicker) view
                .findViewById(R.id.date_picker);
        final TimePicker timePicker = (android.widget.TimePicker) view
                .findViewById(R.id.time_picker);
        builder.setView(view);
        builder.setTitle("选取时间");
        builder.setPositiveButton("确  定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuffer sb = new StringBuffer();
                        sb.append(String.format("%d-%02d-%02d",
                                datePicker.getYear(),
                                datePicker.getMonth() + 1,
                                datePicker.getDayOfMonth()));
                        sb.append(" ");
                        if (timePicker.getCurrentHour() > 9) {
                            sb.append(timePicker.getCurrentHour());
                        } else {
                            sb.append("0" + timePicker.getCurrentHour());
                        }
                        sb.append(":");
                        if (timePicker.getCurrentMinute() > 9) {
                            sb.append(timePicker.getCurrentMinute());
                        } else {
                            sb.append("0" + timePicker.getCurrentMinute());
                        }
                        tvStartDate.setText(sb);
                        dialog.cancel();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 小时 如： 00,01... 09,10,11..23
     */
    private int hourValue;
    /**
     * 分钟 如00,01... 09,10,11..59
     */
    private int minsValue;
    /**
     * 和当前天数的偏移量
     */
    private int dayOffsets;

    // /**
    // * 弹出日期滚轮
    // *
    // * @param tvStartDate
    // */
    // public void showDateWheel(final TextView tvStartDate) {
    // final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
    // Builder builder = new AlertDialog.Builder(mContext);
    // View view = View.inflate(mContext, R.layout.date_pick_wheel, null);
    // final WheelView hours = (WheelView) view.findViewById(R.id.hour_wheel);
    // final WheelView mins = (WheelView) view.findViewById(R.id.mins_wheel);
    // final NumericWheelAdapter hourAdapter = new NumericWheelAdapter(
    // mContext, 0, 23);
    // hourAdapter.setItemResource(R.layout.wheel_text_item);
    // hourAdapter.setItemTextResource(R.id.text);
    // hours.setViewAdapter(hourAdapter);
    //
    // final NumericWheelAdapter minAdapter = new NumericWheelAdapter(
    // mContext, 0, 59, "%02d");
    // minAdapter.setItemResource(R.layout.wheel_text_item);
    // minAdapter.setItemTextResource(R.id.text);
    // mins.setViewAdapter(minAdapter);
    // mins.setCyclic(true);
    //
    // final Button btnCancle = (Button) view
    // .findViewById(R.id.btn_cancel_date_wheel);
    // final Button btnOk = (Button) view.findViewById(R.id.btn_ok_date_wheel);
    //
    // Calendar calendar = Calendar.getInstance(Locale.CHINA);
    // hourValue = calendar.get(Calendar.HOUR);
    // minsValue = calendar.get(Calendar.MINUTE);
    // hours.setCurrentItem(calendar.get(Calendar.HOUR));
    // mins.setCurrentItem(calendar.get(Calendar.MINUTE));
    // final WheelView day = (WheelView) view.findViewById(R.id.day_wheel);
    // day.setViewAdapter(new DayArrayAdapter(mContext, calendar));
    // day.setCurrentItem(15);
    // final AlertDialog dialog = builder.create();
    // dialog.setView(view, 0, 0, 0, 0);
    // dialog.show();
    //
    // day.addChangingListener(new OnWheelChangedListener() {
    // @Override
    // public void onChanged(WheelView wheel, int oldValue, int newValue) {
    // LogUtils.d("wheel", "day：" + newValue);
    // dayOffsets = newValue - 15; // 和当前的偏移量
    // }
    // });
    // hours.addChangingListener(new OnWheelChangedListener() {
    // @Override
    // public void onChanged(WheelView wheel, int oldValue, int newValue) {
    // LogUtils.d("wheel", "小时：" + newValue);
    // hourValue = newValue;
    // }
    // });
    // mins.addChangingListener(new OnWheelChangedListener() {
    // @Override
    // public void onChanged(WheelView wheel, int oldValue, int newValue) {
    // LogUtils.d("wheel", "分钟：" + newValue);
    // minsValue = newValue;
    // }
    // });
    //
    // btnCancle.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // dialog.dismiss();
    // }
    // });
    //
    // btnOk.setOnClickListener(new OnClickListener() {
    // @Override
    // public void onClick(View v) {
    // Calendar dateCalendar = Calendar.getInstance();
    // dateCalendar.add(Calendar.DATE, dayOffsets);
    // String dateStr = dateformat.format(dateCalendar.getTime());
    // String time = String.format("%02d", hourValue) + ":"
    // + String.format("%02d", minsValue) + ":00";
    // String result = dateStr + " " + time;
    // tvStartDate.setText(result);
    // dialog.dismiss();
    // // Toast.makeText(context, "选中时间->" + result,
    // // Toast.LENGTH_SHORT)
    // // .show();
    // }
    // });
    // }

    /**
     * 弹出日期滚轮,年月日时分
     *
     * @param dialogTitle 对话框标题,默认为选取时间
     * @param tvStartDate 要显示时间日期的对话框，可以为空
     */
    public void showDateWheel(final TextView tvStartDate) {
        showDateWheel("选取时间", tvStartDate);
    }

    /**
     * 弹出日期滚轮,年月日时分
     *
     * @param dialogTitle 对话框标题,默认为选取时间
     * @param tvStartDate 要显示时间日期的对话框，可以为空
     */
    public void showDateWheel(String dialogTitle, final TextView tvStartDate) {
        showDateWheel(dialogTitle, tvStartDate, true);
    }

    /**
     * 弹出日期滚轮
     *
     * @param tvStartDate
     * @param isShowTime  是否显示时分
     */
    public void showDateWheel(final TextView tvStartDate,
                              final boolean isShowTime) {
        showDateWheel("选取时间", tvStartDate, isShowTime);
    }

    /***
     * 弹出日期滚轮,年月日时分
     *
     * @param dialogTitle
     *            对话框标题,默认为选取时间
     * @param tvStartDate
     *            要显示时间日期的对话框，可以为空
     * @param isSelectTime
     *            是否选择时间,时分秒
     */
    public void showDateWheel(String dialogTitle, final TextView tvStartDate,
                              final boolean isSelectTime) {
        dialogTitle = TextUtils.isEmpty(dialogTitle) ? "选取时间" : dialogTitle;
        final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        View view = View.inflate(mContext, R.layout.date_pick_wheel, null);
        final Dialog dialog = new Dialog(mContext, R.style.styleNoFrameDialog);
        final WheelView year = (WheelView) view.findViewById(R.id.year_wheel);
        final WheelView month = (WheelView) view.findViewById(R.id.month_wheel);
        final WheelView day = (WheelView) view.findViewById(R.id.day_wheel);
        year.setVisibility(View.VISIBLE);
        month.setVisibility(View.VISIBLE);
        final TextView tvTitle = (TextView) view
                .findViewById(R.id.tv_title_date_wheel);
        final Button btnCancle = (Button) view
                .findViewById(R.id.btn_cancel_date_wheel);
        final Button btnOk = (Button) view.findViewById(R.id.btn_ok_date_wheel);
        tvTitle.setText(dialogTitle);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);

        OnWheelChangedListener listener = new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
                updateDays(year, month, day);
            }
        };

        // month
        int curMonth = calendar.get(Calendar.MONTH);
        String months[] = new String[]{"1月", "2月", "3月", "4月", "5月", "6月",
                "7月", "8月", "9月", "10月", "11月", "12月"};
        DateArrayAdapter monthAdapter = new DateArrayAdapter(mContext, months,
                curMonth);
        monthAdapter.setItemResource(R.layout.wheel_text_item);
        monthAdapter.setItemTextResource(R.id.text);
        month.setViewAdapter(monthAdapter);
        month.setCurrentItem(curMonth);
        month.addChangingListener(listener);

        // year
        int curYear = calendar.get(Calendar.YEAR);
        int currentIndex = curYear - startYear;
        DateNumericAdapter yearAdapter = new DateNumericAdapter(mContext,
                startYear, curYear + 20, currentIndex);
        yearAdapter.setItemResource(R.layout.wheel_text_item);
        yearAdapter.setItemTextResource(R.id.text);
        year.setViewAdapter(yearAdapter);
        year.setCurrentItem(currentIndex);
        year.addChangingListener(listener);

        // day
        updateDays(year, month, day);
        day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);

        // 时间部分
        final WheelView hours = (WheelView) view.findViewById(R.id.hour_wheel);
        final WheelView mins = (WheelView) view.findViewById(R.id.mins_wheel);
        if (isSelectTime) {
            final NumericWheelAdapter hourAdapter = new NumericWheelAdapter(
                    mContext, 0, 23);
            hourAdapter.setItemResource(R.layout.wheel_text_item);
            hourAdapter.setItemTextResource(R.id.text);
            hours.setViewAdapter(hourAdapter);

            final NumericWheelAdapter minAdapter = new NumericWheelAdapter(
                    mContext, 0, 59, "%02d");
            minAdapter.setItemResource(R.layout.wheel_text_item);
            minAdapter.setItemTextResource(R.id.text);
            mins.setViewAdapter(minAdapter);
            mins.setCyclic(true);

            hourValue = calendar.get(Calendar.HOUR);
            minsValue = calendar.get(Calendar.MINUTE);
            hours.setCurrentItem(calendar.get(Calendar.HOUR));
            mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        } else {
            hours.setVisibility(View.GONE);
            mins.setVisibility(View.GONE);
        }

        // dialog.setView(view, 0, 0, 0, 0);
        dialog.setContentView(view);
        dialog.show();
        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("wheel_t",
                        "--" + year.getCurrentItem() + "-"
                                + month.getCurrentItem() + "-"
                                + month.getCurrentItem());
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.add(Calendar.DATE, dayOffsets);
                String dateStr = String.format("%02d",
                        (year.getCurrentItem() + startYear))
                        + "-"
                        + String.format("%02d", (month.getCurrentItem() + 1))
                        + "-"
                        + String.format("%02d", (day.getCurrentItem() + 1));
                dateformat.format(dateCalendar.getTime());

                if (isSelectTime) {
                    hourValue = hours.getCurrentItem();
                    minsValue = mins.getCurrentItem();
                }
                String time = String.format("%02d", hourValue) + ":"
                        + String.format("%02d", minsValue) + ":00";
                String result = dateStr + " " + time;
                if (tvStartDate != null) {
                    tvStartDate.setText(result);
                }
                if (mListener != null) {
                    mListener.onSelected(result);
                }
                dialog.dismiss();
            }
        });
    }

    /***
     * 弹出日期滚轮,年月日时分
     *
     * @param dialogTitle
     *            对话框标题,默认为选取时间
     * @param tvStartDate
     *            要显示时间日期的对话框，可以为空
     * @param isSelectTime
     *            是否选择时间,时分秒
     * @param isSelectDate
     *            是否选择日期 年月日，默认为true
     */
    public void showDateWheel(String dialogTitle, final TextView tvStartDate,
                              final boolean isSelectTime, final boolean isSelectDate) {
        dialogTitle = TextUtils.isEmpty(dialogTitle) ? "选取时间" : dialogTitle;

        final DateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
        View view = View.inflate(mContext, R.layout.date_pick_wheel, null);

        final TextView tvTitle = (TextView) view
                .findViewById(R.id.tv_title_date_wheel);
        final Button btnCancle = (Button) view
                .findViewById(R.id.btn_cancel_date_wheel);
        final Button btnOk = (Button) view.findViewById(R.id.btn_ok_date_wheel);
        tvTitle.setText(dialogTitle);
        Calendar calendar = Calendar.getInstance(Locale.CHINA);
        final Dialog dialog = new Dialog(mContext, R.style.styleNoFrameDialog);
        final WheelView year = (WheelView) view.findViewById(R.id.year_wheel);
        final WheelView month = (WheelView) view.findViewById(R.id.month_wheel);
        final WheelView day = (WheelView) view.findViewById(R.id.day_wheel);

        if (isSelectDate) {
            year.setVisibility(View.VISIBLE);
            month.setVisibility(View.VISIBLE);
            OnWheelChangedListener listener = new OnWheelChangedListener() {
                @Override
                public void onChanged(WheelView wheel, int oldValue,
                                      int newValue) {
                    updateDays(year, month, day);
                }
            };

            // month
            int curMonth = calendar.get(Calendar.MONTH);
            String months[] = new String[]{"1月", "2月", "3月", "4月", "5月",
                    "6月", "7月", "8月", "9月", "10月", "11月", "12月"};
            DateArrayAdapter monthAdapter = new DateArrayAdapter(mContext,
                    months, curMonth);
            monthAdapter.setItemResource(R.layout.wheel_text_item);
            monthAdapter.setItemTextResource(R.id.text);
            month.setViewAdapter(monthAdapter);
            month.setCurrentItem(curMonth);
            month.addChangingListener(listener);

            // year
            int curYear = calendar.get(Calendar.YEAR);
            int currentIndex = curYear - startYear;
            DateNumericAdapter yearAdapter = new DateNumericAdapter(mContext,
                    startYear, curYear + 20, currentIndex);
            yearAdapter.setItemResource(R.layout.wheel_text_item);
            yearAdapter.setItemTextResource(R.id.text);
            year.setViewAdapter(yearAdapter);
            year.setCurrentItem(currentIndex);
            year.addChangingListener(listener);
            // day
            updateDays(year, month, day);
            day.setCurrentItem(calendar.get(Calendar.DAY_OF_MONTH) - 1);
        } else {
            year.setVisibility(View.GONE);
            month.setVisibility(View.GONE);
            day.setVisibility(View.GONE);
        }

        // 时间部分
        final WheelView hours = (WheelView) view.findViewById(R.id.hour_wheel);
        final WheelView mins = (WheelView) view.findViewById(R.id.mins_wheel);
        if (isSelectTime || !isSelectDate) {
            final NumericWheelAdapter hourAdapter = new NumericWheelAdapter(
                    mContext, 0, 23);
            hourAdapter.setItemResource(R.layout.wheel_text_item);
            hourAdapter.setItemTextResource(R.id.text);
            hours.setViewAdapter(hourAdapter);

            final NumericWheelAdapter minAdapter = new NumericWheelAdapter(
                    mContext, 0, 59, "%02d");
            minAdapter.setItemResource(R.layout.wheel_text_item);
            minAdapter.setItemTextResource(R.id.text);
            mins.setViewAdapter(minAdapter);
            mins.setCyclic(true);

            hourValue = calendar.get(Calendar.HOUR);
            minsValue = calendar.get(Calendar.MINUTE);
            hours.setCurrentItem(calendar.get(Calendar.HOUR));
            mins.setCurrentItem(calendar.get(Calendar.MINUTE));
        } else {
            hours.setVisibility(View.GONE);
            mins.setVisibility(View.GONE);
        }

        // dialog.setView(view, 0, 0, 0, 0);
        dialog.setContentView(view);
        dialog.show();
        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnOk.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar dateCalendar = Calendar.getInstance();
                dateCalendar.add(Calendar.DATE, dayOffsets);
                String dateStr = "";
                if (isSelectDate) {
                    dateStr = String.format("%02d",
                            (year.getCurrentItem() + startYear))
                            + "-"
                            + String.format("%02d",
                            (month.getCurrentItem() + 1))
                            + "-"
                            + String.format("%02d", (day.getCurrentItem() + 1));
                } else {
                    dateStr = ViewHelper.getDateToday();
                }

                dateformat.format(dateCalendar.getTime());
                String time = "";
                if (isSelectTime) {
                    hourValue = hours.getCurrentItem();
                    minsValue = mins.getCurrentItem();
                    time = String.format("%02d", hourValue) + ":"
                            + String.format("%02d", minsValue) + ":00";
                }

                String result = dateStr + " " + time;
                if (tvStartDate != null) {
                    tvStartDate.setText(result);
                }
                if (mListener != null) {
                    mListener.onSelected(result);
                }
                dialog.dismiss();
            }
        });
    }

    private ISelected mListener;

    public void setOnSelectedListener(ISelected listener) {
        this.mListener = listener;
    }

    public interface ISelected {
        public abstract void onSelected(String date);
    }

    /**
     * Updates day wheel. Sets max days according to selected month and year
     */
    void updateDays(WheelView year, WheelView month, WheelView day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, startYear + year.getCurrentItem());
        calendar.set(Calendar.MONTH, month.getCurrentItem());
        LogUtils.i(
                "calendark",
                calendar.get(Calendar.YEAR) + "--"
                        + calendar.get(Calendar.MONTH));
        int maxDays = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
        DateNumericAdapter dayAdapter = new DateNumericAdapter(mContext, 1,
                maxDays, calendar.get(Calendar.DAY_OF_MONTH) - 1);
        dayAdapter.setItemResource(R.layout.wheel_text_item);
        dayAdapter.setItemTextResource(R.id.text);
        day.setViewAdapter(dayAdapter);
        int curDay = Math.min(maxDays, day.getCurrentItem() + 1);
        day.setCurrentItem(curDay - 1, true);
    }

    /**
     * Day adapter
     */
    private class DayArrayAdapter extends AbstractWheelTextAdapter {
        // 设置滑轮 滑动天数
        private final int daysCount = 30;
        int maxDays;
        // Calendar
        Calendar calendar;

        /**
         * Constructor
         */
        protected DayArrayAdapter(Context context, Calendar calendar) {
            super(context, R.layout.item_time_day, NO_RESOURCE);
            this.calendar = calendar;
            setItemTextResource(R.id.time2_monthday);
            maxDays = calendar.getMaximum(Calendar.DAY_OF_YEAR);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            // 获取day 总天数的一半; index=0,则day=15
            int day = -daysCount / 2 + index;
            Calendar newCalendar = (Calendar) calendar.clone();
            int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);
            if (dayOfYear + day >= maxDays) {
                newCalendar.roll(Calendar.YEAR, 1);
            } else if (dayOfYear + day < 0) {
                newCalendar.roll(Calendar.YEAR, -1);
            }
            newCalendar.roll(Calendar.DAY_OF_YEAR, day);

            newCalendar.roll(Calendar.DAY_OF_YEAR, day);

            View view = super.getItem(index, cachedView, parent);
            TextView weekday = (TextView) view.findViewById(R.id.time2_weekday);
            if (day == 0) {
                weekday.setText("");
            } else {
                DateFormat format = new SimpleDateFormat("EEE");
                weekday.setText(format.format(newCalendar.getTime()));
            }

            TextView monthday = (TextView) view
                    .findViewById(R.id.time2_monthday);
            if (day == 0) {
                monthday.setText("今日");
                monthday.setTextColor(0xFF0000F0);
            } else {
                DateFormat format = new SimpleDateFormat("MMM d");
                monthday.setText(format.format(newCalendar.getTime()));
                monthday.setTextColor(0xFF111111);
            }
            return view;
        }

        @Override
        public int getItemsCount() {
            return daysCount + 1;
        }

        @Override
        protected CharSequence getItemText(int index) {
            return "";
        }
    }

    /**
     * Adapter for numeric wheels. Highlights the current value.
     */
    private class DateNumericAdapter extends NumericWheelAdapter {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateNumericAdapter(Context context, int minValue, int maxValue,
                                  int current) {
            super(context, minValue, maxValue);
            this.currentValue = current;
            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }

    /**
     * Adapter for string based wheel. Highlights the current value.
     */
    private class DateArrayAdapter extends ArrayWheelAdapter<String> {
        // Index of current item
        int currentItem;
        // Index of item to be highlighted
        int currentValue;

        /**
         * Constructor
         */
        public DateArrayAdapter(Context context, String[] items, int current) {
            super(context, items);
            this.currentValue = current;
            setTextSize(16);
        }

        @Override
        protected void configureTextView(TextView view) {
            super.configureTextView(view);
            if (currentItem == currentValue) {
                view.setTextColor(0xFF0000F0);
            }
            view.setTypeface(Typeface.SANS_SERIF);
        }

        @Override
        public View getItem(int index, View cachedView, ViewGroup parent) {
            currentItem = index;
            return super.getItem(index, cachedView, parent);
        }
    }
}
