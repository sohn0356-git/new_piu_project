package com.example.piu_project.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
//import com.example.piu_project.PostInfo;
import com.example.piu_project.R;
import com.example.piu_project.SongInfo;
//import com.example.piu_project.activity.WritePostActivity;
import com.example.piu_project.adapter.LevelInfoAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class LevelInfoFragment extends Fragment {
    private static final String TAG = "LevelInfoFragment";
    private FirebaseFirestore firebaseFirestore;
    private LevelInfoAdapter levelInfoAdapter;
    private ArrayList<SongInfo> levelInfo;
    private boolean updating;
    private int setLevel;
    private boolean topScrolled;

    public LevelInfoFragment() {
        // Required empty public constructor
    }

    public void SetLevel(int setLevel) {
        this.setLevel = setLevel;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final int numberOfColumns = 3;
        View view = inflater.inflate(R.layout.activity_levelinfo, container, false);
        String a="";
        String b = "";
        RelativeLayout settingBackgroundLayout = getActivity().findViewById(R.id.settingBackgroundLayout);
        firebaseFirestore = FirebaseFirestore.getInstance();
        levelInfo = new ArrayList<>();
        levelInfoAdapter = new LevelInfoAdapter(getActivity(), levelInfo,a,b,getResources(),settingBackgroundLayout);

        final RecyclerView recyclerView = view.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), numberOfColumns));
        recyclerView.setAdapter(levelInfoAdapter);

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

//                if(totalItemCount - 3 <= lastVisibleItemPosition && !updating){
//                    postsUpdate(false);
//                }

                if(0 < firstVisibleItemPosition){
                    topScrolled = false;
                }
            }
        });

        postsUpdate(false);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                /*
                case R.id.logoutButton:
                    FirebaseAuth.getInstance().signOut();
                    myStartActivity(SignUpActivity.class);
                    break;
                */
            }
        }
    };

    private void postsUpdate(final boolean clear) {
        updating = true;
        //Date date = userList.size() == 0 || clear ? new Date() : userList.get(userList.size() - 1).getCreatedAt();
        FirebaseDatabase database = FirebaseDatabase.getInstance();                      //Firebase database와 연동;
        DatabaseReference databaseReference_s = database.getReference("db_song");     //DB 테이블 연결
        databaseReference_s.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //firebase database의 data를 GET
                levelInfo.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()) {
                    boolean isHere = false;
                    HashMap<String,String> h = (HashMap<String, String>)snapshot.getValue();
                    String[] level_s = h.get("level").split(String.valueOf(','));
                    int[] num_s = new int[level_s.length];
                    for(int i=0;i<level_s.length; i++){
                        num_s[i] = Integer.parseInt(level_s[i]);
                        if(num_s[i]==setLevel){
                            isHere = true;
                        }
                    }
                    if(isHere) {
                        HashMap<String,HashMap<String, HashMap<String,String>>> h2 = (HashMap<String,HashMap<String, HashMap<String,String>>>)snapshot.getValue();
                        HashMap<String,HashMap<String,String>> youtubelink = h2.get("youtubeLink");
                        HashMap<String,HashMap<String,String>> stepmaker = h2.get("stepmaker");
//                        levelInfo.add(new SongInfo(
//                                h.get("album"),
//                                h.get("artist"),
//                                h.get("bpm"),
//                                h.get("level"),
//                                h.get("title"),
//                                h.get("category"),
//                                youtubelink,
//                                stepmaker));
                    }
                }
                levelInfoAdapter.notifyDataSetChanged();                         //리스트 저장 및 새로고침
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
//                                levelInfo.clear();
//                            }
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//                                levelInfo.add(new SongInfo(
//                                        document.getData().get("album") == null ? null : document.getData().get("album").toString(),
//                                        document.getData().get("artist").toString(),
//                                        document.getData().get("bpm").toString(),
//                                        document.getData().get("level").toString(),
//                                        document.getData().get("title").toString()));
//                            }
//                            levelInfoAdapter.notifyDataSetChanged();
//                        } else {
//                            Log.d(TAG, "Error getting documents: ", task.getException());
//                        }
//                        updating = false;
//                    }
//                });
    }

    private void myStartActivity(Class c) {
        Intent intent = new Intent(getActivity(), c);
        startActivityForResult(intent, 0);
    }
}
