package com.example.gmiapps;

import android.os.Bundle;
import android.view.animation.AnimationUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.gmiapps.adapter.ProgrammeAdapter;
import com.example.gmiapps.data.ProgrammeRepository;
import com.example.gmiapps.util.AnimUtils;

/** Lists GAPP, the 5 diploma groups and GUFP. Tapping one opens its details. */
public class ProgrammesActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        enableEdgeToEdgeDisplay();
        setContentView(R.layout.activity_programmes);
        setupScreen(R.string.programmes_title);

        RecyclerView list = findViewById(R.id.programme_list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setAdapter(new ProgrammeAdapter(ProgrammeRepository.getAll(), programme ->
                startActivity(ProgrammeDetailActivity.newIntent(this, programme.getId()))));

        if (savedInstanceState == null && AnimUtils.enabled()) {
            // XML layout animation: the cards slide up one after another (res/anim/layout_slide_up.xml)
            list.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_slide_up));
        }
    }
}
