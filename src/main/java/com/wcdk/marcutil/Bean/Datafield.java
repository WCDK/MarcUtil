package com.wcdk.marcutil.Bean;

import com.wcdk.marcutil.RepeatKeyMap;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Datafield {
   private String tag = "";
   private String value = "";
   private String index1 = " ";
   private String index2 = " ";
   private RepeatKeyMap<String,String> subfields = new RepeatKeyMap();
}
