var settingsVersion = 0;
var refreshRate = 2000; //milli seconds
var USER_LIST_URL = buildUrlWithContextPath("userslist");
var EVOLUTION_PROBLEM_LIST_URL = buildUrlWithContextPath("get_settings");
var chatopen = false;
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

function ajaxUsersList() {
    $.ajax({
        url: USER_LIST_URL,
        success: function(users) {
            refreshUsersList(users);
        }
    });
}


//activate the timer calls after the page is loaded
$(function() {
    ajaxUsersList();
    ajaxProblemsList();

    //The users list is refreshed automatically every second
    setInterval(ajaxUsersList, refreshRate);
    setInterval(ajaxProblemsList,refreshRate);
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

    $('#open_chat_btn').on('click',(e)=>{
        chatopen = !chatopen;
        if(chatopen){
            $('#chatcontainer').attr("class","row");
            $('#open_chat_btn').text("Close chat");
        }else{
            $('#chatcontainer').attr("class","hidden");
            $('#open_chat_btn').text("Open chat");
        }
    })
});

function openEvoSettings(number){
    location.href = "../evolutionrun/evolution_run.html?id="+number;
}