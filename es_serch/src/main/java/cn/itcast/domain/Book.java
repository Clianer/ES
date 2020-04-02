package cn.itcast.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Table(name = "book")
public class Book {
    // 图书id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    // 图书名称
    @Column(name = "bookName")
    private String bookName;
    // 图书价格
    private Float price;
    // 图书图片
    private String pic;
    // 图书描述
    @Column(name = "bookDesc")
    private String bookDesc;
}