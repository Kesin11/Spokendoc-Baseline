#!/bin/sh
# ChaSen(+Unidic)を使って名詞のみを取得するサンプル
# 形態素解析スクリプトは、1行に1単語表示されるフォーマットである必要がある
# javaからサブプロセスを呼び出して使用するので、各種パスの記述はフルパスにする必要がある

# ChaSenのオプション
## -r 辞書（例: unidic）のchasenrcへのパス
## -i 入力の文字コードをutf-8に指定
## -F ipadicと同じ出力フォーマット 出現形/ヨミ/基本形/品詞/活用型/活用形

# perlコマンドのオプション
## -n 1行ずつ処理
## -l 強制改行
## -e コマンドライン中の文字列をスクリプトとして認識させる

CHASEN=`which chasen`
# Eclipseで実行するときは`which mecab`でパスを取得できないので、フルパスを書く必要がある
# CHASEN=/opt/local/bin/chasen

RC_PATH=/opt/local/lib/chasen/dic/unidic/chasenrc
$CHASEN -r $RC_PATH -iw -F "%m\\t%y\\t%M\\t%U(%P-)\\t%T \\t%F \\n" | grep "名詞" | perl -nle 's/\t.*// and print'

#実行例
# $ echo "音声ドキュメント検索について" | sh chasen.sh
# 音声
# ドキュメント
# 検索
