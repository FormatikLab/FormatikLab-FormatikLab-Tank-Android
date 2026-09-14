package lab.formatik.tank;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URI;

public class MainActivity extends Activity {

    private static final String PREFS = "formatiklab_tank";
    private static final String KEY_SERVER_URL = "server_url";
    private static final String DEFAULT_SERVER_URL = "http://192.168.68.99:8081";

    private WebView webView;
    private ProgressBar progressBar;
    private TextView titleView;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
        buildUi();
        configureWebView();
        loadPortal();
    }

    private void buildUi() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setBackgroundColor(Color.rgb(9, 18, 31));

        LinearLayout bar = new LinearLayout(this);
        bar.setOrientation(LinearLayout.HORIZONTAL);
        bar.setGravity(Gravity.CENTER_VERTICAL);
        bar.setPadding(dp(14), dp(8), dp(8), dp(8));
        bar.setBackgroundColor(Color.rgb(9, 18, 31));

        titleView = new TextView(this);
        titleView.setText("FormatikLab Tank");
        titleView.setTextColor(Color.WHITE);
        titleView.setTextSize(18);
        titleView.setTypeface(null, 1);
        bar.addView(titleView, new LinearLayout.LayoutParams(0, dp(48), 1f));

        Button refresh = topButton("↻");
        refresh.setContentDescription("Aggiorna");
        refresh.setOnClickListener(v -> webView.reload());
        bar.addView(refresh);

        Button settings = topButton("⚙");
        settings.setContentDescription("Server");
        settings.setOnClickListener(v -> showServerDialog());
        bar.addView(settings);

        Button logout = topButton("Esci");
        logout.setOnClickListener(v -> logout());
        bar.addView(logout);

        root.addView(bar, new LinearLayout.LayoutParams(-1, dp(56)));

        progressBar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setVisibility(View.GONE);
        root.addView(progressBar, new LinearLayout.LayoutParams(-1, dp(3)));

        webView = new WebView(this);
        webView.setBackgroundColor(Color.rgb(9, 18, 31));
        root.addView(webView, new LinearLayout.LayoutParams(-1, 0, 1f));

        setContentView(root);
    }

    private Button topButton(String text) {
        Button b = new Button(this);
        b.setText(text);
        b.setTextColor(Color.WHITE);
        b.setTextSize(13);
        b.setAllCaps(false);
        b.setBackgroundColor(Color.TRANSPARENT);
        b.setPadding(dp(10), 0, dp(10), 0);
        b.setMinWidth(0);
        b.setMinimumWidth(0);
        return b;
    }

    private void configureWebView() {
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setLoadWithOverviewMode(false);
        s.setUseWideViewPort(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        s.setMediaPlaybackRequiresUserGesture(true);
        s.setUserAgentString(s.getUserAgentString() + " FormatikLabTankAndroid/0.1.0");

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, false);

        webView.setWebChromeClient(new android.webkit.WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                progressBar.setProgress(newProgress);
                progressBar.setVisibility(newProgress < 100 ? View.VISIBLE : View.GONE);
            }
        });

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Uri uri = request.getUrl();
                if (isSameServer(uri)) return false;
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, uri));
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(MainActivity.this, "Impossibile aprire il link", Toast.LENGTH_SHORT).show();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                CookieManager.getInstance().flush();
                if (url.contains("/dashboard")) {
                    titleView.setText("FormatikLab Tank • Dashboard");
                } else if (url.contains("/login")) {
                    titleView.setText("FormatikLab Tank • Accesso");
                } else {
                    titleView.setText("FormatikLab Tank");
                }
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (request.isForMainFrame()) {
                    showOfflinePage(error != null ? error.getDescription().toString() : "Connessione non disponibile");
                }
            }
        });
    }

    private void loadPortal() {
        String base = getServerUrl();
        webView.loadUrl(base + "/login");
    }

    private String getServerUrl() {
        return normalizeUrl(prefs.getString(KEY_SERVER_URL, DEFAULT_SERVER_URL));
    }

    private String normalizeUrl(String value) {
        if (value == null) return DEFAULT_SERVER_URL;
        String v = value.trim();
        while (v.endsWith("/")) v = v.substring(0, v.length() - 1);
        if (!(v.startsWith("http://") || v.startsWith("https://"))) v = "http://" + v;
        return v;
    }

    private void showServerDialog() {
        final EditText input = new EditText(this);
        input.setSingleLine(true);
        input.setText(getServerUrl());
        input.setSelectAllOnFocus(true);
        int p = dp(18);

        LinearLayout wrap = new LinearLayout(this);
        wrap.setPadding(p, dp(6), p, 0);
        wrap.addView(input, new LinearLayout.LayoutParams(-1, -2));

        new AlertDialog.Builder(this)
                .setTitle("Indirizzo Tank Server")
                .setMessage("Per il test LAN usa l'indirizzo del PC. In produzione inserisci l'URL HTTPS del server.")
                .setView(wrap)
                .setPositiveButton("Salva", (d, which) -> {
                    String url = normalizeUrl(input.getText().toString());
                    if (!isValidHttpUrl(url)) {
                        Toast.makeText(this, "Indirizzo non valido", Toast.LENGTH_LONG).show();
                        return;
                    }
                    prefs.edit().putString(KEY_SERVER_URL, url).apply();
                    CookieManager.getInstance().removeAllCookies(null);
                    CookieManager.getInstance().flush();
                    webView.clearHistory();
                    loadPortal();
                })
                .setNegativeButton("Annulla", null)
                .show();
    }

    private boolean isValidHttpUrl(String url) {
        try {
            URI u = new URI(url);
            return ("http".equalsIgnoreCase(u.getScheme()) || "https".equalsIgnoreCase(u.getScheme()))
                    && u.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isSameServer(Uri uri) {
        try {
            Uri base = Uri.parse(getServerUrl());
            int basePort = base.getPort() == -1 ? defaultPort(base.getScheme()) : base.getPort();
            int uriPort = uri.getPort() == -1 ? defaultPort(uri.getScheme()) : uri.getPort();
            return safeEquals(base.getScheme(), uri.getScheme())
                    && safeEquals(base.getHost(), uri.getHost())
                    && basePort == uriPort;
        } catch (Exception e) {
            return false;
        }
    }

    private int defaultPort(String scheme) {
        return "https".equalsIgnoreCase(scheme) ? 443 : 80;
    }

    private boolean safeEquals(String a, String b) {
        return a != null && b != null && a.equalsIgnoreCase(b);
    }

    private void logout() {
        webView.loadUrl(getServerUrl() + "/logout");
    }

    private void showOfflinePage(String error) {
        String safeError = escapeHtml(error);
        String safeUrl = escapeHtml(getServerUrl());
        String html = "<!doctype html><html><meta name='viewport' content='width=device-width,initial-scale=1'>" +
                "<body style='margin:0;background:#09121f;color:#eaf2ff;font-family:sans-serif;display:grid;place-items:center;min-height:100vh'>" +
                "<div style='max-width:420px;padding:28px;text-align:center'>" +
                "<div style='font-size:44px'>⚠</div><h2>Server non raggiungibile</h2>" +
                "<p style='color:#9dc5f1'>" + safeUrl + "</p>" +
                "<p style='color:#86a9d4'>" + safeError + "</p>" +
                "<p>Controlla Wi-Fi, indirizzo server e firewall. Tocca ⚙ per modificare il server.</p>" +
                "</div></body></html>";
        webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }
}
