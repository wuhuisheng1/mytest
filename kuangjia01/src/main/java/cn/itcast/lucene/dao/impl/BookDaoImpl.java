package cn.itcast.lucene.dao.impl;

import cn.itcast.lucene.dao.BookDao;
import cn.itcast.lucene.pojo.Book;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 图书数据访问接口实现类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-12<p>
 */
public class BookDaoImpl implements BookDao {

    /** 查询全部图书 */
    public List<Book> findAll() {

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Book> bookList = new ArrayList<Book>();

        try {
            // 1. 加载数据库驱动
            Class.forName("com.mysql.jdbc.Driver");

            // 2. 获取数据库连接
            connection = DriverManager.
                    getConnection("jdbc:mysql://localhost:3306/lucene_db", "root", "root");

            // 3. 定义sql语句
            String sql = "select * from book";

            // 4. 获取statement
            statement = connection.prepareStatement(sql);

            // 5. 执行查询，得到ResultSet结果集
            rs = statement.executeQuery();

            // 6. 迭代结果集合，封装数据
            while (rs.next()){
                Book book = new Book();
                book.setId(rs.getInt(1));
                book.setBookName(rs.getString(2));
                book.setPrice(rs.getFloat(3));
                book.setPic(rs.getString(4));
                book.setBookDesc(rs.getString(5));
                bookList.add(book);
            }

        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            // 7. 释放资源
            try{
                if (rs != null) rs.close();
                if (statement != null) statement.close();
                if (connection != null) connection.close();
            }catch (Exception e){
            }
        }
        return bookList;
    }
}
