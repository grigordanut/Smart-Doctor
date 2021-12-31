package com.example.smartdoctor;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class HospitalAdapterAddDoctor extends RecyclerView.Adapter<HospitalAdapterAddDoctor.ImageViewHolder> {

    private final Context hospitalContext;
    private final List<Hospital> hospitalUploads;

    public HospitalAdapterAddDoctor(Context hospital_context, List<Hospital> hospital_uploads){
        hospitalContext = hospital_context;
        hospitalUploads = hospital_uploads;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(hospitalContext).inflate(R.layout.image_hospital,parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ImageViewHolder holder, int position) {

        final Hospital uploadCurrent = hospitalUploads.get(position);
        holder.tVShowHospName.setText(uploadCurrent.getHosp_Name());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(hospitalContext, DoctorRegistration.class);
                intent.putExtra("HOSPName",uploadCurrent.getHosp_Name());
                intent.putExtra("HOSPKey",uploadCurrent.getHosp_Key());
                hospitalContext.startActivity(intent);
                hospitalUploads.clear();
            }
        });
    }

    @Override
    public int getItemCount() {
        return hospitalUploads.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public TextView tVShowHospName;

        public ImageViewHolder(View itemView) {
            super(itemView);
            tVShowHospName = itemView.findViewById(R.id.tvShowHospName);
        }
    }
}
