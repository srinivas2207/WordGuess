package com.shree.wordguess.fragment;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.TextViewCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.shree.wordguess.R;
import com.shree.wordguess.activity.ParentActivity;
import com.shree.wordguess.custom.CustomAlertDialog;
import com.shree.wordguess.custom.CustomProgressBar;
import com.shree.wordguess.custom.WordBoxTextView;
import com.shree.wordguess.network.NetworkOperations;
import com.shree.wordguess.util.ApplicationConstants;
import com.shree.wordguess.util.DatabaseUtil;
import com.shree.wordguess.util.Utils;
import com.shree.wordguess.util.WordData;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

/**
 * Fragment to show game UI
 */

public class PlayFragment extends Fragment implements FragmentInterface {
    private List<String> wordsInSession = new ArrayList<>();
    private WordData.Word currentGameWord = null;
    private String langCode = null;
    private int category = 0;

    private boolean isTranslationOn = false;
    private int translateTrials = 0;
    private WebView translateWebview = null;
    private List<WordData.Word> randomWords = null;

    private boolean onWindowFocusChanged = false;

    private View content_layout = null;
    private WordBoxTextView wordBlockView = null;
    private TextView translatedView = null;
    private View speakUpView = null;

    private View toolbarContainer = null;
    private View translateTB = null;
    private View speakUpTB = null;
    private View searchTB = null;


    //private TextView gameNumberView = null;
    private TextView scoreView = null;
    private LinearLayout chancesContainer = null;
    private LinearLayout mistakeContainer = null;

    private CustomProgressBar currentScoreView = null;
    private View messageContainer = null;
    private View playContainer = null;

    private TextView messageView = null;
    private ImageView refreshView = null;
    private ProgressBar progressBar = null;
    private View keyboard_include = null;
    private View message_include = null;

    private String uniqueChars = "";
    private String userSelectionChars = "";
    private String wrongSelection = "";
    private boolean shallReveal = false;

    private int gameNumber = 0;
    private int totalScore = 0;
    private int currentScore = 100;

    private String movieType = null;
    private HashMap<String, String> gameStatus = null;
    private boolean isLoading = false;
    boolean isAnimating = false;
    boolean isVocabBee = false;

    private boolean isHalfRevealed = false;

    private boolean canExitFromGame = false;

    private WordPrononcer prononcer;
    private long gameId = 0;

    private int keyBoardIds[] = {R.id.keyA, R.id.keyB, R.id.keyC, R.id.keyD, R.id.keyE, R.id.keyF, R.id.keyG, R.id.keyH, R.id.keyI, R.id.keyJ, R.id.keyK, R.id.keyL, R.id.keyM,
            R.id.keyN, R.id.keyO, R.id.keyP, R.id.keyQ, R.id.keyR, R.id.keyS, R.id.keyT, R.id.keyU, R.id.keyV, R.id.keyW, R.id.keyX, R.id.keyY, R.id.keyZ};

    public void setLangCode(String langCode) {
        this.langCode = langCode;
    }

    public void setGameType(int gameType) {
        if (gameType == 1) {
            this.isVocabBee = true;
        } else {
            this.isVocabBee = false;
        }
    }

    public void setCategory(int category) {
        this.category = category;
    }

    @Override
    public void onUiNotification(int type, String data) {

    }

    @Override
    public void initializeViews() {
        wordBlockView = content_layout.findViewById(R.id.wordBlock);
        translatedView = content_layout.findViewById(R.id.translatedWord);
        speakUpView = content_layout.findViewById(R.id.speakUp);

        TextViewCompat.setAutoSizeTextTypeWithDefaults(translatedView, TextViewCompat.AUTO_SIZE_TEXT_TYPE_UNIFORM);
        TextViewCompat.setAutoSizeTextTypeUniformWithConfiguration(translatedView, 20, 40 , 2, TypedValue.COMPLEX_UNIT_SP);

        chancesContainer = content_layout.findViewById(R.id.chanceContainer);
        mistakeContainer = content_layout.findViewById(R.id.mistackeContainer);

        scoreView =  content_layout.findViewById(R.id.score);
        currentScoreView = content_layout.findViewById(R.id.currentScore);

        progressBar = content_layout.findViewById(R.id.progressBar);
        keyboard_include = content_layout.findViewById(R.id.keyboard_include);
        message_include = content_layout .findViewById(R.id.message_include);

        messageView = content_layout.findViewById(R.id.message);
        refreshView = content_layout.findViewById(R.id.refresh);

        messageContainer = content_layout.findViewById(R.id.messageContainer);
        playContainer = content_layout.findViewById(R.id.playContainer);

        toolbarContainer = content_layout.findViewById(R.id.toolbarContainer);
        translateTB = content_layout.findViewById(R.id.toolbarTranslate);
        speakUpTB = content_layout.findViewById(R.id.toolbarSpeak);
        searchTB = content_layout.findViewById(R.id.toolbarSearch);

        _initTranslateWebview();
        initializeListeners();
    }

    @Override
    public void initializeListeners() {
        for(int key : keyBoardIds) {
            content_layout.findViewById(key).setOnClickListener(onKeyClickListener);
        }

        refreshView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showLoadingPage();
                fetchWords();
            }
        });

        prononcer = new WordPrononcer(getActivity());
        speakUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                prononcer.speak(currentGameWord.getName());
            }
        });

        searchTB.setOnClickListener(onToolbarBtnClickListener);
        translateTB.setOnClickListener(onToolbarBtnClickListener);
        speakUpTB.setOnClickListener(onToolbarBtnClickListener);

        wordBlockView.setOnLongClickListener(longPressListener);
        translatedView.setOnLongClickListener(longPressListener);
    }

    @Override
    public void loadData() {

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_play, menu);

        MenuItem favMenu = menu.findItem(R.id.favourite);
        if (currentGameWord != null && currentGameWord.isFavourite()) {
            favMenu.setIcon(R.drawable.menu_fav);
        } else {
            favMenu.setIcon(R.drawable.menu_fav_unsel);
        }

        MenuItem halfReveal = menu.findItem(R.id.reveal);
        if (isHalfRevealed) {
            halfReveal.setVisible(false);
        } else {
            halfReveal.setVisible(true);
        }

        MenuItem shareMenu = menu.findItem(R.id.share);
        if (isGameSuccessful() || isGameOver()) {
            shareMenu.setVisible(true);
        } else {
            shareMenu.setVisible(false);
        }

        MenuItem hintMenu = menu.findItem(R.id.hint);
        if (currentGameWord == null ||
                (currentGameWord.getType() == null && currentGameWord.getDesc() == null )) {
            hintMenu.setVisible(false);
        } else {
            hintMenu.setVisible(true);
        }
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        content_layout = (View) inflater.inflate(R.layout.play_fragment,
                container, false);
        initializeViews();
        initNewGame();
        _initializeAdMob();
        return content_layout;
    }

    /**
     * Starting a new game session
     */
    public void initNewGame() {
        gameId = new Date().getTime();

        gameNumber = 0;
        totalScore = 0;

        currentGameWord = null;
        wordsInSession = new ArrayList();
        showLoadingPage();
        fetchWords();
    }

    /**
     * Showing loading screen
     */
    private void showLoadingPage() {
        loadingWaitCounter.cancel();
        loadingWaitCounter.start();

        isLoading = true;
        messageContainer.setVisibility(View.VISIBLE);
        playContainer.setVisibility(View.GONE);
        refreshView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        messageView.setText("Please wait a moment");

        // show ad if loading takes more time
    }

    /**
     * Showing network error screen
     */
    private void showConnectionFailure() {
        loadingWaitCounter.cancel();

        messageContainer.setVisibility(View.VISIBLE);
        playContainer.setVisibility(View.GONE);
        refreshView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        messageView.setText("You are not connected");
    }

    /**
     * Showing error screen
     */
    private void showErrorPage() {
        loadingWaitCounter.cancel();

        messageContainer.setVisibility(View.VISIBLE);
        playContainer.setVisibility(View.GONE);
        refreshView.setVisibility(View.VISIBLE);
        progressBar.setVisibility(View.GONE);
        messageView.setText("Some thing broken");
    }

    /**
     * Showing app level error screen
     */
    private void showNoWordError() {
        loadingWaitCounter.cancel();

        messageContainer.setVisibility(View.VISIBLE);
        playContainer.setVisibility(View.GONE);
        refreshView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
        messageView.setText("No words to play, try with other categories !");
    }

    /**
     * Starting a new game | showing a new word
     */
    public void startGame() {
        checkAd();
        ((ParentActivity)getActivity()).addAnalyticData( isVocabBee ? ApplicationConstants.VOCAB_BEE_UP : ApplicationConstants.SPELL_BEE_UP, category + "");

        if (randomWords.size() == 0) {
            showNoWordError();
            return;
        }

        if (isVocabBee) {
            currentGameWord = _fetchTranslatedWord();
        } else {
            currentGameWord = randomWords.get(0);
        }

        if (currentGameWord == null) {
            showLoadingPage();
            return;
        } else {
            randomWords.remove(currentGameWord);
        }

        initializeData();
        showPlayUI();

        if (randomWords.size() == 0) {
            fetchWords();
        }
    }

    /**
     * Intializing background webview, used for translating words
     */
    private void _initTranslateWebview() {
        translateWebview = content_layout.findViewById(R.id.dataLoaderView);
        translateWebview.getSettings().setUseWideViewPort(true);
        translateWebview.getSettings().setJavaScriptEnabled(true);

        translateWebview.addJavascriptInterface(new TranslateLoader(), "TRANSLATE_LOADER");
        translateWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                String code = ApplicationConstants.TRANSLATE_RESULT_FETCHER_JS;
                view.loadUrl(code);
            }
        });
    }

    /**
     * Triggering word translation
     */
    private void _runTranslation() {
        isTranslationOn = true;
        translateTrials = 0;
        String wordName = _fetchNonTranslatedWord();
        if (wordName != null) {
            // Check Internet connection
            if (!NetworkOperations.getInstance().checkNetworkConnection()) {
                isTranslationOn = false;
                showConnectionFailure();
                return;
            }
            _loadTranslateView(wordName);
        } else {
            isTranslationOn = false;
            startGame();
        }
    }

    /**
     * Loading translate webview with the translate URL
     * @param word
     */
    private void _loadTranslateView(String word) {
        String translateUrl  = ApplicationConstants.TRANSLATE_URL_TEMPLATE;
        translateUrl = translateUrl.replace("{code}", langCode);
        translateUrl = translateUrl.replace("{word}", word);
        translateWebview.loadUrl(translateUrl);
        translateTrials ++;
        Utils.log("Loading word ===== > " + word);
    }

    /**
     * Fetching non-translated word from word list
     * @return
     */
    private String _fetchNonTranslatedWord() {
        for (WordData.Word word : randomWords) {
            if (word.getSouceLang() != null && word.getSouceLang().equalsIgnoreCase(langCode) && word.getTranslatedValue() != null) {
                continue;
            } else {
                return word.getName();
            }
        }
        return null;
    }

    /**
     * Fetching translated word from word list
     * @return
     */
    private WordData.Word _fetchTranslatedWord() {
        WordData.Word translatedWord = null;
        for (WordData.Word word : randomWords) {
            if (word.getSouceLang() != null && word.getSouceLang().equalsIgnoreCase(langCode) && word.getTranslatedValue() != null) {
                translatedWord = word;
                break;
            }
        }
        return translatedWord;
    }

    /**
     * JS Translator class to be used by webview to notify the results
     */
    private class TranslateLoader implements JavascriptInterface {
        @Override
        public Class<? extends Annotation> annotationType() {
            return null;
        }

        @JavascriptInterface
        public void setResult(final String source, String result) {

            // Allowing maximum trials of 10
            if (translateTrials >= 10 ) {
                isTranslationOn = false;
                return;
            }

            if (source == null || source.trim().length() == 0) {
                return;
            }

            // Handling translation failures by trying again.
            if (result == null || result.trim().length() == 0) {
                translateWebview.post(new Runnable() {
                    public void run() {
                        _loadTranslateView(source);
                    }
                });
                return;
            }

            for (WordData.Word word : randomWords) {
                if (word.getName().equalsIgnoreCase(source)) {
                    if (source.trim().equalsIgnoreCase(result.trim())) {
                        randomWords.remove(word);
                    } else {
                        word.setTranslatedValue(result);
                        word.setSouceLang(langCode);
                    }
                    break;
                }
            }

            // If the game is in waiting state, resuming the game
            if (isLoading && _fetchTranslatedWord() != null) {
                translateWebview.post(new Runnable() {
                    public void run() {
                        startGame();
                    }
                });
            }

            final String nonTranslatedWord = _fetchNonTranslatedWord();
            if (nonTranslatedWord != null) {
                translateWebview.post(new Runnable() {
                    public void run() {
                        _loadTranslateView(nonTranslatedWord);
                    }
                });
            }
            isTranslationOn = false;
        }
    }

    /**
     * Fetching game words from database
     */
    public void fetchWords() {
        new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                if(randomWords == null || randomWords.size() == 0) {
                    randomWords = DatabaseUtil.getInstance().getRandomWords(category, wordsInSession);
                }
                return null;
            }
            protected void onPostExecute(Boolean result) {
                if (isVocabBee) {
                    _runTranslation();
                } else {
                    startGame();
                }
            }

        }.execute(null, null, null);
    }

    /**
     * Initializing the game and it's details
     */
    public void initializeData() {
        wordsInSession.add(currentGameWord.getName());

        currentScore = 100;
        shallReveal = false;
        canExitFromGame = false;

        wrongSelection = "";
        isHalfRevealed = false;

        uniqueChars = removeDuplicatesAndSpaces(currentGameWord.getName());
        try {
            uniqueChars = uniqueChars.toUpperCase();
        } catch (Exception e) {
            e.printStackTrace();
        }
        userSelectionChars = getNonEnglishChars(uniqueChars);
        gameNumber++;
        adGameCount++;
    }

    /**
     * Displaying game UI
     */
    public void showPlayUI() {
        loadingWaitCounter.cancel();

        updateToolbar();
        ((ParentActivity)getActivity()).configureToolbar("Game : " + gameNumber, true);

        isLoading = false;
        toolbarContainer.setVisibility(View.GONE);
        messageContainer.setVisibility(View.GONE);
        playContainer.setVisibility(View.VISIBLE);

        scoreView.setText(totalScore+"");
        currentScoreView.setProgress(currentScore);

        if (isVocabBee) {
            speakUpView.setVisibility(View.GONE);
            translatedView.setVisibility(View.VISIBLE);
            translatedView.setText(currentGameWord.getTranslatedValue());
        } else {
            speakUpView.setVisibility(View.VISIBLE);
            translatedView.setVisibility(View.GONE);
        }

        //Changing the color of the chance boxes
        for(int i=0;i<ApplicationConstants.GUESS_CHANCES;i++) {
            applyWrongSelection( i , ( i<wrongSelection.length() ? (wrongSelection.charAt(i)+""):null ));
        }

        enableKeyBoardButtons();
        showKeyboardOrMessage();
        drawWordBlocks();

    }

    /**
     * Drawing word using custom wordbox view
     */
    private void drawWordBlocks() {
        wordBlockView.drawWordBlocks(currentGameWord.getName(), userSelectionChars, shallReveal);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        ((ParentActivity)getActivity()).configureToolbar("Game : " + gameNumber, true);
    }


    @Override
    public void onPause() {
        saveGame();
        super.onPause();
    }

    /**
     * Applying changes to the chances UI, on wrong character selection
     * @param index
     * @param character
     */
    private void applyWrongSelection(int index, String character) {
        TextView chance = (TextView) chancesContainer.getChildAt(index);
        GradientDrawable drawable  = (GradientDrawable)  chance.getBackground();

        if(chance == null) {
            return;
        }
        if(character!=null) {
            chance.setText(character);
            drawable.setColor(Color.DKGRAY);
        } else {
            chance.setText("");
            if (Utils.primaryColorDark != 0) {
                drawable.setColor(Utils.primaryColorDark);
            }
        }
    }

    /**
     * Handling custom keyboard buttons based on game state
     */
    private void enableKeyBoardButtons() {
        Button key = null;
        for(int i=0;i<keyBoardIds.length;i++) {
            key = (Button) content_layout.findViewById(keyBoardIds[i]);
            String keyText = key.getText().toString();
            if ((userSelectionChars!=null && userSelectionChars.contains(keyText))
                    || (wrongSelection!=null && wrongSelection.contains(keyText)) ) {
                key.setTextColor(Color.WHITE);
                key.setAlpha(ApplicationConstants.KEYBOARD_ALPHA);
                key.setEnabled(false);
            } else {
                key.setAlpha(1);
                key.setEnabled(true);
            }
        }
    }

    /**
     * Disabling all the keyboard buttons
     */
    private void disableKeyBoardButtons() {
        Button key = null;
        for(int i=0;i<keyBoardIds.length;i++) {
            key = (Button) content_layout.findViewById(keyBoardIds[i]);;
            key.setEnabled(false);
        }
    }

    /**
     * Displaying keyboard or bottom result panel
     */
    public void showKeyboardOrMessage() {
        if(shallReveal) {
            handleGameResult(false, false);
        } else if(isGameSuccessful()) {
            handleGameResult(true, false);
        } else {
            keyboard_include.setVisibility(View.VISIBLE);
            message_include.setVisibility(View.GONE);
        }
    }

    /**
     * Fetching non repetitive chars from string
     * @param str
     * @return
     */
    private String removeDuplicatesAndSpaces(String str) {
        str = str.toUpperCase();
        StringBuilder noDupes = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            String si = str.substring(i, i + 1);
            if (noDupes.indexOf(si) == -1) {
                noDupes.append(si);
            }
        }
        return noDupes.toString().replaceAll(" ", "");
    }

    /**
     * Fetching non-enlish chars from the word
     * @param uniqueChars
     * @return
     */
    private String getNonEnglishChars(String uniqueChars) {
        String nonEnglishChars = "";
        for (int i = 0; i < uniqueChars.length(); i++) {
            char chr = uniqueChars.charAt(i);
            if (chr < 65 || chr > 122 || ( chr> 90 && chr < 97)) {
                nonEnglishChars += chr;
            }
        }
        return nonEnglishChars;
    }

    /**
     * Click event listener of the keyboard keys
     */
    private View.OnClickListener onKeyClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(isAnimating) {
                return;
            } else {
                validateResult(view);
            }
        }
    };

    private View.OnClickListener onToolbarBtnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.toolbarSearch:
                    String url = ApplicationConstants.GOOGLE_SEARCH_PREFIX + currentGameWord.getName() + " meaning";
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(browserIntent);
                    break;
                case R.id.toolbarTranslate:
                    String translateUrl  = ApplicationConstants.TRANSLATE_URL_TEMPLATE;
                    translateUrl = translateUrl.replace("{code}", langCode);
                    translateUrl = translateUrl.replace("{word}", currentGameWord.getName());
                    browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(translateUrl));
                    startActivity(browserIntent);

                    break;
                case R.id.toolbarSpeak:
                    if (prononcer == null) {
                        prononcer = new WordPrononcer(getActivity());
                    }
                    prononcer.speak(currentGameWord.getName());
                    break;
            }
        }
    };

    View.OnLongClickListener longPressListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View view) {
            String text = null;
            if (view.getId() == R.id.translatedWord) {
                text = currentGameWord.getTranslatedValue();
            } else {
                text = currentGameWord.getName();
            }

            if (text!= null && message_include.getVisibility() == View.VISIBLE) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText(text, text);
                clipboard.setPrimaryClip(clip);

                ((ParentActivity) getActivity()).showToast(getResources().getString(R.string.textCopied), Toast.LENGTH_SHORT);
            }

            return false;
        }
    };

    /**
     * Responding to the character selection and changing the game state
     * @param key
     */
    public void validateResult(View key) {
        ((Button) key).setEnabled(false);
        ((Button) key).setTextColor(Color.WHITE);
        ((Button) key).setAlpha(ApplicationConstants.KEYBOARD_ALPHA);

        String character = ((Button) key).getText().toString();
        if (uniqueChars.contains(character)) {
            userSelectionChars += character;
            drawWordBlocks();
            if (userSelectionChars.length() == uniqueChars.length()) {
                handleGameResult(true, true);
            }
        } else {
            applyWrongSelection(wrongSelection.length(), character);
            currentScore = currentScore - ApplicationConstants.MISTAKE_CHARGES;
            currentScoreView.setProgress(currentScore);

            wrongSelection += character;
            if (wrongSelection.length() == ApplicationConstants.GUESS_CHANCES) {
                shallReveal = true;
                drawWordBlocks();
                handleGameResult(false, true);
            }
        }
    }

    /**
     * Handling game based on game finish state
     * @param gameResult
     * @param shouldAnimate
     */
    public void handleGameResult(boolean gameResult, boolean shouldAnimate) {
        updateToolbar();

        toolbarContainer.setVisibility(View.VISIBLE);
        if (isVocabBee) {
            speakUpTB.setVisibility(View.VISIBLE);
            translateTB.setVisibility(View.GONE);
        } else {
            speakUpTB.setVisibility(View.GONE);
            translateTB.setVisibility(View.VISIBLE);
        }

        disableKeyBoardButtons();
        TextView messageTitle = (TextView) content_layout
                .findViewById(R.id.messageTitle);
        TextView messageDesc = (TextView) content_layout
                .findViewById(R.id.messageDesc);
        Button exit = (Button) content_layout.findViewById(R.id.exit);
        Button proceed = (Button) content_layout.findViewById(R.id.proceed);
        Button tryAgain = (Button) content_layout.findViewById(R.id.tryAgain);

        exit.setOnClickListener(messageBtnListeners);
        proceed.setOnClickListener(messageBtnListeners);
        tryAgain.setOnClickListener(messageBtnListeners);

        if (gameResult) {
            totalScore = totalScore + currentScore;
            scoreView.setText(totalScore+"");

            tryAgain.setVisibility(View.GONE);
            proceed.setVisibility(View.VISIBLE);
            messageTitle.setText("Thats right !");
            String messageDescStr = "You got " + currentScore + " points.";
            messageDesc.setText(messageDescStr);
        } else {
            canExitFromGame = true;
            tryAgain.setVisibility(View.VISIBLE);
            proceed.setVisibility(View.GONE);
            messageTitle.setText("Game Over !");
            String messageDescStr = "Your total score is " + totalScore + " points.";
            messageDesc.setText(messageDescStr);

            saveGame();
        }

        if(shouldAnimate) {
            isAnimating = true;
            message_include.setVisibility(View.VISIBLE);
            Animation animShow = AnimationUtils.loadAnimation( getActivity(), R.anim.slide_up);
            animShow.setAnimationListener(animListener);
            message_include.startAnimation( animShow );
        } else {
            message_include.setVisibility(View.VISIBLE);
            keyboard_include.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Animation listener used in bottom result panel
     */
    public Animation.AnimationListener animListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationEnd(Animation animation) {
            isAnimating = false;
        }

        @Override
        public void onAnimationStart(Animation animation) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onAnimationRepeat(Animation animation) {
            // TODO Auto-generated method stub

        }
    };


    View.OnClickListener messageBtnListeners = new View.OnClickListener() {

        @Override
        public void onClick(final View v) {
            if(v.getId() == R.id.exit ) {
                if(wrongSelection.length() == ApplicationConstants.GUESS_CHANCES ) {
                    getActivity().onBackPressed();
                } else {
                    showAppExitDialog();
                }

            } else if(v.getId() == R.id.proceed ) {
                isAnimating = true;
                keyboard_include.setVisibility(View.VISIBLE);
                Animation animShow = AnimationUtils.loadAnimation( getActivity(), R.anim.slide_down);
                animShow.setAnimationListener(animListener);
                message_include.startAnimation( animShow );
                message_include.setVisibility(View.GONE);

                startGame();
            } else if(v.getId() == R.id.tryAgain ) {
                isAnimating = true;
                keyboard_include.setVisibility(View.VISIBLE);
                Animation animShow = AnimationUtils.loadAnimation( getActivity(), R.anim.slide_down);
                animShow.setAnimationListener(animListener);
                message_include.startAnimation( animShow );
                message_include.setVisibility(View.GONE);

                initNewGame();
            }
        }
    };

    /**
     * Showing hint
     */
    public void showHint() {
        CustomAlertDialog hintDiaglog = new CustomAlertDialog(getActivity(), CustomAlertDialog.HINT_DIALOG);
        String hint = "";
        if (currentGameWord.getType() != null) {
            hint += "Type : " + currentGameWord.getDesc() + "\n";
        }
        if (currentGameWord.getDesc() != null) {
            hint +=  currentGameWord.getDesc();
        }

        hintDiaglog.setTitle(getResources().getString(R.string.hint))
                .setBody(hint)
                .setButtons(getResources().getString(R.string.okBtn), null)
                .build()
                .show();
    }

    /**
     * Revealing half of the characters
     */
    public void reveal() {
        CustomAlertDialog dialog = new CustomAlertDialog(getActivity(), CustomAlertDialog.HALF_REVEAL_DIALOG);
        String body = getResources().getString(R.string.halfRevealDesc);
        String revelableChars = getRevelableChars();
        if (revelableChars.length() > 0) {
            int charsToReveal = revelableChars.length();
            int revealCost = charsToReveal*5;
            body = body.replace("{0}", charsToReveal + "");
            body = body.replace("{1}", revealCost + "");

            dialog.setTitle(getResources().getString(R.string.halfReveal))
                    .setDialogListener(dialogListener)
                    .setBody(body)
                    .setButtons(getResources().getString(R.string.proceedBtn), getResources().getString(R.string.cancelBtn))
                    .build()
                    .show();
        }

    }

    /**
     * Checking the game status
     * @return
     */
    public boolean isGameSuccessful() {
        if(uniqueChars.length() == userSelectionChars.length()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checking the game status
     * @return
     */
    public boolean isGameOver() {
        if(wrongSelection!=null && wrongSelection.length()== ApplicationConstants.GUESS_CHANCES) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Restarting the game
     */
    public void refresh() {
        if(! isGameOver()) {
            CustomAlertDialog dialog = new CustomAlertDialog(getActivity(), CustomAlertDialog.GAME_REFRESH_DIALOG);
            dialog.setTitle(getResources().getString(R.string.refresh));
            dialog.setBody(getResources().getString(R.string.refreshBody));
            dialog.setButtons(getResources().getString(R.string.yesBtn),getResources().getString(R.string.cancelBtn));
            dialog.setDialogListener(dialogListener);
            dialog.build().show();
        } else {
            saveGame();
            initNewGame();
        }
    }

    /**
     * Saving the game state
     */
    public void saveGame() {
        int gameCount = gameNumber;
        if (!isGameOver() && !isGameSuccessful() ){
            gameCount --;
        }

        if (gameCount > 0 && totalScore > 0) {
            DatabaseUtil.getInstance().updateScores(gameId, isVocabBee , gameCount, totalScore, category);
        }
    }

    /**
     * Favourite/Unfavouriting the word
     */
    public void favourite() {
        if (currentGameWord != null) {
            currentGameWord.setFavourite(!currentGameWord.isFavourite());
            updateToolbar();

            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... params) {
                    DatabaseUtil.getInstance().updateWord(currentGameWord);
                    return null;
                }
                protected void onPostExecute(Boolean result) {
                }

            }.execute(null, null, null);
        }
    }

    /**
     * Sharing the word
     */
    public void share() {
        if (currentGameWord == null) {
            return;
        }

        String message = "WordGuess word of the day\n\n";
        message += "Word : " + currentGameWord.getName();
        if (currentGameWord.getDesc() != null) {
            message += "\n" + "Description : " + currentGameWord.getDesc();
        }

        if (currentGameWord.getTranslatedValue() != null) {
            message += "\n" + "Translation : " + currentGameWord.getTranslatedValue();
        }

        message += "\n\n" + "https://play.google.com/store/apps/details?id=com.shree.mychat";
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, message);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent, "Share this word with your friends !"));
    }



    public void onWindowFocusChanged(boolean hasFocus) {
        if (hasFocus && !onWindowFocusChanged) {
            onWindowFocusChanged = true;
            if(keyboard_include.getHeight()>message_include.getHeight()) {
                RelativeLayout.LayoutParams messageBlockParams = (RelativeLayout.LayoutParams)message_include.getLayoutParams();
                messageBlockParams.height = keyboard_include.getHeight();
                message_include .setLayoutParams(messageBlockParams);
            }
        }
    }

    /**
     * Text to Speech helper
     */
    public class WordPrononcer implements TextToSpeech.OnInitListener {

        private TextToSpeech tts;
        private boolean pStatus;
        private boolean speachToggle = true;

        // The constructor will create a TextToSpeech instance.
        WordPrononcer(Context context) {
            tts = new TextToSpeech(context, this);
            tts.setPitch(ApplicationConstants.PITCH_RATE);
            tts.setLanguage(Locale.getDefault());
        }

        @Override
        public void onInit(int status) {
            if (status == TextToSpeech.SUCCESS) {
                pStatus = true;
            }
            else {
                pStatus = false;
            }
        }

        public void speak(String text) {
            if (pStatus) {
                if (speachToggle) {
                    tts.setSpeechRate(ApplicationConstants.SPEACH_RATE);
                } else {
                    tts.setSpeechRate(1f);
                }
                speachToggle = !speachToggle;
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        }

        public void destroy() {
            if (tts != null) {
                tts.stop();
                tts.shutdown();
            }
        }
    }

    CustomAlertDialog.DialogListener dialogListener = new CustomAlertDialog.DialogListener() {
        @Override
        public void onPositiveBtnClick(int dialogType) {
            if (dialogType == CustomAlertDialog.HALF_REVEAL_DIALOG) {
                halfRevealChars();
                return;
            }

            if (dialogType == CustomAlertDialog.GAME_REFRESH_DIALOG) {
                saveGame();
                initNewGame();
                return;
            }

            if (dialogType == CustomAlertDialog.EXIT_APP_DIALOG) {
                canExitFromGame = true;
                getActivity().onBackPressed();
                return;
            }
        }

        @Override
        public void onNegativeBtnClick(int dialogType) {

        }
    };

    private void halfRevealChars() {
        String revealableChars = getRevelableChars();
        if (revealableChars.length() > 0) {
            userSelectionChars += revealableChars;
        }
        isHalfRevealed = true;
        currentScore = currentScore - revealableChars.length() * 5;
        showPlayUI();
    }

    private String getRevelableChars() {
        String yetToBefilledChars = "";
        if(userSelectionChars!=null && userSelectionChars.length()>0) {
            for(int i=0;i<uniqueChars.length();i++) {
                if(!userSelectionChars.contains(uniqueChars.charAt(i)+"")) {
                    yetToBefilledChars += uniqueChars.charAt(i);
                }
            }
        } else {
            yetToBefilledChars = uniqueChars;
        }

        String revelableChars = "";
        if(yetToBefilledChars.length()>2) {
            for(int i=0;i<yetToBefilledChars.length();i+=2) {
                revelableChars+= (yetToBefilledChars.charAt(i));
            }
        } else {
            revelableChars += yetToBefilledChars;
        }

        return revelableChars;
    }



    ///////////////////   ADD MOB BLOCK   //////////////////////

    private InterstitialAd mInterstitialAd = null;
    private int adGameCount = 0;

    private void _initializeAdMob() {
        mInterstitialAd = new InterstitialAd(getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ADMOB_INTERSTITIAL_ID));
        _setAdListeners();
        _requestAd();
    }

    private void _requestAd() {
        try {
            AdRequest adRequest = new AdRequest.Builder()
                    .addTestDevice("33BE2250B43518CCDA7DE426D04EE231")
                    .build();

            mInterstitialAd.loadAd(adRequest);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    private void _setAdListeners() {
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
            }

            @Override
            public void onAdClosed() {
            }
        });
    }

    private boolean checkAd() {
        if(adGameCount > ApplicationConstants.AD_INTERVAL) {
            if (mInterstitialAd.isLoaded()) {
                mInterstitialAd.show();
                adGameCount = 0;
                return true;
            }
        } else {
            if(!mInterstitialAd.isLoaded()) {
                _requestAd();
            }
        }
        return false;
    }

    @Override
    public void onDestroy() {
        if (prononcer != null) {
            prononcer.destroy();
        }
        super.onDestroy();
    }

    public boolean canExitFromGame() {
       return canExitFromGame;
    }

    public void showAppExitDialog() {
        CustomAlertDialog dialog = new CustomAlertDialog(getActivity(), CustomAlertDialog.EXIT_APP_DIALOG);
        dialog.setTitle(getResources().getString(R.string.quit));
        dialog.setBody(getResources().getString(R.string.quitDesc));
        dialog.setButtons(getResources().getString(R.string.yesBtn),getResources().getString(R.string.cancelBtn));
        dialog.setDialogListener(dialogListener);
        dialog.build().show();
    }

    private void updateToolbar() {
        getActivity().invalidateOptionsMenu();
    }


    private CountDownTimer loadingWaitCounter = new CountDownTimer(30000, 5000) {
        @Override
        public void onTick(long l) {
            if (!isLoading) {
                cancel();
            }
        }

        @Override
        public void onFinish() {
            if (!isLoading) {
                return;
            }
            if(isTranslationOn && translateWebview != null) {
                translateWebview.loadUrl("about:blank");
            }
            showErrorPage();
        }
    };
}
