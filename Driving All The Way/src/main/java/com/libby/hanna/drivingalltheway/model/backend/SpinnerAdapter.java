/*
Hanna Weissberg 318796398
Libby Olidort 209274612
*/
package com.libby.hanna.drivingalltheway.model.backend;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.libby.hanna.drivingalltheway.R;
import java.util.ArrayList;

/**
 * Spinner adapter with colored items
 */
public class SpinnerAdapter extends BaseAdapter {

        ArrayList<Integer> colors;
        ArrayList<String> text;
        Context context;

        public SpinnerAdapter(Context context)
        {
            this.context=context;
            colors=new ArrayList<Integer>();
            text=new ArrayList<String>();
            String list[]=context.getResources().getStringArray(R.array.StatusSpinner);
            int retrieve []=context.getResources().getIntArray(R.array.androidcolors);
            for(int re:retrieve)
            {
                colors.add(re);

            }
            for(String t:list)
            {
                text.add(t);

            }
        }
        @Override
        public int getCount()
        {
            return colors.size();
        }
        @Override
        public Object getItem(int arg0)
        {
            return text.get(arg0);
        }
        @Override
        public long getItemId(int arg0)
        {
            return arg0;
        }

    /**
     * defines the actual look
     */
        @Override
        public View getView(int pos, View view, ViewGroup parent)
        {
            LayoutInflater inflater=LayoutInflater.from(context);
            view=inflater.inflate(android.R.layout.simple_spinner_dropdown_item, parent,false);
            TextView txv=(TextView)view.findViewById(android.R.id.text1);
            txv.setBackgroundColor(colors.get(pos));
            txv.setTextSize(20f);
            txv.setText(text.get(pos));
            return view;
        }


}
