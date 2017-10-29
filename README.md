# PersianHorizontalExpCalendar

This library offers a customizable and expandable horizontal Persian calendar widget for Android 4.1 (Jelly Bean) (API 16) +.

## Screenshots
![screenshot ](./screenshot/gif01.gif)

## Usage

### Download

1) Add JitPack repository in your root `build.gradle` at the end of `repositories` :

```java

allprojects {
  repositories {
      ...
      maven { url 'https://jitpack.io' }
  }
}

```
 
2) Add dependency to your root `build.gradle` :

```java
dependencies {
  ...
  compile 'com.github.mohamadian:PersianHorizontalExpCalendar:1.4.1'
}
```

### Call JodaTimeAndroid.init()
You must initialize the library before using it by calling `JodaTimeAndroid.init()`. I suggest putting this code in `Application.onCreate()` :

```java
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
```

and use `MyApplication` class in `AndroidManifest.xml` like this:
```java
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.mohamadian.testapp">

    <application
        android:name=".MyApplication"
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
    </application>
</manifest>
```

### How to 

### Credits 
This libary is based on [HorizontalExpandableCalendar-Android](https://github.com/sulo61/HorizontalExpandableCalendar-Android) and [PersianJodaTime](https://github.com/mohamadian/PersianJodaTime).

### Thanks
Special thanks to [MikeSu](https://github.com/sulo61) and [zubinkavarana](https://github.com/zubinkavarana).
