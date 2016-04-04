package com.gank.simonla.gank.bean;

import java.util.List;

/**
 * Created by asus on 2016/4/3.
 */

/*    "_id": "56f8ac1367765933d8be9154",
            "_ns": "ganhuo",
            "createdAt": "2016-03-28T11:59:15.439Z",
            "desc": "3.28",
            "publishedAt": "2016-03-29T11:56:01.215Z",
            "source": "chrome",
            "type": "福利",
            "url": "http://ww3.sinaimg.cn/large/7a8aed7bjw1f2cfxa9joaj20f00fzwg2.jpg",
            "used": true,
            "who": "张涵宇"*/
/*{
    "_id": "56cc6d29421aa95caa708118",
        "_ns": "ganhuo",
        "createdAt": "2016-01-07T01:39:07.601Z",
        "desc": "1.7",
        "publishedAt": "2016-01-07T03:36:25.265Z",
        "type": "福利",
        "url": "http://ww4.sinaimg.cn/large/7a8aed7bjw1ezqon28qrzj20h80pamze.jpg",
        "used": true,
        "who": "张涵宇"
},*/
public class Girls {

    private boolean error;

    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean {
        private String _id;
        private String _ns;
        private String createdAt;
        private String desc;
        private String publishedAt;
        private String type;
        private String url;
        private boolean used;
        private String who;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String get_ns() {
            return _ns;
        }

        public void set_ns(String _ns) {
            this._ns = _ns;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }
    }
}
