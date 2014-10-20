package com.example.AndroidMVC;

import ImagePager.ImageAdapter;
import ImagePager.ImagePagerAdapter;
import android.os.Bundle;
import android.app.Activity;
import java.util.ArrayList;
import java.util.List;
import com.example.AndroidMVC.ImageLoader;
import com.example.AndroidMVC.R;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.widget.ImageView;
 
// it is an activity which shows the Image pager where we can navigate into the images of a flat.

public class ImageViewPager extends Activity {
    // Declare Variable
    String eid;
 
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
        super.onCreate(savedInstanceState);
        // Set title for the ViewPager
        setTitle("Images");
        // Get the view from view_pager.xml
        setContentView(R.layout.viewpager);
        ImageLoader imageLoader;
        // instantiate imageLoader object
        imageLoader=new ImageLoader(getApplicationContext());
        // Retrieve data from MainActivity on item click event
        Intent p = getIntent();
        eid = p.getStringExtra("eid");

        ImageAdapter imageAdapter = new ImageAdapter(this);
        imageAdapter.downloadImages(eid);
        List<ImageView> images = new ArrayList<ImageView>();
        
        // Retrieve all the images
        for (int i = 0; i < imageAdapter.getCount(); i++) {

            ImageView imageView = new ImageView(this);
            
            imageLoader.DisplayImage(imageAdapter.getImgs().get(i), imageView, 0);
            imageView.setScaleType(ImageView.ScaleType.CENTER);
            images.add(imageView);
        }
 
        // Set the images into ViewPager
        ImagePagerAdapter pageradapter = new ImagePagerAdapter(images);
        ViewPager viewpager = (ViewPager) findViewById(R.id.pager);
        viewpager.setAdapter(pageradapter);
        // Show images following the position
        viewpager.setCurrentItem(0);
    }
}
