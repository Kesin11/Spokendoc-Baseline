*SpokenDoc-Baseline
[NTCIR](http://research.nii.ac.jp/ntcir/index-ja.html)の[SpokenDoc](http://research.nii.ac.jp/ntcir/permission/ntcir-10/perm-ja-SPOKENDOC.html)タスクのベースラインとすることを目的とした検索システムです。
SpokenDoc-1, 2でベースライン検索システムとして使用した[GETA](http://geta.ex.nii.ac.jp/geta.html)のfreqfileフォーマット、形態素解析器を使用してGETAの代わりに[Lucene](https://lucene.apache.org/core/)で検索を行います。
検索システムはjarファイルなのでインストールが非常に簡単です。

現在はSpokenDoc-1, SpokenDoc-2のXMLフォーマットで結果を出力可能です。フォーマットに大きな変更がなければ、次回のSpokenDoc-3でも使用可能なはずです。

*Feature
**検索システムに[Lucene](https://lucene.apache.org/core/)を使用
 - インストールが簡単
 - 高速
 - 検索手法の入れ替えが可能（デフォルトでTFIDF, BM25, DirichletLMに対応）
 - OSSなので検索スコアの計算方法の確認が可能
 - Luceneの充実したAPI javadocs
- [GETA](http://geta.ex.nii.ac.jp/geta.html)のfreqfileフォーマット、形態素解析器スクリプトを使用可能
 - 一般的にLuceneで使用されるJavaの形態素解析器である[kuromoji](http://www.atilika.org/)や[lucene-gosen](https://code.google.com/p/lucene-gosen/)は使用しません
 - GETA同様に形態素解析を行うシェルスクリプト等を使用して索引付け、検索を行います
 - MeCab, ChaSenや、任意の辞書を用いた形態素解析が可能です
- SpokenDocのlectureタスク、passageタスクの両方に対応
- Lucene組み込みの豊富なSimilarityを利用可能

*How to use
1. freqfile, 形態素解析スクリプトを用意する
2. .propertiesに各種ファイルのパスやSimilarity等を記述する
3. 索引付け、検索を行う
***索引付け
```
.jar index sample.properties
```
***検索
1クエリ検索
```
SpokendocBaseline.jar search sample.properties "音声ドキュメント検索のついて"
```

クエリファイルによる連続検索
```
SpokendocBaseline.jar search sample.properties path/to/queries/file.txt
```

*File format
各種ファイルのフォーマットは/sample内を参考にしてください

*Similarity
.propertiesのSimilarityの項目を変更することで、スコア計算に使用するSimilarityを変更可能です。
対応しているSimilarityは以下の三つです。BM25, DirichletLMはスコア計算に関係するパラメータも指定する必要があります
- [TFIDF(DefaultSimilarity)](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/TFIDFSimilarity.html)
- [BM25](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/BM25Similarity.html)
- [DirichletLM(クエリ尤度+ディリクレスムージング)](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/LMDirichletSimilarity.html)

*Dependencies
jarを再コンパイルするには以下のjarが必要です
- [Lucene 4.6.1](https://lucene.apache.org/)
- [argparse4j](http://argparse4j.sourceforge.net/)

*License

*Develop system

*Issue
**文字コードの問題
Eclipseだとデフォルトで文字コードはUTF-8なので問題ない
コマンドラインからの実行は明示的に指定しないとまともに動作しない
java jar -Dfile.encoding=UTF-8
又は、.bash_profileでjavaコマンドにaliasを設定するか、JVMにデフォルトオプションを渡す設定を書くらしい
マジか

**特殊文字
記号+ - && || ! ( ) { } [ ] ^ " ~ * ? : \について
上記の記号はLuceneでNOT, AND, OR検索等に使用する演算子なので、形態素解析の時点で削除してください
検索プログラムでも簡易的なエスケープはしていますが、エラーが出る可能性があります
