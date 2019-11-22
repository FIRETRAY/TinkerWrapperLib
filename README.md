# Welcome to TinkerWrapper
[![Release Version](https://img.shields.io/badge/release-1.9.14-red.svg)](https://github.com/FIRETRAY/TinkerWrapperLib/releases)

## 概述
TinkerWrapper是基于Tencent Tinker热修复解决方案的包装工具，便于开发更快速地接入Tinker避，让开发者无需改造自己的Application即可接入Tinker。此外对补丁的下载、安装流程进行了封装，减少了开发者接入Tinker的成本

## 使用方法
在project的build.gradle中加入如下代码，引用TinkerWrapper的Gradle插件
```
buildscript {
    ......
    repositories {
        ......
        maven { url 'https://dl.bintray.com/rafeliu/test' }
    }
    dependencies {
        ......
        classpath 'org.inagora.tinkerwrapperplugin:tinkerwrapperplugin:1.2-beta'
    }
}
```

在app module中的build.gradle中加入以下代码
```
apply plugin: 'org.inagora.tinkerwrapperplugin'
.....
dependencies { 
    implementation 'org.inagora.tinkerwrapper:tinkerwrapper:1.4-beta'
}
```

在app module中加入Tinker的配置Extension
```
    tinkerPatch {
        /**
         * necessary，default 'null'
         * the old apk path, use to diff with the new apk to build
         * add apk from the build/bakApk
         */
        oldApk = "...."

        /**
         * 补丁输出位置
         * */
        outputFolder = "...."

        /**
         * optional，default 'false'
         * there are some cases we may get some warnings
         * if ignoreWarning is true, we would just assert the patch process
         * case 1: minSdkVersion is below 14, but you are using dexMode with raw.
         *         it must be crash when load.
         * case 2: newly added Android Component in AndroidManifest.xml,
         *         it must be crash when load.
         * case 3: loader classes in dex.loader{} are not keep in the main dex,
         *         it must be let tinker not work.
         * case 4: loader classes in dex.loader{} changes,
         *         loader classes is ues to load patch dex. it is useless to change them.
         *         it won't crash, but these changes can't effect. you may ignore it
         * case 5: resources.arsc has changed, but we don't use applyResourceMapping to build
         */
        ignoreWarning = false

        /**
         * optional，default 'true'
         * whether sign the patch file
         * if not, you must do yourself. otherwise it can't check success during the patch loading
         * we will use the sign config with your build type
         */
        useSign = true

        /**
         * optional，default 'true'
         * whether use tinker to build
         */
        tinkerEnable = true

        /**
         * Warning, applyMapping will affect the normal android build!
         * IgnoreWarning is true, but resources.arsc is changed, you should use applyResourceMapping mode to build the new apk, otherwise, it may be crash at some times
         */
        buildConfig {
            /**
             * 可选参数；在编译新的apk时候，我们希望通过保持旧apk的proguard混淆方式，
             * 从而减少补丁包的大小。这个只是推荐设置，不设置applyMapping也不会影响任何的assemble编译。
             */
            applyMapping = "${bakPath}/${VERSION_NAME}-mapping.txt"
            /**
             * 可选参数；在编译新的apk时候，我们希望通过旧apk的R.txt文件保持ResId的分配，
             * 这样不仅可以减少补丁包的大小，同时也避免由于ResId改变导致remote view异常。
             */
            applyResourceMapping = "${bakPath}/${VERSION_NAME}-R.txt"

            /**
             * necessary，default 'null'
             * 在运行过程中，我们需要验证基准apk包的tinkerId是否等于补丁包的tinkerId。
             * 这个是决定补丁包能运行在哪些基准包上面，一般来说我们可以使用git版本号、versionName等等。
             */
            tinkerId = base_apk_version

            /**
             * 如果我们有多个dex,编译补丁时可能会由于类的移动导致变更增多。
             * 若打开keepDexApply模式，补丁包将根据基准包的类分布来编译。
             */
            keepDexApply = false

            /**
             * 是否使用加固模式，仅仅将变更的类合成补丁。注意，这种模式仅仅可以用于加固应用中。
             */
            isProtectedApp = false
        }

        dex {
            /**
             * 只能是’raw’或者’jar’。
             * 对于’raw’模式，我们将会保持输入dex的格式。
             * 对于’jar’模式，我们将会把输入dex重新压缩封装到jar。如果你的minSdkVersion小于14，
             * 你必须选择‘jar’模式，而且它更省存储空间，但是验证md5时比’raw’模式耗时。默认我们并不会去校验md5,一般情况下选择jar模式即可。
             */
            dexMode = "jar"

            /**
             * necessary，default '[]'
             * what dexes in apk are expected to deal with tinkerPatch
             * it support * or ? pattern.
             */
            pattern = ["classes*.dex",
                       "assets/secondary-dex-?.jar"]
            /**
             * necessary，default '[]'
             * 这一项非常重要，它定义了哪些类在加载补丁包的时候会用到。这些类是通过Tinker无法修改的类，也是一定要放在main dex的类。
             * 这里需要定义的类有：
             * 1. 你自己定义的Application类；
             * 2. Tinker库中用于加载补丁包的部分类，即com.tencent.tinker.loader.*；
             * 3. 如果你自定义了TinkerLoader，需要将它以及它引用的所有类也加入loader中；
             * 4. 其他一些你不希望被更改的类，例如Sample中的BaseBuildInfo类。这里需要注意的是，这些类的直接引用类也需要加入到loader中。或者你需要将这个类变成非preverify。
             * 5. 使用1.7.6版本之后版本，参数1、2会自动填写。
             *
             */
            loader = []
        }

        lib {
            /**
             * optional，default '[]'
             * what library in apk are expected to deal with tinkerPatch
             * it support * or ? pattern.
             * for library in assets, we would just recover them in the patch directory
             * you can get them in TinkerLoadResult with Tinker
             */
            pattern = ["lib/*/*.so"]
        }

        res {
            /**
             * optional，default '[]'
             * what resource in apk are expected to deal with tinkerPatch
             * it support * or ? pattern.
             * you must include all your resources in apk here,
             * otherwise, they won't repack in the new apk resources.
             */
            pattern = ["res/*", "assets/*", "resources.arsc", "AndroidManifest.xml"]

            /**
             * optional，default '[]'
             * the resource file exclude patterns, ignore add, delete or modify resource change
             * it support * or ? pattern.
             * Warning, we can only use for files no relative with resources.arsc
             */
            ignoreChange = ["assets/sample_meta.txt"]

            /**
             * default 100kb
             * for modify resource, if it is larger than 'largeModSize'
             * we would like to use bsdiff algorithm to reduce patch file size
             */
            largeModSize = 100
        }

        packageConfig {
            configField("patchVersion", patch_version)
        }

        /**
         * if you don't use zipArtifact or path, we just use 7za to try
         */
        sevenZip {
            /**
             * optional，default '7za'
             * the 7zip artifact path, it will use the right 7za with your platform
             */
            zipArtifact = "com.tencent.mm:SevenZip:1.1.10"
            /**
             * optional，default '7za'
             * you can specify the 7za path yourself, it will overwrite the zipArtifact value
             */
            // path = "/usr/local/bin/7za"
        }
    }

```

开发需要对利用TinkerMgrProxy向TinkerWrapper注入以下信息：
1. setOnPatchInstalledListener，也就是补丁安装成功后的动作（可选）
2. 向TinkerWrapper提供最新版补丁的信息setPatchInfo，开发需要自行实现补丁与补丁元信息的服务端存储和获取
然后调用TinkerMgrProxy.startCheckAsync即可安装补丁

## 常见gradle命令

合成基准apk
```
./gradlew -Pbase_apk_version=1 clean app:assemble{$flavor}${buildType}
```

合成补丁
```
./gradlew -Ppatch_version=1 app:tinkerPatch{$flavor}${buildType}
```

## 注意：
1. 基准包ID可以为打包分支的HEAD commitID，推荐