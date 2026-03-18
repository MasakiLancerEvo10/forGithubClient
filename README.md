# GitHub Client for Android

A modern Android application that interacts with the GitHub API to display users and their details. Built with Jetpack Compose and Material Design 3.

## 🚀 主な機能 (Features)

- **ユーザー探索**: GitHub ユーザーを効率的なリスト形式で閲覧可能。
- **詳細プロフィール**: 特定のユーザーの豊富な情報を表示。
    - アバター画像、自己紹介 (Bio)、場所。
    - 統計情報 (フォロワー数、フォロー数、公開リポジトリ数)。
    - 所属企業やブログリンクの表示。
- **高度な並び替え**: ユーザーリストを動的にソート可能。
    - ID 番号順
    - 名前順 (アルファベット)
    - 公開リポジトリ数順
- **リッチなリスト表示**: 各ユーザーカードに以下の情報を集約。
    - ユーザーアバターとログイン名。
    - リポジトリ数のバッジ表示。
    - 右端に所属企業名を配置。
- **スムーズな UX**:
    - **カスタムロードアニメーション**: データ取得中に「GitHub の宇宙を走る車」の Canvas アニメーションを表示。
    - **エラーハンドリング**: ネットワークエラー時などに再試行 (Retry) 可能なエラー画面を表示。
    - **Material 3 Design**: Google の最新デザインガイドラインに準拠したクリーンでモダンな UI。

## 🛠 技術スタック (Technical Stack)

- **UI**: Jetpack Compose (Material 3)
- **Networking**: Retrofit 2 + Gson
- **Image Loading**: Coil
- **Concurrency**: Kotlin Coroutines & Flow
- **Architecture**: MVVM (Model-View-ViewModel)
- **Navigation**: Compose Navigation

## 📖 セットアップ (Setup)

1. このリポジトリをクローンします。
2. **Android Studio (Ladybug 以降推奨)** でプロジェクトを開きます。
3. Gradle Sync を行い、エミュレータまたは実機で `app` モジュールを実行します。

*注意: このアプリは GitHub のパブリック API を使用しています。短時間に多数のリクエストを行うと、API のレート制限がかかる場合があります。*

## 📈 今後の改善予定 (Future Improvements)

- [ ] **Paging 3**: 無限スクロールの実装によるパフォーマンス向上。
- [ ] **Hilt / Koin**: 依存関係注入 (DI) の導入によるテスタビリティの向上。
- [ ] **検索機能**: ユーザー名による直接検索機能の追加。
- [ ] **オフラインサポート**: Room データベースによるキャッシュの実装。
