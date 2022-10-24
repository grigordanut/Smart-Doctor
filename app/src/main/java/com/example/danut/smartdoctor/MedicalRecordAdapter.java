package com.example.danut.smartdoctor;

import android.content.Context;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.List;

import static android.icu.text.DateFormat.NONE;
import static com.example.danut.smartdoctor.R.*;

public class MedicalRecordAdapter extends RecyclerView.Adapter<MedicalRecordAdapter.ImageViewHolder> {

    //declare variables
    private Context medRecContext;
    private List<MedicalRecord> medRecList;
    private OnItemClickListener clickListener;

    public MedicalRecordAdapter(Context recContext, List<MedicalRecord>medRecUpload){
        medRecContext = recContext;
        medRecList = medRecUpload;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(medRecContext).inflate(layout.med_record_info, parent, false);
        return new ImageViewHolder(v);
    }

    //set the item layout view
    @Override
    public void onBindViewHolder(ImageViewHolder holder, int position) {
        MedicalRecord uploadCurrent = medRecList.get(position);
        holder.textViewPatGender.setText("Patient Gender: "+uploadCurrent.getMedRecord_Gender());
        holder.textViewPatDateBirth.setText("Date Birth: "+uploadCurrent.getMedRecord_DateBirth());
        holder.textViewPPSNo.setText("PPSNo: "+uploadCurrent.getMedRecord_PPS());
        holder.textViewAddress.setText("Address: "+uploadCurrent.getMedRecord_Address());
        holder.textViewPatName.setText("Patient Name: "+uploadCurrent.getRecordPat_ID());
    }

    //assign the values of textViews
    @Override
    public int getItemCount() {
        return medRecList.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener, MenuItem.OnMenuItemClickListener{

        public TextView textViewPatGender;
        public TextView textViewPatDateBirth;
        public TextView textViewPPSNo;
        public TextView textViewAddress;
        public TextView textViewPatName;


        public ImageViewHolder(View itemView) {
            super(itemView);
            textViewPatGender = itemView.findViewById(R.id.tvPatGender);
            textViewPatDateBirth = itemView.findViewById(id.tvPatDateBirth);
            textViewPPSNo = itemView.findViewById(R.id.tvPatPPSNo);
            textViewAddress = itemView.findViewById(id.tvPatAddress);
            textViewPatName = itemView.findViewById(R.id.tvPatName);

            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }

        //create onItem click menu
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            menu.setHeaderTitle("Select an Action");
            MenuItem doUpdate  = menu.add(NONE, 1, 1, "Update");
            MenuItem doDelete  = menu.add(NONE, 2, 2, "Delete");

            doUpdate.setOnMenuItemClickListener(this);
            doDelete.setOnMenuItemClickListener(this);
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(clickListener !=null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    switch (item.getItemId()){
                        case 1:
                            clickListener.onUpdateClick(position);
                            return true;

                        case 2:
                            clickListener.onDeleteClick(position);
                            return true;
                    }
                }
            }

            return false;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        void onUpdateClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
