package com.example.email_in_java.controler;

import com.example.email_in_java.entity.Email;
import com.example.email_in_java.entity.EmailJson;
import com.example.email_in_java.entity.User;
import com.example.email_in_java.repository.EmailRepository;
import com.example.email_in_java.repository.UserRepository;
import com.example.email_in_java.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.google.gson.Gson;

import java.time.LocalDateTime;
import java.util.*;

@Controller
public class MailController {
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    @Autowired
    UserRepository userRepository;

    @Autowired
    EmailRepository emailRepository;

    @Autowired
    EmailService emailService;
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
   // @GetMapping("/emails")
    public String emails_with_box() {
        return "inbox";
    }
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    //@PostMapping(path="/emails", consumes = "application/json")
    @RequestMapping(path="/emails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> compose(@RequestBody com.fasterxml.jackson.databind.JsonNode inf, Model model) {
        //Authentication authentication

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        String kodJson = "";
        Gson gson = new Gson();
        Map<String,String> mapka= new LinkedHashMap<>();

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = "";
        if (principal instanceof UserDetails) {
            username = ((UserDetails)principal).getUsername();
        } else {
            username = principal.toString();
        }




        System.out.println(" a= "+" b= "+" c= " +"d= "+username);

        String recipients = inf.get("recipients").asText();
        String subject = inf.get("subject").asText();
        String body = inf.get("body").asText();

        System.out.println(" a= "+recipients+" b= "+subject+" c= "+body);


        User sender = userRepository.findByEmail(username);
        List<User> odbiorcy = new ArrayList<>();

        String[] split = recipients.split(",");
        if(split.length == 0 || recipients.length() == 0){
            mapka.put("message","At least one recipient required.");
            kodJson=gson.toJson(mapka);
            return ResponseEntity.badRequest()
                    .headers(responseHeaders)
                    .body(kodJson);

        }else {

            for( int i = 0; i < split.length; i++ ){
                String s = split[i];
                try{
                    User byEmail = userRepository.findByEmail(s);
                    System.out.println(byEmail);
                    if(byEmail.equals(null)){
                        throw new NullPointerException();
                    }else
                        odbiorcy.add(byEmail);
                }
                catch (NullPointerException e){
                    mapka.put("message","User with email " + s + " does not exist.");
                    kodJson=gson.toJson(mapka);
                    return ResponseEntity.badRequest()
                            .headers(responseHeaders)
                            .body(kodJson);
                }
            }
        }

        LocalDateTime now = LocalDateTime.now();

        //System.out.println(mail);

        //------  ***************************  -----------------------------
         // Create one email for each recipient,
         for(User u:odbiorcy){
             Email mail = new Email(u,sender,odbiorcy,subject,body,now,false,false);
             mail.setRecipients(odbiorcy);
             emailRepository.save(mail);

         }

        // plus sender
        Email mail = new Email(sender,sender,odbiorcy,subject,body,now,false,false);
        mail.setRecipients(odbiorcy);
        emailRepository.save(mail);

        //------  ***************************  -----------------------------
        mapka.put("message","Email sent successfully.");
        kodJson=gson.toJson(mapka);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(kodJson);
                //.build();
    }
    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    @GetMapping(path="/emails/{mailbox}")
    public ResponseEntity<String> mailbox(@PathVariable("mailbox") String name, Authentication authentication) {

        System.out.println("method mailbox: user = "+authentication.getName()+" wziol = " +name );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        Gson gson = new Gson();
        String kodJson = "";

        List<String> lista= new ArrayList<>();
        List<Email> lista2= new ArrayList<>();
        List<EmailJson> lista3= new ArrayList<>();
        Map<String,String> mapkaOdbiorcy= new LinkedHashMap<>();
        User sender ;
        switch (name){
            case "inbox":
                sender = userRepository.findByEmail(authentication.getName());
                List<Email> allByRecipientArchivedFalse = emailService.findAllByRecipientArchivedFalse(sender);
                for(Email e:allByRecipientArchivedFalse){
                    System.out.println(e.toJson());
                    lista.add(e.toJson());
                    lista2.add(e);
                    lista3.add(new EmailJson(e));

                }

                break;
            case "sent":
                 sender = userRepository.findByEmail(authentication.getName());
                List<Email> allBySender = emailRepository.findAllByUserBySender(sender,sender);

                for(Email e:allBySender){
                    System.out.println(e.toJson());
                    lista.add(e.toJson());
                    lista2.add(e);
                    lista3.add(new EmailJson(e));

                }
                break;

            case "archive":
                sender = userRepository.findByEmail(authentication.getName());
                List<Email> allByRecipientArchivedTrue = emailService.findAllByRecipientArchivedTrue(sender);
                for(Email e:allByRecipientArchivedTrue){
                    System.out.println(e.toJson());
                    lista.add(e.toJson());
                    lista2.add(e);
                    lista3.add(new EmailJson(e));

                }
                break;


            default:
                return new ResponseEntity(HttpStatus.NOT_FOUND);
        }


       // return new ResponseEntity(HttpStatus.NOT_FOUND);
        Collections.reverse(lista3);
        kodJson=gson.toJson(lista3);
        System.out.println(kodJson);

        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(kodJson);
    }


    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    @GetMapping(path="/emails_id/{email_id}")
    public ResponseEntity<String> mail_get(@PathVariable("email_id") Long emailId, Authentication authentication) {

        System.out.println("method mail : user = "+authentication.getName()+" wziol = " +emailId );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        Gson gson = new Gson();
        String kodJson = "";

       // User sender = userRepository.findByEmail(authentication.getName());
            Email e =  emailRepository.findById(emailId).orElse(new Email());
        kodJson=gson.toJson(new EmailJson(e));


        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(kodJson);
    }

    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
    @PutMapping(path="/emails_id/{email_id}")
    public ResponseEntity<String> mail_put(@PathVariable("email_id") Long emailId, Authentication authentication, @RequestBody com.fasterxml.jackson.databind.JsonNode inf, Model model) {

        System.out.println("method mail put : user = "+authentication.getName()+" wziol = " +emailId );

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.set("Content-Type","application/json");
        Gson gson = new Gson();
        String kodJson = "";

        User sender = userRepository.findByEmail(authentication.getName());
        Email e =  emailRepository.findById(emailId).orElse(new Email());
        boolean read ;
        boolean archived;

        try {
             read = inf.get("read").asBoolean();
             e.setRead(read);
             emailRepository.save(e);




        }
        catch (NullPointerException ex){
            System.out.println("nie read");
        }

        try {
             archived = inf.get("archived").asBoolean();
            e.setArchived(archived);
            emailRepository.save(e);



        }
        catch (NullPointerException ex){
            System.out.println("nie archived");
        }






        kodJson=gson.toJson(new EmailJson(e));


        return ResponseEntity.ok()
                .headers(responseHeaders)
                .body(kodJson);
    }

    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------



    //----------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------
}
