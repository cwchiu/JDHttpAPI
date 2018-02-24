fork from [nemec/JDHttpAPI: JDownloader2 plugin with a local HTTP API to add new files for download](https://github.com/nemec/JDHttpAPI)


# Extension: [nemec/JDHttpAPI: JDownloader2 plugin with a local HTTP API to add new files for download](https://github.com/nemec/JDHttpAPI)
* 內嵌 Jetty HTTP server
* 提供 HTTP API 新增連結

## 安裝
1. https://github.com/nemec/JDHttpAPI/releases 下載 JDHttpAPI.jar
2. jar 放在 extensions 
3. tmp/extensioncache/extensionInfos.json 加入如下片段, jarPath 依據安裝環境調整

```
{
  "settings" : true,
  "configInterface" : "org.jdownloader.extensions.httpAPI.HttpAPIConfig",
  "quickToggle" : false,
  "headlessRunnable" : true,
  "description" : "Http API.",
  "lng" : "en_US",
  "iconPath" : "folder_add",
  "linuxRunnable" : true,
  "macRunnable" : true,
  "name" : "HTTP API",
  "version" : -1,
  "windowsRunnable" : true,
  "classname" : "org.jdownloader.extensions.httpAPI.HttpAPIExtension",
  "jarPath" : "/absolute/path/to/jd2/extensions/JDHttpApi.jar"
}
```
4. update/versioninfo/JD/extensions.installed.json 新增 "jdhttpapi"
5. 啟動 JDownloader
6. 設定中啟用 JDHttpAPI

## 編譯打包
1. IDEA 開啟 ext-jdhttpapi
2. Build | Build Artifacts | Rebuild
3. 輸出 out\artifacts\JDHttpApi_jar\JDHttpApi.jar

## 原始碼目錄結構

```
/.idea ; IDEA 配置
/lib ; JDownloader 依賴包
/src/main/java ; 原始碼
    /META-INF
    /org/jdownloader/extensions/httpAPI
        /handlers
            - AjaxHandler.java
            - AuthorizationHandler.java ; 繼承 AbstractHandler 實作 basic 認證
            - BaseHandler.java ; 繼承 AbstractHandler 的基礎 jetty server http handler, 路由定義
            - JDServerGETHandler.java
            - JDServerPOSTHandler.java
        /models ; model 定義
            - AddLinkRequest.java
            - AddLinkResponse.java
            - ErrorResponse.java
        - CFG_HTTPAPI.java ; 讀取配置
        - HttpAPIConfig.java ; 配置 UI 實作
        - HttpAPIConfigPanel.java
        - HttpAPIExtension.java ; 擴展核心
        - HttpAPITranslation.java ; 語系轉換
        - JDLinkController.java  ; 實作 LinkController 介面, 透過 LinkCollector 新增連結
        - LinkController.java ; LinkController 介面
        - ParseException.java ; 自訂例外

```

## HttpAPIExtension.java
* 繼承 AbstractExtension
* 初始配置 UI
* 啟動/停止 jetty server