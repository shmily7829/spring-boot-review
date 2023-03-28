package com.mily.springbootreview;

import com.mily.springbootreview.data.GameState;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//1. spring MVC Request-Controller-Service-Repository-DB 各 Layer 之間的關係
//2. Repository Layer 用來隔離 DB Layer的實作
//3. Json Parse 用 ObjectMapper obj <-> json
//   writerWithDefaultPrettyPrinter ->排版
//4. Bean 註解使用方式
//5. Junit jsonPath 依照 key 取出 json 字串對應的值，並且驗證是否符合預期。可以用$.取得json物件的值。
//6. JPA 提供的語法:
//   (1)CrudRepository 提供 API 執行 CRUD，e.g. findById
//   (2)透過註解(annotation) 加上 SQL command 執行查詢
//   @Query(nativeQuery = true,value = "SELECT * FROM User WHERE account = ?1")
//   User qryUserData(String account);
//   User findByAccount(String account);
//7. 使用 MockMVC 測試時，需要加上@SpringBootApplication

@SpringBootApplication
public class SpringBootReviewApplication {
    public static void main(String[] args) {

        SpringApplication.run(SpringBootReviewApplication.class, args);
    }

}
