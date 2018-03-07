package com.shree.wordguess.util;

import java.util.List;

/**
 * Created by SrinivasDonapati on 2/8/2018.
 */

public class AppData {
    private int adInterval = 0;
    private String translateJs = null;
    private String translateUrl = null;
    private List<Language> languages;
    private List<Category> categories;
    private List<WordFile> files;

    public int getAdInterval() {
        return adInterval;
    }

    public void setAdInterval(int adInterval) {
        this.adInterval = adInterval;
    }

    public String getTranslateJs() {
        return translateJs;
    }

    public void setTranslateJs(String translateJs) {
        this.translateJs = translateJs;
    }

    public String getTranslateUrl() {
        return translateUrl;
    }

    public void setTranslateUrl(String translateUrl) {
        this.translateUrl = translateUrl;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<WordFile> getFiles() {
        return files;
    }

    public void setFiles(List<WordFile> files) {
        this.files = files;
    }

    public class Language {
        private String name;
        private String code;

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Category {
        private int id;
        private String name;
        private String desc;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }
    }

    public class WordFile {
        private String name;
        private String url;
        private int version;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }
    }
}
