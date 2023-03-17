# StarryZAH-Discord
星空之都貿易平台 Discord bot zAuctionHouse

由 zAuctionHouse-Discord 作者分支而來，且將系統訊息繁體中文化。

作者：Poyu39 `Discord:poyu#9239`

### 目前依賴項版本
- zAuctionHouseV3-API `3.1.2.0`
- paper-api `1.19.3-R0.1-SNAPSHOT`
- JDA `4.4.0`
- zTranslator `1.0.0.2`

# 如何設定插件

1. 您必須在開發者面板上創建一個新的<a href="https://discord.com/developers/applications"> APP</a>。
2. 檢查 Bot 是否具有以下權限：
   ![Discord screen](https://img.groupez.xyz/zauctionhouse/v3/discord/discord.png)
3. 下載插件的最新版本。
4. 將插件添加到伺服器上的``plugins``文件夾中。
5. 首次啟動伺服器將用作生成配置文件，然後關閉伺服器。
6. 通過添加 Bot 的``token``、發送消息的``channel ID``以及如果需要的話``gatewayIntents``來設定``config.json``文件。
7. 現在您可以重新啟動伺服器並使用插件。

## 訊息變數

- ``%seller%`` - 獲取賣家名稱
- ``%buyer%`` - 獲取買家名稱，僅用於編輯消息
- ``%price%`` - 獲取價格
- ``%currency%`` - 獲取貨幣
- ``%amount%`` - 獲取物品數量
- ``%material%`` - 獲取材料名稱（或使用<a href="https://groupez.dev/resources/ztranslator.230">zTranslator</a>替換為您的語言）
- ``%enchant%`` - 獲取附魔列表
