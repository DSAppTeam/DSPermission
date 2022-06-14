## 一款支持注入权限使用说明的权限申请库，基于RxPermission

README: [English](https://github.com/DSAppTeam/DSPermission/blob/master/README.md) | [中文](https://github.com/DSAppTeam/DSPermission/blob/master/README-ZH.md)

### 简介

### 支持功能
* 支持单个/多个运行时权限申请
* 支持安装未知应用权限申请
* 支持跳转权限设置页
* 支持权限使用说明视图注入

### 配置
1. 在项目根目录下的build.gradle添加 JitPack 仓库

   ```
    allprojects {
        repositories {
            ...
            maven { url 'https://jitpack.io' }
        }
    }
   ```
2. 在 **app** 目录下的build.gradle中添加依赖
    ```
    dependencies {
	        implementation 'com.github.DSAppTeam:DSPermission:v1.0.0'
	}
    ```


### 如何使用
示例：
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
                        //请求安装应用程序、相机以及麦克风权限
                        .addPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .addPermission(Manifest.permission.CAMERA)
                        .addPermission(Manifest.permission.RECORD_AUDIO)
                        .build()
        );
}
```

效果：

<img src="https://img-blog.csdnimg.cn/6697bf45808a4e7897822234f3200718.png" alt="申请相机权限" align=center />

<img src="https://img-blog.csdnimg.cn/43fab92a1ba04cffa496d04cfa5bb3ee.png" alt="申请麦克风权限" align=center />

<img src="https://img-blog.csdnimg.cn/b39acca1d7304e449b3e1466f969f940.png" alt="申请安装应用程序权限" align=center />

## TODO
* 支持自定义视图

## 许可证
Apache 2.0. 有关详细信息，请参阅 [License](https://github.com/DSAppTeam/DSPermission/blob/master/LICENSE) 。

## 欢迎提需要支持的功能及issue
