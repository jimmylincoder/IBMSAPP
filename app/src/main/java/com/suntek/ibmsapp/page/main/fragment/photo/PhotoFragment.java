package com.suntek.ibmsapp.page.main.fragment.photo;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.ListView;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.PhotoListAdapter;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.model.Photo;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 *  相册界面
 *
 * @author jimmy
 */
public class PhotoFragment extends BaseFragment
{
    //图片路径
    private final String picPath = "/sdcard/DCIM/Camera";

    @BindView(R.id.elv_photo)
    ExpandableListView lvPhoto;

    private PhotoListAdapter photoListAdapter;

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_page_photo;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        File file = new File(picPath);
        try
        {
            File[] files = file.getCanonicalFile().listFiles();
            List<Photo> photos = new ArrayList<>();
            Map<String,List<String>> mapTag = new HashMap<>();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            for(File file1 : files)
            {
                String date = format.format(new Date(file.lastModified()));
                List<String> paths = mapTag.get(date);
                if(paths == null)
                {
                    paths = new ArrayList<>();
                    paths.add(file1.getAbsolutePath());
                }
                else
                {
                    paths.add(file1.getAbsolutePath());
                }
                mapTag.put(date,paths);
            }


            Iterator entries = mapTag.entrySet().iterator();

            while (entries.hasNext()) {

                Map.Entry entry = (Map.Entry) entries.next();

                String key = (String) entry.getKey();

                List<String> value = (List<String>)entry.getValue();

                Photo photo = new Photo();
                photo.setDate(key);
                photo.setPhotoPaths(value);
                photos.add(photo);
            }

            photoListAdapter = new PhotoListAdapter(getActivity(),photos);
            lvPhoto.setAdapter(photoListAdapter);
            for(int i = 0;i < photoListAdapter.getGroupCount();i++)
            {
                lvPhoto.expandGroup(i);
            }

            lvPhoto.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
            {
                @Override
                public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id)
                {
                    return true;
                }
            });


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
