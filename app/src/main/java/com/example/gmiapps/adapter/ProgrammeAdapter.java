package com.example.gmiapps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmiapps.R;
import com.example.gmiapps.data.Programme;

import java.util.List;

/** RecyclerView adapter for the programme cards on the Programmes screen. */
public class ProgrammeAdapter extends RecyclerView.Adapter<ProgrammeAdapter.ProgrammeViewHolder> {

    /** Called when the user taps a programme card. */
    public interface OnProgrammeClickListener {
        void onProgrammeClick(Programme programme);
    }

    private final List<Programme> programmes;
    private final OnProgrammeClickListener listener;

    public ProgrammeAdapter(List<Programme> programmes, OnProgrammeClickListener listener) {
        this.programmes = programmes;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProgrammeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_programme, parent, false);
        return new ProgrammeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProgrammeViewHolder holder, int position) {
        holder.bind(programmes.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return programmes.size();
    }

    static class ProgrammeViewHolder extends RecyclerView.ViewHolder {
        private final ImageView icon;
        private final TextView shortName;
        private final TextView name;
        private final TextView summary;
        private final TextView intake;
        private final TextView requirements;
        private final View detailsButton;

        ProgrammeViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.programme_icon);
            shortName = itemView.findViewById(R.id.programme_short);
            name = itemView.findViewById(R.id.programme_name);
            summary = itemView.findViewById(R.id.programme_summary);
            intake = itemView.findViewById(R.id.programme_intake);
            requirements = itemView.findViewById(R.id.programme_requirements);
            detailsButton = itemView.findViewById(R.id.button_details);
        }

        void bind(Programme programme, OnProgrammeClickListener listener) {
            Context context = itemView.getContext();
            icon.setImageResource(programme.getIconRes());
            shortName.setText(programme.getShortNameRes());
            name.setText(programme.getNameRes());
            summary.setText(programme.getSummaryRes());
            intake.setText(context.getString(R.string.label_intake_value,
                    context.getString(programme.getIntakeRes())));
            requirements.setText(programme.getRequirementSummaryRes());

            View.OnClickListener click = v -> listener.onProgrammeClick(programme);
            itemView.setOnClickListener(click);
            detailsButton.setOnClickListener(click);
        }
    }
}
