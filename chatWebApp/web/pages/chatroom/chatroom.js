var chatVersion = 0;
var settingsVersion = 0;
var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var CHAT_LIST_URL = buildUrlWithContextPath("chat");
var EVOLUTION_PROBLEM_LIST_URL = buildUrlWithContextPath("get_settings");

//users = a list of usernames, essentially an array of javascript strings:
// ["moshe","nachum","nachche"...]
function refreshUsersList(users) {
    //clear all current users
    $("#userslist").empty();
    
    // rebuild the list of users: scan all users and add them to the list of users
    $.each(users || [], function(index, username) {
        console.log("Adding user #" + index + ": " + username);

        //create a new <li> tag with a value in it and append it to the #userslist (div with id=userslist) element
        $('<li>' + username + '</li>')
            .appendTo($("#userslist"));
    });
}

function refreshProblemsList(problems) {
    //clear all current settings
    $("#evo_pro_table").empty();
    $('<tr>' +
        '<th scope="col">ID</th>' +
        '<th scope="col">Owner</th>' +
        '<th scope="col">Data</th>' +
        '<th scope="col">Rules</th>' +
        '<th scope="col">Users ran</th>' +
        '<th scope="col">Best fitness</th>' +
        '<th scope="col"></th>' +
        '</tr>')
        .appendTo($("#evo_pro_table"));
    // rebuild the list of users: scan all users and add them to the list of users
    $.each(problems.items || [], function(index, item) {
        //create a new <li> tag with a value in it and append it to the #userslist (div with id=userslist) element
        $('<tr>' +
            '<td>'+item.id+'</td>' +
            '<td>'+item.owner+'</td>' +
            '<td>'+
            '<div>Days: '+item.days+'</div>'+
            '<div>Hours: '+item.hours+'</div>'+
            '<div>Classes: '+item.classes+'</div>'+
            '<div>Teachers: '+item.teachers+'</div>'+
            '<div>Subjects: '+item.subjects+'</div>'+
            '</td>' +
            '<td>' +
            '<div>Hard: '+item.hardRules+'</div>'+
            '<div>Soft: '+item.softRules+'</div>'+
            '</td>' +
            '<td>'+(item.totalUsers || 0) + '</td>' +
            '<td>'+(item.bestFitness || 0).toFixed(2) +'</td>' +
            '<td><button class="btn btn-primary" onclick="openEvoSettings('+item.id+')">open</button></td>' +
            '</tr>')
            .appendTo($("#evo_pro_table"));
    });
}


//entries = the added chat strings represented as a single string
function appendToChatArea(entries) {
//    $("#chatarea").children(".success").removeClass("success");
    
    // add the relevant entries
    $.each(entries || [], appendChatEntry);
    
    // handle the scroller to auto scroll to the end of the chat area
    var scroller = $("#chatarea");
    var height = scroller[0].scrollHeight - $(scroller).height();
    $(scroller).stop().animate({ scrollTop: height }, "slow");
}

function appendChatEntry(index, entry){
    var entryElement = createChatEntry(entry);
    $("#chatarea").append(entryElement).append("<br>");
}

function createChatEntry (entry){
    entry.chatString = entry.chatString.replace (":)", "<img class='smiley-image' src='../../common/images/smiley.png'/>");
    return $("<span class=\"success\">").append(entry.username + "> " + entry.chatString);
}

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}

//call the server and get the chat version
//we also send it the current chat version so in case there was a change
//in the chat content, we will get the new string as well
function ajaxChatContent() {
    $.ajax({
        url: CHAT_LIST_URL,
        data: "chatversion=" + chatVersion,
        dataType: 'json',
        success: function(data) {
            /*
             data will arrive in the next form:
             {
                "entries": [
                    {
                        "chatString":"Hi",
                        "username":"bbb",
                        "time":1485548397514
                    },
                    {
                        "chatString":"Hello",
                        "username":"bbb",
                        "time":1485548397514
                    }
                ],
                "version":1
             }
             */
            console.log("Server chat version: " + data.version + ", Current chat version: " + chatVersion);
            if (data.version !== chatVersion) {
                chatVersion = data.version;
                appendToChatArea(data.entries);
            }
            triggerAjaxChatContent();
        },
        error: function(error) {
            triggerAjaxChatContent();
        }
    });
}

//add a method to the button in order to make that form use AJAX
//and not actually submit the form
$(function() { // onload...do
    //add a function to the submit event
    $("#chatform").submit(function() {
        $.ajax({
            data: $(this).serialize(),
            url: this.action,
            timeout: 2000,
            error: function(e) {
                console.error("Failed to submit");
            },
            success: function(r) {
                //do not add the user string to the chat area
                //since it's going to be retrieved from the server
                //$("#result h1").text(r);
            }
        });

        $("#userstring").val("");
        // by default - we'll always return false so it doesn't redirect the user.
        return false;
    });
});

function triggerAjaxChatContent() {
    setTimeout(ajaxChatContent, refreshRate);
}

//activate the timer calls after the page is loaded
$(function() {

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxProblemsList,refreshRate);
    //The chat content is refreshed only once (using a timeout) but
    //on each call it triggers another execution of itself later (1 second later)
    triggerAjaxChatContent();
});

function ajaxProblemsList() {
    $.ajax({
        url: EVOLUTION_PROBLEM_LIST_URL,
        data:"settingsversion=" + settingsVersion,
        success: function(evoPro) {
            if(evoPro.version !== settingsVersion) {
                settingsVersion = evoPro.version;
                refreshProblemsList(evoPro);
            }
        }
    });
}

$(function() { // onload...do
    //add a function to the submit event
    $("#upload_file").submit(function(e) {
        var url = this.action;
        e.preventDefault();

        $.ajax({
            data: new FormData(document.getElementById("upload_file")),
            url: url,
            processData: false,
            contentType: false,
            // timeout: 10000,
            method: "POST",
            error: function(error) {
                console.error("Failed to submit");
                alert(error.responseText || "Failed to load file")
            },

            success: function(r) {
                refreshProblemsList(r);
                alert("File loaded successfully");
            }
        });

        $("#input_file").val("");
        // by default - we'll always return false so it doesn't redirect the user.

        return false;
    });
});

function openEvoSettings(number){
    location.href = "../evolutionrun/evolution_run.html?id="+number;
}