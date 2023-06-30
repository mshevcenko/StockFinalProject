package com.mshevchenko.stock_objects;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class Product {

    private int productId;
    private int groupId;
    private String name;
    private String description;
    private String producer;
    private double price;
    private int quantity;

}
