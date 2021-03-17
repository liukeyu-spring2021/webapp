
package com.cloud.controller;

import com.cloud.service.*;
import org.apache.catalina.User;
import org.apache.commons.validator.GenericValidator;
import org.hibernate.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import com.cloud.domain.*;

import java.sql.Array;
import java.sql.Timestamp;
import java.util.*;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
public class CustomerController {
    @Autowired // This means to get the bean called userRepository
    // Which is auto-generated by Spring, we will use it to handle the data
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private FileInfoRepository fileInfoRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserService userService;

    @Autowired
    private FileStorageService fileStorageService;


    @PostMapping(path="/v1/user",produces = "application/json") // Map ONLY POST Requests
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResponseEntity createUser (@RequestBody userInfo info ) {
        if(info.getPassword()==null||info.getEmail_address()==null||info.getFirst_name()==null||info.getLast_name()==null){
            //System.out.println("1");
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        if(info.getLast_name().equals("")||info.getFirst_name().equals("")){
            //System.out.println("2");
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        if(!userService.pwdValidation(info.getPassword())){
           // System.out.println("3");
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        if(!userService.emailVaildation(info.getEmail_address())){
           // System.out.println("4");
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }
        UserAccount n= new UserAccount();
        n.setFirst_name(info.getFirst_name());
        n.setLast_name(info.getLast_name());
        n.setPassword(info.getPassword());
        n.setEmailAddress(info.getEmail_address());
        n.setAccount_updated(new Timestamp(System.currentTimeMillis()).toString());
        n.setAccount_created(new Timestamp(System.currentTimeMillis()).toString());
        if(userService.CheckIfEmailExists(n.getEmailAddress())){
            return new ResponseEntity<>(HttpStatus.valueOf(400));
        }

        userService.saveWithEncoder(n);
        return new ResponseEntity<>(new userInfo_noPwd(n.getId(),n.getFirst_name(),n.getLast_name(),n.getEmailAddress(),n.getAccount_created(),n.getAccount_updated()),HttpStatus.CREATED);
    }


    //Get User Info
    @GetMapping(path="/v1/user/self",produces = "application/json")
    public ResponseEntity getUserInfo (){
      Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
            if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
            System.out.println(authentication.getName());
            UserAccount n=userService.findByEmail(authentication.getName());

            return new ResponseEntity(new userInfo_noPwd(n.getId(),n.getFirst_name(),n.getLast_name(),n.getEmailAddress(),n.getAccount_created(),n.getAccount_updated()),HttpStatus.OK);



    }

    //Update user info
    @PutMapping(path="/v1/user/self",produces = "application/json")
    public ResponseEntity updateUserInfo (@RequestBody UserAccount_v2 n){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        //System.out.println(authentication.getName());
        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
        UserAccount old=userService.findByEmail(authentication.getName());
        try {
            int i = 0;
            if (n.getFirst_name() != null && !"".equals(n.getFirst_name())) {
                old.setFirst_name(n.getFirst_name());
                i++;
            }
            if (n.getLast_name() != null && !"".equals(n.getLast_name())) {
                old.setLast_name(n.getLast_name());
                i++;
            }
            if (n.getPassword() != null && !"".equals(n.getPassword())) {
                if (!userService.pwdValidation(n.getPassword())) {
                    return new ResponseEntity(HttpStatus.valueOf(400));
                }
                //System.out.println("password changed");
                old.setPassword(bCryptPasswordEncoder.encode(n.getPassword()));
                i++;
            }
        }catch (Exception e){
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        old.setAccount_updated(new Timestamp(System.currentTimeMillis()).toString());
        userService.update(old);
        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    @PostMapping(path="/v1/books",produces = "application/json") // Map ONLY POST Requests
    public ResponseEntity createBill (@RequestBody BookInfo info ) {

            Authentication authentication =
                    SecurityContextHolder.getContext().getAuthentication();
            if (authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
//            if (QuestionInfoCheck(info)) return new ResponseEntity(HttpStatus.valueOf(400));

            UserAccount user = userService.findByEmail(authentication.getName());

            Book b = new Book();

//        CategoryInfo old;
//
//        for(CategoryInfo categoryInfo: info.getCategories()) {
//            try {
//                old = categoryRepository.findByCategory(categoryInfo.getCategory()).get();
//            } catch (Exception e) {
//                categoryRepository.save(categoryInfo);
//                b.getCategories().add(categoryInfo);
//            }
//        }
            if (info.getAuthor() == null || info.getIsbn() == null || info.getPublished_date() == null || info.getTitle() == null)
                return new ResponseEntity(HttpStatus.BAD_REQUEST);

            try {
                b.setTitle(info.getTitle());
                b.setAuthor(info.getAuthor());
                b.setIsbn(info.getIsbn());
                b.setPublished_date(info.getPublished_date());
                b.setBook_created(new Timestamp(System.currentTimeMillis()).toString());
                b.setUserId(user.getId());
                bookRepository.save(b);
            }catch (Exception e){
                return new ResponseEntity(HttpStatus.BAD_REQUEST);
            }
            return new ResponseEntity<>(b, HttpStatus.CREATED);

    }

    @GetMapping(path="/v1/books",produces = "application/json")
    public ResponseEntity getAllQuestions() {
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//        System.out.println(authentication.getName());
//        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
//        UserAccount user= userService.findByEmail(authentication.getName());
        Iterable<Book> questions= bookRepository.findAll();
        // This returns a JSON or XML with the users
        return new ResponseEntity(questions, HttpStatus.valueOf(200));
    }

//    @GetMapping(path="/v1/user1/{id}",produces = "application/json")
//    public ResponseEntity getUser(@RequestParam String id) {
//        //Authentication authentication =
//        //        SecurityContextHolder.getContext().getAuthentication();
//        //System.out.println(authentication.getName());
//        //if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
//        //UserAccount user=userService.findByEmail(authentication.getName());
//
//        UserAccount n = userRepository.findById(id).get();
//        return new ResponseEntity(new userInfo_noPwd(n.getId(),n.getFirst_name(),n.getLast_name(),n.getEmailAddress(),n.getAccount_created(),n.getAccount_updated()),HttpStatus.OK);
//    }

    @GetMapping(path="/v1/books/{id}",produces = "application/json")
    public ResponseEntity getBook(@PathVariable String id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        //System.out.println(authentication.getName());
        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
        UserAccount user=userService.findByEmail(authentication.getName());

            Optional<Book> book = bookRepository.findById(id);
            if(!book.get().getId().equals(id)){
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }

        return new ResponseEntity(book,HttpStatus.OK);
    }

    @DeleteMapping(path="/v1/books/{id}",produces = "application/json")
    public ResponseEntity deleteQuestion(@PathVariable String id) {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        //System.out.println(authentication.getName());
        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
        UserAccount user=userService.findByEmail(authentication.getName());
        Book book;
        try {
            book = bookRepository.findById(id).get();
            if(!book.getUserId().equals(user.getId())){
                return new ResponseEntity(HttpStatus.NOT_FOUND);
            }
        }
        catch (Exception e){
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        bookRepository.delete(book);

        return new ResponseEntity(HttpStatus.valueOf(204));
    }

    @PostMapping(path="/v1/books/{id}/image",produces = "application/json") // Map ONLY POST Requests
    private ResponseEntity attachImage(@RequestParam("image") MultipartFile file ,@PathVariable String id){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        //System.out.println(authentication.getName());
        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
        UserAccount user=userService.findByEmail(authentication.getName());

        Book book = bookRepository.findById(id).get();

        if(!book.getUserId().equals(user.getId())){
            System.out.println("You are not allowed to post image for others' book");
                return new ResponseEntity(HttpStatus.valueOf(401));
        }

//
//        if (book.getBook_images() != null) return new ResponseEntity(HttpStatus.BAD_REQUEST);

//        String contentType = file.getContentType();
//        if(!contentType.equals("application/pdf") && !contentType.equals("image/png") && !contentType.equals("image/jpg") && !contentType
//        .equals("image/jpeg")){
//            System.out.println("Content-type limited");
//            return new ResponseEntity(HttpStatus.BAD_REQUEST);
//        }

        FileInfo fileInfo = fileStorageService.storeFile(file, user.getId(), book.getId());
        List<FileInfo> list = book.getBook_images() == null ? new ArrayList<>() : book.getBook_images();
        list.add(fileInfo);
        bookRepository.save(book);
        return new ResponseEntity(fileInfo, HttpStatus.valueOf(201));
    }

//    @GetMapping(path="/v1/question/{questionId}/answer/{answerId}",produces = "application/json")
//    private ResponseEntity getFile(@PathVariable String questionId, @PathVariable String answerId, HttpServletRequest request){
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//        //System.out.println(authentication.getName());
//        //if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
//        try{
//            Optional<AnswerInfo> answerInfos =answerRepository.findById(answerId);
//            return ResponseEntity.ok().body(answerInfos.get());
//        }catch (Exception e){
//            return new ResponseEntity(HttpStatus.valueOf(404));
//        }
//    }
//
    @DeleteMapping(path="/v1/books/{bookId}/image/{imageId}",produces = "application/json")
    private ResponseEntity deleteFile(@PathVariable String bookId, @PathVariable String imageId, HttpServletRequest request){
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        //System.out.println(authentication.getName());
        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));

        try {
            UserAccount user = userService.findByEmail(authentication.getName());
            Book book = bookRepository.findById(bookId).get();
            List<FileInfo> list =  book.getBook_images();
            FileInfo info = fileInfoRepository.findById(imageId).get();

            if (!book.getUserId().equals(user.getId())) {
                return new ResponseEntity(HttpStatus.valueOf(401));
            }


//            fileStorageService.deleteFile(info, user.getId(), book.getId());
            fileStorageService.deleteFromS3(info);
            list.remove(info);
            book.setBook_images(list);
            bookRepository.save(book);
            fileInfoRepository.delete(info);
            System.out.println("answer in question deleted");
        }catch(Exception e){
            return new ResponseEntity(HttpStatus.valueOf(400));
        }
            return new ResponseEntity(HttpStatus.NO_CONTENT);


    }
//
//
//    @PutMapping(path="/v1/question/{question_id}/answer/{answer_id}",produces = "application/json")
//    private ResponseEntity updateFile(@PathVariable String question_id, @PathVariable String answer_id, @RequestBody AnswerText answer_text, HttpServletRequest request){
//        Authentication authentication =
//                SecurityContextHolder.getContext().getAuthentication();
//        //System.out.println(authentication.getName());
//        if(authentication.getName().equals("anonymousUser")) return new ResponseEntity(HttpStatus.valueOf(401));
//
//        try{
//            UserAccount user=userService.findByEmail(authentication.getName());
//            Question question=questionRepository.findById(question_id).get();
//            if(!question.getUserId().equals(user.getId())){
//                return new ResponseEntity(HttpStatus.valueOf(404));
//            }
//            Optional<AnswerInfo> answerInfo = answerRepository.findById(answer_id);
//            answerInfo.get().setAnswer_text(answer_text.getAnswer_text());
//
//            List<AnswerInfo> old = question.getAnswers();
//            for (AnswerInfo info : old){
//                if (info.getId() == answer_id){
//                    info.setAnswer_text(answer_text.getAnswer_text());
//                }
//            }
//            question.setAnswers(old);
//            questionRepository.save(question);
//            answerRepository.save(answerInfo.get());
//
//            return new ResponseEntity(HttpStatus.NO_CONTENT);
//        }catch (Exception ex){
//            return new ResponseEntity(HttpStatus.valueOf(404));
//        }
//
//    }

}
