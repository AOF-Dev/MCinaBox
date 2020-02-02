package com.aof.mcinabox.jsonUtils;

import java.text.AttributedCharacterIterator;
import java.util.List;

public class AnaliesVersionManifestJson {
    //解析version_manifest.

    //定义一个List用于存放数据
    private List<AttributedCharacterIterator.Attribute> list;

    public List<AttributedCharacterIterator.Attribute> getList() {
        return list;
    }
    public void setList(List<AttributedCharacterIterator.Attribute> list) {
        this.list = list;
    }
}
