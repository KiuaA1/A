package com.kdt.mcgui;

import android.content.*;
import android.util.*;
import androidx.core.content.res.ResourcesCompat;
import git.artdeell.mojo.R;

public class MineEditText extends androidx.appcompat.widget.AppCompatEditText {
    public MineEditText(Context ctx) { super(ctx); init(); }
    public MineEditText(Context ctx, AttributeSet attrs) { super(ctx, attrs); init(); }
    public void init() {
        setBackground(ResourcesCompat.getDrawable(getResources(), R.drawable.a_input, getContext().getTheme()));
        setTextColor(getResources().getColor(R.color.primary_text));
        setHintTextColor(getResources().getColor(R.color.secondary_text));
        setPadding(getResources().getDimensionPixelSize(R.dimen._12sdp), getResources().getDimensionPixelSize(R.dimen.padding_input_top), getResources().getDimensionPixelSize(R.dimen._12sdp), getResources().getDimensionPixelSize(R.dimen.padding_input_bottom));
    }
}
