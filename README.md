#SpokenDoc-Baseline
[NTCIR](http://research.nii.ac.jp/ntcir/index-ja.html)の[SpokenDoc](http://research.nii.ac.jp/ntcir/permission/ntcir-10/perm-ja-SPOKENDOC.html)の音声コンテンツ検索（SCR）のベースラインとすることを目的とした検索システムです。  
SpokenDoc-1, 2でベースライン検索システムとして使用した[GETA](http://geta.ex.nii.ac.jp/geta.html)のfreqfileフォーマット、形態素解析スクリプトを流用して[Lucene](https://lucene.apache.org/core/)で検索を行います。  
検索システムはjarファイルなのでインストールが非常に簡単です。

現在はSpokenDoc-1, SpokenDoc-2のXMLフォーマットで結果を出力可能です。  
フォーマットに大きな変更がなければ、次回のSpokenDoc-3でも使用可能なはずです。

##Feature
- SpokenDocのlecture、passageタスクの両方に対応
- [GETA](http://geta.ex.nii.ac.jp/geta.html)のfreqfileフォーマット、形態素解析器スクリプトを使用可能
- MeCab, ChaSenや、任意の辞書を用いた形態素解析が可能です
 - 一般的にLuceneで使用されるJavaの形態素解析器である[kuromoji](http://www.atilika.org/)や[lucene-gosen](https://code.google.com/p/lucene-gosen/)は使用しません


##検索システムに[Lucene](https://lucene.apache.org/core/)を使用
- インストールが簡単
- 索引付け、検索が高速
- 検索手法の入れ替えが可能（デフォルトでTFIDF, BM25, DirichletLMに対応）
 - Lucene組み込みの豊富なSimilarityを利用可能
 - OSSなので検索スコアの計算方法の確認が可能
- Luceneの充実したAPI javadocs

##How to use
1. freqfile, 形態素解析スクリプトを用意する
2. .propertiesに各種ファイルのパスやSimilarity等を記述する
3. 索引付け、検索を行う

###索引付け
```bash
SpokendocBaseline.jar index sample/tfidf.properties
```

###検索
1クエリ検索
```bash
SpokendocBaseline.jar search sample/tfidf.properties "音声ドキュメント検索のついて"
```

クエリファイルによる連続検索
```bash
SpokendocBaseline.jar search sample/tfidf.properties sample/queries.txt
```

##File format
各種ファイルのフォーマットは/sample内を参考にしてください

##Similarity
.propertiesのSimilarityの項目を変更することで、スコア計算に使用するSimilarityを変更可能です。  
デフォルトで対応しているSimilarityは以下の三つです。BM25, DirichletLMはスコア計算に関係するパラメータも指定する必要があります。
- [TFIDF(DefaultSimilarity)](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/TFIDFSimilarity.html)
- [BM25](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/BM25Similarity.html)
- [DirichletLM(クエリ尤度+ディリクレスムージング)](https://lucene.apache.org/core/4_0_0/core/org/apache/lucene/search/similarities/LMDirichletSimilarity.html)

##Dependencies
jarを再コンパイルするには以下の外部jarが必要です
- [Lucene 4.6](https://lucene.apache.org/)
- [argparse4j](http://argparse4j.sourceforge.net/)

.classpathに従って必要なjarを用意し、Eclipseでエクスポート→実行可能なJARファイル→「生成されるJARに必要ライブラリーをパッケージ」にチェックしてjarを生成してください。

##Supported OS
OSX 10.9, 10.8 + Java 1.6  
CentOS 6 + Java 1.6

##Issue
###文字コードの問題
文字コードはUTF-8のみ対応です。  
Eclipseではデフォルトで文字コードをUTF-8にしていれば問題なく動作するはずですが、JavaのバージョンによってはCLIからの実行は以下のように明示的に指定しないと動作しない可能性があります。  
(OSX 10.8, 10.9で問題が起こることを確認しています）

```bash
java -jar -Dfile.encoding=UTF-8
```

又は、.bash_profileに上記のコマンドをjavaコマンドにaliasを設定するか、JVMにデフォルトでUTF-8で実行するようにオプションを渡す設定を書く必要があるようです。

###特殊文字
記号文字「+ - && || ! ( ) { } [ ] ^ " ~ * ? : \」はLuceneでNOT, AND, OR検索等に使用する演算子に指定されています。  
検索プログラム内でも簡易的なエスケープはしていますが、エラーが出る可能性があるので、可能であれば形態素解析の時点で削除してください。

###形態素解析スクリプト
jarを実行する際にはスクリプト中でChaSenやMeCabのフルパスをwhichで取得することが可能ですが、Eclipseで実行するときにはwhichコマンドで取得することができないため、クエリに１文字も入らずエラーが起きてしまうようです。  
Eclipseで実行するときには.properties内のChaSenやMeCabのパスは自分でフルパスを記入する必要があります。
