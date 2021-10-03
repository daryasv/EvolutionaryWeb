var SETTINGS = buildUrlWithContextPath("settings");
var RUN_URL = buildUrlWithContextPath("run_evolution");

function getId(){
  const urlSearchParams = new URLSearchParams(window.location.search);
  return urlSearchParams.get("id");
}

var id = getId();



function back(){
  location.href = "../chatroom/chatroom.html";
}

function loadPageData(evolutionData){
  $('#settings-text').val(evolutionData.settings);
  const percentage = evolutionData.percentage || 0;
  $('#progress-bar').css('width',(percentage)+'%');
  $('#progress-text').text((percentage) + '%');
  $('#viewingOptions').html(evolutionData.viewingOptions);
  if(evolutionData.solutionFitness){
    $('#solutionTable').html(evolutionData.solutionFitness);
  }

}

function getPageData(){
  $.ajax({
    url: SETTINGS + "?evoId="+id,
    success: function(evolutionData) {
      loadPageData(evolutionData);
    }
  });
}

$(function() { // onload...do
  $('#ev_id').text(id);

 getPageData();

  setInterval(getPageData,1000);

  $("#run_evolution").submit(function(e) {
    var url = RUN_URL+"?evoId="+id;
    e.preventDefault();

    $.ajax({
      data: "",
      url: url,
      // timeout: 10000,
      method: "POST",
      error: function() {
        console.error("Failed to submit");
        alert("Failed ")
      },
      success: function(r) {
        alert("Success");
      }
    });




    // $("#input_file").val("");
    // by default - we'll always return false so it doesn't redirect the user.

    return false;
  });
});
