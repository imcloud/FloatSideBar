package com.aceegg.sidebar

/**
 * 可传入实现此接口的对象数组做索引
 * Created by jinwenxiu on 2017/12/1.
 */
interface IndexAble {
    /**
     * 定义你的对象对应的索引
     */
    fun indexOfThisObject(): String
}