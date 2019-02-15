package cn.itcast.lucene.dao;

import cn.itcast.lucene.pojo.Book;

import java.util.List;

/**
 * 图书数据访问接口
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-12<p>
 */
public interface BookDao {

    List<Book> findAll();
}
