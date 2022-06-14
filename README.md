# 一款支持注入权限使用说明的权限申请库，基于RxPermission

## 支持功能
* 支持单个/多个运行时权限申请
* 支持安装未知应用权限申请
* 支持跳转权限设置页
* 支持权限使用说明视图注入

## 如何使用
```
GLPermissionManager.get()
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
                        .addPermission(Manifest.permission.REQUEST_INSTALL_PACKAGES)
                        .addPermission(Manifest.permission.CAMERA)
                        .addPermission(Manifest.permission.RECORD_AUDIO)
                        .build()
        );
}
```

## 效果预览

## TODO
* 支持自定义视图

## 欢迎提需要支持的功能及issue
