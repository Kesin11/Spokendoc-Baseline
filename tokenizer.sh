#!/bin/sh
# ChaSenを使って入力文を形態素解析し1形態素1行で表示
# perlコマンドのオプション
# -n 1行ずつ処理
# -l 強制改行
# -e コマンドライン中の文字列をスクリプトとして認識させる
# chasenはeucで形態素解析するので,文字コードを一旦eucに変換してutf8に戻す.
# javaのシェルはmecabのパスを知らないので、フルパスを指定する必要がある

MECAB=/usr/local/bin/mecab
$MECAB | grep "名詞" | perl -nle 's/\t.*// and print'
#chasen -i w -F "%m\t%H\n" | grep "名詞" | perl -nle 's/\t.*// and print'
#nkf -e | chasen | nkf -w | perl -nle 's/\t.*// and print'
