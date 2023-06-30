package com.mshevchenko.stock_objects;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Group {

    private int groupId;
    private String name;
    private String description;

}
