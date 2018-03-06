package com.shree.varikolepahani.network;

import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.shree.varikolepahani.util.ApplicationConstants;

import java.lang.annotation.Annotation;

/**
 * Created by SrinivasDonapati on 2/5/2018.
 */

public class WebviewDataLoader {
    private WebView webView;
    private boolean isOneB;
    private boolean isKhatha;
    private String id;
    private WebviewDataLoaderListener listener;

    public WebviewDataLoader(WebView webView, String id, boolean isOneB, boolean isKhatha) {
        this.webView = webView;
        this.isOneB = isOneB;
        this.isKhatha = isKhatha;
        this.id = id;
        _init();
    }

    public void setListener(WebviewDataLoaderListener listener) {
        this.listener = listener;
    }
    public void load() {
        String htmlContent = "";
        if (isOneB) {
            htmlContent = ApplicationConstants.ONE_B_FORM_HTML;
            if (isKhatha) {
                htmlContent = htmlContent.replaceFirst(ApplicationConstants.ONE_B_KHATHA_HTML_PLACEHOLDER, ApplicationConstants.ONE_B_FORM_KHATHA_HTML);
            } else {
                htmlContent = htmlContent.replaceFirst(ApplicationConstants.ONE_B_ADHAR_HTML_PLACEHOLDER, ApplicationConstants.ONE_B_FORM_ADHAAR_HTML);
            }
        } else {
            htmlContent = ApplicationConstants.PAHANI_ADHAR_FORM_HTML;
        }

        if (isKhatha) {
            htmlContent = htmlContent.replaceFirst(ApplicationConstants.KHATHA_PLACEHOLDER, id);
        } else {
            htmlContent = htmlContent.replaceFirst(ApplicationConstants.ADHAR_PLACEHOLDER, id);
        }

        htmlContent = htmlContent.replaceFirst(ApplicationConstants.DIST_PLACEHOLDER, ApplicationConstants.DISTRICT_CODE);
        htmlContent = htmlContent.replaceFirst(ApplicationConstants.MDL_PLACEHOLDER, ApplicationConstants.MDL_CODE);
        htmlContent = htmlContent.replaceFirst(ApplicationConstants.VILL_PLACEHOLDER, ApplicationConstants.VILLAGE_CODE);
        webView.loadData(htmlContent, "text/html; charset=utf-8", "UTF-8");
    }

    private void _init() {
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.addJavascriptInterface(new MaBhoomiDataLoader(), "MABHOOMI_LOADER");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String code = "javascript: var maBhoomiLoader = window.MABHOOMI_LOADER; var formPage = document.getElementById(\"divGridViewScroll\"); if (formPage) { var results = document.getElementById(\"divGridViewScroll\").getElementsByTagName(\"tr\"); if (results && results.length > 1) { if (results.length > 2) { var data = formPage.innerHTML; maBhoomiLoader.setMultipleResults(data); } else { var anchorElms = results[1].getElementsByTagName(\"a\"); if (anchorElms.length > 0) { anchorElms[0].click(); } else { maBhoomiLoader.setFailure(); } } } else { maBhoomiLoader.setFailure(); } } var printDiv = document.getElementById(\"PrinterDiv\"); if (printDiv) { var data = document.body.innerHTML; maBhoomiLoader.setData(data); } else { maBhoomiLoader.setFailure(); }";
                view.loadUrl(code);
            }
        });
    }

    private class MaBhoomiDataLoader implements JavascriptInterface {
        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }

        @JavascriptInterface
        public void setFailure() {
            listener.setFailure();
            System.out.println("===== FAILURE =============================");
        }

        @JavascriptInterface
        public void setMultipleResults(String data) {
            listener.setMultipleResults(data);
            System.out.println("===== Multi result =============================");
        }

        @JavascriptInterface
        public void setData(String data) {
            listener.setData(data);
            System.out.println("===== data =============================" + data);
        }
    }

    public interface WebviewDataLoaderListener {
        void setFailure();
        void setData(String data);
        void setMultipleResults(String data);
    }
}
