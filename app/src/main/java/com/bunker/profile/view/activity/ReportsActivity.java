package com.bunker.profile.view.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bunker.profile.R;
import com.bunker.profile.utils.Person;
import com.bunker.profile.utils.PersonRepository;
import com.bunker.profile.utils.db.DBController;

import java.util.List;

public class ReportsActivity extends AppCompatActivity {

    private static final String TAG = "REPORTS_ACTIVITY";

    private RecyclerView mPersonRecyclerView;
    private PersonAdapter mPersonAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reports);

        mPersonRecyclerView = (RecyclerView) findViewById(R.id.recycler_user_list);
        mPersonRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        updateUI();
    }

    public void updateUI() {
        List<Person> persons = PersonRepository.getInstance().getAll();

        mPersonAdapter = new PersonAdapter(persons);
        mPersonRecyclerView.setAdapter(mPersonAdapter);
    }

    private class PersonHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView mTextName;
        private TextView mTextDesc;

        private Person mPerson;

        public PersonHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_person, parent, false));
            itemView.setOnClickListener(this);

            mTextName = (TextView) findViewById(R.id.text_name);
            mTextDesc = (TextView) findViewById(R.id.text_desc);


        }

        public void bind(Person person) {

        }

        @Override
        public void onClick(View v) {

        }
    }

    private class PersonAdapter extends RecyclerView.Adapter<PersonHolder> {

        private List<Person> mPersons;

        private PersonAdapter(List<Person> persons) {
            mPersons = persons;
        }

        @Override
        public PersonHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());

            return new PersonHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(PersonHolder holder, int position) {
            Person person = mPersons.get(position);
            holder.bind(person);
        }

        @Override
        public int getItemCount() {
            return mPersons.size();
        }
    }
}
