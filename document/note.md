# Note

いまはValidationのあり方を探求したい。
国際化、パラメータ名、値、基準値
デフォルトではどうなっているのか

Validationのdefaultを取得しているのが気になる。果たして使用しているValidationと同じなのか。
今は同じだろう。カスタムValidationを導入したら違うかもしれない。

とりあえず、JAX-RSのアクセス経路は確保した。
つぎは、beanを受けるAPI作成かな。


## 世界観

- イミュータブル
  - コンストラクタインジェクション
  - ビルダー
- 短命
  - RequestScoped
- 不完全は存在しない
- データは必ず文字表現を持つ
- 不揮発データはシリアライズ形式をデータ自体が定義する
- スコープの最小化を優先する

## Rule

- 固有名詞はItaric
- packageのjavadocにはauther/version書かない
- Implのjavadocには@hidden

## API

http://localhost:8080/safi/apis/tests/ping

## WEBAPI

`| HTTP client | -- [HTTP] --> | App server |`

`| Jakarta RESTful Web Services | -- rooting --> | Java method ( var bean ) |`

API引数の制約違反をレスポンスとして返すため、あえて `bean` に `@Valid` を **付与しない** 。
代わりに手動でValidateを手作業で行い、制約違反があれば例外ではなくレスポンスを作って返すことにする。

@ValidはConstraintViolationExceptionを発生させる。この例外は内部の処理でも発生する上に発生元を正確に特定できない。
API以外でのパラメータ不正は外部に伝えたくないので、区別をつける必要がある。
区別の手段として、API引数に@Validを付与せず手動検査する方式を採用する。

レスポンスの言語はEnglishだが、勉強のためにローカライズにも対応させる。
ローカライズはHTTPヘッダで示された言語に従い、Jakarta Validationの機能で実現。

## HeartbeatService

- 1秒周期CDIイベント発行。間隔で処理を行うサービスにTick提供を行う。
- 開始時にRESET CDIイベント発行。
  1秒周期蓄積者はカウントアップをリセットする機会が得られる。
- イベント受信側はRequestContextが有効化されており、RequestScopeクラスを使用可能。
  スコープはイベント発行毎に閉じられる。

## ConfigService

- アプリケーション設定を提供する
- microprofile.configを利用し、JNDI/環境変数/システムプロパティから値を提供する。
  それにより、一時的/環境依存の設定提供を実装修正なしで実現する。

## ValidationMessageLocalizer

