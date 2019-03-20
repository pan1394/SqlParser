package com.link.test;

import java.io.Serializable;

public class TableEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private String name;
    private String logiccName;
    private String dataType;
    private String nullAble;
    private String defaultSet;
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLogiccName() {
        return logiccName;
    }

    public void setLogiccName(String logiccName) {
        this.logiccName = logiccName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getNullAble() {
        return nullAble;
    }

    public void setNullAble(String nullAble) {
        this.nullAble = nullAble;
    }

    public String getDefaultSet() {
        return defaultSet;
    }

    public void setDefaultSet(String defaultSet) {
        this.defaultSet = defaultSet;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
