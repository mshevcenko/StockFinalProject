package com.mshevchenko.packet;

public abstract class Commands {

    public static final int UNKNOWN = 0;
    public static final int INSERT_GROUP = 1;
    public static final int INSERT_PRODUCT = 2;
    public static final int GET_GROUPS = 3;
    public static final int GET_PRODUCTS = 4;
    public static final int UPDATE_GROUP = 5;
    public static final int UPDATE_PRODUCT = 6;
    public static final int DELETE_GROUP = 7;
    public static final int DELETE_PRODUCT = 8;
    public static final int GET_GROUPS_BY_FILTER = 9;
    public static final int GET_PRODUCTS_BY_FILTER = 10;
    public static final int GET_GROUP_BY_ID = 11;
    public static final int GET_PRODUCT_BY_ID = 12;
    public static final int DELETE_GROUP_BY_ID = 13;
    public static final int DELETE_PRODUCT_BY_ID = 14;
    public static final int DELETE_GROUPS_BY_IDS = 15;
    public static final int DELETE_PRODUCTS_BY_IDS = 16;
    public static final int GET_PRODUCTS_INNER_JOIN_GROUPS = 17;
    public static final int GET_PRODUCTS_INNER_JOIN_GROUPS_BY_FILTER = 18;
    public static final int INCREASE_PRODUCT_QUANTITY = 19;
    public static final int INCREASE_PRODUCTS_QUANTITY = 20;
    public static final int DECREASE_PRODUCT_QUANTITY = 21;
    public static final int STOP = 22;

}
