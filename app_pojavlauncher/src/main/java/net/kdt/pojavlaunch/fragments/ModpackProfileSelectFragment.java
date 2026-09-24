package net.kdt.pojavlaunch.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import git.artdeell.mojo.R;
import net.kdt.pojavlaunch.Tools;

public class ModpackProfileSelectFragment extends Fragment {
    public static final String TAG = "ModpackProfileSelectFragment";

    private final ActivityResultLauncher<String> mImportLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri == null) return;
                SearchModFragment fragment = new SearchModFragment();
                getParentFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment, SearchModFragment.TAG)
                        .addToBackStack(TAG)
                        .commit();
                fragment.getViewLifecycleOwnerLiveData().observe(this, owner -> {
                    if (owner != null) fragment.performLocalInstall(uri, requireContext(), requireContext().getContentResolver());
                });
            });

    public ModpackProfileSelectFragment() {
        super(R.layout.fragment_modpack_profile);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.modpack_profile_back).setOnClickListener(v -> getParentFragmentManager().popBackStack());
        view.findViewById(R.id.modpack_profile_browse).setOnClickListener(v ->
                Tools.swapFragment(requireActivity(), SearchModFragment.class, SearchModFragment.TAG, null));
        view.findViewById(R.id.modpack_profile_import).setOnClickListener(v -> mImportLauncher.launch("*/*"));
    }
}