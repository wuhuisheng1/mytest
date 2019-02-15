package cn.itcast.lucene;

import cn.itcast.lucene.dao.BookDao;
import cn.itcast.lucene.dao.impl.BookDaoImpl;
import cn.itcast.lucene.pojo.Book;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.*;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;
import org.junit.Test;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 索引库管理测试类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-12<p>
 */
public class IndexManager {

    /** 创建索引库(存储了索引与文档) */
    @Test
    public void createIndex() throws  Exception{
        // 1. 采集数据
        BookDao bookDao = new BookDaoImpl();
        List<Book> bookList = bookDao.findAll();
        System.out.println(bookList);

        // 2. 准备文档对象(数据库表中的一行数据)
        // 定义集合封装多个文档
        List<Document> documents = new ArrayList<Document>();
        for (Book book : bookList) {
            // 把 book 对象 转化成 Document
            Document document = new Document(); // 一行数据
            // 文档中添加Field (添加列的数据Field)
            /**
             * document.add(Field)
             * TextField:
             *   String name: 列的名称(域的名称)
             *   String value: 列的值 (域的值)
             *   Store store: 是否把域的值存储到文档中
             */
            /**
             * 图书Id
             是否分词：不需要分词
             是否索引：需要索引
             是否存储：需要存储
             -- StringField
             */
           document.add(new StringField("id", book.getId() + "", Field.Store.YES));
            /**
             * 图书名称
             是否分词：需要分词
             是否索引：需要索引
             是否存储：需要存储
             -- TextField
             */
           document.add(new TextField("bookName", book.getBookName(), Field.Store.YES));
            /**
             * 图书价格
             是否分词：（数值型的Field lucene使用内部的分词）
             是否索引：需要索引
             是否存储：需要存储
             -- DoubleField
             */
           document.add(new DoubleField("bookPrice", book.getPrice(), Field.Store.YES));
            /**
             * 图书图片
             是否分词：不需要分词
             是否索引：不需要索引
             是否存储：需要存储
             -- StoredField
             */
           document.add(new StoredField("bookPic", book.getPic()));
            /**
             * 图书描述
             是否分词：需要分词
             是否索引：需要索引
             是否存储：不需要存储
             -- TextField
             */
           document.add(new TextField("bookDesc", book.getBookDesc(), Field.Store.NO));

            documents.add(document);
        }

        // 3. 创建分词器(对Field中值进行分词) 中国人  --> 中  国  人
        Analyzer analyzer = new IKAnalyzer(); // 单字分词器 （一元切分法）

        // 4. 创建写索引配置信息对象
        // 第一个参数：版本号
        // 第二个参数：分词器
        IndexWriterConfig indexWriterConfig = new IndexWriterConfig(Version.LUCENE_4_10_3, analyzer);
        // 4.1 设置索引库的打开模式
        // IndexWriterConfig.OpenMode.CREATE: 每次都重新创建索引库
        // IndexWriterConfig.OpenMode.APPEND: 追加的模式(第一次不能用)
        // IndexWriterConfig.OpenMode.CREATE_OR_APPEND: 如果索引库不存在，就创建索引库，如果存在就追加(默认)
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);


        // 5. 创建写索引对象(操作索引库)
        // Directory d (磁盘目录) 指定索引库存储的目录

        Directory directory = FSDirectory.open(new File("F:\\index"));
        IndexWriter indexWriter = new IndexWriter(directory, indexWriterConfig);

        // 6. 循环文档集合，把文档添加到索引库
        for (Document document : documents) {
            indexWriter.addDocument(document);
            // 提交事务
            indexWriter.commit();
        }

        // 7. 释放资源
        indexWriter.close();

    }

    /** 根据关键字搜索索引库中的文档 */
    @Test
    public void searchIndex() throws Exception{
        // 1. 创建分词器(对查询条件作分词)
        Analyzer analyzer = new IKAnalyzer();

        // 2. 创建查询对象(封装查询条件)
        // 2.1 查询解释对象
        QueryParser queryParser = new QueryParser("bookName", analyzer);
        // 2.2 得到查询对象
        Query query = queryParser.parse("java");
        // bookName:java
        System.out.println("查询语法：" + query);

        // 3. 创建IndexReader把索引库中的索引数据加载到内存中
        // 3.1 指定索引库存储的位置
        Directory directory = FSDirectory.open(new File("F:\\index_db"));
        IndexReader indexReader = DirectoryReader.open(directory);

        // 4. 创建IndexSearcher对象
        IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        // 4.1 执行搜索
        // 第一个参数：查询对象
        // 第二个参数：返回最前面的文档数量
        TopDocs topDocs = indexSearcher.search(query, 10);
        System.out.println("总命中数量：" + topDocs.totalHits);

        // 4.2 获取分数文档数组
        // ScoreDoc: 文档编号与文档分数
        ScoreDoc[] scoreDocs = topDocs.scoreDocs;
        for (ScoreDoc scoreDoc : scoreDocs) {
            System.out.println("===========华丽丽分割线============");
            System.out.println("文档编号: " + scoreDoc.doc);
            System.out.println("文档分数: " + scoreDoc.score);
            // 根据Doc.Id 获取文档对象
            Document doc = indexSearcher.doc(scoreDoc.doc);
            System.out.println("图书id: " + doc.get("id"));
            System.out.println("图书名称: " + doc.get("bookName"));
            System.out.println("图书价格: " + doc.get("bookPrice"));
            System.out.println("图书图片: " + doc.get("bookPic"));
            System.out.println("图书描述: " + doc.get("bookDesc"));
        }

        // 5. 释放资源
        indexReader.close();

    }
}
