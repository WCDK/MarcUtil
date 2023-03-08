package com.wcdk.marcutil.Bibframe;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class BibAttribute {
    String name;
    String prefix;
    String value;
}
