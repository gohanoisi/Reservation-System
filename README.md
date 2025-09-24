# Name
Reservation-System

## DEMO
<img width="550" height="279" alt="main" src="https://github.com/user-attachments/assets/229b3f7a-26e2-470f-8edf-0dfe85382a34" />
<img width="586" height="227" alt="reservation1" src="https://github.com/user-attachments/assets/b7fbc5a7-37c8-46a9-aab4-1080a9dbfd5e" />
<img width="676" height="279" alt="reservation2" src="https://github.com/user-attachments/assets/a49f93b9-9a84-4168-8ae8-bff1461ea1fc" />
<img width="600" height="1397" alt="シーケンス図" src="https://github.com/user-attachments/assets/69f614e5-32ec-429f-baca-68ada7d59b4d" />

## Features
- **ログイン/ログアウト**：ユーザIDとパスワードで認証（`LoginDialog`）。
- **教室概要表示**：選択した教室の説明・利用可能時間を表示（`getFacilityExplanation`）。
- **予約状況表示**：日付指定で予約一覧を表示（`getReservations`）。
- **新規予約**：教室・日付・開始/終了時刻を指定して予約登録（`ReservationDialog` → `makeReservation`）。
- **空き時間検索**：条件（教室/期間/曜日/開始・終了時刻）で空き時間を最大255件まで一覧（`SearchNoReservationDialog` → `searchNoReservation`）。
- **自己予約確認/キャンセル**：自分の予約を一覧表示し、個別にキャンセル（`SelfReservationsDialog` / `cancelReservation`）。
- **UI**：AWTによるシンプルなデスクトップGUI（`MainFrame`）。

## Requirement
- **Java 11 以上**（Java 8 でも可）
- **MySQL 8.0 以上**
- **MySQL Connector/J**（コードは `org.gjt.mm.mysql.Driver` を参照しているため、実行時はドライバをクラスパスに追加）
- **開発/実行想定**：Eclipse 2025（GUIアプリとして起動）
- 文字コード：DB接続URLは `useUnicode=true&characterEncoding=SJIS` を使用（必要に応じてUTF-8に変更可）

## Installation
1. リポジトリを取得  
   ```bash
   git clone https://github.com/gohanoisi/Reservation-System.git
   cd Reservation-System
   ```

2. **DB作成（例）**：最低限のスキーマ例です。実際の要件に合わせて調整してください。  
   ```sql
   CREATE DATABASE IF NOT EXISTS db_reservation DEFAULT CHARACTER SET utf8mb4;
   USE db_reservation;

   CREATE TABLE user (
     user_id   VARCHAR(64) PRIMARY KEY,
     password  VARCHAR(255) NOT NULL   -- 現行コードは平文照合。将来的にハッシュ化を推奨。
   );

   CREATE TABLE facility (
     facility_id  VARCHAR(32) PRIMARY KEY,
     explanation  TEXT NOT NULL,       -- 例: 「353講義室 座席数：120席 …」
     open_time    TIME NOT NULL,       -- 例: '08:30:00'
     close_time   TIME NOT NULL        -- 例: '21:00:00'
   );

   CREATE TABLE reservation (
     id          BIGINT AUTO_INCREMENT PRIMARY KEY,
     facility_id VARCHAR(32) NOT NULL,
     user_id     VARCHAR(64) NOT NULL,
     date        DATETIME NOT NULL,    -- 予約登録日時
     day         DATE NOT NULL,        -- 利用日（yyyy-MM-dd）
     start_time  TIME NOT NULL,
     end_time    TIME NOT NULL,
     CONSTRAINT fk_res_fac  FOREIGN KEY (facility_id) REFERENCES facility(facility_id),
     CONSTRAINT fk_res_user FOREIGN KEY (user_id)    REFERENCES user(user_id)
   );

   -- サンプルデータ
   INSERT INTO user(user_id, password) VALUES ('puser', '1234');
   INSERT INTO facility VALUES
     ('351', '351講義室 座席数：80席',  '08:30:00', '21:00:00'),
     ('353', '353講義室 座席数：120席', '08:30:00', '21:00:00');
   ```

3. **接続情報の設定**（`src/client_system/ReservationControl.java`）  
   ```java
   // ドライバ・URL・ユーザは環境に合わせて調整
   Class.forName("org.gjt.mm.mysql.Driver");
   String url = "jdbc:mysql://localhost?useUnicode=true&characterEncoding=SJIS";
   String sqlUserID = "puser";
   String sqlPassword = "1234";
   ```
   > **NOTE**: URLにDB名が含まれていないため、SQLは `db_reservation.テーブル名` で参照しています。環境により `jdbc:mysql://localhost/db_reservation?...` 形式に変更しても構いません。

4. **ドライバの配置**  
   - `mysql-connector-j-<version>.jar` をクラスパスに追加（Eclipseのビルド・パス設定／もしくは `-cp` オプション）。

## Usage
### Eclipseで起動（推奨）
- プロジェクトをインポート → `client_system.ReservationSystem` の `main` を実行。  
- 画面操作：  
  1. 上部の「ログイン」→ ユーザID/パスワード（例：`puser` / `1234`）  
  2. 「教室」プルダウンで施設を選択  
  3. 「教室概要」/「予約状況」を切り替えて確認  
  4. 「新規予約」で日付・時間帯を指定して登録（重複や時間外はエラー表示）  
  5. 「空き検索」で条件を設定し、空き時間候補を一覧表示  
  6. 「自己予約確認」から自身の予約一覧を確認・個別キャンセル

## Note
- **認証**：現行は平文パスワード照合です。**TODO: ハッシュ化 & 認証処理の統一（PreparedStatement化）**  
- **SQL組み立て**：一部で文字列連結によるSQLが存在。**TODO: プレースホルダへ統一**（SQLインジェクション対策）。  
- **facility_idの型**：コード内で引用符アリ/ナシの両方が見られます。**TODO: `VARCHAR` へ統一し、SQLをプレースホルダ化。**  
- **タイムゾーン/日付**：`Calendar`/`LocalDate` 混在。**TODO: `java.time` に統一**。  
- **文字コード**：接続URLはSJIS指定。環境によりUTF-8へ変更可。  
- **権限**：本番運用では最小権限ユーザを使用し、接続情報は環境変数や外部設定に移行。  
- **想定ユースケース**：大学内の教室を学生/教職員が予約・管理。学内ネットワーク上のDBで運用。  
- **既知の制限**：同時更新の競合制御や、予約最小単位（15分/5分刻み）等の業務ルールは簡易実装。**TODO: 仕様化とサーバ側整合性チェック**。

## Author
- **國政 蒼矢（東京国際工科専門職大学 / AI戦略コース）**  
- 開発体制：7人グループで開発を実施し、**UI班（4人）**と**プログラム班（3人）**に分かれて作業。仕様書を完成させた後、私はプログラム班として実装を担当しました。  
- 主な担当：自己予約確認機能・空き検索機能の実装、UI仕様書との整合性確認。  
- 経験ポイント：チーム内での役割分担、仕様書ベースの開発フロー、UI班との調整を通じて「仕様→設計→実装」の一連のプロセスを学習。  

