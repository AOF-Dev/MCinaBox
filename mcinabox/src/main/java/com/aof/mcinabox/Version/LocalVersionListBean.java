package com.aof.mcinabox.Version;

import com.aof.mcinabox.R;

public class LocalVersionListBean {
    private String version_Id;
    private int version_image;

    public LocalVersionListBean(){
        this.version_Id = "1.7.10"; //仅作为文本填充，无实际意义。
        this.version_image = R.drawable.ic_extension_black_24dp; //作为自定义图标的一个预留实例变量
    }

    public String getVersion_Id() { return version_Id;}
    public void setVersion_Id(String version_Id) { this.version_Id = version_Id; }
    public int getVersion_image() { return version_image; }
    public void setVersion_image(int version_image) { this.version_image = version_image; }
}
