package cn.itcast.es.mapper;

import cn.itcast.domain.Book;
import cn.itcast.mapper.BookMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:applicationContext-dao.xml")
public class BookMapperTest {
    @Autowired
    private BookMapper bookMapper;

    @Test
    public void find(){
        // 查询全部
        System.out.println(bookMapper.selectAll());

        // 根据条件查询
        Book book = new Book();
        book.setBookName("java web开发");
        System.out.println(bookMapper.select(book));

        System.out.println(bookMapper.selectByPrimaryKey(2));
    }

    @Test
    public void cud(){
        //bookMapper.insert();
        //bookMapper.updateByPrimaryKey();
        //bookMapper.deleteByPrimaryKey();
    }
}