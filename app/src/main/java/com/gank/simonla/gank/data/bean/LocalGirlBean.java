package com.gank.simonla.gank.data.bean;

/**
 * Created by simonla on 2016/7/4.
 * Have a good day.
 */
public class LocalGirlBean implements Girl {

    private String _id;
    private String url;
    private String desc;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public String getId() {
        return _id;
    }

    @Override
    public String getDesc() {
        return desc;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setId(String id) {
        this._id = id;
    }

    @Override
    public void setDesc(String desc) {
        this.desc = desc;
    }

}
