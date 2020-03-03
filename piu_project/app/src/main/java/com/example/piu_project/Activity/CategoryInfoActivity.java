package com.example.piu_project.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
import com.example.piu_project.adapter.CategoryInfoAdapter;
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
    private String category;
    private boolean topScrolled;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categoryinfo);
        setToolbarTitle("CATEGORY");

        Intent intent = getIntent();
        category = intent.getStringExtra("setCategory");


        final int numberOfColumns = 1;
        firebaseFirestore = FirebaseFirestore.getInstance();
        categoryInfo = new ArrayList<>();
        categoryInfoAdapter = new CategoryInfoAdapter(this, categoryInfo);

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

    private void postsUpdate(final boolean clear) {
        updating = true;
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //firebase database의 data를 GET
                categoryInfo.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    HashMap<String,String> h = (HashMap<String, String>)snapshot.getValue();
                    String h_category = h.get("category");
                    if(h_category.equals(category)) {
                        categoryInfo.add(new SongInfo(
                                h.get("album"),
                                h.get("artist"),
                                h.get("bpm"),
                                h.get("level"),
                                h.get("title"),
                                h.get("category")));
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