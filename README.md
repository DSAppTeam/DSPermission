## A permission application library that supports injection permission instructions, based on RxPermission

README: [English](https://github.com/DSAppTeam/DSPermission/blob/master/README.md) | [中文](https://github.com/DSAppTeam/DSPermission/blob/master/README-ZH.md)

### Introduction

### Function
* Single/multiple runtime permission requests are supported 
* The permission for installing unknown applications is supported 
* Jump to the permission setting page is supported 
* Permission usage instructions view injection is supported

### Setup
1. Add the JitPack repository to your build.gradle in the project root path

   ```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
   ```
2. add the dependency to your build.gradle in the module path
    ```
    dependencies {
	        implementation 'com.github.DSAppTeam:DSPermission:v1.0.0'
	}
    ```


### Usage
Sample：
```
PermissionManager.get()
        .inject(context)
        .request(new ResultCall() {
                     @Override
                     public void granted() {
                         Toast.makeText(context, "granted", Toast.LENGTH_LONG).show();
                     }

                     @Override
                     public void denied(boolean never) {
                     	 //nerver : 是否永不询问
                         Toast.makeText(context, "denied " + never, Toast.LENGTH_LONG).show();
                     }
                 }, new PermissionConfig.Builder()
                 //Request the permission to install the application, use the camera and microphone
                        .addPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .addPermission(Manifest.permission.CAMERA)
                        .addPermission(Manifest.permission.RECORD_AUDIO)
                        .build()
        );
}

```

Effect：

<img src="https://img-blog.csdnimg.cn/6697bf45808a4e7897822234f3200718.png" alt="申请相机权限" width = "152" height = "304.25" align=center />

<img src="https://img-blog.csdnimg.cn/43fab92a1ba04cffa496d04cfa5bb3ee.png" alt="申请麦克风权限" width = "152" height = "304.25" align=center />

<img src="https://img-blog.csdnimg.cn/b39acca1d7304e449b3e1466f969f940.png" alt="申请安装应用程序权限" width = "152" height = "304.25" align=center />

## TODO
* custom views is supported

## License
Apache 2.0. See the [License](https://github.com/DSAppTeam/DSPermission/blob/master/LICENSE)  for details.

## Welcome to mention the functions and issues what you need
