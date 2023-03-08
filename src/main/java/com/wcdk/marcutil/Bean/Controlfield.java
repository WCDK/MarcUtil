package com.wcdk.marcutil.Bean;

import lombok.Data;

@Data
public class Controlfield {
    private String tag = "";
    private String value = "";
    /** marc格式数据**/
    private String valueSource= "";

    public Controlfield(String tag, String value) {
        this.tag = tag;
        this.value = value;
    }

    public Controlfield() {
    }
}
