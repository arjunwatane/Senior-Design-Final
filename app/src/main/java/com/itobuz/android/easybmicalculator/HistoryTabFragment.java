package com.itobuz.android.easybmicalculator;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryTabFragment extends Fragment {
    private DatabaseHelper dbHelper;
    private net.sqlcipher.database.SQLiteDatabase sqLiteDatabase;
    private List<DatabaseHelper.QuerySet> mBmiList;
    private ListDataAdapter mListDataAdapter;
    ListView listView;
    Cursor cursor;

    private int list_id;
    private String list_date;
    private String list_age;
    private Float list_result;


    public static final String ARG_PAGE = "ARG_PAGE";
    private int mPageNo;

    public static HistoryTabFragment newInstance(int pageNo) {

        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, pageNo);
        HistoryTabFragment historyFragment = new HistoryTabFragment();
        historyFragment.setArguments(args);
        return historyFragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNo = getArguments().getInt(ARG_PAGE);
    }

    //todo this method actually wants gluc info
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.fragment_history, container, false);

        listView = (ListView) mview.findViewById(R.id.lv_bmi);
        mBmiList = new ArrayList<>();
        dbHelper = new DatabaseHelper(getContext());
        //Cursor cursor = dbHelper.getBmiInformation();
//        ArrayList<DatabaseHelper.QuerySet> cursor = dbHelper.search(Hold.getName(),Integer.parseInt(Hold.getPass()));
        ArrayList<DatabaseHelper.QuerySet> cursor = dbHelper.search(Hold.getName(), Hold.getId());

        //todo: do not have to iterate through the generated list
/*        if (cursor.moveToFirst()){
            do{

                list_id = Integer.parseInt(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_B)));
                list_date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                list_age = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
                list_result = Float.parseFloat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESULT)));
                DataProvider dataProvider = new DataProvider(list_id,list_date,list_age,list_result);
                mBmiList.add(dataProvider);
            }while (cursor.moveToNext());
        }
*/

//        mListDataAdapter = new ListDataAdapter(getContext(),R.layout.row_history,mBmiList);
        mListDataAdapter = new ListDataAdapter(getContext(),R.layout.row_history,mBmiList);
        listView.setAdapter(mListDataAdapter);

        return mview;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        //updatePersonList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser){
            updatePersonList();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        mListDataAdapter.notifyDataSetChanged();
        updatePersonList();
    }

    //todo change dataprovider fields
    public void updatePersonList(){
        mBmiList.clear();
        dbHelper = new DatabaseHelper(getContext());
        //cursor = dbHelper.getBmiInformation();
//        ArrayList<DatabaseHelper.QuerySet> cursor = dbHelper.search(Hold.getName(),Integer.parseInt(Hold.getPass()));
        ArrayList<DatabaseHelper.QuerySet> cursor = dbHelper.search(Hold.getName(), Hold.getId());

/*        if (cursor.moveToFirst()){
            do{
                list_id = cursor.getInt(cursor.getColumnIndex(DatabaseHelper.COLUMN_ID_B));
                list_date = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_DATE));
                list_age = cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_AGE));
                list_result = Float.parseFloat(cursor.getString(cursor.getColumnIndex(DatabaseHelper.COLUMN_RESULT)));

                DataProvider dataProvider = new DataProvider(list_id,list_date,list_age,list_result);
                mBmiList.add(dataProvider);
            }while (cursor.moveToNext());
        }

*/
        //add each queryset of gluc history to the
 //       for(int i=0; i<cursor.size(); i++) mBmiList
        for(DatabaseHelper.QuerySet q : cursor) mBmiList.add(q);
        mListDataAdapter.notifyDataSetChanged();
    }
}