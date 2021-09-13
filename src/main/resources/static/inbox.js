document.addEventListener('DOMContentLoaded', function() {

  // Use buttons to toggle between views
  document.querySelector('#inbox').addEventListener('click', () => load_mailbox('inbox'));
  document.querySelector('#sent').addEventListener('click', () => load_mailbox('sent'));
  document.querySelector('#archived').addEventListener('click', () => load_mailbox('archive'));
  document.querySelector('#compose').addEventListener('click', compose_email);

  //document.querySelector('#compose-form').addEventListener('submit', () => send_email());
  document.querySelector('#compose-form').onsubmit = function() {

              //--------------------------------------------------

              recipients_form = document.querySelector('#compose-recipients').value;
              subject_form = document.querySelector('#compose-subject').value;
              message_body = document.querySelector('#compose-body').value;
              console.log('odbiorcy='+ recipients_form + ", temat: "+ subject_form);
              result_of_sending = "";
              komunikat = "";
              komunikat2 = "";
              kopia = "";

              fetch('/emails', {
                method: 'POST',
                headers: {
                      'Accept': 'application/json',
                      'Content-Type': 'application/json'
                    },
                body: JSON.stringify({
                    recipients: recipients_form,
                    subject: subject_form,
                    body: message_body
                })
              })
              .then((response) => {
                 return response.json();
              })
              .then((data) => {

                   result_of_sending = JSON.stringify(data);
                   console.log('rezultat: '+result_of_sending);
                  // console.log(result_of_sending);
                   komunikat = result_of_sending.substring(2,7);
                   komunikat.replace('}','');
                   komunikat.replace('"','');
                   //console.log("komunikat "+ komunikat);
                   komunikat2 = result_of_sending.substring(10);
                   komunikat2=komunikat2.substring(0,komunikat2.lenght-2);
                   //komunikat2.replace('}','');
                   //komunikat2.replace('"','');
                   //console.log("komunikat2 "+ komunikat2);

                   if (komunikat2.includes('At least one recipient required')){
                    alert(komunikat2);
                    console.log('brak odbiorcow');
                    throw new Error("no recipients ");

                  }
                  if (komunikat2.includes('not exist')){
                    alert(komunikat2);
                    console.log('odbiorca nie istnieje');
                    throw new Error("one of recieipients not exists");
                  }
                  //********* */

                  load_mailbox('sent')
              })
              .catch(error => {
                    console.log('Error:->', error);
                    return false;
                });

              console.log('koniec sendmaila');
              return false;
                //--------------------------------------------------
  };

  // By default, load the inbox
  //console.log("pierwsze loading");
  const e0 = document.createElement('div');
  e0.id="email_container";
  document.querySelector('#emails-view').append(e0);

  load_mailbox('inbox');
//------------------------------------------------------
});
//-------------------------------------------------------------------------------------------------------------
function compose_email() {

  // Show compose view and hide other views
  document.querySelector('#emails-view').style.display = 'none';
  document.querySelector('#compose-view').style.display = 'block';

  // Clear out composition fields
  document.querySelector('#compose-recipients').value = '';
  document.querySelector('#compose-subject').value = '';
  document.querySelector('#compose-body').value = '';
}
//-------------------------------------------------------------------------------------------------------------
function load_mailbox(mailbox) {

  // Show the mailbox and hide other views
  document.querySelector('#emails-view').style.display = 'block';
  document.querySelector('#compose-view').style.display = 'none';

  // Show the mailbox name
  document.querySelector('#emails-view').innerHTML = `<h3>${mailbox.charAt(0).toUpperCase() + mailbox.slice(1)}</h3>`;

      resetContainer();
      load_mailbox_items(mailbox);
}
//-------------------------------------------------------------------------------------------------------------
function resetContainer(){
  var el = document.getElementById('email_container');
  if(el === null)
    console.log('null value');
  else
    el.remove(); // Removes the div with the 'email_container' id
  const e0 = document.createElement('div');
  e0.id="email_container";
  document.querySelector('#emails-view').append(e0);
}
//-------------------------------------------------------------------------------------------------------------
function load_mailbox_items(mailbox) {

  fetch('/emails/'+mailbox)
  .then(response => response.json())
  .then(emails => {
      // Print emails
      console.log(emails);
      // ... do something else with emails ...

    for (var i = 0; i < emails.length; i++){
        var email = emails[i];
        e_id =email.id;
        e_sender= email.sender;
        e_recipients = email.recipients;
        e_subject = email.subject;
        e_body = email.body;
        e_readed = email.read;
        e_archived = email.archived;
        e_timestamp = email.timestamp;
        console.log("first--> "+e_id+" "+e_sender+" "+e_recipients+" "+e_subject+" "+e_body + " " + e_readed +" " + e_archived);
        //console.log('1 e_archived='+e_archived);

       // mailitem(e_id,e_sender,e_recipients,e_subject,e_body,e_readed,e_timestamp,mailbox);
        //----------------------------------------------------

        switch(mailbox){
            case 'inbox':
              {
              console.log('inbox');
              if(e_archived == false){
                mailitem(e_id,e_sender,e_recipients,e_subject,e_body,e_readed,e_timestamp,mailbox);
               }
               break;
              }
            case 'archive':
              {
              console.log('archiwum');
              console.log('e_archived='+e_archived);
              if(e_archived == true){

                mailitem(e_id,e_sender,e_recipients,e_subject,e_body,e_readed,e_timestamp,mailbox);
               }

               break;
              }
               case 'sent':
              {
               console.log('sent');
                mailitem(e_id,e_sender,e_recipients,e_subject,e_body,e_readed,e_timestamp,mailbox);
                break;
              }


        }

        //----------------------------------------------------

    }
   // console.log('koniec load mailbox'+mailbox);

  });

}
//-------------------------------------------------------------------------------------------------------------
function mailitem(e_id,e_sender,e_recipients,e_subject,e_body,e_readed,e_timestamp,mailbox){

  const e1 = document.createElement('div');
  e1.id="email_content_"+e_id;
  e1.className = "d-flex border justify-content-between";
  if(e_readed){
    e1.setAttribute("style","background-color: lightgrey;margin-bottom:2px;");
  }else{
    e1.setAttribute("style","background-color: white;margin-bottom:2px;");
  }

  const e2a = document.createElement('div');
  e2a.className = "p-2 d-flex align-items-center";

  const e3a = document.createElement('div');
  e3a.className = "container";
  e3a.setAttribute("style","width: 200px; font-weight: bold;");
  e3a.innerHTML =e_sender;

  const e3b = document.createElement('div');
  e3b.className = "container";

  const e3b4a = document.createElement('div');
  e3b4a.className = "text-dark";
  e3b4a.setAttribute("style","font-weight: bold;");
  e3b4a.innerHTML =e_subject;

  const e3b4b = document.createElement('div');
  e3b4b.className = "text-secndary";
  e3b4b.innerHTML =e_body.substring(0,30);

  const e2b = document.createElement('div');
  e2b.className = "ml-auto p-2 d-flex align-items-center";

  const e2b3a = document.createElement('div');
  e2b3a.innerHTML =e_timestamp;

  const e2b3b = document.createElement('button');
  e2b3b.className = "btn btn-sm btn-outline-primary";
  e2b3b.id="archive";
  e2b3b.setAttribute("style","margin-left:15px;");
  e2b3b.innerHTML ="Archive";

  const e2b3b2 = document.createElement('button');
  e2b3b2.className = "btn btn-sm btn-outline-primary";
  e2b3b2.id="unarchive";
  e2b3b2.setAttribute("style","margin-left:15px;");
  e2b3b2.innerHTML ="unArchive";

  e2a.appendChild(e3a);

  e3b.appendChild(e3b4a);
  e3b.appendChild(e3b4b);
  e2a.appendChild(e3b);

  e2b.appendChild(e2b3a);
  e1.appendChild(e2a);
  e1.appendChild(e2b);

  e1.addEventListener('click', function() {
    //console.log('This element has been clicked!')
    getEmail(e_id,mailbox);

    });


  document.querySelector('#email_container').append(e1);
}
//-------------------------------------------------------------------------------------------------------------
function getEmail(e_id,mailbox){

  fetch('/emails_id/'+e_id)
  .then(response => response.json())
  .then(email => {
      // Print email
      console.log(email);

      // Show the mailbox and hide other views
      document.querySelector('#emails-view').style.display = 'block';
      document.querySelector('#compose-view').style.display = 'none';


      // ... do something else with email ...
      markReadEmail(e_id);
      showEmail(email,mailbox);
  });

}
//-------------------------------------------------------------------------------------------------------------
function showEmail(email,mailbox){
  resetContainer();
  const e1 = document.createElement('div');
  e1.id="email_content_show";
  e1.className = "container border";

  const e2 = document.createElement('div');
  e2.className = "container d-flex";
  const e21 = document.createElement('div');
  e21.setAttribute("style","font-weight: bold; margin-right:5px;width: 100px;");
  e21.innerHTML ="From:";
  const e22 = document.createElement('div');
  e22.innerHTML =email.sender;
  e2.appendChild(e21);
  e2.appendChild(e22);

  const e3 = document.createElement('div');
  e3.className = "container d-flex";
  const e31 = document.createElement('div');
  e31.setAttribute("style","font-weight: bold; margin-right:5px;width: 100px;");
  e31.innerHTML ="To:";
  const e32 = document.createElement('div');
  e32.innerHTML =email.recipients;
  e3.appendChild(e31);
  e3.appendChild(e32);

  const e4 = document.createElement('div');
  e4.className = "container d-flex";
  const e41 = document.createElement('div');
  e41.setAttribute("style","font-weight: bold; margin-right:5px;width: 100px;");
  e41.innerHTML ="Subject:";
  const e42 = document.createElement('div');
  e42.innerHTML =email.subject;
  e4.appendChild(e41);
  e4.appendChild(e42);


  const e5 = document.createElement('div');
  e5.className = "container d-flex";
  const e51 = document.createElement('div');
  e51.setAttribute("style","font-weight: bold; margin-right:5px;width: 100px;");
  e51.innerHTML ="Timestamp:";
  const e52 = document.createElement('div');
  e52.innerHTML =email.timestamp;
  e5.appendChild(e51);
  e5.appendChild(e52);


  const e2b3b = document.createElement('button');
  e2b3b.className = "btn btn-sm btn-outline-primary";
  e2b3b.id="archive";
  e2b3b.setAttribute("style","margin-left:15px;margin-bottom: 5px;");
  e2b3b.innerHTML ="Archive";

  const e2b3b2 = document.createElement('button');
  e2b3b2.className = "btn btn-sm btn-outline-primary";
  e2b3b2.id="unarchive";
  e2b3b2.setAttribute("style","margin-left:15px;margin-bottom: 5px;");
  e2b3b2.innerHTML ="unArchive";

  const e6 = document.createElement('button');
  e6.className = "btn btn-sm btn-outline-primary";
  e6.id="reply";
  e6.setAttribute("style","margin-left:15px;margin-bottom: 5px;");
  e6.innerHTML ="Reply";
  e6.addEventListener('click', function() {
    console.log('button reply clicked!')
    // reply ----------------

    let new_recipients= email.sender;
    e_recipients = email.recipients;

    let new_subject ="";
    if(email.subject.substring(0,4) ==="Re: ")
      new_subject = email.subject;
    else
      new_subject = "Re: "+email.subject;

    let new_body = "On "+email.timestamp+" "+email.sender+" wrote: \n"+email.body+"\n_________________________________________________________\n";
    e_timestamp = email.timestamp;
    compose_replay(new_recipients,new_subject,new_body);

    // reply ----------------
    });

/*
  const e7 = document.createElement('div');
  e7.className = "container border";
  e7.id="tresc";
  e7.setAttribute("style","margin-bottom: 5px;");
  e7.innerHTML =email.body;
*/
  const e7 = document.createElement('textarea');
  e7.className = "form-control";
  e7.id="tresc";
  e7.setAttribute("style","margin-bottom: 5px;");
  e7.innerHTML =email.body;


 e1.appendChild(e2);
 e1.appendChild(e3);
 e1.appendChild(e4);
 e1.appendChild(e5);

  if(mailbox === "inbox")
    e1.appendChild(e2b3b);
    if(mailbox === "archive")
    e1.appendChild(e2b3b2);

 e1.appendChild(e6);
 e1.appendChild(e7);

  //document.querySelector('#email_container').append(e1);
  document.querySelector('#emails-view').append(e1);

  e2b3b.addEventListener('click', function() {

    console.log('button to archive clicked!')
    putToArchiveEmail(email.id);
    load_mailbox('inbox');
    });


  e2b3b2.addEventListener('click', function() {

    console.log('button from archive clicked!')
    getFromArchiveEmail(email.id);
    load_mailbox('inbox');
    });

  //console.log("showing emaila"+email);
}
//-------------------------------------------------------------------------------------------------------------
function putToArchiveEmail(e_id){

  fetch('/emails_id/'+e_id,{
  method: 'PUT',
  headers: {
            'Accept': 'application/json',
             'Content-Type': 'application/json'
             },
  body: JSON.stringify({
      archived: true
  })
})

resetContainer();
fetch('/emails/'+'inbox');
}
//-------------------------------------------------------------------------------------------------------------
function getFromArchiveEmail(e_id){

  fetch('/emails_id/'+e_id,{
  method: 'PUT',
  headers: {
           'Accept': 'application/json',
           'Content-Type': 'application/json'
           },
  body: JSON.stringify({
      archived: false
  })
})

resetContainer();
fetch('/emails/'+'inbox');
}
//-------------------------------------------------------------------------------------------------------------
function compose_replay(recipients,subject,body) {

  // Show compose view and hide other views
  document.querySelector('#emails-view').style.display = 'none';
  document.querySelector('#compose-view').style.display = 'block';

  // Clear out composition fields
  document.querySelector('#compose-recipients').value = recipients;
  document.querySelector('#compose-subject').value = subject;
  document.querySelector('#compose-body').value = body;
}
//-------------------------------------------------------------------------------------------------------------
function markReadEmail(e_id){

  fetch('/emails_id/'+e_id,{
  method: 'PUT',
                    headers: {
                        'Accept': 'application/json',
                        'Content-Type': 'application/json'
                      },
  body: JSON.stringify({
    read: true
  })
})
}
//-------------------------------------------------------------------------------------------------------------

