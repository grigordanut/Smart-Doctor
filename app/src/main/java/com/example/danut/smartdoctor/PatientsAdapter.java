package com.example.danut.smartdoctor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.ImageViewHolder> {

    private final Context patientsContext;
    private final List<Patients> patientsUploads;

    private OnItemClickListener clickListener;

    public PatientsAdapter(Context patients_context, List<Patients> patients_uploads){
        patientsContext = patients_context;
        patientsUploads = patients_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(patientsContext).inflate(R.layout.image_patient_new,parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Patients uploadCurrent = patientsUploads.get(position);

        holder.tVShowPatFirstName.setText(uploadCurrent.getPatient_FirstName());
        holder.tVShowPatLastName.setText(uploadCurrent.getPatient_LastName());
        holder.tVShowPatUniqueCode.setText(uploadCurrent.getPatient_UniqueCode());
    }

    @Override
    public int getItemCount() {
        return patientsUploads.size();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tVShowPatFirstName;
        public TextView tVShowPatLastName;
        public TextView tVShowPatUniqueCode;

        public ImageViewHolder(View itemView) {
            super(itemView);

            tVShowPatFirstName = itemView.findViewById(R.id.tvShowPatFirstName);
            tVShowPatLastName = itemView.findViewById(R.id.tvShowPatLastName);
            tVShowPatUniqueCode = itemView.findViewById(R.id.tvShowPatUniqueCode);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if(clickListener != null){
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION){
                    clickListener.onItemClick(position);
                }
            }
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItmClickListener(OnItemClickListener listener){
        clickListener = listener;
    }
}
