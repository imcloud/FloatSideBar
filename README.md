# FloatSideBar

a sidebar like sony z1
仿索尼z1手机通讯录的侧边栏

# GIF图预览

![](https://github.com/imcloud/FloatSideBar/blob/master/sidebar.gif)

# 使用方式

### kotlin编写，所以必须支持kotlin

在项目/build.gradle
```
buildscript {
    ext.kotlin_version = '1.1.51'  // 或更新版本
    repositories {
        ...
    }
    dependencies {
        ...
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        ...
    }
}
```
### 引用库
在app/build.gradle

```
 implementation 'com.aceegg.sidebar:floatsidebar:1.0.2'
```
在layout里, 宽高必须match_parent
```
<com.aceegg.sidebar.FloatSideBar
    android:id="@+id/side_bar"
    android:layout_width="match_parent" // must be use match_parent
    android:layout_height="match_parent" // must be use match_parent
    app:index="@array/sidebar_index" // (option)
    />
```

默认的array/sidebar_index，你也可以自定义一组在xml里赋值给view
```
<resources>
    <string-array name="sidebar_index">
        <item>!</item>
        <item>A</item>
        <item>B</item>
        <item>C</item>
        <item>D</item>
        <item>E</item>
        <item>F</item>
        <item>G</item>
        <item>H</item>
        <item>I</item>
        <item>J</item>
        <item>K</item>
        <item>L</item>
        <item>M</item>
        <item>N</item>
        <item>O</item>
        <item>P</item>
        <item>Q</item>
        <item>R</item>
        <item>S</item>
        <item>T</item>
        <item>U</item>
        <item>V</item>
        <item>W</item>
        <item>X</item>
        <item>Y</item>
        <item>Z</item>
    </string-array>
</resources>
```
# License
-------
Copyright (C) 2017-2018 imcloud (aceegg.com)

FloatSideBar binaries and source code can be used according to the [Apache License, Version 2.0](LICENSE).
