package com.mshevchenko.stock_objects;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Pair<T, V> {

    private T first;
    private V second;

}
