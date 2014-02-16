#!/bin/sh
# MeCabを使って名詞のみを取得するサンプル
# 形態素解析スクリプトは、1行に1単語表示されるフォーマットである必要がある
# javaからサブプロセスを呼び出して使用するので、各種パスの記述はフルパスにする必要がある

# perlコマンドのオプション
# -n 1行ずつ処理
# -l 強制改行
# -e コマンドライン中の文字列をスクリプトとして認識させる
# chasenはeucで形態素解析するので,文字コードを一旦eucに変換してutf8に戻す.
# javaのシェルはmecabのパスを知らないので、フルパスを指定する必要がある

MECAB=`which mecab`
# Eclipseで実行するときは`which mecab`でパスを取得できないので、フルパスを書く必要がある
# MECAB=/usr/local/bin/mecab

$MECAB | grep "名詞" | perl -nle 's/\t.*// and print'

#実行例
# $ echo "音声ドキュメント検索について" | sh mecab.sh
# 音声
# ドキュメント
# 検索
