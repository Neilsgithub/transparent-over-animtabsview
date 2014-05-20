
package tree.love.animtabsview.demo;

import tree.love.animtabsview.AnimTabsView;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MainActivity extends FragmentActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment()).commit();
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        private AnimTabsView mTabsView;
        private ViewPager mViewPager;
        private MyPagerAdapter mPagerAdapter;

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            setupViews(rootView);
            return rootView;
        }

        private void setupViews(View rootView) {
            mTabsView = (AnimTabsView) rootView.findViewById(R.id.publiclisten_tab);
            mPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
            mViewPager = (ViewPager) rootView.findViewById(R.id.pager);
            mViewPager.setAdapter(mPagerAdapter);
            mTabsView.setViewPager(mViewPager);
        }

        public class MyPagerAdapter extends FragmentPagerAdapter {

            private final String[] TITLES = { "推荐", "排行榜", "歌单", "DJ节目"};

            public MyPagerAdapter(FragmentManager fm) {
                super(fm);
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return TITLES[position];
            }

            @Override
            public int getCount() {
                return TITLES.length;
            }

            @Override
            public Fragment getItem(int position) {
                return SuperAwesomeCardFragment.newInstance(position);
            }

        }
    }

}
