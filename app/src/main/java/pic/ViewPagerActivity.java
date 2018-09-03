/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package pic;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import DBUtils.DBOpenHelper;
import zj.com.mc.Myapplilcation;
import zj.com.mc.UtilisClass;

public class ViewPagerActivity extends Activity {
    private ViewPager mViewPager;
    private List<Map> filelist;
    private DBOpenHelper dbOpenHelper;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbOpenHelper = DBOpenHelper.getInstance(getApplicationContext());
        mViewPager = new HackyViewPager(this);
        setContentView(mViewPager);
        Myapplilcation.addActivity(this);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        String fileId = bundle.getString("FileId");
        String currentId = bundle.getString("currentId");

        filelist = UtilisClass.getFilelist2(dbOpenHelper, fileId);

        mViewPager.setAdapter(new SamplePagerAdapter(this, filelist, currentId));
    }

    static class SamplePagerAdapter extends PagerAdapter {
        private Context context;
        private List<Map> filelist;
        private String currentId;

        public SamplePagerAdapter(Context context, List<Map> filelist, String currentId) {
            this.context = context;
            this.filelist = filelist;
            this.currentId = currentId;
        }

        @Override
        public int getCount() {
            return filelist.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());

            //photoView.setImageResource(sDrawables[position]);
            //int index = Integer.parseInt(currentId);
            //position = index;

            String filepath = filelist.get(position).get("LocaPath") + "";
            photoView.setImageURI(Uri.parse(filepath));
            container.addView(photoView, LayoutParams.MATCH_PARENT,
                    LayoutParams.MATCH_PARENT);

            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            //position = 0;
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Myapplilcation.removeActivity(this);
    }
}
