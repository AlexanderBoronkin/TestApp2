package com.rule_2.testapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.appodeal.ads.Appodeal;
import com.appodeal.ads.BannerCallbacks;
import com.appodeal.ads.InterstitialCallbacks;
import com.appodeal.ads.NativeAdView;
import com.appodeal.ads.RewardedVideoCallbacks;
import com.appodeal.ads.NativeAd;
import com.appodeal.ads.NativeCallbacks;
import com.appodeal.ads.native_ad.views.NativeAdViewAppWall;
import com.appodeal.ads.utils.PermissionsHelper;

import java.util.ArrayList;
import java.util.List;

import io.presage.finder.model.App;

public class MainActivity extends AppCompatActivity {
    int countOfShownBanners = 0;
    int countOfShownRVideos = 0;
    boolean bannerShown = false;
    boolean NativeShown = false;
    String mPlacementName = "default";
    String RVideoPlacement = "forRVideo";
    List<NativeAd> nativeAds = new ArrayList<>();
    LinearLayout nativeAdsListView;

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt("countOfShownBanners",countOfShownBanners);
        outState.putInt("countOfShownRVideos",countOfShownRVideos);
        outState.putBoolean("bannerShown",bannerShown);
        outState.putBoolean("NativeShown",NativeShown);

        Button btnRVideo = (Button) findViewById(R.id.btnRVideo);
        outState.putBoolean("RVideoEnabled", btnRVideo.isEnabled());
        super.onSaveInstanceState(outState);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            countOfShownBanners = savedInstanceState.getInt("countOfShownBanners", 0);
            countOfShownRVideos = savedInstanceState.getInt("countOfShownRVideos", 0);
            bannerShown = savedInstanceState.getBoolean("bannerShown", false);
            NativeShown = savedInstanceState.getBoolean("NativeShown", false);

            Button btnRVideo = (Button) findViewById(R.id.btnRVideo);
            btnRVideo.setEnabled(savedInstanceState.getBoolean("RVideoEnabled", false));

            if(bannerShown) {
                Appodeal.show(this, Appodeal.BANNER_TOP);
            }
            if(countOfShownBanners >= 7){
                Button btnInterstitials = (Button) findViewById(R.id.btnInterstitials);
                btnInterstitials.setEnabled(true);
            }
            if(countOfShownRVideos >= 3){
                Button btnNative = (Button) findViewById(R.id.btnNative);
                btnNative.setVisibility(View.VISIBLE);
                if(NativeShown) {
                    btnNative.performClick();
                }
            }
        }

        if (Build.VERSION.SDK_INT >= 23 &&
                (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED))
        {
            Appodeal.requestAndroidMPermissions(this, new PermissionsHelper.AppodealPermissionCallbacks(){
                @Override
                public void writeExternalStorageResponse(int result) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        Utils.showToast( MainActivity.this, "WRITE_EXTERNAL_STORAGE permission was granted");
                    } else {
                        Utils.showToast( MainActivity.this, "WRITE_EXTERNAL_STORAGE permission was NOT granted");
                    }
                }
                @Override
                public void accessCoarseLocationResponse(int result) {
                    if (result == PackageManager.PERMISSION_GRANTED) {
                        Utils.showToast(MainActivity.this, "ACCESS_COARSE_LOCATION permission was granted");
                    } else {
                        Utils.showToast(MainActivity.this, "ACCESS_COARSE_LOCATION permission was NOT granted");
                    }
                }
            });
        }

        Appodeal.setTesting(true);

        Appodeal.initialize(this, "0dbcc75e03399c1d008ee29c0cd4cad758b053e2d18ed559",
                Appodeal.INTERSTITIAL |
                        Appodeal.REWARDED_VIDEO |
                        Appodeal.NATIVE |
                        Appodeal.BANNER);

        Appodeal.setRewardedVideoCallbacks(new RewardedVideoCallbacks() {
            @Override
            public void onRewardedVideoLoaded(boolean isPrecache) {
                if(countOfShownRVideos < 3) {
                    Button btnRVideo = (Button) findViewById(R.id.btnRVideo);
                    btnRVideo.setEnabled(true);
                }
            }
            @Override
            public void onRewardedVideoFailedToLoad() {
                Button btnRVideo = (Button)findViewById(R.id.btnRVideo);
                btnRVideo.setEnabled(false);
            }
            @Override
            public void onRewardedVideoShown() {
                countOfShownRVideos++;
                if(countOfShownRVideos >= 3) {
                    Button btnNative = (Button) findViewById(R.id.btnNative);
                    btnNative.setVisibility(View.VISIBLE);
                }
                Button btnRVideo = (Button)findViewById(R.id.btnRVideo);
                btnRVideo.setEnabled(false);
            }
            @Override
            public void onRewardedVideoClicked() {
                // Вызывается при нажатии на видео с наградой за просмотр
            }
            @Override
            public void onRewardedVideoFinished(double amount, String name) {

            }
            @Override
            public void onRewardedVideoClosed(boolean finished) {
                if(bannerShown) {
                    Appodeal.show(MainActivity.this, Appodeal.BANNER_TOP);
                }
            }
            @Override
            public void onRewardedVideoExpired() {
               // Вызывается, когда видео с наградой за просмотр больше не доступно
            }
        });

        Appodeal.setNativeCallbacks(new NativeCallbacks() {
            @Override
            public void onNativeLoaded() {
                Appodeal.cache(MainActivity.this, Appodeal.NATIVE, 4);
            }

            @Override
            public void onNativeFailedToLoad() {
                //Toast.makeText(MainActivity.this, "onNativeFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeShown(NativeAd nativeAd) {
                //Toast.makeText(MainActivity.this, "onNativeShown", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeClicked(NativeAd nativeAd) {
                //Toast.makeText(MainActivity.this, "onNativeClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNativeExpired() {
                //Toast.makeText(MainActivity.this, "onNativeExpired", Toast.LENGTH_SHORT).show();
            }
        });

        Appodeal.setInterstitialCallbacks(new InterstitialCallbacks() {
            @Override
            public void onInterstitialLoaded(boolean b) {
                if(countOfShownBanners >= 7) {
                    Button btnInterstitials = (Button) findViewById(R.id.btnInterstitials);
                    btnInterstitials.setEnabled(true);
                }
            }

            @Override
            public void onInterstitialFailedToLoad() {
                //Toast.makeText(MainActivity.this, "onInterstitialFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialShown() {
                Button btnInterstitials = (Button) findViewById(R.id.btnInterstitials);
                btnInterstitials.setEnabled(false);
            }

            @Override
            public void onInterstitialClicked() {
                //Toast.makeText(MainActivity.this, "onInterstitialClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onInterstitialClosed() {
                if(bannerShown) {
                    Appodeal.show(MainActivity.this, Appodeal.BANNER_TOP);
                }
            }

            @Override
            public void onInterstitialExpired() {
                //Toast.makeText(MainActivity.this, "onInterstitialExpired", Toast.LENGTH_SHORT).show();
            }
        });

        Appodeal.setBannerCallbacks(new BannerCallbacks() {
            @Override
            public void onBannerLoaded(int i, boolean b) {
                //Toast.makeText(MainActivity.this, "onBannerLoaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerFailedToLoad() {
                //Toast.makeText(MainActivity.this, "onBannerFailedToLoad", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerShown() {
                countOfShownBanners++;
                Toast.makeText(MainActivity.this, "Banner shown " + countOfShownBanners, Toast.LENGTH_SHORT).show();
                if(countOfShownBanners == 7)
                {
                    if(Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
                        Button btnInterstitials = (Button) findViewById(R.id.btnInterstitials);
                        btnInterstitials.setEnabled(true);
                    }
                }
            }

            @Override
            public void onBannerClicked() {
                //Toast.makeText(MainActivity.this, "onBannerClicked", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onBannerExpired() {
                //Toast.makeText(MainActivity.this, "onBannerExpired", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void OnBtnBannersClick(View v) {
        if (Appodeal.isLoaded(Appodeal.BANNER_TOP)) {
            if(NativeShown){
                hideNative();
                NativeShown = false;
            }
            Appodeal.show(this, Appodeal.BANNER_TOP);
            bannerShown = true;
        }
        else{
            Toast.makeText(this,
                    "Banner not downloaded",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onBtnInterstitialsClick(View v){
        if(Appodeal.isLoaded(Appodeal.INTERSTITIAL)) {
            if(bannerShown) {
                Appodeal.hide(this, Appodeal.BANNER_TOP);
            }
            Appodeal.show(this, Appodeal.INTERSTITIAL);
        }
        else{
            Toast.makeText(this,
                    "Interstitial not downloaded",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onBtnRVideoClick(View v){
        if (Appodeal.canShow(Appodeal.REWARDED_VIDEO, RVideoPlacement)) {
            if(bannerShown) {
                Appodeal.hide(this, Appodeal.BANNER_TOP);
            }
            Appodeal.show(this, Appodeal.REWARDED_VIDEO, RVideoPlacement);
        }
        else {
            Toast.makeText(this,
                    "Can't show rewarded video",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onBtbNativeClick(View v){
        if(bannerShown) {
            Appodeal.hide(this, Appodeal.BANNER_TOP);
            bannerShown = false;
        }
        hideNative();
        nativeAds = Appodeal.getNativeAds(4);
        if(nativeAds.size() > 0) {
            NativeShown = true;
            nativeAdsListView = findViewById(R.id.nativeAdsListView);
            for (NativeAd nativeAd : nativeAds) {
                NativeAdView nativeAdView = new NativeAdViewAppWall(nativeAdsListView.getContext(), nativeAd, ((MainActivity) nativeAdsListView.getContext()).mPlacementName);
                nativeAdsListView.addView(nativeAdView);
            }
        }
    }

    private void hideNative(){
        LinearLayout nativeListView = findViewById(R.id.nativeAdsListView);
        int childCount = nativeListView.getChildCount();
        for (int i = 0; i < childCount; i++) {
            NativeAdView child = (NativeAdView) nativeListView.getChildAt(i);
            child.unregisterViewForInteraction();
            child.destroy();
        }
        nativeListView.removeAllViews();
    }
}
