# Note

## HeartbeatService

- 1秒周期CDIイベント発行。間隔で処理を行うサービスにTick提供を行う。
- 開始時にRESET CDIイベント発行。
  1秒周期蓄積者はカウントアップをリセットする機会が得られる。
- イベント受信側はRequestContextが有効化されており、RequestScopeクラスを使用可能。
  スコープはイベント発行毎に閉じられる。
