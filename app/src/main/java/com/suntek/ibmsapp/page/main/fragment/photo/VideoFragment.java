package com.suntek.ibmsapp.page.main.fragment.photo;

import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.adapter.PhotoListAdapter;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.model.Photo;
import com.suntek.ibmsapp.util.DateUtil;
import com.suntek.ibmsapp.util.PermissionRequest;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 视频界面
 *
 * @author jimmy
 */
public class VideoFragment extends BaseFragment
{
    //相册路径
    private final String picPath = "/sdcard/DCIM/Camera/ibms";

    @BindView(R.id.elv_photo)
    ExpandableListView lvPhoto;

    @BindView(R.id.ll_message)
    LinearLayout llMessage;

    private PhotoListAdapter photoListAdapter;

    private List<Photo> photos = new ArrayList<>();

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_page_video;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {

    }

    /**
     * 初始化列表数据
     */
    private void initListView()
    {
        if (photos.isEmpty())
            llMessage.setVisibility(View.VISIBLE);
        else
            llMessage.setVisibility(View.GONE);
        Collections.sort(photos, new Comparator<Photo>()
        {
            @Override
            public int compare(Photo o1, Photo o2)
            {
                long date1 = DateUtil.convertToLong(o1.getDate(), "yyyyMMdd");
                long date2 = DateUtil.convertToLong(o2.getDate(), "yyyyMMdd");
                if (date1 < date2)
                    return 1;
                else if (date1 == date2)
                    return 0;
                else
                    return -1;
            }
        });
        photoListAdapter = new PhotoListAdapter(getActivity(), photos);
        lvPhoto.setAdapter(photoListAdapter);
        for (int i = 0; i < photoListAdapter.getGroupCount(); i++)
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
    }

    /**
     * 初始化相册数据
     */
    private void initData()
    {
        PermissionRequest.verifyStoragePermissions(getActivity());
        photos.clear();
        File file = new File(picPath);
        if (!file.exists())
            file.mkdir();
        try
        {
            File[] files = file.getCanonicalFile().listFiles();
            Map<String, List<String>> mapTag = new HashMap<>();
            if (files == null)
                return;
            for (File file1 : files)
            {
                if (file1.getName().startsWith("VID_"))
                {
                    String[] strs = file1.getName().split("_");
                    String date = strs[1];
                    List<String> paths = mapTag.get(date);
                    if (paths == null)
                    {
                        paths = new ArrayList<>();
                        paths.add(file1.getAbsolutePath());
                    }
                    else
                    {
                        paths.add(file1.getAbsolutePath());
                    }
                    mapTag.put(date, paths);
                }
            }


            Iterator entries = mapTag.entrySet().iterator();

            while (entries.hasNext())
            {

                Map.Entry entry = (Map.Entry) entries.next();

                String key = (String) entry.getKey();

                List<String> value = (List<String>) entry.getValue();

                Photo photo = new Photo();
                photo.setDate(key);
                photo.setPhotoPaths(value);
                photos.add(photo);
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void setEdit(boolean isEdit)
    {
        photoListAdapter.setEdit(isEdit);
    }

    public boolean isEdit()
    {
        return photoListAdapter.isEdit();
    }

    public void clearChoose()
    {
        photoListAdapter.clearChoose();
        photoListAdapter.notifyDataSetChanged();
    }

    public List<String> getSelectedPath()
    {
        return photoListAdapter.getSelectedPath();
    }

    public void update()
    {
        initData();
        initListView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        update();
    }

    public void selecteAll()
    {
        photoListAdapter.selectAll();
    }
}
