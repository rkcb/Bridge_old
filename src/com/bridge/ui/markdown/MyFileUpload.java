package com.bridge.ui.markdown;

import org.vaadin.easyuploads.UploadField;

@SuppressWarnings("serial")
public class MyFileUpload extends UploadField {

    private MyFileFac factory;

    public MyFileUpload(String path) {
        super(StorageMode.FILE);
        factory = new MyFileFac(path);
    }

    @Override
    protected String getDisplayDetails() {

        StringBuilder sb = new StringBuilder();
        sb.append("File: ");
        sb.append(getLastFileName());
        sb.append("</br> <em>");
        return sb.toString();
    }

    @Override
    public String getLastFileName() {
        return super.getLastFileName();
    }

    @Override
    public MyFileFac getFileFactory() {
        return factory;
    }
}
