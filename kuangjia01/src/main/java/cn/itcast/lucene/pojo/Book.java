package cn.itcast.lucene.pojo;

/**
 * 图书实体类
 *
 * @author lee.siu.wah
 * @version 1.0
 * <p>File Created at 2019-02-12<p>
 */
public class Book {
    private int id;
    private String bookName;
    private float price;
    private String pic;
    private String bookDesc;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public float getPrice() {
        return price;
    }

    public void setPrice(float price) {
        this.price = price;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getBookDesc() {
        return bookDesc;
    }

    public void setBookDesc(String bookDesc) {
        this.bookDesc = bookDesc;
    }

    @Override
    public String toString() {
        return "Book{" +
                "id=" + id +
                ", bookName='" + bookName + '\'' +
                ", price=" + price +
                ", pic='" + pic + '\'' +
                ", bookDesc='" + bookDesc + '\'' +
                '}';
    }
}
