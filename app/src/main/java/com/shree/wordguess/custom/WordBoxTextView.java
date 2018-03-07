package com.shree.wordguess.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;

import com.shree.wordguess.util.Utils;

import java.util.ArrayList;

/**
 * Custom view to draw word in boxes
 */

public class WordBoxTextView extends View {

	private int borderColor = Color.BLACK;
	private int textColor = Color.WHITE;
	private int textSize = sp(30);

	private Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	private Rect textBgRect = new Rect();

	
	private Paint bgPaintRed = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	private Paint bgPaintGreen = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	private Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
	
	private int successBackgroundColor = Utils.primaryColorDark; //Color.parseColor("#006600");
	private int failureBackgroundColor = Color.DKGRAY;//Color.parseColor("#CC0000");
	private int whiteBackgroundColor = Color.WHITE;
	
	
	private String word = "COMMON FROG";
	private String userRevealed = "APEISNZPLK";
	private boolean shallReveal = true;
	private ArrayList<WordPositionDetails> wordSplitList = null;

	int marginX = 10;
	int marginY = 5;
	int rowMargin = 15;
	
	int width;
	int height;

	int boxWidth ;
	int boxHeight;
	
	int maxWidth = 0;
	int maxHeight = 0;
	
	class WordPositionDetails {
		private String word;
		private int positionX;
		private int positionY;
		private boolean isPartialFromPrevious;
		
		WordPositionDetails(String word, int positionX, int positionY, boolean isPartialFromPrevious) {
			this.word = word;
			this.positionX = positionX;
			this.positionY = positionY;
			this.isPartialFromPrevious= isPartialFromPrevious;
		}
		
		public String getWord() {
			return this.word;
		}
		
		public int getPositionX() {
			return this.positionX;
		}
		
		public int getPositionY() {
			return this.positionY;
		}
		
		public boolean isPartialFromPrevious() {
			return this.isPartialFromPrevious;
		}
	}
	

	public WordBoxTextView(Context context) {
	    this(context, null);
	}

	public WordBoxTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * Drawing words in boxes
	 * @param word Word to be drawn
	 * @param revealedChars Revealed characters with different color
	 * @param shallReveal Reveal status of the word
	 */
	public void drawWordBlocks(String word, String revealedChars, boolean shallReveal) {
		try {
			word = word.toUpperCase();
		} catch (Exception e) {
			e.printStackTrace();
		}
		this.word = word;
		this.userRevealed = revealedChars;
		this.shallReveal = shallReveal;
		maxWidth = 0;
		maxHeight = 0;

		calculateTheWordPositions();
		invalidate();
	}

	private void init(Context context) {
	    textPaint.setColor(textColor);
	    textPaint.setTextAlign(Align.CENTER);
	    textPaint.setTextSize(textSize);
	    textPaint.setFakeBoldText(true);
	    
	    bgPaintGreen.setColor(successBackgroundColor);
	    bgPaintGreen.setStyle(Style.FILL);
	    
	    bgPaintRed.setColor(failureBackgroundColor);
	    bgPaintRed.setStyle(Style.FILL);
	    
	    borderPaint.setColor(borderColor);
	    borderPaint.setStyle(Style.STROKE);
	    borderPaint.setStrokeWidth(2);
	    
	    DisplayMetrics metrics = context.getResources().getDisplayMetrics();
	    width = metrics.widthPixels-20;
		height = metrics.heightPixels-20;

	    boxWidth = boxHeight = (width)/10;
	    calculateTheWordPositions();
	}

	/**
	 * Calculating the position of the each character
	 */
	public void calculateTheWordPositions() {
		wordSplitList = new ArrayList<>();
		String[] splittedWords = word.split(" ");
		
		int X = 0;

		int positionX = marginX ;
		int positionY = marginY;
		
		WordPositionDetails wordPositionDetails;
		for(int i=0;i<splittedWords.length;i++) {
			String word = splittedWords[i];
			int wordLength = word.length()*boxWidth;
			if(wordLength <= (width-X)) {
				
				if(maxWidth<(positionX+wordLength)) {
					maxWidth = positionX+wordLength;
				}
				
				wordPositionDetails = new WordPositionDetails(word, positionX, positionY, false);
				wordSplitList.add(wordPositionDetails);
				X+= (wordLength +20);
				positionX += (wordLength +20);
			} else {
				if(wordLength > width) {
					if(X>0) {
						X = 0;
						positionX = X + marginX;
						positionY = positionY + boxHeight + rowMargin;
					}
					
					int charsCanFit = (width-X)/boxWidth;
					String partialString1 = word.substring(0, charsCanFit);
					if(maxWidth< positionX+(partialString1.length()*boxWidth)) {
						maxWidth = positionX+(partialString1.length()*boxWidth);
					}
					
					wordPositionDetails = new WordPositionDetails(partialString1, positionX, positionY, false);
					wordSplitList.add(wordPositionDetails);
					X = 0;
					positionX = X+marginX;
					positionY = positionY+boxHeight+rowMargin;
					
					String partialString2 = word.substring(charsCanFit,word.length());
					if(maxWidth< positionX+(partialString2.length()*boxWidth)) {
						maxWidth = positionX+(partialString2.length()*boxWidth);
					}
					wordPositionDetails = new WordPositionDetails(partialString2, positionX, positionY, true);
					wordSplitList.add(wordPositionDetails);
					X+= ((word.length()-charsCanFit)*boxWidth +20);
					positionX+=X;
					
				} else {
					X = 0;
					positionX = X+marginX;
					positionY = positionY+boxHeight+rowMargin;
					
					if(maxWidth<(positionX+wordLength)) {
						maxWidth = positionX+wordLength;
					}
					
					wordPositionDetails = new WordPositionDetails(word, positionX, positionY, false);
					wordSplitList.add(wordPositionDetails);
					X+= (wordLength +20);
					positionX += (wordLength +20);
				}
			}
		}
		if(wordSplitList.size()>0) {
			maxHeight = wordSplitList.get(wordSplitList.size()-1).getPositionY() + boxWidth;
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
	    super.onDraw(canvas);
	    
	    canvas.drawColor(whiteBackgroundColor);
	    canvas.save();
	    drawWords(canvas);
	    canvas.restore();
	}

	/**
	 * Drawing words on canvas, using the pre-calculated positions
	 * @param canvas
	 */
	private void drawWords(Canvas canvas) {
		int extraWidthSpace = width - maxWidth;
		if (extraWidthSpace > 0) {
			extraWidthSpace = extraWidthSpace/2;
		} else {
			extraWidthSpace = 0;
		}

		int extraHeightSpace = 0;
		//height - maxHeight;
		if (extraHeightSpace > 0) {
			extraHeightSpace = extraHeightSpace/2;
		} else {
			extraHeightSpace = 0;
		}


		for(int i=0;i<wordSplitList.size();i++) {
			WordPositionDetails wordPositionDetails = wordSplitList.get(i);
			String word = wordPositionDetails.getWord();
			
			int left = wordPositionDetails.getPositionX() + extraWidthSpace;
		    int top =  wordPositionDetails.getPositionY() + extraHeightSpace;
		    int right = left + boxWidth;
		    int bottom = top + boxHeight;
		    boolean isPartialWord = wordPositionDetails.isPartialFromPrevious();
			
		    if(isPartialWord) {
		    	textBgRect.set(left-1, top-rowMargin+1, left+word.length()*boxWidth+1, top-1);
		    	Paint shadowPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
		    	shadowPaint.setStyle(Style.FILL);
		    	shadowPaint.setColor(Color.DKGRAY);		    	
		    	canvas.drawRect(textBgRect, shadowPaint);
		    }
		    
			for(int j=0;j<word.length();j++) {
				textBgRect.set(left, top, right, bottom);
				
				String character = String.valueOf(word.charAt(j));	
				
				//Filling the color based on the character's status
				Paint fillPaint = getCharBackgroundPaint(character);
				if(fillPaint!=null) {
					canvas.drawRect(textBgRect, fillPaint);
				}
				
				// Drawing border
		    	canvas.drawRect(textBgRect, borderPaint);
		    
		    	// Showing character
		    	if(fillPaint!=null) {
		    		drawChar(canvas,character,top,left);
		    	}
		    	
		    	left += boxWidth;
		    	right = left+boxWidth;
			}
			
		}
	}
	
	private Paint getCharBackgroundPaint(String character) {
		if(userRevealed.contains(character)) {
			return bgPaintGreen;
		} else if(shallReveal) {
			return bgPaintRed;
		} 
		return null;
	}
	
	private void drawChar(Canvas canvas, String character, int top, int left) {
		int bgCenterY = top + boxHeight / 2;

		FontMetrics metric = textPaint.getFontMetrics();
		int textHeight = (int) Math.ceil(metric.descent - metric.ascent);
		int y = (int) (bgCenterY + textHeight / 2 - metric.descent);

		int x = left + boxWidth / 2;
		canvas.drawText(character, x, y, textPaint);
	}


	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	    super.onMeasure(widthMeasureSpec, heightMeasureSpec);

	    setMeasuredDimension(dp(maxWidth+marginX), dp(maxHeight+10));
	}

	private int dp(int value) {
	    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
	}

	private int sp(int value) {
	    return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
	}

}
