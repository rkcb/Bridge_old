package com.bridge.ui.markdown;

import java.io.File;

import org.vaadin.easyuploads.FileFactory;

public class MyFileFac implements FileFactory {
	private String fileName = null;
	private String mimeType = null;
	private String path = null;
	private File file = null;
	
	public MyFileFac(String path) {
		this.path = path;
		if (path == null) throw new NullPointerException("Path cannot be null");
	}
	
	@Override
	public File createFile(String fileName, String mimeType) {
            File f = new File(path+fileName);
            this.fileName = fileName;
            this.mimeType = mimeType;
            this.file = f;
            return f; 
	}
	
	public String getLastFileName() { return fileName; }
	public String getLastMimeType() { return mimeType; }
	public File getLastFile() { return file; }

}
