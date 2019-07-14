package myapplication.callthelist.Adapter;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import myapplication.callthelist.EventBusModel.EventBusModels;
import myapplication.callthelist.Model.Person;
import myapplication.callthelist.R;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<Person> personList;
    private LayoutInflater layoutInflater;
    private Context context;
    private ContentResolver resolver;


    public MyAdapter(Context context, ArrayList<Person> list) {
        this.context = context;
        this.personList = list;
        this.layoutInflater = LayoutInflater.from(context);
        this.resolver = context.getContentResolver();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = layoutInflater.inflate(R.layout.row_item, viewGroup, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {
        Person person = personList.get(i);
        myViewHolder.setData(person,i);
    }

    @Override
    public int getItemCount() {
        return personList.size();
    }

    public void update(ArrayList<Person> list) {
        this.personList = list;
        notifyDataSetChanged();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private TextView mName;
        private TextView mPhoneNumber;
        private ImageView mDeleteImage;
        private int clickedPosition;
        private Person person;

        public MyViewHolder(View itemView) {
            super(itemView);

            mName = itemView.findViewById(R.id.txt_name);
            mPhoneNumber = itemView.findViewById(R.id.txt_phone_number);
            mDeleteImage = itemView.findViewById(R.id.delete_imageView);

            mDeleteImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventBus.getDefault().post(new EventBusModels.DeleteSelectedItem(person.getId()));
                }
            });
        }

        @SuppressLint("SetTextI18n")
        private void setData(Person person, int position){

            mName.setText(person.getName());
            mPhoneNumber.setText("+90"+person.getPhoneNumber());
            clickedPosition = position;
            this.person = person;
        }


    }
}
