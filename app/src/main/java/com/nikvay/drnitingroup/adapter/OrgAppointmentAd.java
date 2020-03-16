package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.StudentData;
import com.nikvay.drnitingroup.utilities.UserSession;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class OrgAppointmentAd extends RecyclerView.Adapter<OrgAppointmentAd.SingleItemRowHolder> {

    public static String menuId = "";
    String className = "";
    ArrayList<StudentData> itemsList;
    UserSession userSession;
    private Context mContext;
    private int TAB_INDEX = 0;

    public OrgAppointmentAd(Context context, ArrayList<StudentData> itemsList, UserSession userSession, int TAB_INDEX) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.userSession = userSession;
        this.TAB_INDEX = TAB_INDEX;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        StudentData testData = itemsList.get(i);

        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.attendance_item_view, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);

        return mh;

    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final StudentData testData = itemsList.get(i);
        holder.testTitle.setText("Hello");

        Date date = null;
        try {
            date = new SimpleDateFormat("yyyy-MM-dd").parse("2020-02-19");
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM");
            String month = formatter.format(date);
            holder.monthText.setText(month.toUpperCase());
            formatter = new SimpleDateFormat("dd");
            holder.dateText.setText(formatter.format(date));
            holder.tvTitle.setText("Booking Id: ");
            holder.testDate.setText("DOB: ");
            holder.testExpiryTest.setText("Appointment Time: ");

        } catch (ParseException e) {
            e.printStackTrace();
        }

        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView monthText, dateText, testTitle, testDate, testExpiryTest;
        protected View hrView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.monthText = view.findViewById(R.id.monthText);
            this.dateText = view.findViewById(R.id.dateText);

            this.testTitle = view.findViewById(R.id.testTitle);
            this.testDate = view.findViewById(R.id.testDate);
            this.testExpiryTest = view.findViewById(R.id.testExpiry);
        }

    }

}