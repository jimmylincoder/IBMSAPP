package com.suntek.ibmsapp.page.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.suntek.ibmsapp.R;
import com.suntek.ibmsapp.component.base.BaseFragment;
import com.suntek.ibmsapp.widget.GestureImageView.GestureImageView;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import butterknife.BindView;

/**
 * 相册滑动
 *
 * @author jimmy
 */
public class PhotoPagerFragment extends BaseFragment
{
    private String path;

    @BindView(R.id.giv_detail)
    GestureImageView givPhoto;

    public static PhotoPagerFragment newInstance(String path)
    {
        PhotoPagerFragment photoPagerFragment = new PhotoPagerFragment();

        Bundle args = new Bundle();
        args.putString("path", path);
        photoPagerFragment.setArguments(args);

        return photoPagerFragment;
    }

    @Override
    public int getLayoutId()
    {
        return R.layout.fragment_photo_pager;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        path = getArguments() != null ? getArguments().getString("path") : null;
    }

    @Override
    public void initViews(Bundle savedInstanceState)
    {
        Bitmap bitmap = getBitmapByPath(path);
        givPhoto.setImageBitmap(bitmap);
    }

    private Bitmap getBitmapByPath(String path)
    {
        FileInputStream fis = null;
        try
        {
            fis = new FileInputStream(path);
        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        Bitmap bitmap = BitmapFactory.decodeStream(fis);
        // ImageLoader.getInstance().displayImage("file://" + photoPath, givPhoto);
        return bitmap;
    }
}
