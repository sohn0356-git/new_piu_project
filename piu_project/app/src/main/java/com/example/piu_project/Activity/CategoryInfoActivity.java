package com.example.piu_project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
import com.example.piu_project.adapter.CategoryInfoAdapter;
import com.example.piu_project.adapter.CustomSpinnerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class CategoryInfoActivity extends BasicActivity {
    private static final String TAG = "CategoryInfoActivity";
    private FirebaseFirestore firebaseFirestore;
    private CategoryInfoAdapter categoryInfoAdapter;
    private ArrayList<SongInfo> categoryInfo;
    private boolean updating;
    private String category="All";
    private String version = "All";
    private Spinner spinner_version;
    private Spinner spinner_category;
    private String[] categoryList = new String[]{"All","K-pop", "Original", "World music", "J-music", "Xross", "Shortcut", "Remix", "Full song"};
    private String[] versionList = new String[]{"All","1st_to_perf", "Extra_to_prex3", "Exceed_to_zero", "Nx_to_nxa", "Fiesta_to_fiesta2", "Prime", "Prime2", "XX"};
    private boolean topScrolled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryinfo);
        setToolbarTitle("CATEGORY");

        final int numberOfColumns = 1;
        firebaseFirestore = FirebaseFirestore.getInstance();
        categoryInfo = new ArrayList<>();
        categoryInfoAdapter = new CategoryInfoAdapter(this, categoryInfo,getResources());
        spinner_category = (Spinner)findViewById(R.id.spinner_category);
        spinner_version = (Spinner)findViewById(R.id.spinner_version);
        int[] si_category = new int[]{R.drawable.ct_kp00, R.drawable.ct_or00, R.drawable.ct_wm00, R.drawable.ct_jm00, R.drawable.ct_xr00, R.drawable.ct_sc00, R.drawable.ct_re00, R.drawable.ct_fs00};
//        int[] si_category = new int[]{R.drawable.ct_nt00, R.drawable.ct_kp00};
        int[] si_version = new int[]{R.drawable.first_to_perf, R.drawable.extra_to_prex3, R.drawable.exceed_to_zero, R.drawable.nx_to_nxa, R.drawable.fiesta_to_fiesta2,
                                R.drawable.prime, R.drawable.prime2, R.drawable.xx};
        CustomSpinnerAdapter csa_category = new CustomSpinnerAdapter(CategoryInfoActivity.this, si_category);
        spinner_category.setAdapter(csa_category);
        // 스피너에서 아이템 선택시 호출하도록 합니다.


        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                category = categoryList[spinner_category.getSelectedItemPosition()];
                postsUpdate(true);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        CustomSpinnerAdapter csa_version = new CustomSpinnerAdapter(CategoryInfoActivity.this, si_version);
        spinner_version.setAdapter(csa_version);

        // 스피너에서 아이템 선택시 호출하도록 합니다.
        spinner_version.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                version = versionList[spinner_version.getSelectedItemPosition()];
                postsUpdate(false);
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(this, numberOfColumns));
        recyclerView.setAdapter(categoryInfoAdapter);


        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();

                if(newState == 1 && firstVisibleItemPosition == 0){
                    topScrolled = true;
                }
                if(newState == 0 && topScrolled){
                    postsUpdate(true);
                    topScrolled = false;
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy){
                super.onScrolled(recyclerView, dx, dy);

                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = ((LinearLayoutManager)layoutManager).findFirstVisibleItemPosition();
                int lastVisibleItemPosition = ((LinearLayoutManager)layoutManager).findLastVisibleItemPosition();

                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
                    postsUpdate(false);
                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });
        postsUpdate(false);
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    private void postsUpdate(final boolean flag) {
        updating = true;
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //firebase database의 data를 GET

                categoryInfo.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HashMap<String, String> h = (HashMap<String, String>) snapshot.getValue();
                    String h_category = h.get("category");
                    String h_version = h.get("version");
                    Log.d(TAG, version + ": " + h_version + " " + h.get("title"));
                    HashMap<String, HashMap<String, HashMap<String, String>>> h2 = (HashMap<String, HashMap<String, HashMap<String, String>>>) snapshot.getValue();
                    HashMap<String, HashMap<String, String>> youtubelink = h2.get("youtubeLink");
                    HashMap<String, HashMap<String, String>> stepmaker = h2.get("stepmaker");
                    if (version.equals("All")) {
                        if (category.equals("All")) {
                            HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                            String s = h3.get("song_id").toString();
                            categoryInfo.add(new SongInfo(
                                    s,
                                    h.get("artist"),
                                    h.get("title"),
                                    h.get("level"),
                                    h.get("bpm"),
                                    h.get("category"),
                                    h.get("version"),
                                    stepmaker,
                                    youtubelink));
                        } else if (h_category.equals(category)) {
                            HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                            String s = h3.get("song_id").toString();
                            categoryInfo.add(new SongInfo(
                                    s,
                                    h.get("artist"),
                                    h.get("title"),
                                    h.get("level"),
                                    h.get("bpm"),
                                    h.get("category"),
                                    h.get("version"),
                                    stepmaker,
                                    youtubelink));
                        }
                    } else if (version.equals("1st_to_perf")) {
                        if (h_version.equals("1st") || h_version.equals("2nd") || h_version.equals("OBG3") || h_version.equals("OBGS") || h_version.equals("Perf")) {
                            if (category.equals("All")) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                                Log.d(TAG, version + ": " + h_version + " " + h.get("title"));
                            } else if (h_category.equals(category)) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            }
                        }
                    } else if (version.equals("Extra_to_prex3")) {
                        if (h_version.equals("Extra") || h_version.equals("Rebirth") || h_version.equals("Premiere3") || h_version.equals("Prex3")) {
                            if (category.equals("All")) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            } else if (h_category.equals(category)) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            }
                        }
                    } else if (version.equals("Exceed_to_zero")) {
                        if (h_version.equals("Exceed") || h_version.equals("Exceed2") || h_version.equals("Zero")) {
                            if (category.equals("All")) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            } else if (h_category.equals(category)) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            }
                        }
                    } else if (version.equals("Nx_to_nxa")) {
                        if (h_version.equals("NX") || h_version.equals("NX2") || h_version.equals("NXA")) {
                            if (category.equals("All")) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            } else if (h_category.equals(category)) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            }
                            Log.d(TAG, version + ": " + h_version + " " + h.get("title"));
                        }
                    } else if (version.equals("Fiesta_to_fiesta2")) {
                        if (h_version.equals("Fiesta") || h_version.equals("FiestaEX") || h_version.equals("FIESTA2")) {
                            if (category.equals("All")) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            } else if (h_category.equals(category)) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            }
                            Log.d(TAG, version + ": " + h_version + " " + h.get("title"));
                        }
                    } else {
                        if (h_version.equals(version)) {
                            if (category.equals("All")) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            } else if (h_category.equals(category)) {
                                HashMap<String, Long> h3 = (HashMap<String, Long>) snapshot.getValue();
                                String s = h3.get("song_id").toString();
                                categoryInfo.add(new SongInfo(
                                        s,
                                        h.get("artist"),
                                        h.get("title"),
                                        h.get("level"),
                                        h.get("bpm"),
                                        h.get("category"),
                                        h.get("version"),
                                        stepmaker,
                                        youtubelink));
                            }
                            Log.d(TAG, version + ": " + h_version + " " + h.get("title"));
                        }
                    }
                }
                categoryInfoAdapter.notifyDataSetChanged();                         //리스트 저장 및 새로고침
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //error 발생시
                Log.e("MainActivity", String.valueOf(databaseError.toException()));
            }
        });

//        CollectionReference collectionReference = firebaseFirestore.collection("song_db");
//        collectionReference.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            if(clear){
//                                categoryInfo.clear();
//                            }
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                categoryInfo.add(new SongInfo(
//                                        document.getData().get("album") == null ? null : document.getData().get("album").toString(),
//                                        document.getData().get("artist").toString(),
//                                        document.getData().get("bpm").toString(),
//                                        document.getData().get("level").toString(),
//                                        document.getData().get("title").toString()));
//                            }
//                            categoryInfoAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                        updating = false;
//                    }
//                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(this, c);
        startActivityForResult(intent, 0);
    }
}