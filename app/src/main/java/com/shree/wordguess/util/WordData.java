package com.shree.wordguess.util;

import java.util.List;


public class WordData {
    private int category;
    private List<Word> words;

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }


    public static class Word {
        private long id;
        private String name;
        private String type;
        private String desc;
        private int category;
        private String translatedValue;
        private String souceLang;
        private boolean isFavourite;

        public long getId() {
            return id;
        }

        public void setId(long id) {
            this.id = id;
        }

        public boolean isFavourite() {
            return isFavourite;
        }

        public void setFavourite(boolean favourite) {
            isFavourite = favourite;
        }

        public int getCategory() {
            return category;
        }

        public void setCategory(int category) {
            this.category = category;
        }

        public String getTranslatedValue() {
            return translatedValue;
        }

        public void setTranslatedValue(String translatedValue) {
            this.translatedValue = translatedValue;
        }

        public String getSouceLang() {
            return souceLang;
        }

        public void setSouceLang(String souceLang) {
            this.souceLang = souceLang;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            desc = sentenceCapitalize(desc);
            this.desc = desc;
        }

        private String sentenceCapitalize(String text) {
            if (text == null || text.trim().length() == 0) {
                return  null;
            }

            text = text.trim();
            int pos = 0;
            boolean capitalize = true;
            StringBuilder sb = new StringBuilder(text);
            while (pos < sb.length()) {
                if (sb.charAt(pos) == '.') {
                    capitalize = true;
                } else if (capitalize && !Character.isWhitespace(sb.charAt(pos))) {
                    sb.setCharAt(pos, Character.toUpperCase(sb.charAt(pos)));
                    capitalize = false;
                }
                pos++;
            }
           return sb.toString();
        }
    }
}
